package Renderer;

import org.lwjgl.opengl.GL11;

public class Camera {

	float[] pos;
	float pitch;
	float yaw;
	
	public Camera(float start_x, float start_y, float start_z) {
		
		pos = new float[]{start_x, start_y, start_z};
		pitch = 0.0f;
		yaw = 0.0f;
		
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
	}
	 
	//moves the camera backward relative to its current rotation (yaw)
	public void walkBackwards(float distance) {
		pos[0] += distance * (float)Math.sin(Math.toRadians(yaw));
		pos[2] -= distance * (float)Math.cos(Math.toRadians(yaw));
	}
	
	public void jump(float distance) {
		pos[1] -= distance;
	}
	
	public void move_down(float distance) {
		pos[1] += distance;
	}
	 
	//strafes the camera left relitive to its current rotation (yaw)
	public void strafeLeft(float distance) {
		pos[0] -= distance * (float)Math.sin(Math.toRadians(yaw-90));
		pos[2] += distance * (float)Math.cos(Math.toRadians(yaw-90));
	}
	 
	//strafes the camera right relitive to its current rotation (yaw)
	public void strafeRight(float distance)
	{
		pos[0] -= distance * (float)Math.sin(Math.toRadians(yaw+90));
		pos[2] += distance * (float)Math.cos(Math.toRadians(yaw+90));
	}
	
	public void apply_camera_transform() {
		
		//roatate the pitch around the X axis
        GL11.glRotatef(pitch, 1.0f, 0.0f, 0.0f);
        //roatate the yaw around the Y axis
        GL11.glRotatef(yaw, 0.0f, 1.0f, 0.0f);
        //translate to the position vector's location
        GL11.glTranslatef(pos[0], pos[1], pos[2]);
		
	}
	
}
