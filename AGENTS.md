# Development guidelines

All updates should make future Minecraft and NeoForge version migrations easier.

- Prefer supported framework APIs and Mojang/NeoForge data generators over handwritten resource schemas. Follow `docs/DATA_GENERATION.md`; commit providers and generated output together.
- Keep gameplay rules separate from registration, networking, persistence, and rendering APIs. Contain version-sensitive calls in focused classes or helpers when this reduces duplication; avoid speculative compatibility frameworks.
- Minimize mixins and direct reliance on Minecraft internals. When required, document why a supported API is insufficient and the behavior that must survive migration.
- Use registry keys, tags, and codecs where appropriate instead of numeric IDs, hardcoded membership lists, or custom serializers duplicating framework behavior.
- Preserve saved-world and network compatibility deliberately. Document changes to stored data and provide migration handling when existing saves would otherwise break.
- Centralize dependency versions and keep client-only code out of common/server initialization.
- Validate relevant gameplay behavior, generated resource consistency, and client/server builds when changing version-sensitive code. Record known migration constraints alongside the affected implementation.

Apply these principles to new and modified code without expanding each feature into an unrelated repository-wide refactor.
