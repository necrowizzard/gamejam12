import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

import GUI.MouseGUI;
import Renderer.Camera;
import Renderer.FBO;
import Renderer.RenderWorld;
import Renderer.Shader;


public class Game {
	
	boolean running;
	
	MouseGUI mouseGUI;
	
	private RenderWorld renderer;
	
	private Camera camera1, camera2;
	float dx        = 0.0f;
    float dy        = 0.0f;
    float dt        = 0.0f; //length of frame
    double lastTime  = 0.0f; // when the last frame was
    double time      = 0.0f;
	float mouseSensitivity = 0.08f;
    float movementSpeed = 10.0f; //move 10 units per second
    float rotationSpeed = 200.0f;
    
    boolean was_pressed = false;
    
    boolean started = false;
	
    //VERY UGLY: not time!!!
	private Shader post;
	private Shader post2;
	private int post2_ind_texture, post2_ind_texture2;
	private FBO framebuffer;
    
	public Game() {
		
		running = true;
		
		init_window();
		
		mouseGUI = new MouseGUI();
		
		renderer = new RenderWorld();
		camera1 = new Camera(0, 0, -5);
		
		camera2 = new Camera(0, 0, 5);
		
		framebuffer = new FBO(RenderWorld.SIZEX, RenderWorld.SIZEY);
		post = new Renderer.Shader("/shader/post");
		post2 = new Renderer.Shader("/shader/post2");
		post2_ind_texture = post2.initValue1i("color_texture");
		post2_ind_texture2 = post2.initValue1i("glow_texture");
		
		//System.out.println("Blaaaaaa: " + level.debug_get_texture()[1]);
	}
	
	private void init_window() {
		Display.setTitle("Perlin Noise");
		try {
			Display.setFullscreen(false);
			Display.setDisplayMode(new DisplayMode(RenderWorld.SIZEX,RenderWorld.SIZEY));
			Display.create();
		} catch (LWJGLException e) {
			e.printStackTrace();
		}
	}
	
	public void run() {
		
		Mouse.setGrabbed(false);
		
		//game loop
		while(running && !Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
			
			time = Sys.getTime();
	        dt = (float)(time - lastTime)/1000.0f;
	        lastTime = time;
	        
	        //INPUT
	        if (Mouse.isButtonDown(1)) {
		        //distance in mouse movement from the last getDX() call.
		        dx = Mouse.getDX();
		        //distance in mouse movement from the last getDY() call.
		        dy = Mouse.getDY();
		 
		        //System.out.println(dx +"/"+ dy);
		        
		        //controll camera yaw from x movement fromt the mouse
		        camera1.yaw(dx * mouseSensitivity);
		        //controll camera pitch from y movement fromt the mouse
		        camera1.pitch(- dy * mouseSensitivity); 
	        }
			
	        //System.out.println(dt);
	        
	        /*
	         * idea:
	         * 
	         * wasd
	         * 
	         * up,left,down,right
	         * 
	         * and combinations for moving in all directions of the screen...
	         */
	        
			if (Keyboard.isKeyDown(Keyboard.KEY_W)) {
				
				if (!started) started = true;
				//System.out.println(dt);
				camera1.walkForward(movementSpeed*dt);
	        }
	        if (Keyboard.isKeyDown(Keyboard.KEY_S)) {
	        	camera1.walkBackwards(movementSpeed*dt);
	        }
	        if (Keyboard.isKeyDown(Keyboard.KEY_A)) {
	        	camera1.strafeLeft(movementSpeed*dt);
	        }
	        if (Keyboard.isKeyDown(Keyboard.KEY_D)) {
	        	camera1.strafeRight(movementSpeed*dt);
	        }
	        
	        if (Keyboard.isKeyDown(Keyboard.KEY_UP)) {
	        	
	        	if (!started) started = true;
				//System.out.println(dt);
				//camera2.walkForward(movementSpeed*dt);
				
				camera2.pitch(-rotationSpeed*dt);
	        }
	        if (Keyboard.isKeyDown(Keyboard.KEY_DOWN)) {
	        	//camera2.walkBackwards(movementSpeed*dt);
	        	
	        	camera2.pitch(rotationSpeed*dt);
	        }
	        if (Keyboard.isKeyDown(Keyboard.KEY_LEFT)) {
	        	//camera2.strafeLeft(movementSpeed*dt);
	        	camera2.yaw(-rotationSpeed*dt);
	        }
	        if (Keyboard.isKeyDown(Keyboard.KEY_RIGHT)) {
	        	//camera2.strafeRight(movementSpeed*dt);
	        	camera2.yaw(rotationSpeed*dt);
	        }
	        
	        if (Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {
	        	camera1.jump(movementSpeed*dt);
	        }
	        if (Keyboard.isKeyDown(Keyboard.KEY_X)) {
	        	camera1.move_down(movementSpeed*dt);
	        }
	        if (Keyboard.isKeyDown(Keyboard.KEY_BACK)) {
	            //delete
	        	
	        }
	        
	        //LMB select
	        if (Mouse.isButtonDown(0)) {
	        	if (!was_pressed) {
	        		float[] ray = mouseGUI.getViewRay((float)Mouse.getX(), (float)Mouse.getY());
	        		//System.out.println(ray[0] +"/"+ ray[2]);
	        		was_pressed = true;
	        	}
	        }
	        if (was_pressed && !Mouse.isButtonDown(0)) was_pressed = false;
	        //INPUT END
	        
			Display.update();
			
			if (Display.isCloseRequested()) {
				running = false;
			} else {
				
				if (started) {
					camera1.walkForward(movementSpeed*dt);
			        camera2.walkForward(movementSpeed*dt);
				}
				
				renderer.update(dt, camera1, 0);
				renderer.update(dt, camera2, 1);
				
				GL11.glLoadIdentity();
				
				framebuffer.bind(0);
				
				camera1.apply_camera_transform();
				
				renderer.draw(0, camera1);
				
				GL11.glLoadIdentity();
				
				camera2.apply_camera_transform();
				
				renderer.draw(1, camera2);
				
				final_pass();
			}
			
		}
		
		Display.destroy();
		
	}
	
	private void final_pass () {
		framebuffer.unbind();
		
		framebuffer.bind(1);
		
		//DRAW TO FBO
		
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glPushMatrix();
		
		post.bind();
		
		framebuffer.bind_texture(0);
		
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity(); // not a huge problem

		GL11.glOrtho(-1.0, 1.0, -1.0, 1.0, 1.0, 40.0);

		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glLoadIdentity();

		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT
				| GL11.GL_STENCIL_BUFFER_BIT);

		// draw view plane
		GL11.glColor3f(1.0f, 1.0f, 1.0f);
		GL11.glBegin(GL11.GL_QUADS);

		GL11.glTexCoord2f(0.0f, 1.0f);
		GL11.glVertex3f(-1.0f, 1.0f, -1.0f);

		GL11.glTexCoord2f(0.0f, 0.0f);
		GL11.glVertex3f(-1.0f, -1.0f, -1.0f);

		GL11.glTexCoord2f(1.0f, 0.0f);
		GL11.glVertex3f(1.0f, -1.0f, -1.0f);

		GL11.glTexCoord2f(1.0f, 1.0f);
		GL11.glVertex3f(1.0f, 1.0f, -1.0f);

		GL11.glEnd();
		
		framebuffer.unbind_texture();
		
		//GL11.glPopMatrix();
		
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glPopMatrix();
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		
		post.unbind();
		
		framebuffer.unbind();
		
		
		//TODO: FINAL PASS
		
		//DRAW TO FBO
		
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glPushMatrix();
		
		post2.bind();
		
		post2.setValue1i(post2_ind_texture, 0);
		framebuffer.bind_texture(0);
		
		GL13.glActiveTexture(GL13.GL_TEXTURE1);
		post2.setValue1i(post2_ind_texture2, 1);
		framebuffer.bind_texture(1);
		
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity(); // not a huge problem

		GL11.glOrtho(-1.0, 1.0, -1.0, 1.0, 1.0, 40.0);

		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glLoadIdentity();

		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT
				| GL11.GL_STENCIL_BUFFER_BIT);

		// draw view plane
		GL11.glColor3f(1.0f, 1.0f, 1.0f);
		GL11.glBegin(GL11.GL_QUADS);

		GL11.glTexCoord2f(0.0f, 1.0f);
		GL11.glVertex3f(-1.0f, 1.0f, -1.0f);

		GL11.glTexCoord2f(0.0f, 0.0f);
		GL11.glVertex3f(-1.0f, -1.0f, -1.0f);

		GL11.glTexCoord2f(1.0f, 0.0f);
		GL11.glVertex3f(1.0f, -1.0f, -1.0f);

		GL11.glTexCoord2f(1.0f, 1.0f);
		GL11.glVertex3f(1.0f, 1.0f, -1.0f);

		GL11.glEnd();
		
		framebuffer.unbind_texture();
		
		//GL11.glPopMatrix();
		
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glPopMatrix();
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		
		post2.unbind();
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		
		GL11.glDisable(GL11.GL_TEXTURE_2D);
	}
	
	public void stop() {
		
		running = false;
		
	}
	
}
