package renderer;
import renderer.Transform;
import java.awt.Color;

/**
 * Polygon stores data about a single polygon in a scene, keeping track of
 * (at least!) its three vertices and its reflectance.
 *
 * This class has been done for you.
 */
public class Polygon {

	private Vector3D[] vertices;
	private Color reflectance;
	private Vector3D normal;
	private boolean hidden;
	private float minX, maxX, minY, maxY;

	/**
	 * @param points
	 *            An array of floats with 9 elements, corresponding to the
	 *            (x,y,z) coordinates of the three vertices that make up
	 *            this polygon. If the three vertices are A, B, C then the
	 *            array should be [A_x, A_y, A_z, B_x, B_y, B_z, C_x, C_y,
	 *            C_z].
	 * @param color
	 *            An array of three ints corresponding to the RGB values of
	 *            the polygon, i.e. [r, g, b] where all values are between 0
	 *            and 255.
	 */
	public Polygon(float[] points, int[] color) {
		this.vertices = new Vector3D[3];

		float x, y, z;
		// adds vectors from points
		for (int i = 0; i < 3; i++) {
			x = points[i * 3];
			y = points[i * 3 + 1];
			z = points[i * 3 + 2];
			this.vertices[i] = new Vector3D(x, y, z);

		}
		// adds new color
		int r = color[0];
		int g = color[1];
		int b = color[2];
		this.reflectance = new Color(r, g, b);
	}

	/**
	 * An alternative constructor that directly takes three Vector3D objects
	 * and a Color object.
	 */
	public Polygon(Vector3D a, Vector3D b, Vector3D c, Color color) {
		this.vertices = new Vector3D[] { a, b, c };
		this.reflectance = color;
	}


	public Vector3D[] getVertices() {
		return vertices;
	}

	public Color getReflectance() {
		return reflectance;
	}

	public void setNormal(Vector3D n){
		this.normal = n;
	}

	public Vector3D getNormal(){
		return this.normal;
	}

	public void setHidden(boolean b){
		this.hidden = b;
	}

	public boolean isHidden(){
		return this.hidden;
	}

	public float getHeight(){
		return this.maxY- this.minY;
	}

	public float getWidth(){
		return this.maxX- this.minX;
	}

	public int getMinY(){		
		int min = (int) Math.min(vertices[0].y , Math.min(vertices[1].y, vertices[2].y) );
		return min;
	}
	public int getMaxY(){
		int max = (int) Math.max(vertices[0].y , Math.max(vertices[1].y, vertices[2].y) );
		return max;
	}
	public int getMinX() throws Exception{
		throw new Exception("Shouldn't be called");
	}	
	public int getMaxX() throws Exception{
		throw new Exception("Shouldn't be called");
	}

	public Vector3D getMinYVector(){
		float minY = Integer.MAX_VALUE;
		int index = -1;
		
		for(int i=0; i < vertices.length; i++){
			if(vertices[i].y < minY){
				minY = vertices[i].y;
				index = i;
			}
		}

		return vertices[index];
	}


	public void setVectors(Vector3D[] v){
		this.vertices = v;
	}

	@Override
	public String toString() {
		String str = "polygon:";

		for (Vector3D p : vertices)
			str += "\n  " + p.toString();

		str += "\n  " + reflectance.toString();

		return str;
	}
}