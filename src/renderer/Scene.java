package renderer;

import java.awt.Color;
import java.util.List;

/**
 * The Scene class is where we store data about a 3D model and light source
 * inside our renderer. It also contains a static inner class that represents one
 * single polygon.
 * 
 * Method stubs have been provided, but you'll need to fill them in.
 * 
 * If you were to implement more fancy rendering, e.g. Phong shading, you'd want
 * to store more information in this class.
 */
public class Scene {

	private Vector3D lightPos;
	private List<Polygon> polygons;

	public Scene(List<Polygon> polygons, Vector3D lightPos) {
		this.lightPos = lightPos;
		this.polygons = polygons;
	}

	public Vector3D getLight() {
		return this.lightPos;
	}

	public List<Polygon> getPolygons() {
		return this.polygons;
	}

	public void applyTransformation(Transform t) {	
		if(polygons == null)
			return;
		
		lightPos = t.multiply(lightPos);

		for(Polygon poly : polygons){

			Vector3D[] vects = poly.getVertices();

			for(int i=0; i < 3; i++){
				vects[i] = t.multiply(vects[i]);	
			}

			// set the polygons new vectors
			poly.setVectors(vects);
		}
	}
}







// code for COMP261 assignments
