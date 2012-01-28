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
		
		scale = 5.0f; //3.0
	}
	
	public void setPos(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public void update() {
		//scale += 0.001f;
		
		//System.out.println("update");
	}
	
	public void draw() {
		
		GL11.glPushMatrix();
		
		GL11.glTranslatef(x, y, z);
		GL11.glScalef(scale, scale, scale);
		
		/*GL11.glBegin(GL11.GL_TRIANGLES);
			GL11.glVertex3f( 0.0f, 1.0f, -0.5f);
			GL11.glVertex3f(-1.0f,-1.0f, -0.5f);
			GL11.glVertex3f( 1.0f,-1.0f, -0.5f);
		GL11.glEnd();*/
		
		GL11.glBegin(GL11.GL_QUADS);
			GL11.glTexCoord2f(0.0f, 1.0f);
			GL11.glVertex3f(-1.0f, 1.0f, 0.0f);
			GL11.glTexCoord2f(0.0f, 0.0f);
			GL11.glVertex3f(-1.0f,-1.0f, 0.0f);
			GL11.glTexCoord2f(1.0f, 0.0f);
			GL11.glVertex3f( 1.0f,-1.0f, 0.0f);
			GL11.glTexCoord2f(1.0f, 1.0f);
			GL11.glVertex3f( 1.0f,1.0f, 0.0f);
		GL11.glEnd();
		
		GL11.glPopMatrix();
	}
	
	public void draw_offset(int ox, int oy, int oz) {
		
		GL11.glPushMatrix();
		
		GL11.glTranslatef(ox, oy, oz);
		
		GL11.glTranslatef(x, y, z);
		GL11.glScalef(scale, scale, scale);
		
		/*GL11.glBegin(GL11.GL_TRIANGLES);
			GL11.glVertex3f( 0.0f, 1.0f, -0.5f);
			GL11.glVertex3f(-1.0f,-1.0f, -0.5f);
			GL11.glVertex3f( 1.0f,-1.0f, -0.5f);
		GL11.glEnd();*/
		
		GL11.glBegin(GL11.GL_QUADS);
			GL11.glTexCoord2f(0.0f, 1.0f);
			GL11.glVertex3f(-1.0f, 1.0f, 0.0f);
			GL11.glTexCoord2f(0.0f, 0.0f);
			GL11.glVertex3f(-1.0f,-1.0f, 0.0f);
			GL11.glTexCoord2f(1.0f, 0.0f);
			GL11.glVertex3f( 1.0f,-1.0f, 0.0f);
			GL11.glTexCoord2f(1.0f, 1.0f);
			GL11.glVertex3f( 1.0f,1.0f, 0.0f);
		GL11.glEnd();
		
		GL11.glPopMatrix();
		
	}
	
}
