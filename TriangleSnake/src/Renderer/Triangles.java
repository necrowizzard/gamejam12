package Renderer;

import java.util.ArrayList;

public class Triangles {

	public static final float TIMETOADD = 2.0f; //0.8
	
	private ArrayList<GameObject> obj_list;
	
	private float time;
	
	public Triangles () {
		
		obj_list = new ArrayList<GameObject>();
		
		//obj_list.add(new GameObject(0, 0, 0));
		
	}
	
	public void update(float dt, Camera camera) {
		
		time += dt;
		
		if (time > TIMETOADD) {
			float [] pos = camera.getPos();
			
			//System.out.println("added triangle: " + pos[0] +"/"+ pos[1] +"/"+ pos[2]);
			
			//TODO: WHY INVERTED?
			obj_list.add(new GameObject(-pos[0], -pos[1], -pos[2] + 1.0f));
			time = 0;
		}
		
		for (int i=0; i<obj_list.size(); i++) {
			obj_list.get(i).update();
		}
	}
	
	public void draw() {
		for (int i=0; i<obj_list.size(); i++) {
			obj_list.get(i).draw();
			
			obj_list.get(i).draw_offset(60, 0, 0);
			obj_list.get(i).draw_offset(-60, 0, 0);
			obj_list.get(i).draw_offset(0, 60, 0);
			obj_list.get(i).draw_offset(0, -60, 0);
			obj_list.get(i).draw_offset(0, 0, 60);
			obj_list.get(i).draw_offset(0, 0, -60);
			//obj_list.get(i).draw_offset(30, 0, 0);
			//obj_list.get(i).draw_offset(-30, 0, 0);
		}
	}
	
}
