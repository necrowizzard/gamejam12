package Renderer;

import java.util.ArrayList;

public class Triangles {

	public static final int TIMETOADD = 10;
	
	private ArrayList<GameObject> obj_list;
	
	private float time;
	
	public Triangles () {
		
		obj_list = new ArrayList<GameObject>();
		
		obj_list.add(new GameObject(0, 0, 0));
		
	}
	
	public void update(float dt) {
		
		time += dt;
		
		if (time > TIMETOADD) {
			obj_list.add(new GameObject(0, 0, 0));
			time = 0;
		}
		
		for (int i=0; i<obj_list.size(); i++) {
			obj_list.get(i).update();
		}
	}
	
	public void draw() {
		for (int i=0; i<obj_list.size(); i++) {
			obj_list.get(i).draw();
		}
	}
	
}
