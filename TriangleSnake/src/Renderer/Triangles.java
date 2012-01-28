package Renderer;

import java.util.ArrayList;

import org.lwjgl.util.vector.Vector3f;

public class Triangles {

	public static final int LIMIT = 1000;
	public static final float TIMETOADD = 2.0f; //0.8
	
	private ArrayList<GameObject> obj_list;
	
	private float time;
	private int counter = 0;
	
	public Triangles () {
		
		obj_list = new ArrayList<GameObject>();
		
		//obj_list.add(new GameObject(0, 0, 0));
		
	}
	
	public boolean collide(float px, float py, float pz) {
		
		
		
		return false;
	}
	
	public void update(float dt, Camera camera) {
		
		time += dt;
		
		if (time > TIMETOADD) {
			Vector3f pos = camera.getPos();
			
			//System.out.println("added triangle: " + pos[0] +"/"+ pos[1] +"/"+ pos[2]);
			
			if (obj_list.size() < LIMIT)
				obj_list.add(new GameObject(pos.x, pos.y, pos.z + 1.0f));
			else {
				//Limit size
				if (counter > LIMIT) {
					System.out.println("start new fill");
					counter = 0;
				}
				obj_list.add(counter, new GameObject(pos.x, pos.y, pos.z + 1.0f));
				
				counter++;
			}
				
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
			
			//diagonal horizontal
			obj_list.get(i).draw_offset(60, 0, 60);
			obj_list.get(i).draw_offset(-60, 0, 60);
			obj_list.get(i).draw_offset(60, 0, -60);
			obj_list.get(i).draw_offset(-60, 0, -60);
			
			//diagonal center
			obj_list.get(i).draw_offset(0, 60, -60);
			obj_list.get(i).draw_offset(0, 60, 60);
			obj_list.get(i).draw_offset(0, -60, 60);
			obj_list.get(i).draw_offset(0, -60, -60);
		}
	}
	
}
