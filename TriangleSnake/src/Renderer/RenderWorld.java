package Renderer;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;
import org.lwjgl.util.glu.Sphere;
import org.lwjgl.util.vector.Vector3f;

public class RenderWorld {

	public static final int SIZEX = 1440;
	public static final int SIZEY = 720;
	
	private Shader basic;
	private float basic_animate = 0.0f;
	private int basic_ind_animate, basic_ind_texture;
	private float basic_animate_d = 10.0f;
	
	private Textures textures;
	private Triangles triangles1, triangles2;
	
	private boolean firstloop = true;
	
	private Sphere sun;
	public boolean did_collide;
	
	private float screen_aspect = 0.5f;
	private int who_collided;
	
	public RenderWorld(float aspect, Textures textures) {
		
		this.textures = textures;
		screen_aspect = aspect;
		
		triangles1 = new Triangles();
		triangles2 = new Triangles();
		
		//textures = new Textures();
		//debug!
		//textures.add_Texture(null, 32, 32);
		
		basic = new Shader("/shader/basic");
		basic_ind_animate = basic.initValue1f("animate");
		basic_ind_texture = basic.initValue1i("color_texture");
		
		sun = new Sphere();
		//sun.draw(50);
		
		basic_animate = 0.0f;
		
		createSkyboxTexture();
	}
	
	private void setup_camera(int view) {
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		if (view == 0)
			GLU.gluPerspective(75.0f, 2.0f*screen_aspect, 1.0f, 500.0f); //TODO: set aspect ratio here
		if (view == 1)
			GLU.gluPerspective(75.0f, 1.0f/(2.0f*screen_aspect), 1.0f, 500.0f); //TODO: set aspect ratio here
		
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glLoadIdentity();
		if (view == 0)
			GL11.glViewport(0, 0, Display.getDisplayMode().getWidth()/2, Display.getDisplayMode().getHeight());
		if (view == 1)
			GL11.glViewport(Display.getDisplayMode().getWidth()/2, 0, Display.getDisplayMode().getWidth()/2, Display.getDisplayMode().getHeight());
		
	}
	
	private void createSkyboxTexture() {
		float[] texture = new float[3*640*640];
		
		for (int j=0; j<640; j++) {
			for (int i=0; i<640; i++) {
				
				texture[3*(640*j+i)] = (float)Noise.noise(10.0f*(float)i/640.0f,10.0f*(float)j/640.0f,10.0f*(float)i/640.0f); //0.9
				texture[3*(640*j+i)+1] = (float)Noise.noise(10.0f*(float)i/640.0f,10.0f*(float)j/640.0f,10.0f*(float)i/640.0f);
				texture[3*(640*j+i)+2] = (float)Noise.noise(10.0f*(float)i/640.0f,10.0f*(float)j/640.0f,10.0f*(float)i/640.0f); //1.1
				
				//texture[3*(640*j+i)] = (float)Noise.noise(10.0f*(float)i/640.0f,10.0f*(float)j/640.0f,0.85f); //0.9
				//texture[3*(640*j+i)+1] = (float)Noise.noise(10.0f*(float)i/640.0f,10.0f*(float)j/640.0f,1.0f);
				//texture[3*(640*j+i)+2] = (float)Noise.noise(10.0f*(float)i/640.0f,10.0f*(float)j/640.0f,1.2f); //1.1
				//cast -1/1 to 0/1
				texture[3*(640*j+i)] = (texture[3*(640*j+i)]+1.0f)/2.0f;
				texture[3*(640*j+i)+1] = (texture[3*(640*j+i)+1]+1.0f)/2.0f;
				texture[3*(640*j+i)+2] = (texture[3*(640*j+i)+2]+1.0f)/2.0f;
				
				//old parameters
				
				texture[3*(640*j+i)] *= 10.0f;
				texture[3*(640*j+i)+1] *= 10.0f;
				texture[3*(640*j+i)+2] *= 10.0f;
				
				//texture[3*(640*j+i)] = texture[3*(640*j+i)]-(int)texture[3*(640*j+i)];
				//texture[3*(640*j+i)+1] = texture[3*(640*j+i)+1]-(int)texture[3*(640*j+i)+1];
				texture[3*(640*j+i)+2] = texture[3*(640*j+i)+2]-(int)texture[3*(640*j+i)+2];
				
				//texture[3*(640*j+i)] = (float)Math.cos((float)(10.0f*(float)i/640.0f) + texture[3*(640*j+i)]);
				texture[3*(640*j+i)+1] = (float)Math.cos((float)(10.0f*(float)i/640.0f) + texture[3*(640*j+i)]+1);
				//texture[3*(640*j+i)+2] = (float)Math.cos((float)(10.0f*(float)j/640.0f) + texture[3*(640*j+i)]+2);
				
			}
		}
		
		textures.add_Texture(texture, 640, 640, 0);
		
		//2nd
		for (int j=0; j<640; j++) {
			for (int i=0; i<640; i++) {
				texture[3*(640*j+i)] = (float)Noise.noise(10.0f*(float)i/640.0f,10.0f*(float)j/640.0f,0.9f);
				texture[3*(640*j+i)+1] = (float)Noise.noise(10.0f*(float)i/640.0f,10.0f*(float)j/640.0f,1.0f);
				texture[3*(640*j+i)+2] = (float)Noise.noise(10.0f*(float)i/640.0f,10.0f*(float)j/640.0f,1.1f);
				//cast -1/1 to 0/1
				texture[3*(640*j+i)] = (texture[3*(640*j+i)]+1.0f)/2.0f;
				texture[3*(640*j+i)+1] = (texture[3*(640*j+i)+1]+1.0f)/2.0f;
				texture[3*(640*j+i)+2] = (texture[3*(640*j+i)+2]+1.0f)/2.0f;
				
				texture[3*(640*j+i)] *= 2.0f;
				texture[3*(640*j+i)+1] *= 2.0f;
				texture[3*(640*j+i)+2] *= 2.0f;
				
				texture[3*(640*j+i)] = texture[3*(640*j+i)]-(int)texture[3*(640*j+i)];
				texture[3*(640*j+i)+1] = texture[3*(640*j+i)+1]-(int)texture[3*(640*j+i)+1];
				texture[3*(640*j+i)+2] = texture[3*(640*j+i)+2]-(int)texture[3*(640*j+i)+2];
			}
		}
		
		textures.add_Texture(texture, 640, 640, 1);
	}
	
	public static void drawBox() {
		
		int[][] faces = { 	{0, 1, 2, 3}, {3, 2, 6, 7}, {7, 6, 5, 4},
							{4, 5, 1, 0}, {5, 6, 2, 1}, {7, 4, 0, 3} };
		
		float[][] v = new float[8][3];
		
		float s = 1;
		
		v[0][0] = v[1][0] = v[2][0] = v[3][0] = -s;
		v[4][0] = v[5][0] = v[6][0] = v[7][0] = s;
		v[0][1] = v[1][1] = v[4][1] = v[5][1] = -s;
		v[2][1] = v[3][1] = v[6][1] = v[7][1] = s;
		v[0][2] = v[3][2] = v[4][2] = v[7][2] = s;
		v[1][2] = v[2][2] = v[5][2] = v[6][2] = -s;
	
		//GL11.glColor3f(0.0f, 1.0f, 0.0f);
		
		for (int i = 0; i < 6; i++) {
		    GL11.glBegin(GL11.GL_QUADS);
		    GL11.glTexCoord2f(0.0f, 1.0f);
		    GL11.glVertex3f(v[faces[i][0]][0], v[faces[i][0]][1], v[faces[i][0]][2]);
		    GL11.glTexCoord2f(0.0f, 0.0f);
		    GL11.glVertex3f(v[faces[i][1]][0], v[faces[i][1]][1], v[faces[i][1]][2]);
		    GL11.glTexCoord2f(1.0f, 0.0f);
		    GL11.glVertex3f(v[faces[i][2]][0], v[faces[i][2]][1], v[faces[i][2]][2]);
		    GL11.glTexCoord2f(1.0f, 1.0f);
		    GL11.glVertex3f(v[faces[i][3]][0], v[faces[i][3]][1], v[faces[i][3]][2]);
		    GL11.glEnd();
		}
		
	}
	
	public void updateimage(float dt) {
		
		//screen_aspect -= 0.01f;
		//if (screen_aspect < 0.1f) screen_aspect = 0.9f;
		
		//System.out.println("dt" + dt);
		
		//System.out.println(basic_animate);
		
		basic_animate += dt/10.0f * basic_animate_d;
		if (basic_animate > 1.0f || basic_animate < 0.0f) {
			basic_animate_d *= - 1.0f;
		}
		
		//System.out.println(basic_animate);
		
	}
	
	public void update(float dt, Camera camera, int view) {
		
		if (view == 0)
			triangles1.update(dt, camera);
		
		if (view == 1)
			triangles2.update(dt, camera);
	}
	
	public void draw(int view, Camera camera, Camera enemy_cam) {
		
		if (view == 0)
			GL11.glViewport(0, 0, (int)(Display.getDisplayMode().getWidth()*screen_aspect) -5, Display.getDisplayMode().getHeight());
		if (view == 1)
			GL11.glViewport((int)(Display.getDisplayMode().getWidth()*screen_aspect) + 5, 0, (int)(Display.getDisplayMode().getWidth()*(1.0-screen_aspect)) -5, Display.getDisplayMode().getHeight());
		
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glPushMatrix();
		
		if (firstloop) {
			setup_camera(view); //lwjgl example
			
			if (view == 1) firstloop = false;
		} else {
			GL11.glMatrixMode(GL11.GL_PROJECTION);
			GL11.glLoadIdentity();
			if (view == 0)
				GLU.gluPerspective(75.0f, 2.0f*screen_aspect, 1.0f, 500.0f); //TODO: set aspect ratio here
			if (view == 1)
				GLU.gluPerspective(75.0f, 1.0f/(2.0f*screen_aspect), 1.0f, 500.0f); //TODO: set aspect ratio here
			GL11.glMatrixMode(GL11.GL_MODELVIEW);
			
		}
		
		int err = GL11.glGetError();
		
		if (err != GL11.GL_NO_ERROR) System.out.println("Error: " + err);
		
		//basic example
		
		//System.out.println("krap");
		
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		//GL11.glEnable(GL11.GL_CULL_FACE);
		
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
		
		GL11.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
		if (view == 0)
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
		GL11.glDisable(GL11.GL_TEXTURE_2D);*/
		
		
		//test cube
		//drawBox();
		
		
		GL11.glPushMatrix();
		
		//SKYBOX
		if (view == 0)
			GL11.glColor3f(0.8f, 0.8f, 0.8f);
		if (view == 1)
			GL11.glColor3f(0.9f, 0.9f, 0.9f);
		
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		textures.bind_texture(0);
		
		basic.bind();
		basic.setValue1f(basic_ind_animate, basic_animate);
		basic.setValue1i(basic_ind_texture, 0);
		
		Vector3f pos = camera.pos;
		GL11.glTranslatef(pos.x, pos.y, pos.z);
		float s = Camera.size / 2 + 2;
		GL11.glPushMatrix();
		GL11.glScalef(s, s, s);
		drawBox();
		GL11.glPopMatrix();
		GL11.glTranslatef(-pos.x, -pos.y, -pos.z);
		basic.unbind();
		//SKYBOX END
		
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
		//drawBox();
		GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		
		//if (view == 0)
		//	GL11.glColor3f(1.0f, 0.0f, 0.0f);
		//if (view == 1)
		//	GL11.glColor3f(0.8f, 0.0f, 0.0f);
		
		//GL11.glEnable(GL11.GL_BLEND);
		//GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ZERO);
		
		textures.bind_texture(1);
		
		
		if (camera.who == 0) triangles1.draw(camera, true, 0.0f);
		else triangles1.draw(enemy_cam, false, 0.0f);
		
		//if (view == 0)
		//	GL11.glColor3f(0.0f, 1.0f, 0.0f);
		//if (view == 1)
		//	GL11.glColor3f(0.0f, 0.8f, 0.0f);
		
		//GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_SRC_COLOR);
		//triangle.setValue1f(triangle_ind_color, 1.0f); //DIFFERENT COLOR
		
		if (camera.who == 0) triangles2.draw(enemy_cam, false, 1.0f);
		else triangles2.draw(camera, true, 1.0f);
		
		//GL11.glDisable(GL11.GL_BLEND);
		
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
		
		GL11.glPopMatrix();
		
		//GL11.glCl
		
		
	}

	/*public boolean collide(GameObject gob) {
		return triangles1.collide(gob);// | triangles2.collide(gob);
	}*/
	
	public boolean collide_sphere(Vector3f center) {
		return triangles1.collide_sphere(center) ||
				triangles2.collide_sphere(center);
	}

	public void collide_event(int who) {
		did_collide = true;
		
		who_collided = who;
		
		System.out.println(who);
		
		/*if (who == 0) {
			screen_aspect -= 0.1f;
		} else {
			screen_aspect += 0.1f;
		}
		
		if (screen_aspect < 0.2f || screen_aspect > 0.8f) System.out.println("over");*/
	}
	
	public int who_collided() {
		return who_collided;
	}
	
}
