//TODO: ADAPTED FROM PERSONAL MOD PROJECT


package Renderer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

public class Shader {

	private int vertex_shader = 0;
	private int fragment_shader = 0;
	private int program = 0;
	private FloatBuffer matrix = BufferUtils.createFloatBuffer(16);
	
	private boolean disable_error_msg = true;
	
	//is filled with the UNIFORM location connections
	private ArrayList<Integer> value_ids = new ArrayList<Integer>();
	
	Shader(String path) {
		
		String vert_path = path + ".vert";
		String frag_path = path + ".frag";
		
		//requires GL20!
		String line = "";
		String vertex_shader_source = "";
		
		URI uri = null;
		try {
			uri = new URI("file://"+Shader.class.getProtectionDomain().getCodeSource().getLocation().getPath());
		} catch (URISyntaxException e1) {
			throw new RuntimeException("URI fault");
		}
		
        File f = new File(uri);
		
		BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader(f.getParent() + vert_path));
			while( (line=reader.readLine()) != null){
				vertex_shader_source += line + "\n";
			}
		} catch (Exception e) {
			//f = new File(f.getParent() + vert_path);
			throw new RuntimeException("Couldnt load shader! Was looking for: " + f.getParent() + vert_path +
					" \nReason: " + e);
		}
		
		//CHECK version (REMOVED)

		vertex_shader = compile(GL20.GL_VERTEX_SHADER, vertex_shader_source);
		
		if (vertex_shader == 0) System.out.println("Error Vertex Shader!");
		
		
		String fragment_shader_source = "";
		
		try {
			reader = new BufferedReader(new FileReader(f.getParent() + frag_path));
			while( (line=reader.readLine()) != null){
				fragment_shader_source += line + "\n";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//System.out.println("Compiling " + vert_path);
		fragment_shader = compile(GL20.GL_FRAGMENT_SHADER, fragment_shader_source);
		
		if (fragment_shader == 0) System.out.println("Error Fragment Shader!");
		
		link(path);
		
	}
	
	private int compile(int type, String shader_source) {
		
		int shader = GL20.glCreateShader(type);
		if (shader == 0) {
			System.out.println("Impossible to create shader object!");
			return 0;
		}
		
		GL20.glShaderSource(shader, shader_source);
		GL20.glCompileShader(shader);
		
		return shader;
	}
	
	//0.8%
	private void link(String source) {
		
		program = GL20.glCreateProgram();
		
		GL20.glAttachShader(program, vertex_shader);
		GL20.glAttachShader(program, fragment_shader);
		
		GL20.glLinkProgram(program);
		
		IntBuffer result = BufferUtils.createIntBuffer(1);
		GL20.glGetProgram(program, GL20.GL_LINK_STATUS, result);
		
		//System.out.println(result.get(0));
		
		if(result.get(0) != GL11.GL_TRUE){
			throw new RuntimeException("Shader compile error! \n\n" + 
					"in: " + source + "\n\n" +
					GL20.glGetProgramInfoLog(program, 5000));
		}
		
		System.out.println(GL20.glGetProgramInfoLog(program, 5000));
		
	}
	
	//0.1%
	public void bind() {
		GL20.glUseProgram(program);
	}
	
	//0.1%
	public void unbind() {
			GL20.glUseProgram(0);
	}
	
	public int initValue1i(String name) {
		int location = 0;
			location = GL20.glGetUniformLocation(program, name);
		
		if (!disable_error_msg && location == -1) System.out.println("Error binding shader int.");
		
		value_ids.add(location);
		
		return value_ids.size()-1;
	}
	
	public void setValue1i(int position_in_location_array, int value) {
		
			GL20.glUniform1i(value_ids.get(position_in_location_array), value);
		
	}
	
	public int initValue1f(String name) {
		int location = 0;
			location = GL20.glGetUniformLocation(program, name);
		
		if (!disable_error_msg && location == -1) System.out.println("Error binding shader float.");
		
		value_ids.add(location);
		
		return value_ids.size()-1;
	}
	
	public void setValue1f(int position_in_location_array, float value) {
		
			GL20.glUniform1f(value_ids.get(position_in_location_array), value);
		
	}
	
	public int initValueVec3f(String name) {
		int location = 0;
			location = GL20.glGetUniformLocation(program, name);
		
		if (!disable_error_msg && location == -1) System.out.println("Error binding shader vec3.");
		
		value_ids.add(location);
		
		return value_ids.size()-1;
	}
	
	public void setValueVec3f(int position_in_location_array, float x, float y, float z) {
		
			GL20.glUniform3f(value_ids.get(position_in_location_array), x, y, z);
		
	}
	
	public int initValueMat4f(String name) {
		int location = 0;
		location = GL20.glGetUniformLocation(program, name);
		
		if (!disable_error_msg && location == -1) System.out.println("Error binding shader matrix.");
		
		value_ids.add(location);
		
		return value_ids.size()-1;
	}
	
	public void setValueMat4f(int position_in_location_array, float [] value) {
		
		matrix.clear();
		for(int i=0; i<16; i++) {
			matrix.put(value[i]);
		}
		matrix.flip();
		
			GL20.glUniformMatrix4(value_ids.get(position_in_location_array), true, matrix);
		
	}
	
}
