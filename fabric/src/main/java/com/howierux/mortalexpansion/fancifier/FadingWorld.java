package com.howierux.mortalexpansion.fancifier;
import com.howierux.mortalexpansion.catalog.*;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.*;
import java.util.*;
import java.util.function.*;
/** Small version-independent metadata rules; rendering and native placement stay separate. */
public final class FadingWorld {
    private FadingWorld(){}
    public static BiConsumer<LevelChunk,FadingData> clientChanged=(chunk,data)->{};
    public static int index(LevelChunk chunk,BlockPos pos){return ((pos.getY()&0xFFF)<<8)|((pos.getZ()&15)<<4)|(pos.getX()&15);}
    public static BlockPos position(LevelChunk chunk,int index){return new BlockPos(chunk.getPos().getMinBlockX()+(index&15),((index>>>8)<<20)>>20,chunk.getPos().getMinBlockZ()+((index>>>4)&15));}
    public static int opacity(Level level,BlockPos pos){
        var chunk=(level.hasChunkAt(pos)?level.getChunkAt(pos):null);
        return chunk==null || !((net.fabricmc.fabric.api.attachment.v1.AttachmentTarget)chunk).hasAttached(com.howierux.mortalexpansion.content.ContentRuntime.FADING_BLOCKS)?100:((net.fabricmc.fabric.api.attachment.v1.AttachmentTarget)chunk).getAttachedOrCreate(com.howierux.mortalexpansion.content.ContentRuntime.FADING_BLOCKS).entries().getOrDefault(index(chunk,pos),100);
    }
    public static void set(Level level,BlockPos pos,int opacity){
        if(level.isClientSide || !FancifierRules.validOpacity(opacity))return;
        var chunk=(level.hasChunkAt(pos)?level.getChunkAt(pos):null);if(chunk==null)return;
        var previous=((net.fabricmc.fabric.api.attachment.v1.AttachmentTarget)chunk).hasAttached(com.howierux.mortalexpansion.content.ContentRuntime.FADING_BLOCKS)?((net.fabricmc.fabric.api.attachment.v1.AttachmentTarget)chunk).getAttachedOrCreate(com.howierux.mortalexpansion.content.ContentRuntime.FADING_BLOCKS):FadingData.EMPTY;
        int index=index(chunk,pos);if(previous.entries().getOrDefault(index,100)==opacity)return;
        var next=new HashMap<>(previous.entries());if(opacity==100)next.remove(index);else next.put(index,opacity);
        ((net.fabricmc.fabric.api.attachment.v1.AttachmentTarget)chunk).setAttached(com.howierux.mortalexpansion.content.ContentRuntime.FADING_BLOCKS,new FadingData(next));chunk.setUnsaved(true);
    }
    public static final class Placement {
        public final Level level;public final int opacity;public final Set<BlockPos> positions=new HashSet<>();
        public net.minecraft.world.level.block.Block block;
        public Placement(Level level,int opacity){this.level=level;this.opacity=opacity;}
    }
    public static final ThreadLocal<Placement> PLACEMENT=new ThreadLocal<>();
    private record Removed(Level level,long time,BlockState state,int opacity){}
    private static final class Recent {
        Level level;long time;final Map<BlockPos,Removed> entries=new HashMap<>();
        void prepare(Level current){if(level!=current || time!=current.getGameTime()){entries.clear();level=current;time=current.getGameTime();}}
    }
    private static final ThreadLocal<Recent> REMOVED=ThreadLocal.withInitial(Recent::new);
    public static void changed(LevelChunk chunk,BlockPos pos,BlockState before,BlockState after){
        if(before==null || chunk.getLevel().isClientSide)return;
        var level=chunk.getLevel();var scope=PLACEMENT.get();
        if(scope!=null && scope.level==level && (scope.block==null || after.getBlock()==scope.block) && !after.isAir() && before.getBlock()!=after.getBlock())scope.positions.add(pos.immutable());
        if(before.getBlock()==after.getBlock())return;
        int opacity=((net.fabricmc.fabric.api.attachment.v1.AttachmentTarget)chunk).hasAttached(com.howierux.mortalexpansion.content.ContentRuntime.FADING_BLOCKS)?((net.fabricmc.fabric.api.attachment.v1.AttachmentTarget)chunk).getAttachedOrCreate(com.howierux.mortalexpansion.content.ContentRuntime.FADING_BLOCKS).entries().getOrDefault(index(chunk,pos),100):100;
        var recent=REMOVED.get();recent.prepare(level);var removed=recent.entries;
        if(opacity==100){if(!after.isAir())removed.remove(pos);return;}
        // Related native object transitions retain the same cosmetic choice.
        if(before.getBlock() instanceof net.minecraft.world.level.block.AbstractCauldronBlock && after.getBlock() instanceof net.minecraft.world.level.block.AbstractCauldronBlock)return;
        var beforeMaterial=net.minecraft.world.item.HoneycombItem.WAX_OFF_BY_BLOCK.get().getOrDefault(before.getBlock(),before.getBlock());
        var afterMaterial=net.minecraft.world.item.HoneycombItem.WAX_OFF_BY_BLOCK.get().getOrDefault(after.getBlock(),after.getBlock());
        if(net.minecraft.world.level.block.WeatheringCopper.getFirst(beforeMaterial)==net.minecraft.world.level.block.WeatheringCopper.getFirst(afterMaterial))return;
        if(after.isAir()){if(removed.size()>4096)removed.clear();removed.put(pos.immutable(),new Removed(level,level.getGameTime(),before,opacity));}
        else removed.remove(pos);
        if(before.is(net.minecraft.world.level.block.Blocks.MOVING_PISTON) && !after.isAir())return;
        set(level,pos,100);
    }
    public static int dropOpacity(Level level,BlockPos pos,BlockState state){
        int opacity=opacity(level,pos);if(opacity<100)return opacity;
        var recent=REMOVED.get();recent.prepare(level);var old=recent.entries.get(pos);
        return old!=null && old.state.getBlock()==state.getBlock()?old.opacity:100;
    }
    public static java.util.List<ItemStack> decorateDrops(java.util.List<ItemStack> drops,Level level,BlockPos pos,BlockState state){
        int alpha=dropOpacity(level,pos,state);if(alpha==100)return drops;
        var result=new ArrayList<ItemStack>(drops.size());for(var stack:drops){
            if(stack.getItem() instanceof BlockItem) {stack=stack.copy();stack.set(com.howierux.mortalexpansion.content.ContentRuntime.FADING,alpha);}result.add(stack);
        }return result;
    }
}
