package Renderer;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

public class RenderWorld {

	private Textures textures;
	private Triangles triangles;
	
	public RenderWorld() {
		
		triangles = new Triangles();
		
		textures = new Textures();
		//debug!
		//textures.add_Texture(null, 32, 32);
		
		setup_camera(); //lwjgl example
	}
	
	private void setup_camera() {
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		GLU.gluPerspective(75.0f, 4.0f/3.0f, 1.0f, 100.0f);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glLoadIdentity();
		GL11.glViewport(0, 0, Display.getDisplayMode().getWidth(), Display.getDisplayMode().getHeight());
	}
	
	public void drawBox() {
		
		int[][] faces = { 	{0, 1, 2, 3}, {3, 2, 6, 7}, {7, 6, 5, 4},
							{4, 5, 1, 0}, {5, 6, 2, 1}, {7, 4, 0, 3} };
		
		float[][] v = new float[8][3];
		
		v[0][0] = v[1][0] = v[2][0] = v[3][0] = -1;
		v[4][0] = v[5][0] = v[6][0] = v[7][0] = 1;
		v[0][1] = v[1][1] = v[4][1] = v[5][1] = -1;
		v[2][1] = v[3][1] = v[6][1] = v[7][1] = 1;
		v[0][2] = v[3][2] = v[4][2] = v[7][2] = 1;
		v[1][2] = v[2][2] = v[5][2] = v[6][2] = -1;
	
		GL11.glColor3f(0.0f, 1.0f, 0.0f);
		
		for (int i = 0; i < 6; i++) {
		    GL11.glBegin(GL11.GL_QUADS);
		    GL11.glVertex3f(v[faces[i][0]][0], v[faces[i][0]][1], v[faces[i][0]][2]);
		    GL11.glVertex3f(v[faces[i][1]][0], v[faces[i][1]][1], v[faces[i][1]][2]);
		    GL11.glVertex3f(v[faces[i][2]][0], v[faces[i][2]][1], v[faces[i][2]][2]);
		    GL11.glVertex3f(v[faces[i][3]][0], v[faces[i][3]][1], v[faces[i][3]][2]);
		    GL11.glEnd();
		}
		
	}
	
	public void update(float dt, Camera camera) {
		triangles.update(dt, camera);
	}
	
	public void draw() {
		
		int err = GL11.glGetError();
		
		if (err != GL11.GL_NO_ERROR) System.out.println("Error: " + err);
		
		//basic example
		
		//System.out.println("krap");
		
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_CULL_FACE);
		
		//------- LIGHTING -------
		
		/*GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glEnable(GL11.GL_LIGHT0);
		GL11.glEnable(GL11.GL_COLOR_MATERIAL); //light and color
		GL11.glColorMaterial(GL11.GL_FRONT, GL11.GL_AMBIENT_AND_DIFFUSE);
		//GL11.glLightModeli(GL11.GL_LIGHT_MODEL_TWO_SIDE, GL11.GL_TRUE);
		
		//float light_position[] = {1.0f, 1.0f, 1.0f, 0.0f};
		FloatBuffer light_position = BufferUtils.createFloatBuffer(4);
		light_position.put(new float[]{-0.5f, 2.0f, 0.2f, 0.0f});
		light_position.position(0);
		GL11.glLight(GL11.GL_LIGHT0, GL11.GL_POSITION, light_position);*/
		
		//-------LIGHTING END-------
		
		GL11.glClearColor(0.3f, 0.3f, 0.3f, 1.0f);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		
		GL11.glPushMatrix();
		
		/*
		//GL11.glTranslatef(Display.getDisplayMode().getWidth()/2, Display.getDisplayMode().getHeight()/2, 0.0f);
		GL11.glTranslatef(0.0f, 0.0f , -5.0f);
		GL11.glRotatef(0, 0, 0, 1.0f);
		
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		textures.bind_texture(0);
		
		GL11.glBegin(GL11.GL_QUADS);
		
		GL11.glColor3f(1.0f, 1.0f, 1.0f);
		//GL11.glNormal3f(0.0f, 1.0f, 0.0f);
		
		GL11.glTexCoord2f(1.0f, 1.0f);
		GL11.glVertex2f(-2.0f, -2.0f);
		
		//GL11.glColor3f(1.0f, 0.0f, 1.0f);
		GL11.glTexCoord2f(1.0f, 0.0f);
		GL11.glVertex2f(2.0f, -2.0f);
		
		GL11.glTexCoord2f(0.0f, 0.0f);
		GL11.glVertex2f(2.0f, 2.0f);
		
		GL11.glTexCoord2f(0.0f, 1.0f);
		GL11.glVertex2f(-2.0f, 2.0f);
		
		GL11.glEnd();
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		
		
		//test cube
		drawBox();*/
		
		
		GL11.glPushMatrix();
		GL11.glColor3f(1.0f, 0.0f, 0.0f);
		
		triangles.draw();
		
		//GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
		//GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
		
		/*
		GL11.glColor3f(1.0f, 0.0f, 0.0f);
		levelblocks[0].draw();
		GL11.glColor3f(1.0f, 1.0f, 0.0f);
		levelblocks[1].draw();
		GL11.glColor3f(0.0f, 1.0f, 0.0f);
		levelblocks[Level.SX].draw();
		GL11.glColor3f(0.0f, 1.0f, 1.0f);
		levelblocks[Level.SX+1].draw();
		*/
		
		GL11.glPopMatrix();
		
		
		GL11.glPopMatrix();
		
	}
	
}
