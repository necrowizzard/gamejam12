package GUI;

import java.util.ArrayList;

import org.lwjgl.opengl.GL11;

public class GUIWorldElements {

	//FOR IN GAME GUI ELEMENTS (INSIDE THE LEVEL)
	ArrayList<int[]> brick_selection_position;
	
	int[][] faces = { 	{0, 1, 2, 3}, {3, 2, 6, 7}, {7, 6, 5, 4},
			{4, 5, 1, 0}, {5, 6, 2, 1}, {7, 4, 0, 3} };

	float[][] v = new float[8][3];
	
	public GUIWorldElements() {
		//cube init
		v[0][0] = v[1][0] = v[2][0] = v[3][0] = 1.001f;
		v[4][0] = v[5][0] = v[6][0] = v[7][0] = -0.001f;
		v[0][1] = v[1][1] = v[4][1] = v[5][1] = 0.501f;
		v[2][1] = v[3][1] = v[6][1] = v[7][1] = -0.001f;
		v[0][2] = v[3][2] = v[4][2] = v[7][2] = -0.001f;
		v[1][2] = v[2][2] = v[5][2] = v[6][2] = 1.001f;
		
		brick_selection_position = new ArrayList<int[]>();
	}
	
	public void set_brick_selection(int x, int y, int z, int levelblock) {
		if (levelblock != -1) {
			//System.out.println("selection at: " + y);
			
			//check for already selected
			boolean already_selected = false;
			for (int i=0; i<brick_selection_position.size(); i++) {
				int[] p = brick_selection_position.get(i);
				if (p[0] == x && p[1] == y && p[2] == z && p[3] == levelblock) {
					already_selected = true;
					break;
				}
			}
			
			if (!already_selected)
				brick_selection_position.add(new int[]{x, y, z, levelblock});
		}
	}
	
	public int[] get_brick_selection() {
		
		int[] out = new int[brick_selection_position.size()*4];
		
		for(int i=0; i<brick_selection_position.size(); i++) {
			out[4*i] = brick_selection_position.get(i)[0];
			out[4*i+1] = brick_selection_position.get(i)[1];
			out[4*i+2] = brick_selection_position.get(i)[2];
			out[4*i+3] = brick_selection_position.get(i)[3];
		}
		
		return out;
	}
	
	public void clear_selection() {
		
	}
	
	public void draw() {
		
//		//brick selections
//		for (int i=0; i<brick_selection_position.size(); i++) {
//			
//			GL11.glPushMatrix();
//			
//			//set to level-block position
//			int[] p = Level.get_level_block_position(brick_selection_position.get(i)[3]);
//			GL11.glTranslatef((float)p[0], (float)p[1], (float)p[2]);
//			//set to brick position
//			GL11.glTranslatef(	(float)(brick_selection_position.get(i)[0]),
//								(float)(brick_selection_position.get(i)[1])*0.5f, 
//								(float)(brick_selection_position.get(i)[2]));
//			
//			//System.out.println("selection at render: " + brick_selection_position.get(i)[1] +"/"+ p[1]);
//			
//			GL11.glColor3f(0.0f, 1.0f, 0.0f);
//			
//			for (int j=0; j<6; j++) {
//				GL11.glBegin(GL11.GL_QUADS);
//				GL11.glVertex3f(v[faces[j][0]][0], v[faces[j][0]][1], v[faces[j][0]][2]);
//				GL11.glVertex3f(v[faces[j][1]][0], v[faces[j][1]][1], v[faces[j][1]][2]);
//				GL11.glVertex3f(v[faces[j][2]][0], v[faces[j][2]][1], v[faces[j][2]][2]);
//				GL11.glVertex3f(v[faces[j][3]][0], v[faces[j][3]][1], v[faces[j][3]][2]);
//				GL11.glEnd();
//			}
//			
//			GL11.glPopMatrix();
//			
//		}
		
	}
	
}
