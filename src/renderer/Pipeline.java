package renderer;

import java.awt.Color;
import renderer.Polygon;

/**
 * The Pipeline class has method stubs for all the major components of the
 * rendering pipeline, for you to fill in.
 * 
 * Some of these methods can get quite long, in which case you should strongly
 * consider moving them out into their own file. You'll need to update the
 * imports in the test suite if you do.
 */
public class Pipeline {

	/**
	 * Returns true if the given polygon is facing away from the camera (and so
	 * should be hidden), and false otherwise.
	 */
	public static boolean isHidden(Polygon poly) {
		Vector3D[] verts =  poly.getVertices();
		Vector3D a = verts[0];
		Vector3D b = verts[1];
		Vector3D c = verts[2];

		// formula from the lecture slides
		return ((b.x - a.x) * (c.y - b.y) > (b.y - a.y) * (c.x - b.x));
	}

	/**
	 * Computes the colour of a polygon on the screen, once the lights, their
	 * angles relative to the polygon's face, and the reflectance of the polygon
	 * have been accounted for.
	 * 
	 * @param lightDirection
	 *            The Vector3D pointing to the directional light read in from
	 *            the file.
	 * @param lightColor
	 *            The color of that directional light.
	 * @param ambientLight
	 *            The ambient light in the scene, i.e. light that doesn't depend
	 *            on the direction.
	 */
	public static Color getShading(Polygon poly, Vector3D lightDirection, Color lightColor, Color ambientLight) {	

 		Color reflectivity = poly.getReflectance();
 		lightDirection = lightDirection.unitVector();

		Vector3D[] verticies = poly.getVertices();
		Vector3D a = verticies[0];
		Vector3D b = verticies[1];
		Vector3D c = verticies[2];

		Vector3D normal = (b.minus(a)).crossProduct((c.minus(b))).unitVector();
		float cosAngle = normal.cosTheta(lightDirection);		

		int rCol, gCol, bCol;
		if(lightDirection.z > 0){
			rCol = (int) ((ambientLight.getRed()  * cosAngle) * (double)(reflectivity.getRed() / 255));
			gCol = (int) ((ambientLight.getGreen() * cosAngle) * (double)(reflectivity.getGreen() / 255));
			bCol = (int) ((ambientLight.getBlue() * cosAngle) * (double)(reflectivity.getBlue() / 255));
		}else{
			rCol = (int) ((ambientLight.getRed() + lightColor.getRed()  * cosAngle) * (double) reflectivity.getRed() / 255);
			gCol = (int) ((ambientLight.getGreen() + lightColor.getGreen() * cosAngle) * (double) reflectivity.getGreen() / 255);
			bCol = (int) ((ambientLight.getBlue() + lightColor.getBlue() * cosAngle) * (double) reflectivity.getBlue() / 255);
		}
		
		rCol = checkBounds(rCol);
		gCol = checkBounds(gCol);
		bCol = checkBounds(bCol);

		return new Color(Math.abs(rCol), Math.abs(gCol), Math.abs(bCol));
	}



	public static int checkBounds(int number){

		number = number > 255 ? 255 : number; // check upper bounds
		number = number < 0 ? 0 : number; // check lower bounds
		return (int) number;
	}

	/**
	 * This method should rotate the polygons and light such that the viewer is
	 * looking down the Z-axis. The idea is that it returns an entirely new
	 * Scene object, filled with new Polygons, that have been rotated.
	 * 
	 * @param scene
	 *            The original Scene.
	 * @param xRot
	 *            An angle describing the viewer's rotation in the YZ-plane (i.e
	 *            around the X-axis).
	 * @param yRot
	 *            An angle describing the viewer's rotation in the XZ-plane (i.e
	 *            around the Y-axis).
	 * @return A new Scene where all the polygons and the light source have been
	 *         rotated accordingly.
	 */
	public static Scene rotateScene(Scene scene, float xRot, float yRot) {

		if(xRot != 0f && yRot != 0f){
			Transform xRotation = Transform.newXRotation(xRot);
			Transform yRotation = Transform.newYRotation(yRot);
			Transform total = xRotation.compose(yRotation);
			scene.applyTransformation(total);	

		}else if(xRot != 0f){
			Transform xRotation = Transform.newXRotation(xRot);
			scene.applyTransformation(xRotation);	

		}else if(yRot != 0f){
			Transform yRotation = Transform.newYRotation(yRot);
			scene.applyTransformation(yRotation);		
		}
		return scene;
	}

	/**
	 * This should translate the scene by the appropriate amount.
	 * 
	 * @param scene
	 * @param dx
	 * @param dy
	 * @param dz
	 * @return
	 */
	public static Scene translateScene(Scene scene, float dx, float dy, float dz) {
		Transform translation = Transform.newTranslation(dx, dy, dy);
		
		scene.applyTransformation(translation);
		return scene;
	}

	/**
	 * This should scale the scen2e.
	 * 
	 * @param scene The scene to perform the scale on
	 * @param scale Amount to scale by
	 * @return
	 */
	public static Scene scaleScene(Scene scene, float scale) {

		Transform translation = Transform.newScale(scale, scale, scale);
		scene.applyTransformation(translation);	
		return scene;

	}

	/**
	 * Computes the edgelist of a single provided polygon, as per the lecture
	 * slides.
	 * 
	 *  left 
	 * 	- x = 0
	 * 	- y = 1
	 *  right
	 * 	- x = 2
	 * 	- y = 3
	 */
	public static EdgeList computeEdgeList(Polygon poly) {
		if(poly.getMinY() < 0)
			System.out.println(poly.getMinY());
		
		int min = poly.getMinY() < 0 ? poly.getMaxY() : 0; 
		EdgeList edgeList = new EdgeList(min, poly.getMaxY() + 1 );

		Vector3D[] verts = poly.getVertices();

		// calculate edges of the polygon
		Edge[] edges = new Edge[3];
		edges[0] = new Edge(verts[0] , verts[1]);
		edges[1] = new Edge(verts[1] , verts[2]);
		edges[2] = new Edge(verts[2] , verts[0]);		

		for(Edge e : edges){

			Vector3D a = e.getMinY(); 
			Vector3D b = e.getMaxY();

			float mx = (b.x - a.x) / (b.y - a.y);
			float mz = (b.z - a.z) / (b.y - a.y);

			float x = a.x;
			float z = a.z;

			int i = Math.round(a.y);
			double maxI = Math.round(b.y);

			while(i < maxI){

				// if left of left.x
				if (x < edgeList.edge[i][0]) {
					edgeList.edge[i][0] = x;
					edgeList.edge[i][1] = z;
				}

				// if right of right.x
				if (x > edgeList.edge[i][2]) {
					if(x > 600){
						System.out.println("x: "+ x + " y:"+i);
					}
					edgeList.edge[i][2] = x;
					edgeList.edge[i][3] = z;
				}

				i++;
				x += mx;
				z += mz;
			}
		}

		return edgeList;
	}



	/**
	 * Fills a zbuffer with the contents of a single edge list according to the
	 * lecture slides.
	 * 
	 * The idea here is to make zbuffer and zdepth arrays in your main loop, and
	 * pass them into the method to be modified.
	 * 
	 * @param zbuffer
	 *            A double array of colours representing the Color at each pixel
	 *            so far.
	 * @param zdepth
	 *            A double array of floats storing the z-value of each pixel
	 *            that has been coloured in so far.
	 * @param polyEdgeList
	 *            The edgelist of the polygon to add into the zbuffer.
	 * @param polyColor
	 *            The colour of the polygon to add into the zbuffer.
	 */
	public static void computeZBuffer(Color[][] zbuffer, float[][] zdepth, EdgeList edgeList, Color polyColor) {
		for(int y = 0 ; y < edgeList.getEndY() ; y++){

			int x = (int) Math.ceil(edgeList.getLeftX(y));

			float z = edgeList.getLeftZ(y);
			int rightX =  Math.round(edgeList.getRightX(y)); // rightmost x

			float mz = (edgeList.getRightZ(y) - edgeList.getLeftZ(y)) /
					(edgeList.getRightX(y) - edgeList.getLeftX(y));

			while(x < rightX){
				
				//System.out.printf("%d, %d, %f \n" ,x,y,z);
				
				if(x >= zdepth.length || y > zdepth[0].length || x < 0 || y < 0){
					System.out.printf("%d, %d, %f \n", x, y, z);	

					break;
				}
				
				
				if(x < 0 || y < 0){
					x++;
					z+=mz;
					continue;
				}
				
				

				
				if(z < zdepth[x][y]){
					zdepth[x][y] = z; // set the depth
					zbuffer[x][y] = polyColor;
				}
				x++;
				z+=mz;
			}
		}
	}




}

// code for comp261 assignments
