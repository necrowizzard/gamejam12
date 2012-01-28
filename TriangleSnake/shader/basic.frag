varying vec3 position;
varying vec2 texture_coordinate;

uniform sampler2D color_texture;
uniform float animate;

void main()
{

	//position *= sin(animate);

	//problem without +0.001 (looks like z fighting, but isnt)
    //gl_FragColor = vec4(mod(position.x+0.001, 10.0)/10.0, mod(position.y+0.001, 10.0)/10.0, mod(position.z+0.001, 10.0)/10.0, 1.0);
    
    gl_FragColor = texture2D(color_texture, texture_coordinate);
    
    float color = mix(gl_FragColor.r, gl_FragColor.g, animate);
    color = color * mix(gl_FragColor.g, gl_FragColor.b, animate);
    
    gl_FragColor = vec4(1.0-color,1.0-color,gl_FragColor.b,1.0);
    
    gl_FragColor = 1.0/gl_FragColor * vec4(0.1, 0.1, 0.1, 1.0);
    
    //gl_FragColor *= sin(animate);
    
    //gl_FragColor = vec4(position.x,position.y, position.z, 1.0);
}