package Renderer;

import java.util.ArrayList;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;

public class Triangles {

	public static final int LIMIT = 300;
	public static final float TIMETOADD = 3.0f; //0.8 //1.2 //2.0
	
	private ArrayList<GameObject> obj_list;
	private GameObject food;
	
	private float time;
	private int counter = 0;
	
	private Shader player_shader;
	private int player_shader_ind_texture, player_shader_ind_color;
	
	private Shader triangle;
	private int triangle_ind_texture;
	private int triangle_ind_color;
	
	public Triangles () {
		
		obj_list = new ArrayList<GameObject>();
		
		//obj_list.add(new GameObject(0, 0, 0));
		
		player_shader = new Shader("/shader/player");
		player_shader_ind_texture = player_shader.initValue1i("color_texture");
		player_shader_ind_color = player_shader.initValue1f("color_parameter");
		
		triangle = new Shader("/shader/triangle");
		triangle_ind_texture = triangle.initValue1i("color_texture");
		triangle_ind_color = triangle.initValue1f("color_parameter");
	}
	/*
	public boolean collide(GameObject gob) {
		
		for (int i=0; i<obj_list.size(); i++) {
			if (gob.collide(obj_list.get(i))) return true;
		}
		
		return false;
	}*/
	
	public boolean collide_sphere(Vector3f center) {
		
		for (int i=0; i<obj_list.size(); i++) {
			GameObject go = obj_list.get(i);
			if (go.collide_sphere(center)) return true;
		}
		
		return false;
	}
	
	public void positionFood() {
		if (food == null) {
			float x = (float) (Math.random() * Camera.size);
			float y = (float) (Math.random() * Camera.size);
			float z = (float) (Math.random() * Camera.size);
			food = new GameObject(x, y, z, null);
		}
	}
	
	public void update(float dt, Camera camera) {
		
		time += dt;
		
		if (time > TIMETOADD) {
			Vector3f pos = camera.getPos();
			
			//System.out.println("added triangle: " + pos[0] +"/"+ pos[1] +"/"+ pos[2]);
			
			float s = 0;
			if (obj_list.size() < LIMIT)
				obj_list.add(new GameObject(
						pos.x - camera.forward.x * s,
						pos.y - camera.forward.y * s,
						pos.z - camera.forward.z * s, camera));
			else {
				//Limit size
				if (counter > LIMIT) {
					System.out.println("start new fill");
					counter = 0;
				}
				obj_list.add(new GameObject(
						pos.x - camera.forward.x * s,
						pos.y - camera.forward.y * s,
						pos.z - camera.forward.z * s, camera));
				
				counter++;
			}
				
			time = 0;
		}
		
		for (int i=0; i<obj_list.size(); i++) {
			obj_list.get(i).update();
		}
	}
	
	public void draw(Camera player, boolean self, float color) {
		int s = Camera.size;
			
		for (int y = -2; y <= 2; y++) {
			for (int x = -2; x <= 2; x++) {
				for (int z = -2; z <= 2; z++) {
					
					triangle.bind();
					triangle.setValue1f(triangle_ind_color, color);
					triangle.setValue1i(triangle_ind_texture, 0);
					
					for (int i=0; i<obj_list.size(); i++) {
						obj_list.get(i).draw_offset(s * x, s * y, s * z);
					}
					
					triangle.unbind();
					
					//TODO: needs to be either removed or drawn different (seemed confusing) 
					// test: draw player
					GL11.glPushMatrix();
					GL11.glTranslatef(s * x, s * y, s * z);
					player.apply_player_transform();
					if (!self){//&& x == 0 && y == 0 && z == 0
						GL11.glScalef(1.5f, 1.5f, 1.5f);
						
						player_shader.bind();
						player_shader.setValue1f(player_shader_ind_color, color);
						player_shader.setValue1i(player_shader_ind_texture, 0);
						RenderWorld.drawBox();
						
						GL11.glScalef(0.95f, 0.95f, 0.95f);
						player_shader.setValue1f(player_shader_ind_color, 0.5f);
						RenderWorld.drawBox();
						player_shader.unbind();
					}
					
					GL11.glPopMatrix();
				}
			}
			
			/*obj_list.get(i).draw();
			
			obj_list.get(i).draw_offset(s, 0, 0);
			obj_list.get(i).draw_offset(-s, 0, 0);
			obj_list.get(i).draw_offset(0, s, 0);
			obj_list.get(i).draw_offset(0, -s, 0);
			obj_list.get(i).draw_offset(0, 0, s);
			obj_list.get(i).draw_offset(0, 0, -s);
			//obj_list.get(i).draw_offset(30, 0, 0);
			//obj_list.get(i).draw_offset(-30, 0, 0);
			
			//diagonal horizontal
			obj_list.get(i).draw_offset(s, 0, s);
			obj_list.get(i).draw_offset(-s, 0, s);
			obj_list.get(i).draw_offset(s, 0, -s);
			obj_list.get(i).draw_offset(-s, 0, -s);
			
			//diagonal center
			obj_list.get(i).draw_offset(0, s, -s);
			obj_list.get(i).draw_offset(0, s, s);
			obj_list.get(i).draw_offset(0, -s, s);
			obj_list.get(i).draw_offset(0, -s, -s);*/
		}
	}
	
	// found on http://gamedev.stackexchange.com/questions/12360/how-do-you-determine-which-object-surface-the-users-pointing-at-with-lwjgl
	static public boolean ray_intersects_triangle(Vector3f ray_pos, Vector3f ray_dir, float ray_length,
			Vector3f vertex1, Vector3f vertex2, Vector3f vertex3) {
	    // Compute vectors along two edges of the triangle.
	    Vector3f edge1 = null, edge2 = null;

	    edge1 = Vector3f.sub(vertex2, vertex1, edge1);
	    edge2 = Vector3f.sub(vertex3, vertex1, edge2);

	    // Compute the determinant.
	    Vector3f directionCrossEdge2 = null;
	    directionCrossEdge2 = Vector3f.cross(ray_dir, edge2, directionCrossEdge2);


	    float determinant = Vector3f.dot(directionCrossEdge2, edge1);
	    // If the ray and triangle are parallel, there is no collision.
	    if (determinant > -.0000001f && determinant < .0000001f) {
	        return false;
	    }

	    float inverseDeterminant = 1.0f / determinant;

	    // Calculate the U parameter of the intersection point.
	    Vector3f distanceVector = null;
	    distanceVector = Vector3f.sub(ray_pos, vertex1, distanceVector);


	    float triangleU = Vector3f.dot(directionCrossEdge2, distanceVector);
	    triangleU *= inverseDeterminant;

	    // Make sure the U is inside the triangle.
	    if (triangleU < 0 || triangleU > 1) {
	        return false;
	    }

	    // Calculate the V parameter of the intersection point.
	    Vector3f distanceCrossEdge1 = null;
	    distanceCrossEdge1 = Vector3f.cross(distanceVector, edge1, distanceCrossEdge1);


	    float triangleV = Vector3f.dot(ray_dir, distanceCrossEdge1);
	    triangleV *= inverseDeterminant;

	    // Make sure the V is inside the triangle.
	    if (triangleV < 0 || triangleU + triangleV > 1) {
	        return false;
	    }

	    // Get the distance to the face from our ray origin
	    float rayDistance = Vector3f.dot(distanceCrossEdge1, edge2);
	    rayDistance *= inverseDeterminant;

	    // Is the triangle behind us?
	    if (rayDistance < 0) {
	        return false;
	    }
	    
	    if (rayDistance > ray_length) {
	    	return false;
	    }
	    return true;
	}
	
	static private Vector3f diffvec(Vector3f v1, Vector3f v2) {
		return new Vector3f(v1.x - v2.x, v1.y - v2.y, v1.z - v2.z);
	}
	
	static boolean triangle_intersects_triangle(Vector3f vertex1, Vector3f vertex2, Vector3f vertex3,
			Vector3f other1, Vector3f other2, Vector3f other3) {
		for (int i = 0; i < 2; i++) {
			if (ray_intersects_triangle(vertex1, diffvec(vertex2, vertex1), diffvec(vertex2, vertex1).length(),
					other1, other2, other3)) return true;
			if (ray_intersects_triangle(vertex2, diffvec(vertex3, vertex2), diffvec(vertex3, vertex2).length(),
					other1, other2, other3)) return true;
			if (ray_intersects_triangle(vertex3, diffvec(vertex1, vertex3), diffvec(vertex1, vertex3).length(),
					other1, other2, other3)) return true;
			Vector3f temp1 = other1, temp2 = other2, temp3 = other3;
			other1 = vertex1;
			other2 = vertex2;
			other3 = vertex3;
			vertex1 = temp1;
			vertex2 = temp2;
			vertex3 = temp3;
		}
	
		return false;
	}

	
}
