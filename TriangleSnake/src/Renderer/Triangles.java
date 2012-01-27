package Renderer;

import java.util.ArrayList;

public class Triangles {

	private ArrayList<GameObject> obj_list;
	
	public Triangles () {
		
		obj_list = new ArrayList<GameObject>();
		
		obj_list.add(new GameObject(0, 0, 0));
		
	}
	
	public void draw() {
		for (int i=0; i<obj_list.size(); i++) {
			obj_list.get(i).draw();
		}
	}
	
}
