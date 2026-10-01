"""Validate guide translations, bounded page text, and recipe resources."""
import json
import re
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
LANG = json.loads((ROOT / "src/main/resources/assets/mortal_expansion/lang/en_us.json").read_text())
PREFIX = "book.mortal_expansion.ectcetera."
SOURCE = (ROOT / "src/main/java/com/howierux/mortalexpansion/guide/EctceteraGuide.java").read_text()
CHAPTERS = re.findall(r'new Chapter\("([^"]+)", (\d+)\)', SOURCE)

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

assert len(CHAPTERS) == len({key for key, _ in CHAPTERS})
expected = {PREFIX + "cover", PREFIX + "contents"}
for key, count in CHAPTERS:
    expected.add(PREFIX + "chapter." + key)
    for part in range(1, int(count) + 1):
        page = PREFIX + "page." + key + "." + str(part)
        expected.add(page)
        assert LANG[page].strip(), page
        assert lines(LANG[page]) <= 9, (page, "body exceeds page budget")
assert expected == {key for key in LANG if key.startswith(PREFIX)}, "Missing or orphan guide keys"
assert lines(LANG[PREFIX + "cover"] + "\n\nContents") <= 14
total = 1 + (len(CHAPTERS) + 4) // 5 + sum(int(count) for _, count in CHAPTERS)
assert total <= 100
recipe = json.loads((ROOT / "src/main/resources/data/mortal_expansion/recipe/book_of_ectcetera.json").read_text())
assert recipe["result"]["id"] == "mortal_expansion:book_of_ectcetera"
assert {ingredient["item"] for ingredient in recipe["ingredients"]} == {"minecraft:book", "minecraft:rotten_flesh"}
print(f"Ectcetera guide checks passed: {len(CHAPTERS)} chapters, {total} pages")
