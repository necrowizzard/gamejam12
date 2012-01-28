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
				obj_list.add(new GameObject(pos.x, pos.y, pos.z + 1.0f, camera));
			else {
				//Limit size
				if (counter > LIMIT) {
					System.out.println("start new fill");
					counter = 0;
				}
				obj_list.add(counter, new GameObject(pos.x, pos.y, pos.z + 1.0f, camera));
				
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
	
	public float intersect(Vector3f ray_pos, Vector3f ray_dir) {
	    // Compute vectors along two edges of the triangle.
	    Vector3f edge1 = null, edge2 = null;
	    Vector3f vertex1 = null;

	    //edge1 = Vector3f.sub(vertex2, vertex1, edge1);
	    //edge2 = Vector3f.sub(vertex3, vertex1, edge2);

	    // Compute the determinant.
	    Vector3f directionCrossEdge2 = null;
	    directionCrossEdge2 = Vector3f.cross(ray_pos, edge2, directionCrossEdge2);


	    float determinant = Vector3f.dot(directionCrossEdge2, edge1);
	    // If the ray and triangle are parallel, there is no collision.
	    if (determinant > -.0000001f && determinant < .0000001f) {
	        return Float.MAX_VALUE;
	    }

	    float inverseDeterminant = 1.0f / determinant;

	    // Calculate the U parameter of the intersection point.
	    Vector3f distanceVector = null;
	    distanceVector = Vector3f.sub(ray_pos, vertex1, distanceVector);


	    float triangleU = Vector3f.dot(directionCrossEdge2, distanceVector);
	    triangleU *= inverseDeterminant;

	    // Make sure the U is inside the triangle.
	    if (triangleU < 0 || triangleU > 1) {
	        return Float.MAX_VALUE;
	    }

	    // Calculate the V parameter of the intersection point.
	    Vector3f distanceCrossEdge1 = null;
	    distanceCrossEdge1 = Vector3f.cross(distanceVector, edge1, distanceCrossEdge1);


	    float triangleV = Vector3f.dot(ray_dir, distanceCrossEdge1);
	    triangleV *= inverseDeterminant;

	    // Make sure the V is inside the triangle.
	    if (triangleV < 0 || triangleU + triangleV > 1) {
	        return Float.MAX_VALUE;
	    }

	    // Get the distance to the face from our ray origin
	    float rayDistance = Vector3f.dot(distanceCrossEdge1, edge2);
	    rayDistance *= inverseDeterminant;


	    // Is the triangle behind us?
	    if (rayDistance < 0) {
	        rayDistance *= -1;
	        return Float.MAX_VALUE;
	    }
	    return rayDistance;
	}

	
}
