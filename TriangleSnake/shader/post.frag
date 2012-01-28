varying vec2 texture_coordinate;

uniform sampler2D color_texture;

void main()
{
    
    vec4 color = texture2D(color_texture, texture_coordinate);
    
    if (color.r+color.g+color.b < 1.7 && color.r+color.g+color.b > 0.3) discard;
    
    
    gl_FragColor = color;
}