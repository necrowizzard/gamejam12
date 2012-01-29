package Renderer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;

public class GameObject {

	private float x, y, z;
	private Vector3f forward; // normal vector of the plane
	private Vector3f up;
	private Vector3f right;
	static final float scale = 15; //5
	private float current_scale;
	
	// axis aligned bounding box
	private Vector3f aabb1, aabb2;
	
	public GameObject(float x, float y, float z, Camera cam) {
		aabb1 = new Vector3f();
		aabb2 = new Vector3f();
		current_scale = 1;
		setPos(x, y, z, cam);
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
		//this.right.scale(scale);
		//this.up.scale(scale);
		//this.forward.scale(scale);
		//update_bounding_box();
	}

	public void update() {
		if (current_scale < scale) {
			current_scale *= 1.01f;
			this.right.scale(1.01f);
			this.up.scale(1.01f);
			this.forward.scale(1.01f);
		}
		
		//System.out.println("update");
	}
	
	private Vector3f scaledVector(float r, float u) {
		
		return new Vector3f(right.x * r	+ up.x * u,
				right.y * r + up.y * u,
				right.z * r + up.z * u);
	}
	
	// left up
	public Vector3f getVertex1() {
		return scaledVector(-1, 1);
	}
	
	// left down
	public Vector3f getVertex2() {
		return scaledVector(-1, -1);
	}
	
	// right up
	public Vector3f getVertex3() {
		return scaledVector(1, 1);
	}

	// right down
	public Vector3f getVertex4() {
		return scaledVector(1, -1);
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
		//GL11.glScalef(scale, scale, scale);
		
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
		//GL11.glScalef(scale, scale, scale);
		
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
	
	private boolean collide(Vector3f center, float radius, float r, float u) {
		float dx = center.x - (x + right.x * r + up.x * u);
		float dy = center.y - (y + right.y * r + up.y * u);
		float dz = center.z - (z + right.z * r + up.z * u);
		return dx * dx + dy * dy + dz * dz < radius * radius;
	}

	public boolean collide_sphere(Vector3f center) {
		if (current_scale < scale) return false;
		if (Math.abs(center.x - x) > scale * 2) return false;
		if (Math.abs(center.y - y) > scale * 2) return false;
		if (Math.abs(center.z - z) > scale * 2) return false;
		for (int j = -1; j <= 1; j++) {
			for (int i = -1; i <= 1; i++) {
				if (collide(center, GameObject.scale / 2, i * 0.66f, j * 0.66f)) return true;
			}
		}

		return false;
	}
	
}
