from pathlib import Path
import json, shutil, copy
ROOT=Path(__file__).resolve().parents[2]
def bucket_model():
 # Preserve Mojang bucket artwork. Fluid-colored quads cover only the three liquid rows,
 # using the same native grayscale water sprite as the original fluid-container renderer.
 elements=[{'from':[0,0,8],'to':[16,16,8],'faces':{side:{'texture':'#base','uv':[0,0,16,16]} for side in ['north','south']}}]
 for row,left,right in [(3,4,12),(4,3,13),(5,5,11)]:
  for side,z in [('north',7.99),('south',8.01)]:
   # North faces mirror X in model UV space.
   x0,x1=(16-right,16-left) if side=='north' else (left,right)
   elements.append({'from':[x0,15-row,z],'to':[x1,16-row,z],'faces':{side:{'texture':'#fluid','uv':[left,row,right,row+1],'tintindex':0}}})
 return {'gui_light':'front','textures':{'base':'minecraft:item/water_bucket','fluid':'minecraft:block/water_still','particle':'minecraft:item/bucket'},'elements':elements,
 'display':{'ground':{'translation':[0,2,0],'scale':[0.5]*3},'head':{'rotation':[0,180,0],'translation':[0,13,7]},'thirdperson_righthand':{'translation':[0,3,1],'scale':[0.55]*3},'firstperson_righthand':{'rotation':[0,-90,25],'translation':[1.13,3.2,1.13],'scale':[0.68]*3},'fixed':{'rotation':[0,180,0]}}}
def main():
 out=ROOT/'fabric/src/main/resources/assets/mortal_expansion'
 # Art is copied byte-for-byte; no recoloring or replacement placeholders.
 for folder in ['textures','lang']:
  shutil.copytree(ROOT/'src/main/resources/assets/mortal_expansion'/folder,out/folder,dirs_exist_ok=True)
 for meta in (out/'textures').rglob('*.mcmeta'):
  meta.write_text(meta.read_text(encoding='utf-8'),encoding='utf-8',newline='\n')
 for folder in ['models','blockstates']:
  for src in (ROOT/'src/generated/resources/assets/mortal_expansion'/folder).rglob('*.json'):
   data=json.loads(src.read_text(encoding='utf-8'))
   if data.get('loader')=='neoforge:composite':
    # Vanilla JSON accepts the same elements, with a translucent block render layer on Fabric.
    elements=[]; textures=dict(data.get('textures',{}))
    for name,child in data.pop('children').items():
     for key,value in child.get('textures',{}).items(): textures[name+'_'+key]=value
     for elem in child['elements']:
      elem=copy.deepcopy(elem)
      for face in elem['faces'].values():
       if face['texture'].startswith('#'): face['texture']='#'+name+'_'+face['texture'][1:]
      elements.append(elem)
    data={'parent':'minecraft:block/block','textures':textures,'elements':elements}
   if data.get('loader')=='neoforge:fluid_container':
    data=bucket_model()
   if src.name in ['emf_reader.json','book_of_ectcetera.json']:
    divisor=12 if src.name=='emf_reader.json' else 4
    for override in data.get('overrides',[]):
     for key in override.get('predicate',{}): override['predicate'][key]/=divisor
   if src.name=='precise_emf_reader.json': data={'parent':'builtin/entity','display':json.loads((ROOT/'src/generated/resources/assets/mortal_expansion/models/item/emf_reader_0.json').read_text()) .get('display',{})}
   if src.name=='fancified_block.json':
    data={'variants':{'':{'model':'mortal_expansion:block/gravestone'}}} if folder=='blockstates' else {'parent':'mortal_expansion:block/gravestone'}
   if data.get('parent')=='minecraft:block/cube_orientable': data['parent']='minecraft:block/orientable'
   data.pop('render_type',None)
   dest=out/folder/src.relative_to(ROOT/'src/generated/resources/assets/mortal_expansion'/folder)
   dest.parent.mkdir(parents=True,exist_ok=True)
   dest.write_text(json.dumps(data,indent=2)+'\n',encoding='utf-8',newline='\n')
 for fluid in ['liquid_ectoplasm','holy_water','purified_ectoplasm_fluid']:
  (out/'blockstates'/f'{fluid}.json').write_text(json.dumps({'variants':{'':{'model':'minecraft:block/water'}}},indent=2)+'\n',encoding='utf-8',newline='\n')
 lang=out/'lang/en_us.json'
 data=json.loads(lang.read_text(encoding='utf-8')); data['tooltip.mortal_expansion.fabric.pending']='Fabric preview: special behavior not yet ported.'; data['gui.mortal_expansion.enchantment_generator.processing']='Extracting enchantments...'
 data.update({
  'gui.mortal_expansion.fabric.dead_tab':'Dead Secrets',
  'gui.mortal_expansion.fabric.guide_notice':'Fabric development: some features are still being ported.',
  'message.mortal_expansion.fabric.offering_gift':'Something unseen leaves salt among your offerings.',
  'message.mortal_expansion.fabric.offering_blessing':'A gentle presence feeds the waiting flame.',
  'tooltip.mortal_expansion.fabric.darkhold_pending':'Darkhold exposure and sin powers await their Fabric port.'
 })
 lang.write_text(json.dumps(data,indent=2,ensure_ascii=False)+'\n',encoding='utf-8',newline='\n')
 print('Imported existing catalog models, blockstates, textures and translations.')
if __name__=='__main__': main()
