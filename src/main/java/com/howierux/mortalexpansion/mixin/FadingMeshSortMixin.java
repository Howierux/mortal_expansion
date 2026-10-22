package com.howierux.mortalexpansion.mixin;
import com.howierux.mortalexpansion.client.*;
import com.mojang.blaze3d.vertex.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
/** Hook the mesh itself: Iris batches bypass vanilla BufferSource.endBatch entirely. */
@Mixin(MeshData.class)
public abstract class FadingMeshSortMixin implements FadingMesh {
    @Unique private boolean mortalExpansion$faded;
    @Unique private VertexSorting mortalExpansion$sorting;
    @Override public void mortalExpansion$markFaded(){mortalExpansion$faded=true;}
    @ModifyVariable(method="sortQuads",at=@At("HEAD"),argsOnly=true)
    private VertexSorting mortalExpansion$stableOverlap(VertexSorting sorting){
        if(!mortalExpansion$faded)return sorting;
        if(mortalExpansion$sorting==null)mortalExpansion$sorting=FadingOverlap.prepare((MeshData)(Object)this,sorting);
        return mortalExpansion$sorting;
    }
}
