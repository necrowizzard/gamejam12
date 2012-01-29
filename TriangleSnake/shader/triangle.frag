varying vec3 position;
varying vec2 texture_coordinate;

uniform sampler2D color_texture;
uniform float color_parameter;

void main()
{

	vec4 color = texture2D(color_texture, texture_coordinate*1.0);

	//problem without +0.001 (looks like z fighting, but isnt)
    //gl_FragColor = vec4(mod(position.x+0.001, 10.0)/10.0, mod(position.y+0.001, 10.0)/10.0, mod(position.z+0.001, 10.0)/10.0, 1.0);
    
    gl_FragColor = vec4(color.x, color.y, color.z, 1.0);
    
    if (texture_coordinate.x > 0.15 && texture_coordinate.x < 0.85
    	&& texture_coordinate.y > 0.15 && texture_coordinate.y < 0.85
    	&& color.x < 0.75) discard;
    //if ( color.x < 0.75) discard;
    
    //gl_FragColor.r *= 1.1*color_parameter;
    //gl_FragColor.g *= 1.5-color_parameter;
    //gl_FragColor.b *= 0.55-color_parameter;
    
    //OLD
    gl_FragColor.r *= 1.5*color_parameter;
    gl_FragColor.g *= 0.6*(1.0-color_parameter);
    gl_FragColor.b *= 0.8;
    
    //gl_FragColor *= vec4(0.15, 0.1, 0.1, 1.0); //dark
    
    //OLD (BW)
    //gl_FragColor += vec4(0.5-color_parameter*1.5, 0.5-color_parameter*1.9, 0.1, 1.0);
    
    gl_FragColor += vec4(0.5, 0.5, 0.1, 1.0);
    
    //gl_FragColor.a = 2.0 - abs(position.z/50.0);
    
    //z is positive and starts around 0
    //x colors are 0-1(0: dark)
    //if (100 < position.z) {
    	//gl_FragColor.rgb = vec3(0.3, 0.3, 0.6);
    	//gl_FragColor.b += 0.3;
    	//gl_FragColor.rg *= 0.5;
    	//gl_FragColor = smoothstep(gl_FragColor,vec4(0.3, 0.3, 0.6, 1.0), position.z/100.0);
    //}
    //2 default
    gl_FragColor.rg *= clamp(50.0/position.z, 0.0, 1.0);
    if (color_parameter > 0.9) { //red recoloring in distance
    	gl_FragColor.r *= 0.5*clamp(50.0/position.z, 0.0, 1.0);
    }
    
    gl_FragColor.rgb *= clamp(150.0/position.z, 0.0, 1.0);
    
    if (150.0*color.x < position.z) discard;
    
    //gl_FragColor = vec4(position.x,position.y, position.z, 1.0);
}