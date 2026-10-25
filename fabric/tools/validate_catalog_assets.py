"""Validate catalog coverage and native model dependencies without launching Minecraft."""
from pathlib import Path
import json,re
ROOT=Path(__file__).resolve().parents[2]
ASSETS=ROOT/'fabric/src/main/resources/assets/mortal_expansion'
def main():
 manifest=json.loads((ROOT/'fabric/tools/catalog_manifest.json').read_text())
 for kind in ['Items','Blocks']:
  original=(ROOT/f'src/main/java/com/howierux/mortalexpansion/registry/Mod{kind}.java').read_text()
  fields=set(re.findall(r'public static final Deferred(?:Item|Block)<[^;=]+>\s+(\w+)\s*=',original))
  assert fields==set(manifest[kind.lower()]),f'{kind} catalog coverage changed; update manifest and native registrations'
  native=(ROOT/f'fabric/src/main/java/com/howierux/mortalexpansion/catalog/Catalog{kind}.java').read_text()
  assert set(re.findall(r'public static final (?:Item|Block) (\w+)=register',native))==fields
 for item in manifest['items']: assert (ASSETS/f'models/item/{item.lower()}.json').is_file(),item
 for block in manifest['blocks'].values(): assert (ASSETS/f'blockstates/{block}.json').is_file(),block
 def inspect(node):
  if isinstance(node,dict):
   assert not str(node.get('loader','')).startswith('neoforge:'),'NeoForge loader in Fabric model'
   for key,value in node.items():
    if key in ['parent','model'] and isinstance(value,str) and value.startswith('mortal_expansion:'):
     assert (ASSETS/'models'/(value.split(':')[1]+'.json')).is_file(),value
    if key=='textures':
     for texture in value.values():
      if texture.startswith('mortal_expansion:'): assert (ASSETS/'textures'/(texture.split(':')[1]+'.png')).is_file(),texture
    inspect(value)
  elif isinstance(node,list):
   for entry in node: inspect(entry)
 for folder in ['models','blockstates']:
  for file in (ASSETS/folder).rglob('*.json'): inspect(json.loads(file.read_text()))
 originals=ROOT/'src/main/resources/assets/mortal_expansion/textures'
 for file in originals.rglob('*'):
  if file.is_file(): assert file.read_bytes()==(ASSETS/'textures'/file.relative_to(originals)).read_bytes(),file
 print(f"Catalog validated: {len(manifest['items'])} items, {len(manifest['blocks'])} blocks; original texture bytes and native model references preserved.")
if __name__=='__main__': main()
