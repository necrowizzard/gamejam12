package GUI;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

public class MouseGUI {

	float[] current_ray = new float[6];
	
	public MouseGUI() {
		
	}
	
	public float[] getViewRay(float pix_x, float pix_y) {
		
		//float[] current_ray = new float[6];
		
		
		//.......test get selction ray
		FloatBuffer mv = BufferUtils.createFloatBuffer(16);
		FloatBuffer proj = BufferUtils.createFloatBuffer(16);
		IntBuffer vp = BufferUtils.createIntBuffer(16);
		FloatBuffer result = BufferUtils.createFloatBuffer(3);
		
		GL11.glGetFloat(GL11.GL_MODELVIEW_MATRIX, mv);
		mv.position(0);
		GL11.glGetFloat(GL11.GL_PROJECTION_MATRIX, proj);
		proj.position(0);
		GL11.glGetInteger(GL11.GL_VIEWPORT, vp);
		vp.position(0);
		
		//float pix_x = (float)Display.getDisplayMode().getWidth()/2.0f;
		//float pix_y = (float)vp.get(3) - (float)Display.getDisplayMode().getHeight()/2.0f + 4.0f;
		//pix_y = (float)vp.get(3) - pix_y + 4.0f;
		
		GLU.gluUnProject(pix_x, pix_y, 0.0f, mv, proj, vp, result);
		//System.out.println("Picked: " + result.get(0) + "/" + result.get(1) + "/" + result.get(2));
		
		current_ray[0] = result.get(0);
		current_ray[1] = result.get(1);
		current_ray[2] = result.get(2);
		
		GLU.gluUnProject(pix_x, pix_y, 1.0f, mv, proj, vp, result);
		//System.out.println("Picked: " + result.get(0) + "/" + result.get(1) + "/" + result.get(2));
		//.......get selection ray end
		
		current_ray[3] = result.get(0);
		current_ray[4] = result.get(1);
		current_ray[5] = result.get(2);
		
		return current_ray;
	}
	
	public void debug_draw() {
		
		GL11.glPushMatrix();
		
		GL11.glBegin(GL11.GL_LINE);
		
		GL11.glVertex3f(current_ray[0], current_ray[1], current_ray[2]);
		GL11.glVertex3f(current_ray[3], current_ray[4], current_ray[5]);
		
		GL11.glEnd();
		
		GL11.glPopMatrix();
		
	}
	
}
