package Renderer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;

public class GameObject {

	private float x, y, z;
	private Vector3f forward; // normal vector of the plane
	private Vector3f up;
	private Vector3f right;
	private float scale;
	
	public GameObject(float x, float y, float z, Camera cam) {
		setPos(x, y, z, cam);
		
		scale = 5.0f; //3.0
	}
	
	public void setPos(float x, float y, float z, Camera cam) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.right = new Vector3f(cam.right);
		this.up = new Vector3f(cam.up);
		this.forward = new Vector3f(cam.forward);
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
			GL11.glVertex3f(-right.x + up.x,-right.y+up.y, -right.z+up.z);
			GL11.glTexCoord2f(0.0f, 0.0f);
			GL11.glVertex3f(-right.x - up.x,-right.y-up.y, -right.z-up.z);
			GL11.glTexCoord2f(1.0f, 0.0f);
			GL11.glVertex3f(right.x - up.x,right.y-up.y, right.z-up.z);
			GL11.glTexCoord2f(1.0f, 1.0f);
			GL11.glVertex3f(right.x + up.x,right.y+up.y, right.z+up.z);
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
			GL11.glVertex3f(-right.x + up.x,-right.y+up.y, -right.z+up.z);
			GL11.glTexCoord2f(0.0f, 0.0f);
			GL11.glVertex3f(-right.x - up.x,-right.y-up.y, -right.z-up.z);
			GL11.glTexCoord2f(1.0f, 0.0f);
			GL11.glVertex3f(right.x - up.x,right.y-up.y, right.z-up.z);
			GL11.glTexCoord2f(1.0f, 1.0f);
			GL11.glVertex3f(right.x + up.x,right.y+up.y, right.z+up.z);
		GL11.glEnd();
		
		GL11.glPopMatrix();
		
	}
	
}
