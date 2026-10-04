"""Check generated-resource coverage, semantic snapshots, and local model references."""
import argparse
import hashlib
import json
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
GENERATED = ROOT / "src/generated/resources"
AUTHORED = ROOT / "src/main/resources"
BASELINE = ROOT / "tools/datagen-baseline.json"


def normalize(value):
    """Ignore only equivalent serializer defaults, namespace spellings, and float noise."""
    if isinstance(value, list):
        return [normalize(entry) for entry in value]
    if not isinstance(value, dict):
        if isinstance(value, float):
            rounded = round(value, 5)
            return int(rounded) if rounded.is_integer() else rounded
        return value
    value = dict(value)
    if "render_type" in value and ":" not in value["render_type"]:
        value["render_type"] = "minecraft:" + value["render_type"]
    if "variants" in value:
        value["variants"] = {",".join(sorted(key.split(","))): entry
                             for key, entry in value["variants"].items()}
    if "faces" in value and "from" in value:
        start, end = value["from"], value["to"]
        defaults = {
            "down": [start[0], 16-end[2], end[0], 16-start[2]],
            "up": [start[0], start[2], end[0], end[2]],
            "north": [16-end[0], 16-end[1], 16-start[0], 16-start[1]],
            "south": [start[0], 16-end[1], end[0], 16-start[1]],
            "west": [start[2], 16-end[1], end[2], 16-start[1]],
            "east": [16-end[2], 16-end[1], 16-start[2], 16-start[1]],
        }
        value["faces"] = {key: dict(face) for key, face in value["faces"].items()}
        for direction, face in value["faces"].items():
            if "uv" in face and all(abs(a-b) < 1e-5 for a, b in zip(face["uv"], defaults[direction])):
                face.pop("uv")
    defaults = {"replace": False, "bonus_rolls": 0, "weight": 1, "x": 0, "y": 0,
                "uvlock": False, "count": 1, "elements": [], "group": "", "show_notification": True}
    return {key: normalize(entry) for key, entry in value.items()
            if not (key in defaults and entry == defaults[key])}


def fingerprint(value):
    canonical = json.dumps(normalize(value), sort_keys=True, separators=(",", ":"))
    return hashlib.sha256(canonical.encode()).hexdigest()


def check_references(value):
    if isinstance(value, list):
        for entry in value:
            check_references(entry)
    elif isinstance(value, dict):
        for key in ("model", "parent"):
            target = value.get(key)
            if isinstance(target, str) and target.startswith("mortal_expansion:"):
                path = "assets/mortal_expansion/models/" + target.split(":", 1)[1] + ".json"
                assert (GENERATED / path).is_file(), f"Missing generated model: {target}"
        for target in value.get("textures", {}).values():
            if target.startswith("mortal_expansion:"):
                path = "assets/mortal_expansion/textures/" + target.split(":", 1)[1] + ".png"
                assert (AUTHORED / path).is_file() or (GENERATED / path).is_file(), f"Missing texture: {target}"
        for entry in value.values():
            check_references(entry)


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--update-baseline", action="store_true",
                        help="Record intentionally reviewed content changes after runData")
    args = parser.parse_args()
    files = sorted(path for path in GENERATED.rglob("*.json") if ".cache" not in path.parts)
    assert files, "No generated JSON found; run gradlew runData first"
    actual = {}
    for path in files:
        relative = path.relative_to(GENERATED).as_posix()
        assert not (AUTHORED / relative).exists(), f"Duplicate authored/generated resource: {relative}"
        value = json.loads(path.read_text(encoding="utf-8"))
        check_references(value)
        actual[relative] = fingerprint(value)
    if args.update_baseline:
        BASELINE.write_text(json.dumps(actual, indent=2) + "\n", encoding="utf-8")
    else:
        expected = json.loads(BASELINE.read_text(encoding="utf-8"))
        missing, added = expected.keys() - actual.keys(), actual.keys() - expected.keys()
        changed = [path for path in expected.keys() & actual.keys() if expected[path] != actual[path]]
        assert not (missing or added or changed), f"Generated content changed: missing={sorted(missing)}, added={sorted(added)}, changed={sorted(changed)}"
    print(f"Data-generation checks passed: {len(actual)} resources; no duplicate files or missing model references")


if __name__ == "__main__":
    main()
