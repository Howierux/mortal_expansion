package com.howierux.mortalexpansion.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import java.util.concurrent.CompletableFuture;

/** Minecraft 1.21.1 post-chain schema, generated alongside other client resources. */
public final class ModPostEffects implements DataProvider {
    private final PackOutput output;
    public ModPostEffects(PackOutput output) { this.output = output; }
    @Override public String getName() { return "Spirit Journey post effects"; }
    @Override public CompletableFuture<?> run(CachedOutput cache) {
        var root = new JsonObject();
        var targets = new JsonArray();
        targets.add("swap");
        root.add("targets", targets);
        var passes = new JsonArray();
        var grayscale = pass("color_convolve", "minecraft:main", "swap");
        var uniforms = new JsonArray();
        var saturation = new JsonObject();
        saturation.addProperty("name", "Saturation");
        var values = new JsonArray();
        values.add(0.0);
        saturation.add("values", values);
        uniforms.add(saturation);
        grayscale.add("uniforms", uniforms);
        passes.add(grayscale);
        passes.add(pass("blit", "swap", "minecraft:main"));
        root.add("passes", passes);
        return DataProvider.saveStable(cache, root, output.getOutputFolder(PackOutput.Target.RESOURCE_PACK)
                .resolve("mortal_expansion/shaders/post/spirit_journey.json"));
    }
    private static JsonObject pass(String name, String input, String output) {
        var pass = new JsonObject();
        pass.addProperty("name", name);
        pass.addProperty("intarget", input);
        pass.addProperty("outtarget", output);
        return pass;
    }
}
