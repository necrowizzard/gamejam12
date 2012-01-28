#version 120

//from: http://wiki.delphigl.com/index.php/shader_blur2

varying vec2 texture_coordinate;

uniform sampler2D color_texture;
uniform sampler2D glow_texture;

const int gaussRadius = 11;
const float gaussFilter[gaussRadius] = float[gaussRadius](
	0.0402,0.0623,0.0877,0.1120,0.1297,0.1362,0.1297,0.1120,0.0877,0.0623,0.0402
);

void main()
{
    
    //"filter"
    //vec2 uv = texture_coordinate;
    //float d = 0.002;
    //vec4 add = texture2D(glow_texture, uv)/9.0;
    //add += texture2D(glow_texture, uv+d)/9.0;
    //add += texture2D(glow_texture, uv-d)/9.0;
    //add += texture2D(glow_texture, uv-vec2(d,0.0))/9.0;
    //add += texture2D(glow_texture, uv-vec2(0.00,d))/9.0;
    //add += texture2D(glow_texture, uv+vec2(d,0.0))/9.0;
    //add += texture2D(glow_texture, uv+vec2(0.00,d))/9.0;
    //add += texture2D(glow_texture, uv+vec2(d,-d))/9.0;
    //add += texture2D(glow_texture, uv-vec2(d,-d))/9.0;
    
    vec2 uShift = vec2(0.0005, 0.00);
    
    vec2 texCoord = texture_coordinate.xy - float(int(gaussRadius/2)) * uShift;
	vec3 add = vec3(0.0, 0.0, 0.0); 
	for (int i=0; i<gaussRadius; ++i) { 
		add += gaussFilter[i] * texture2D(glow_texture, texCoord).xyz;
		texCoord += uShift;
	}
	
	uShift = vec2(0.0000, 0.0005);
	
	texCoord = texture_coordinate.xy - float(int(gaussRadius/2)) * uShift;
	for (int i=0; i<gaussRadius; ++i) { 
		add += gaussFilter[i] * texture2D(glow_texture, texCoord).xyz;
		texCoord += uShift;
	}
    
    vec4 color = texture2D(color_texture, texture_coordinate);
    
    color += 1.5*vec4(add-0.05,1.0);
    
    //if (color.r+color.g+color.b < 1.5) discard;
    
    
    gl_FragColor = color;
}