package com.howierux.mortalexpansion.mixin;
import com.howierux.mortalexpansion.client.FadingMesh;
import com.mojang.blaze3d.vertex.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
@Mixin(BufferBuilder.class)
public abstract class FadingMeshBuilderMixin implements FadingMesh {
    @Unique private boolean mortalExpansion$faded;
    @Override public void mortalExpansion$markFaded(){mortalExpansion$faded=true;}
    @Inject(method="build",at=@At("RETURN"))
    private void mortalExpansion$carryMarker(CallbackInfoReturnable<MeshData> callback){
        if(mortalExpansion$faded && callback.getReturnValue()!=null)((FadingMesh)(Object)callback.getReturnValue()).mortalExpansion$markFaded();
    }
}
