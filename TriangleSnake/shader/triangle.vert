varying vec2 texture_coordinate;
varying vec3 position;

void main()
{
	texture_coordinate = vec2(gl_MultiTexCoord0);
	position = (gl_Vertex).xyz;

    // Transforming The Vertex
    gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;
}