package Renderer;

import org.lwjgl.opengl.GL11;

public class GameObject {

	private float x, y, z;
	private float rx, ry, rz;
	private float scale;
	
	public GameObject(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
		
		scale = 1.0f;
	}
	
	public void setPos(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public void update() {
		//scale += 0.0001f;
		
		//System.out.println("update");
	}
	
	public void draw() {
		
		GL11.glScalef(scale, scale, scale);
		
		GL11.glBegin(GL11.GL_TRIANGLES);
			GL11.glVertex3f( 0.0f, 1.0f, -0.5f);
			GL11.glVertex3f(-1.0f,-1.0f, -0.5f);
			GL11.glVertex3f( 1.0f,-1.0f, -0.5f);
		GL11.glEnd();
		
	}
	
}
