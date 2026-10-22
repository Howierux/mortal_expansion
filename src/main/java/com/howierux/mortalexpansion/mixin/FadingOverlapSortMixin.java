package com.howierux.mortalexpansion.mixin;
import com.howierux.mortalexpansion.client.*;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.renderer.*;
import com.llamalad7.mixinextras.injector.wrapoperation.*;
import com.llamalad7.mixinextras.sugar.Local;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
/** No framework callback exists between immediate mesh assembly and index sorting. */
@Mixin(MultiBufferSource.BufferSource.class)
public abstract class FadingOverlapSortMixin {
    @WrapOperation(method="endBatch(Lnet/minecraft/client/renderer/RenderType;Lcom/mojang/blaze3d/vertex/BufferBuilder;)V",
        at=@At(value="INVOKE",target="Lcom/mojang/blaze3d/vertex/MeshData;sortQuads(Lcom/mojang/blaze3d/vertex/ByteBufferBuilder;Lcom/mojang/blaze3d/vertex/VertexSorting;)Lcom/mojang/blaze3d/vertex/MeshData$SortState;"))
    private MeshData.SortState mortalExpansion$stableOverlap(MeshData mesh,ByteBufferBuilder builder,VertexSorting sorting,Operation<MeshData.SortState> original,@Local(argsOnly=true) RenderType type){
        return original.call(mesh,builder,FadingRenderTypes.isFadedMaterial(type)?FadingOverlap.prepare(mesh,sorting):sorting);
    }
}
