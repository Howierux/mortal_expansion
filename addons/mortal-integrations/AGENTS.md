# Mortal Integrations development

Follow the parent repository's migration guidelines. Keep this addon independently buildable and produce a separate JAR. Depend on JEI's supported API and Mortal Expansion's public helpers; never add JEI classes to the base mod. Keep client integrations in the `jei` package and out of common initialization. Show secret recipes for reference with the spectral notice; do not grant knowledge or bypass server validation. Versions belong in gradle.properties. Do not commit local dependency JARs or build caches.
