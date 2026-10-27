"""Compare the checked-in NeoForge reference with native Fabric resources.
Run from any directory after runDatagen. This is a resource/registry audit;
it does not claim behavioral equivalence or third-party compatibility.
"""
from pathlib import Path
import hashlib, json, re, subprocess
from validate_catalog_assets import main as validate_catalog
ROOT = Path(__file__).resolve().parents[2]

def overlay(*roots):
    result = {}
    for root in roots:
        for file in (ROOT / root).rglob("*"):
            if file.is_file() and ".cache" not in file.parts:
                result[file.relative_to(ROOT / root).as_posix()] = file
    return result

def normalized(node):
    if isinstance(node, dict):
        result = {k: normalized(v) for k, v in node.items() if k != "category"}
        if result.get("count") == 1: result.pop("count")
        components = result.get("components", {})
        if components.get("mortal_expansion:ectcetera_tier") == 0:
            components.pop("mortal_expansion:ectcetera_tier")
            if not components: result.pop("components")
        return result
    if isinstance(node, list): return [normalized(v) for v in node]
    return node

def main():
    validate_catalog()
    git = lambda *args: subprocess.check_output(["git", *args], cwd=ROOT, text=True).strip()
    sha = git("rev-parse", "origin/dev")
    assert not git("diff", "origin/dev", "--", "src"), "NeoForge reference differs from fetched dev"
    neo = overlay("src/main/resources/data", "src/generated/resources/data")
    fab = overlay("fabric/src/main/resources/data", "fabric/src/main/generated/data")
    recipes = {k for k in neo if k.startswith("mortal_expansion/recipe/")}
    missing = sorted(recipes - fab.keys())
    assert not missing, f"Missing ME recipes: {missing}"
    different = [k for k in sorted(recipes) if normalized(json.loads(neo[k].read_text(encoding="utf-8"))) != normalized(json.loads(fab[k].read_text(encoding="utf-8")))]
    assert not different, f"Recipe semantics require review: {different}"
    missing_tags = []
    for key, file in neo.items():
        if "/tags/" not in key or not key.endswith(".json"): continue
        reference = json.loads(file.read_text(encoding="utf-8"))
        actual = json.loads(fab[key].read_text(encoding="utf-8")) if key in fab else {"values": []}
        values = {v if isinstance(v, str) else v["id"] for v in actual.get("values", [])}
        for value in reference.get("values", []):
            value = value if isinstance(value, str) else value["id"]
            if value not in values: missing_tags.append(f"{key}: {value}")
    assert not missing_tags, "Missing public tag members: " + repr(missing_tags)
    original = overlay("src/main/resources/assets/mortal_expansion", "src/generated/resources/assets/mortal_expansion")
    native = overlay("fabric/src/main/resources/assets/mortal_expansion", "fabric/src/main/generated/assets/mortal_expansion")
    media = [k for k in original if k.startswith(("textures/", "sounds/", "shaders/"))]
    missing_media = [k for k in media if k not in native or (original[k].read_bytes() != native[k].read_bytes() if k.endswith((".png", ".ogg")) else original[k].read_text(encoding="utf-8") != native[k].read_text(encoding="utf-8"))]
    assert not missing_media, f"Missing/changed reference media: {missing_media}"
    print(f"NeoForge dev: {sha}; Fabric comparison: {git('rev-parse', 'HEAD')}")
    print(f"PASS: {len(recipes)} recipe definitions, public tag membership, {len(media)} media assets.")
    print("Native GameTests and client checks are separate; see docs/FABRIC_PARITY_AUDIT.md.")

if __name__ == "__main__": main()
