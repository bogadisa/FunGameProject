#type vertex
#version 330 core
layout (location=0) in vec3 aPos;
layout (location=1) in vec4 aColor;
layout (location=2) in vec2 aTexCoords;
layout (location=3) in float aTexId;

uniform mat4 uProjection;
uniform mat4 uView;

out vec4 fColor;
out vec2 fTexCoords;
out float fTexId;

void main()
{
    fColor = aColor;
    fTexCoords = aTexCoords;
    fTexId = aTexId;

    gl_Position = uProjection * uView * vec4(aPos, 1.0);
}

#type fragment
#version 330 core

in vec4 fColor;
in vec2 fTexCoords;
in float fTexId;

uniform sampler2D uTextures[8];

out vec4 color;
// out vec3 color;

void main()
{
    // Example:
    // (1, 1, 1, 1) * (0.4, 2, 3, 1)
    // Which is why we made it white, but it allows for tinting
    switch (int(fTexId)) {
        case 0:
            color = fColor;
            break;
        case 1:
            // color = vec4(fTexCoords, 1, 1);
            // color = fColor * texture(uTextures[1], fTexCoords);
            // color = texture(uTextures[1], fTexCoords);
            float c = texture(uTextures[1], fTexCoords).r;
            color = vec4(0, c, 0, 1.0);
            // color = vec4(1, 1, 1, c) * vec4(fColor.rgb, 1);
            // color = fColor*vec4(1.0, 1.0, 1.0, texture(uTextures[1], fTexCoords).r);
            // vec4 c = texture(uTextures[1], fTexCoords);
            // color = c* fColor;
            break;
        case 2:
            // color = fColor * texture(uTextures[2], fTexCoords);
            color = vec4(fTexCoords, 1.0, 1.0);
            // color = vec4(texture(uTextures[2], fTexCoords).r, 0, 0, 1);
            break;
        case 3:
            color = fColor * texture(uTextures[3], fTexCoords);
            // color = vec4(texture(uTextures[3], fTexCoords).r, 0, 0, 1);
            break;
        case 4:
            color = fColor * texture(uTextures[4], fTexCoords);
            // color = vec4(texture(uTextures[4], fTexCoords).r, 0, 0, 1);
            break;
        case 5:
            color = fColor * texture(uTextures[5], fTexCoords);
            // color = vec4(texture(uTextures[5], fTexCoords).r, 0, 0, 1);
            break;
        case 6:
            color = fColor * texture(uTextures[6], fTexCoords);
            // color = vec4(texture(uTextures[6], fTexCoords).r, 0, 0, 1);
            break;
        case 7:
            color = fColor * texture(uTextures[7], fTexCoords);
            // color = vec4(texture(uTextures[7], fTexCoords).r, 0, 0, 1);
            break;
    }
}