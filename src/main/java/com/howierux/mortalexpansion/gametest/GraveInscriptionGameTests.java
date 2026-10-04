package com.howierux.mortalexpansion.gametest;

import com.howierux.mortalexpansion.MortalExpansion;
import com.howierux.mortalexpansion.block.GraveBlock;
import com.howierux.mortalexpansion.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

@GameTestHolder(MortalExpansion.MOD_ID)
@PrefixGameTestTemplate(false)
public final class GraveInscriptionGameTests {
    @GameTest(template = "deferred_items_test")
    public static void inscriptions_follow_all_grave_faces_and_depths(GameTestHelper helper) {
        for (var facing : Direction.Plane.HORIZONTAL) for (int depth = 0; depth < 3; depth++) {
            var state = ModBlocks.GRAVE.get().defaultBlockState().setValue(GraveBlock.FACING, facing).setValue(GraveBlock.DEPTH, depth);
            var bounds = state.getShape(helper.getLevel(), BlockPos.ZERO).bounds();
            var anchor = GraveBlock.inscriptionAnchor(state);
            double face = facing.getAxisDirection() == Direction.AxisDirection.POSITIVE
                    ? bounds.max(facing.getAxis()) : bounds.min(facing.getAxis());
            double coordinate = facing.getAxis() == Direction.Axis.X ? anchor.x : anchor.z;
            double outward = (coordinate - face) * facing.getAxisDirection().getStep();
            helper.assertTrue(Math.abs(outward - .002) < .00001, "Inscription lies just outside front face for " + facing + "/" + depth);
            helper.assertTrue(anchor.y > bounds.minY && anchor.y < bounds.maxY, "Inscription height stays on stone");
            helper.assertTrue((facing.getAxis() == Direction.Axis.X ? anchor.z : anchor.x) == .5, "Inscription centered across front");
        }
        helper.succeed();
    }
}
