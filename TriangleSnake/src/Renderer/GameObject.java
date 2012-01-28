package Renderer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;

public class GameObject {

	private float x, y, z;
	private Vector3f forward; // normal vector of the plane
	private Vector3f up;
	private Vector3f right;
	private float scale;
	
	// axis aligned bounding box
	private Vector3f aabb1, aabb2;
	
	public GameObject(float x, float y, float z, Camera cam) {
		aabb1 = new Vector3f();
		aabb2 = new Vector3f();
		
		setPos(x, y, z, cam);
		
		scale = 5.0f; //3.0
	}
	
	public void rotate_90_y() {
		Vector3f temp = forward;
		forward = right;
		right = temp;
	}
	
	public void rotate_90_x() {
		Vector3f temp = forward;
		forward = up;
		up = temp;
	}
	
	public void setPos(float x, float y, float z, Camera cam) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.right = new Vector3f(cam.right);
		this.up = new Vector3f(cam.up);
		this.forward = new Vector3f(cam.forward);
		update_bounding_box();
	}

	public void update_bounding_box() {
		aabb1.x = Math.min(-right.x - up.x, right.x - up.x);
		aabb1.x = Math.min(aabb1.x, -right.x + up.x);
		aabb1.x = Math.min(aabb1.x, right.x + up.x);
		
		aabb1.y = Math.min(-right.y - up.y, right.y - up.y);
		aabb1.y = Math.min(aabb1.y, -right.y + up.y);
		aabb1.y = Math.min(aabb1.y, right.y + up.y);
		
		aabb1.z = Math.min(-right.z - up.z, right.z - up.z);
		aabb1.z = Math.min(aabb1.z, -right.z + up.z);
		aabb1.z = Math.min(aabb1.z, right.z + up.z);
		
		aabb2.x = Math.max(-right.x - up.x, right.x - up.x);
		aabb2.x = Math.max(aabb1.x, -right.x + up.x);
		aabb2.x = Math.max(aabb1.x, right.x + up.x);
		
		aabb2.y = Math.max(-right.y - up.y, right.y - up.y);
		aabb2.y = Math.max(aabb1.y, -right.y + up.y);
		aabb2.y = Math.max(aabb1.y, right.y + up.y);
		
		aabb2.z = Math.max(-right.z - up.z, right.z - up.z);
		aabb2.z = Math.max(aabb1.z, -right.z + up.z);
		aabb2.z = Math.max(aabb1.z, right.z + up.z);
	}
	
	public void update() {
		//scale += 0.001f;
		
		//System.out.println("update");
	}
	
	// left up
	public Vector3f getVertex1() {
		return new Vector3f(-right.x + up.x,-right.y+up.y, -right.z+up.z);
	}
	
	// left down
	public Vector3f getVertex2() {
		return new Vector3f(-right.x - up.x,-right.y-up.y, -right.z-up.z);
	}
	
	// right up
	public Vector3f getVertex3() {
		return new Vector3f(+right.x + up.x,+right.y+up.y, +right.z+up.z);
	}

	// right down
	public Vector3f getVertex4() {
		return new Vector3f(+right.x - up.x,+right.y-up.y, +right.z-up.z);
	}
	
	public boolean collide(GameObject other) {
		if (Triangles.triangle_intersects_triangle(getVertex1(), getVertex2(), getVertex3(),
				other.getVertex1(), other.getVertex2(), other.getVertex3())) return true;
		if (Triangles.triangle_intersects_triangle(getVertex3(), getVertex2(), getVertex4(),
				other.getVertex1(), other.getVertex2(), other.getVertex3())) return true;
		if (Triangles.triangle_intersects_triangle(getVertex1(), getVertex2(), getVertex3(),
				other.getVertex3(), other.getVertex2(), other.getVertex4())) return true;
		if (Triangles.triangle_intersects_triangle(getVertex3(), getVertex2(), getVertex4(),
				other.getVertex3(), other.getVertex2(), other.getVertex4())) return true;
		return false;
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
