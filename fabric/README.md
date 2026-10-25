# Mortal Expansion: Fabric development

**Gameplay port in progress. Persistent Haunt, commands, kill gains and loaded-chunk simulation are implemented; other content is still pending.** Original NeoForge source remains at the repository root as the reference.

Develop on **fabric-dev**. The stable **fabric** branch is reserved until a validated release exists.

## Build

Open this fabric/ directory as the Gradle project in your IDE, with JDK 21 for the Gradle JVM/toolchain.

~~~text
cd fabric
gradlew.bat build
~~~

Linux/macOS: run ./gradlew build inside fabric/. Output: build/libs/mortal_expansion-fabric-1.0.0-fabric-dev.jar. Use fresh test worlds: the original content registry and NeoForge save migration are not ported.

Other development tasks: runClient, runServer, runDatagen. Before launching a client, set soundCategory_master:0.3 in fabric/run/options.txt, preserving other options. Server launches require normal Minecraft EULA acceptance.

## Toolchain

Minecraft 1.21.1; Fabric Loader 0.20.1; Fabric API 0.116.17+1.21.1; Java 21; Gradle 9.8.2; Loom 1.17.21; official Mojang mappings. Existing Kotlin bootstrap uses Kotlin 2.4.21 and Fabric Language Kotlin 1.14.2+kotlin.2.4.21. Java gameplay can be ported without changing languages.

Namespace: mortal_expansion. The artifact filename includes -fabric. Datagen supplies the three Haunt biome-classification tags. Mod metadata retains All Rights Reserved and original branding; template provenance is in TEMPLATE_LICENSE.txt.

The build runs four isolated Fabric GameTests automatically. These tests are not packaged in the gameplay jar. See the [Haunt foundation](../docs/FABRIC_HAUNT_FOUNDATION.md) for commands and exact scope.

See [branch policy](../docs/LOADER_BRANCHES.md) and [port status](../docs/FABRIC_PORT.md). Add/test dependencies such as TerraBlender and recipe-viewer integrations alongside their associated ports.
