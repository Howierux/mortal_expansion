# Loader branches and project folders

| Branch | Purpose | Build location |
| --- | --- | --- |
| main | Consolidated NeoForge/release baseline | Repository root |
| dev | NeoForge development | Repository root |
| fabric-dev | Fabric 1.21.1 port development | fabric/ |
| fabric | Reserved for validated Fabric releases; not created yet | fabric/ |

fabric-dev starts from NeoForge dev commit f080fdc36b994f3abcd2a0b2e0a0687c5130e545. Repository history and all current NeoForge source/assets remain available.

## Policy

Open fabric/ as the Fabric Gradle project. The root remains a separate NeoForge reference build, not the Fabric build entry point. Keep changes for each loader on its development branch. Promotion to either stable branch requires an explicit request and validation.

Do not merge fabric-dev wholesale into main/dev: branch documentation and CI routing are loader-specific. Review and port gameplay changes deliberately. The later common/ + neoforge/ + fabric/ shared-code structure is future maintenance work, not an implemented architecture.

On fabric-dev, existing NeoForge workflows exclude fabric, fabric-dev and fabric/**. Fabric CI builds fabric/ on these pushes, pull requests targeting fabric-dev/fabric, and manual dispatch. CI uploads development artifacts, not public releases.

Preserve the mortal_expansion registry namespace. Save migration still needs a design for NeoForge attachments and other loader-specific storage.

## Local folders

NeoForge playtest: C:/Users/scott/Documents/MyMods/mortal_expansion

Fabric checkout: C:/Users/scott/Documents/MyMods/mortal_expansion_fabric

Fabric project inside that checkout: fabric/

The original downloaded template is preserved separately. Caches, build output, run worlds, IDE state and local logs are excluded from Git.
