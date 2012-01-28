varying vec3 position;
varying vec2 texture_coordinate;

uniform sampler2D color_texture;
uniform float color_parameter;

void main()
{

	vec4 color = texture2D(color_texture, texture_coordinate*1.0);

	//problem without +0.001 (looks like z fighting, but isnt)
    //gl_FragColor = vec4(mod(position.x+0.001, 10.0)/10.0, mod(position.y+0.001, 10.0)/10.0, mod(position.z+0.001, 10.0)/10.0, 1.0);
    
    gl_FragColor = vec4(color.x, color.x, color.x, 1.0);
    
    //if (texture_coordinate.x > 0.15 && texture_coordinate.x < 0.85
    //	&& texture_coordinate.y > 0.15 && texture_coordinate.y < 0.85
    //	&& color.x < 0.8) discard;
    if ( color.x < 0.75) discard;
    
    gl_FragColor.r *= color_parameter;
    gl_FragColor.g *= 0.7-color_parameter;
    gl_FragColor.b *= 0.5-color_parameter;
    
    //gl_FragColor *= vec4(0.15, 0.1, 0.1, 1.0); //dark
    
    gl_FragColor += vec4(0.5-color_parameter*1.5, 0.5-color_parameter*1.5, 0.1, 1.0);
    
    //gl_FragColor = vec4(position.x,position.y, position.z, 1.0);
}