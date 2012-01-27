import javax.xml.parsers.DocumentBuilderFactory;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;


public class Main {
	
	private static Game game;
	
	public static void main(String[] args) {
		
		System.out.println("test");
		
		game = new Game();
		
		game.run();
		
		/*

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		
		while(true) {
			
		}*/
	}
	
}