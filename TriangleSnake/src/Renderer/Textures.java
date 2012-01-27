package Renderer;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

public class Textures {

	private int current_index;
	private IntBuffer textures;
	
	public Textures() {
		
	}
	
	public int add_Texture(String name) {
		
		
		return 0;
	}
	
	public int add_Texture(float[] data, int sx, int sy) {
		
		FloatBuffer tex_data = BufferUtils.createFloatBuffer(3*2*sx*sy); //why 4*2?
		tex_data.put(data);
		tex_data.position(0); //GRRRRRRR!!!!1 //NEEDED!!!
		
		textures = BufferUtils.createIntBuffer(1);
		
		GL11.glGenTextures(textures);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, textures.get(0));
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, 
				GL11.GL_RGB, sx, sy, 
				0, GL11.GL_RGB, GL11.GL_FLOAT, tex_data);
		
		return 0;
	}
	
	public void bind_texture(int index) {
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, textures.get(index));
	}
	
}
