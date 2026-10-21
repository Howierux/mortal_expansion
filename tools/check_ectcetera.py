"""Validate guide translations, bounded page text, and recipe resources."""
import json
import re
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
LANG = json.loads((ROOT / "src/main/resources/assets/mortal_expansion/lang/en_us.json").read_text())
PREFIX = "book.mortal_expansion.ectcetera."
SOURCE = (ROOT / "src/main/java/com/howierux/mortalexpansion/guide/EctceteraGuide.java").read_text()
CHAPTERS = re.findall(r'new Chapter\("([^"]+)", (\d+), ([0-3])\)', SOURCE)

# Conservative default-font width estimate; actual in-game rendering remains a smoke test.
def width(text):
    narrow = {" ": 4, "i": 2, "l": 3, "t": 4, "f": 5, ".": 2, ",": 2,
              ":": 2, ";": 2, "'": 2, "!": 2, "I": 4}
    return sum(narrow.get(char, 6) for char in text)

def lines(text):
    total = 0
    for paragraph in text.split("\n"):
        used, count = 0, 1
        for word in paragraph.split():
            size = width(word)
            if used and used + 4 + size > 114:
                count += 1
                used = size
            else:
                used += (4 if used else 0) + size
        total += count
    return total

assert len(CHAPTERS) == len({key for key, _, _ in CHAPTERS})
expected = {PREFIX + "cover", PREFIX + "contents"}
for key, count, tier in CHAPTERS:
    expected.add(PREFIX + "chapter." + key)
    for part in range(1, int(count) + 1):
        page = PREFIX + "page." + key + "." + str(part)
        expected.add(page)
        assert LANG[page].strip(), page
        assert lines(LANG[page]) <= 9, (page, "body exceeds page budget")
assert expected == {key for key in LANG if key.startswith(PREFIX)}, "Missing or orphan guide keys"
for key,text in LANG.items():
    if key.startswith("insight.mortal_expansion.") and key.endswith((".guidance", ".locked")):
        assert lines(text) <= 9, (key, "dynamic guidance exceeds page budget")
for part in range(1, 4):
    key = "guide.mortal_expansion.grave_porting.page." + str(part)
    assert LANG[key].strip() and lines(LANG[key]) <= 9, (key, "map entry exceeds page budget")
assert LANG["guide.mortal_expansion.grave_porting.name"].strip()
for part in range(1, 5):
    key = "guide.mortal_expansion.ecto_infrastructure.page." + str(part)
    assert LANG[key].strip() and lines(LANG[key]) <= 9, (key, "map entry exceeds page budget")
assert LANG["guide.mortal_expansion.ecto_infrastructure.name"].strip()
# Research-map-only entries have their own text without changing legacy linear-book offsets.
for part in range(1, 4):
    key = "guide.mortal_expansion.wisp_o_lantern.page." + str(part)
    assert LANG[key].strip() and lines(LANG[key]) <= 9, (key, "map entry exceeds page budget")
assert LANG["guide.mortal_expansion.wisp_o_lantern.name"].strip()
for part in range(1, 7):
    key = "guide.mortal_expansion.mortal_golem.page." + str(part)
    assert LANG[key].strip() and lines(LANG[key]) <= 9, (key, "map entry exceeds page budget")
assert LANG["guide.mortal_expansion.mortal_golem.name"].strip()
for chapter in ("darkhold", "demons"):
    assert LANG[f"darkhold.mortal_expansion.{chapter}.name"].strip()
    for part in range(1, 5 if chapter=="demons" else 3):
        assert LANG[f"darkhold.mortal_expansion.{chapter}.page.{part}"].strip()
assert lines(LANG[PREFIX + "cover"] + "\n\nContents") <= 14
total = 1 + (len(CHAPTERS) + 4) // 5 + sum(int(count) for _, count, _ in CHAPTERS)
assert total <= 128
handoffs = ["unlock_bone", "unlock_raw", "unlock_pure", "mastery"]
for tier in range(4):
    unlocked = [(key, int(count)) for key, count, level in CHAPTERS if int(level) <= tier]
    assert unlocked[-1][0] == handoffs[tier], (tier, "missing final progression guidance")
    assert all(int(level) <= tier for key, _, level in CHAPTERS if key in {key for key, _ in unlocked})
    tier_total = 1 + (len(unlocked) + 4) // 5 + sum(count for _, count in unlocked)
    assert tier_total <= 128
recipe = json.loads((ROOT / "src/generated/resources/data/mortal_expansion/recipe/book_of_ectcetera.json").read_text())
assert recipe["result"]["id"] == "mortal_expansion:book_of_ectcetera"
assert {ingredient["item"] for ingredient in recipe["ingredients"]} == {"minecraft:book", "minecraft:rotten_flesh"}
assert recipe["result"]["components"]["mortal_expansion:ectcetera_tier"] == 0
for tier in range(4):
    upgrade = json.loads((ROOT / f"src/generated/resources/data/mortal_expansion/recipe/book_of_ectcetera_upgrade_{tier + 1}.json").read_text())
    assert upgrade["type"] == "mortal_expansion:ectcetera_upgrade" and upgrade["from_tier"] == tier
print(f"Ectcetera guide checks passed: {len(CHAPTERS)} chapters, {total} pages")
