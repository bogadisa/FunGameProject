#type vertex
#version 330 core
layout (location=0) in vec3 aPos;
layout (location=1) in vec4 aColor;
layout (location=2) in vec2 aTexCoords;

uniform mat4 uProjection;
uniform mat4 uView;

out vec4 fColor;
out vec2 fTexCoords;

void main()
{
    fColor = aColor;
    fTexCoords = aTexCoords;

    gl_Position = uProjection * uView * vec4(aPos, 1.0);
} 

#type fragment
#version 330 core

in vec4 fColor;
in vec2 fTexCoords;

uniform sampler2D uFontTexture;

out vec4 color;
// out float color;

void main()
{    
    // float r = 0.0;
    // if (texture(uFontTexture, fTexCoords).r == 0.0) {
    //     r = 1.0;
    // }
    // vec4 sampled = vec4(1.0, 1.0, 1.0, texture(uFontTexture, fTexCoords).r);
    // color = fColor * sampled;
    // color = sampled;
    // color = texture(uFontTexture, fTexCoords);
    
    float c = texture(uFontTexture, fTexCoords).r;
    // color = c;
    // color = vec4(0, 1, 0, c);
    color = fColor * c;
}  