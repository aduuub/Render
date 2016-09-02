package renderer;
import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

public class Renderer extends GUI {

	private static Color[][] colour;
	private static float[][] depth;
	
	private BoundingBox boundingBox;

	private float shift = 0.5f;
	private Scene scene;
	private Vector3D lightDirection;


	private Renderer(){
		colour = new Color[CANVAS_HEIGHT][CANVAS_WIDTH];
		depth = new float[CANVAS_HEIGHT][CANVAS_WIDTH];
	}


	@Override
	protected void onLoad(File file)  {
		List<Polygon> polygons = new ArrayList<Polygon>();

		try{
			BufferedReader data = new BufferedReader(new FileReader(file) );

			String headerLine = data.readLine();
			this.lightDirection = createVector(headerLine);

			String line;
			while((line = data.readLine()) != null){

				String[] values = line.split(" ");

				// add points
				float[] points = new float[9];
				for(int i=0; i < 9; i++) 
					points[i] = Float.parseFloat(values[i]);

				//add color				
				int[] colour = new int[3];
				colour[0] = Integer.parseInt(values[9]);
				colour[1] = Integer.parseInt(values[10]);
				colour[2] = Integer.parseInt(values[11]);


				polygons.add(new Polygon(points, colour));
			}



			this.scene = new Scene(polygons, this.lightDirection);
			render();

		}catch(IOException e){
			e.printStackTrace();
		}
	}


	/**
	 * Creates a new 3D vector
	 * @param line String inputed from the line. Use space to separate values
	 * @return Vector3D 
	 */
	private Vector3D createVector(String line){
		String[] data = line.split(" ");
		float a = Float.parseFloat(data[0]);
		float b = Float.parseFloat(data[1]);
		float c = Float.parseFloat(data[2]);
		return new Vector3D(a,b,c);
	}

	/**
	 * Creates a new bounding box that contains all the polygons
	 * @return
	 */
	private BoundingBox createBoundingBox() {
		int maxX = Integer.MIN_VALUE;
		int minX = Integer.MAX_VALUE;
		int maxY = Integer.MIN_VALUE;
		int minY = Integer.MAX_VALUE;

		for (Polygon p : this.scene.getPolygons()) {
			for (Vector3D v : p.getVertices()) {
				if (v.x > maxX) {
					maxX = Math.round(v.x);
				}
				if (v.x < minX) {
					minX = Math.round(v.x);
				}
				if (v.y > maxY) {
					maxY = Math.round(v.y);
				}
				if (v.y < minY) {
					minY = Math.round(v.y);
				}
			}
		}
		return new BoundingBox(minX, minY, maxX - minX, maxY - minY);
	}


	@Override
	protected void onKeyPress(KeyEvent ev) {
		if (ev.getKeyCode() == KeyEvent.VK_LEFT
				|| Character.toUpperCase(ev.getKeyChar()) == 'A')
			Pipeline.rotateScene(this.scene, 0 , shift);
		else if (ev.getKeyCode() == KeyEvent.VK_RIGHT
				|| Character.toUpperCase(ev.getKeyChar()) == 'D')
			Pipeline.rotateScene(this.scene, 0, -shift);
		else if (ev.getKeyCode() == KeyEvent.VK_UP
				|| Character.toUpperCase(ev.getKeyChar()) == 'W')
			Pipeline.rotateScene(this.scene, -shift, 0);
		else if (ev.getKeyCode() == KeyEvent.VK_DOWN
				|| Character.toUpperCase(ev.getKeyChar()) == 'S')
			Pipeline.rotateScene(this.scene, shift, 0);

	}


	/*
	 * This method should put together the pieces of your renderer, as
	 * described in the lecture. This will involve calling each of the
	 * static method stubs in the Pipeline class, which you also need to
	 * fill in.
	 */
	@Override
	protected BufferedImage render(){
		if(this.scene == null || this.scene.getPolygons() == null)
			return null;

		initBuffer(); // set bufferer to default
		calculateIfHidden(); // calculate which polygons are hidden or not
		computeNormals(); // calculate all polygons normals
		movePolygonsIntoView();	

		for(Polygon poly : this.scene.getPolygons()){

			int[] ambVals = getAmbientLight();
			Color ambientLight = new Color(ambVals[0], ambVals[1], ambVals[2]);
			Color lightColor = new Color(255,255,255);

			Color polyColor = Pipeline.getShading(poly, this.lightDirection, lightColor, ambientLight);

			EdgeList polyEdgeList = Pipeline.computeEdgeList(poly);
			Pipeline.computeZBuffer(colour, depth, polyEdgeList, polyColor);			
		}	
		return convertBitmapToImage(colour);
	}

	/**
	 * Sets the polygons to be hidden or not
	 */
	public void calculateIfHidden(){			
		for(Polygon p : this.scene.getPolygons())
			p.setHidden(Pipeline.isHidden(p));
	}

	/**
	 * Calculates the normal for all polygons in the scene
	 */
	public void computeNormals(){
		for(Polygon p : scene.getPolygons()){
			Vector3D[] vectors = p.getVertices();
			Vector3D first = vectors[0];
			Vector3D second = vectors[1];
			Vector3D third = vectors[2];

			Vector3D secondV = second.minus(first);
			Vector3D thirdV = third.minus(second);
			
			Vector3D normal = secondV.crossProduct(thirdV).unitVector();
			p.setNormal(normal);
		}
	}


	/**
	 * Initilises the buffer to the defaults
	 */
	private void initBuffer() {
		for (int i = 0; i < CANVAS_HEIGHT; i++) {
			for (int j = 0; j < CANVAS_WIDTH; j++) {
				colour[i][j] = Color.gray;
				depth[i][j] = Integer.MAX_VALUE;
			}
		}
	}
	
	
	/**
	 * Sets up the polygons and moves them onto the screen
	 */
	private void movePolygonsIntoView() {
		
//		this.boundingBox = createBoundingBox();
//		float width = CANVAS_WIDTH / this.boundingBox.getWidth();
//		float height = CANVAS_HEIGHT / this.boundingBox.getHeight();
		
		if (this.boundingBox == null) {
			this.boundingBox = createBoundingBox();
			float scaleX = 300f / this.boundingBox.getWidth();
			float scaleY = 300f / this.boundingBox.getHeight();
			float scale = Math.min(scaleX, scaleY);

			Pipeline.scaleScene(this.scene, scale);
		}

		this.boundingBox = createBoundingBox();

		System.out.println("Bounds: " + this.boundingBox.getX() + ", "
				+ this.boundingBox.getY() + ", " + this.boundingBox.getWidth() + ", "
				+ this.boundingBox.getHeight());

		float shiftX = this.boundingBox.getX();
		float shiftY = this.boundingBox.getY();
		Pipeline.translateScene(scene,-shiftX + 50, -shiftY + 50, 0);
		this.boundingBox = createBoundingBox();

	}
	
	/**
	 * Converts a 2D array of Colors to a BufferedImage. Assumes that bitmap is
	 * indexed by column then row and has imageHeight rows and imageWidth
	 * columns. Note that image.setRGB requires x (col) and y (row) are given in
	 * that order.
	 */
	private BufferedImage convertBitmapToImage(Color[][] bitmap) {
		BufferedImage image = new BufferedImage(CANVAS_WIDTH, CANVAS_HEIGHT, BufferedImage.TYPE_INT_RGB);
		for (int x = 0; x < CANVAS_WIDTH; x++) {
			for (int y = 0; y < CANVAS_HEIGHT; y++) {
				image.setRGB(x, y, bitmap[x][y].getRGB());
			}
		}
		return image;
	}

	public static void main(String[] args) {
		new Renderer();
	}
}

// code for comp261 assignments
