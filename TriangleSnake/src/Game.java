import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

import GUI.MouseGUI;
import Renderer.Camera;
import Renderer.RenderWorld;


public class Game {

	boolean running;
	
	MouseGUI mouseGUI;
	
	private RenderWorld renderer;
	
	private Camera camera;
	float dx        = 0.0f;
    float dy        = 0.0f;
    float dt        = 0.0f; //length of frame
    float lastTime  = 0.0f; // when the last frame was
    float time      = 0.0f;
	float mouseSensitivity = 0.08f;
    float movementSpeed = 10.0f; //move 10 units per second
    
    boolean was_pressed = false;
	
	public Game() {
		
		running = true;
		
		init_window();
		
		mouseGUI = new MouseGUI();
		
		renderer = new RenderWorld();
		camera = new Camera(0, 0, 0);
		
		//System.out.println("Blaaaaaa: " + level.debug_get_texture()[1]);
	}
	
	private void init_window() {
		Display.setTitle("Perlin Noise");
		try {
			Display.setFullscreen(false);
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
	        dt = (time - lastTime)/1000.0f;
	        lastTime = time;
	 
	        //INPUT
	        if (Mouse.isButtonDown(1)) {
		        //distance in mouse movement from the last getDX() call.
		        dx = Mouse.getDX();
		        //distance in mouse movement from the last getDY() call.
		        dy = Mouse.getDY();
		 
		        //controll camera yaw from x movement fromt the mouse
		        camera.yaw(dx * mouseSensitivity);
		        //controll camera pitch from y movement fromt the mouse
		        camera.pitch(- dy * mouseSensitivity); 
	        }
			
			if (Keyboard.isKeyDown(Keyboard.KEY_W)) {
	            camera.walkForward(movementSpeed*dt);
	        }
	        if (Keyboard.isKeyDown(Keyboard.KEY_S)) {
	            camera.walkBackwards(movementSpeed*dt);
	        }
	        if (Keyboard.isKeyDown(Keyboard.KEY_A)) {
	            camera.strafeLeft(movementSpeed*dt);
	        }
	        if (Keyboard.isKeyDown(Keyboard.KEY_D)) {
	            camera.strafeRight(movementSpeed*dt);
	        }
	        if (Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {
	            camera.jump(movementSpeed*dt);
	        }
	        if (Keyboard.isKeyDown(Keyboard.KEY_X)) {
	            camera.move_down(movementSpeed*dt);
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
				
				GL11.glLoadIdentity();
				
				camera.apply_camera_transform();
				
				renderer.draw();
				
			}
			
		}
		
		Display.destroy();
		
	}
	
	public void stop() {
		
		running = false;
		
	}
	
}
