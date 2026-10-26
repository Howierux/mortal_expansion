#version 150
#moj_import <fog.glsl>
uniform sampler2D Sampler0;
uniform vec4 ColorModulator;
uniform float FogStart;
uniform float FogEnd;
uniform vec4 FogColor;
in float vertexDistance;
in vec4 vertexColor;
in vec2 texCoord0;
out vec4 fragColor;
void main() {
    vec4 texel = texture(Sampler0, texCoord0);
    // Cut out empty texture pixels BEFORE applying the selected opacity.
    if (texel.a < 0.1) discard;
    fragColor = linear_fog(texel * vertexColor * ColorModulator, vertexDistance, FogStart, FogEnd, FogColor);
}
