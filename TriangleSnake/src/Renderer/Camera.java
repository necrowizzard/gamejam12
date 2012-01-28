package Renderer;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Matrix3f;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

public class Camera {

	public static final int size = 60;
	
	int who;
	
	Vector3f pos;
	Vector3f forward;
	Vector3f right;
	Vector3f up;
	
	RenderWorld render;
	
	public Camera(int who, RenderWorld render, float start_x, float start_y, float start_z) {
		this.who = who;
		pos = new Vector3f(start_x, start_y, start_z);
		forward = new Vector3f(0.0f, 0.0f, -1.0f);
		right = new Vector3f(1.0f, 0.0f, 0.0f);
		up = new Vector3f(0.0f, 1.0f, 0.0f);
		this.render = render;
	}
	
	public Vector3f getPos() {
		return pos;
	}
	
	void rotate(Matrix4f m, Vector3f v) {
		Vector4f v4 = new Vector4f(v.x, v.y, v.z, 1);
		Matrix4f.transform(m, v4, v4);
		v.x = v4.x;
		v.y = v4.y;
		v.z = v4.z;
	}
	
	//increment the camera's current yaw rotation
	public void yaw(float amount) {	
	    //increment the yaw by the amount param
	    Matrix4f m = new Matrix4f();
	    m.rotate((float)(amount * Math.PI / 180), up);
	    rotate(m, forward);
	    rotate(m, right);
	}
	 
	//increment the camera's current yaw rotation
	public void pitch(float amount) {
	    //increment the pitch by the amount param
		Matrix4f m = new Matrix4f();
	    m.rotate((float)(amount * Math.PI / 180), right);
	    rotate(m, up);
	    rotate(m, forward);
	}
	
	//moves the camera forward relative to its current 3d orientation
	public void walkForward(float distance) {
		pos.x += forward.x * distance;
		pos.y += forward.y * distance;
		pos.z += forward.z * distance;
		
		check_inside_and_collision();
	}
	 
	//moves the camera backward relative to its current 3d orientation
	public void walkBackwards(float distance) {
		pos.x -= forward.x * distance;
		pos.y -= forward.y * distance;
		pos.z -= forward.z * distance;

		check_inside_and_collision();
	}
	
	public void jump(float distance) {
		pos.x += up.x * distance;
		pos.y += up.y * distance;
		pos.z += up.z * distance;
		
		check_inside_and_collision();
	}
	
	public void move_down(float distance) {
		pos.x -= up.x * distance;
		pos.y -= up.y * distance;
		pos.z -= up.z * distance;
		
		check_inside_and_collision();
	}
	 
	//strafes the camera left relitive to its current rotation (yaw)
	public void strafeLeft(float distance) {
		pos.x -= right.x * distance;
		pos.y -= right.y * distance;
		pos.z -= right.z * distance;
		
		check_inside_and_collision();
	}
	 
	//strafes the camera right relitive to its current rotation (yaw)
	public void strafeRight(float distance)
	{
		pos.x += right.x * distance;
		pos.y += right.y * distance;
		pos.z += right.z * distance;
		
		check_inside_and_collision();
	}
	
	public void apply_camera_transform() {
		ByteBuffer bytes = ByteBuffer.allocateDirect(16 * 4);
		bytes.order(ByteOrder.LITTLE_ENDIAN);
		FloatBuffer buffer = bytes.asFloatBuffer();
		
		GL11.glLoadIdentity();
		GL11.glTranslatef(0, 0, -4);
		
		buffer.put(right.x);
		buffer.put(up.x);
		buffer.put(-forward.x);
		buffer.put(0);
		buffer.put(right.y);
		buffer.put(up.y);
		buffer.put(-forward.y);
		buffer.put(0);
		buffer.put(right.z);
		buffer.put(up.z);
		buffer.put(-forward.z);
		buffer.put(0);	
		buffer.put(0);
		buffer.put(0);
		buffer.put(0);
		buffer.put(1);
		buffer.flip();
		
		GL11.glMultMatrix(buffer);
		GL11.glTranslatef(-pos.x, -pos.y, -pos.z);

	}
	
	void apply_player_transform() {
		ByteBuffer bytes = ByteBuffer.allocateDirect(16 * 4);
		bytes.order(ByteOrder.LITTLE_ENDIAN);
		FloatBuffer buffer = bytes.asFloatBuffer();
		GL11.glTranslatef(pos.x, pos.y, pos.z);
		buffer.put(right.x);
		buffer.put(right.y);
		buffer.put(right.z);
		buffer.put(0);
		buffer.put(up.x);
		buffer.put(up.y);
		buffer.put(up.z);
		buffer.put(0);
		buffer.put(-forward.x);
		buffer.put(-forward.y);
		buffer.put(-forward.z);
		buffer.put(0);	
		buffer.put(0);
		buffer.put(0);
		buffer.put(0);
		buffer.put(1);
		buffer.flip();
		
		GL11.glMultMatrix(buffer);
	}
	
	private void check_inside_and_collision() {
		if (pos.x > (float)size/2.0f) {
			pos.x = (float)-size + pos.x;
			System.out.println("0+: " +pos.x);
		}
		else if (pos.x < -(float)size/2.0f) {
			pos.x = (float)size - pos.x;
			System.out.println("0-: " +pos.x);
		}
		
		if (pos.y > (float)size/2.0f) {
			pos.y = -size + pos.y;
			System.out.println("1: " +pos.y);
		}
		else if (pos.y < (float)-size/2.0f) {
			pos.y = size - pos.y;
			System.out.println("1: " +pos.y);
		}
		
		if (pos.z > (float)size/2.0f) {
			pos.z = -size + pos.z;
			System.out.println("2: " +pos.z);
		}
		else if (pos.z < (float)-size/2.0f) {
			pos.z = size - pos.z;
			System.out.println("2: " +pos.z);
		}
		
		// Create a temporary game object for collision checking. We create a "cross like"
		// object out of three game objects to check against.
		//GameObject gob = new GameObject(pos.x, pos.y, pos.z, this);
		//gob.setCollisionScale(1, 1);
		boolean a = render.collide_sphere(pos);
		
		/*gob = new GameObject(pos.x, pos.y, pos.z, this);
		gob.setCollisionScale(0.5f, 1);
		gob.rotate_90_y();
		boolean b = render.collide(gob);
		
		gob = new GameObject(pos.x, pos.y, pos.z, this);
		gob.setCollisionScale(1, 0.5f);
		gob.rotate_90_x();
		boolean c = render.collide(gob);*/
		if (a) {
			System.out.println("collide");
			render.collide_event();
		}
	}
	
}
