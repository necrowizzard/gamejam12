varying vec3 position;
varying vec2 texture_coordinate;

uniform sampler2D color_texture;
uniform float animate;


//OLD
//gl_FragColor = texture2D(color_texture, texture_coordinate);
    
//float color = mix(gl_FragColor.r, gl_FragColor.g, animate);
//color = color * mix(gl_FragColor.g, gl_FragColor.b, animate);
    
//gl_FragColor = vec4(1.0-color,1.0-color,gl_FragColor.b,1.0);
    
//0.12, 0.12, 0.15
//gl_FragColor = 1.0/gl_FragColor * vec4(0.05, 0.05, 0.05, 1.0); //0.1, 0.1, 0.1
    
//gl_FragColor = gl_FragColor * vec4(1.5, 1.5, 1.5, 1.0);
//OLD END


void main()
{

	//position *= sin(animate);
	
	//animate = sin(animate);
	
	float animate1 = animate*0.4;

	//problem without +0.001 (looks like z fighting, but isnt)
    //gl_FragColor = vec4(mod(position.x+0.001, 10.0)/10.0, mod(position.y+0.001, 10.0)/10.0, mod(position.z+0.001, 10.0)/10.0, 1.0);
    
    gl_FragColor = texture2D(color_texture, texture_coordinate);
    
    float color = mix(gl_FragColor.r, gl_FragColor.g, animate1);
    color = color * mix(gl_FragColor.g, gl_FragColor.b, animate1);
    
    //color *= 0.8;
    
    gl_FragColor = vec4(gl_FragColor.r,1.1-color,1.2-color,1.0); //gl_FragColor.r,1.1-color,1.2-color
    
    //0.12, 0.12, 0.15
    //vec4(0.5, 0.1, 0.1, 1.0)
    gl_FragColor = 1.0/gl_FragColor * vec4(0.05, 0.05, 0.25, 1.0); //0.1, 0.1, 0.1 //0.05, 0.05, 0.8
    
    //gl_FragColor = gl_FragColor * vec4(0.3, 0.3, 0.3, 1.0);
    
    gl_FragColor = gl_FragColor * vec4(1.2, 1.2, 1.2, 1.0);
    
    //gl_FragColor *= animate;
    
    //gl_FragColor *= 0.01;
    
    //gl_FragColor *= sin(animate);
    
    //gl_FragColor = vec4(position.x,position.y, position.z, 1.0);
}