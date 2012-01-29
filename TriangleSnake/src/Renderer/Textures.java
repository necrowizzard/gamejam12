package Renderer;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL14;

import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;

public class Textures {

	//private int current_index;
	private IntBuffer textures;
	private ArrayList<Texture> texture_list;
	
	public Textures() {
		
		textures = BufferUtils.createIntBuffer(3);
		GL11.glGenTextures(textures);
		
		texture_list = new ArrayList<Texture>();
		try {
			texture_list.add(TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("img/start.png")));
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	public int add_Texture(String name) {
		
		
		return 0;
	}
	
	public int add_Texture(float[] data, int sx, int sy, int index) {
		
		FloatBuffer tex_data = BufferUtils.createFloatBuffer(3*2*sx*sy); //why 4*2?
		tex_data.put(data);
		tex_data.position(0); //GRRRRRRR!!!!1 //NEEDED!!!

		
		
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, textures.get(index));
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL14.GL_GENERATE_MIPMAP, GL11.GL_TRUE);
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
		if (index != 2) {
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, textures.get(index));
		} else {
			texture_list.get(0).bind();
		}
		
	}
	
}
