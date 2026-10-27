# Optional Fabric JEI integration

JEI 19.21.1.300 is compile-only and is not bundled or required. The Fabric `jei_mod_plugin` entry point is only loaded by JEI when installed. The development switch `-PwithJei` adds the Fabric JEI runtime for local checks. Version selection is centralized in fabric/gradle.properties.

The port provides Ectchantment transformations derived from the actual machine, Purification Station and heated-cauldron pages, both altar offering categories derived from loaded offering tags/durations, catalysts, book-tier subtypes and extra book variants. Crafting and smithing secret recipes remain visible with the reference spooky Secrets of the Dead banner. The banner explains the acquisition route and does not bypass server knowledge checks.

Native brewing uses actual Holy Water potion contents rather than fake crafting outputs. JEI's normal brewing support can display its native potion-container conversions; the empty-bottle/bucket custom operation remains implemented by the native recipe adapter.

Validation: the dedicated-server/build tests run with no JEI runtime. An integrated client launched successfully without JEI, then restarted with JEI and registered the Mortal Expansion plugin, categories, recipes and decorators without errors. Both runs used 30% volume. Other recipe viewers and third-party automated-crafting ownership remain separate integrations.
