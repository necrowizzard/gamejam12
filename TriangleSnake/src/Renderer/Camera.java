package Renderer;

import org.lwjgl.opengl.GL11;

public class Camera {

	public static final int size = 60;
	
	float[] pos;
	float pitch;
	float yaw;
	
	public Camera(float start_x, float start_y, float start_z) {
		
		pos = new float[]{start_x, start_y, start_z};
		pitch = 0.0f;
		yaw = 0.0f;
		
	}
	
	public float[] getPos() {
		return pos;
	}
	
	//increment the camera's current yaw rotation
	public void yaw(float amount) {
	    //increment the yaw by the amount param
	    yaw += amount;
	}
	 
	//increment the camera's current yaw rotation
	public void pitch(float amount) {
	    //increment the pitch by the amount param
	    pitch += amount;
	}
	
	//moves the camera forward relative to its current rotation (yaw)
	public void walkForward(float distance) {
		pos[0] -= distance * (float)Math.sin(Math.toRadians(yaw));
		pos[2] += distance * (float)Math.cos(Math.toRadians(yaw));
		
		check_outside_area();
	}
	 
	//moves the camera backward relative to its current rotation (yaw)
	public void walkBackwards(float distance) {
		pos[0] += distance * (float)Math.sin(Math.toRadians(yaw));
		pos[2] -= distance * (float)Math.cos(Math.toRadians(yaw));
		
		check_outside_area();
	}
	
	public void jump(float distance) {
		pos[1] -= distance;
		
		check_outside_area();
	}
	
	public void move_down(float distance) {
		pos[1] += distance;
		
		check_outside_area();
	}
	 
	//strafes the camera left relitive to its current rotation (yaw)
	public void strafeLeft(float distance) {
		pos[0] -= distance * (float)Math.sin(Math.toRadians(yaw-90));
		pos[2] += distance * (float)Math.cos(Math.toRadians(yaw-90));
		
		check_outside_area();
	}
	 
	//strafes the camera right relitive to its current rotation (yaw)
	public void strafeRight(float distance)
	{
		pos[0] -= distance * (float)Math.sin(Math.toRadians(yaw+90));
		pos[2] += distance * (float)Math.cos(Math.toRadians(yaw+90));
		
		check_outside_area();
	}
	
	public void apply_camera_transform() {
		
		//roatate the pitch around the X axis
        GL11.glRotatef(pitch, 1.0f, 0.0f, 0.0f);
        //roatate the yaw around the Y axis
        GL11.glRotatef(yaw, 0.0f, 1.0f, 0.0f);
        //translate to the position vector's location
        GL11.glTranslatef(pos[0], pos[1], pos[2]);
		
	}
	
	private void check_outside_area() {
		if (pos[0] > (float)size/2.0f) {
			pos[0] = (float)-size + pos[0];
			System.out.println("0+: " +pos[0]);
		}
		else if (pos[0] < -(float)size/2.0f) {
			pos[0] = (float)size - pos[0];
			System.out.println("0-: " +pos[0]);
		}
		
		if (pos[1] > (float)size/2.0f) {
			pos[1] = -size + pos[1];
			System.out.println("1: " +pos[1]);
		}
		else if (pos[1] < (float)-size/2.0f) {
			pos[1] = size - pos[1];
			System.out.println("1: " +pos[1]);
		}
		
		if (pos[2] > (float)size/2.0f) {
			pos[2] = -size + pos[2];
			System.out.println("2: " +pos[2]);
		}
		else if (pos[2] < (float)-size/2.0f) {
			pos[2] = size - pos[2];
			System.out.println("2: " +pos[2]);
		}
	}
	
}
