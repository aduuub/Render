package renderer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * EdgeList should store the data for the edge list of a single polygon in your
 * scene. A few method stubs have been provided so that it can be tested, but
 * you'll need to fill in all the details.
 *
 * You'll probably want to add some setters as well as getters or, for example,
 * an addRow(y, xLeft, xRight, zLeft, zRight) method.
 */
public class EdgeList {

	// LEFT X : LEFT Z : RIGHT X : RIGHT Z
	public float[][] edge;
	private int startY;
	private int endY;

	public EdgeList(int startY, int endY) {
		
		if(endY > 599)
			return;
		this.startY = startY;
		this.endY = endY;
		edge = new float[endY + 1][4];
		init();
		
	}
	
	
	public void init(){
		for(int i=0; i< endY; i++){
			edge[i][0] = Float.POSITIVE_INFINITY;
			edge[i][1] = Float.POSITIVE_INFINITY;
			edge[i][2] = Float.NEGATIVE_INFINITY;
			edge[i][3] = Float.POSITIVE_INFINITY;
		}
			
	}

	public int getStartY() {
		return this.startY;
	}

	public int getEndY() {
		return this.endY;
	}

	public float getLeftX(int y) {
		if(y >= edge.length)
			throw new IndexOutOfBoundsException();
		return  edge[y][0];
	}

	public float getRightX(int y) {
		if(y >= edge.length)
			throw new IndexOutOfBoundsException();
		return  edge[y][2];
	}

	public float getLeftZ(int y) {
		if(y >= edge.length)
			throw new IndexOutOfBoundsException();
		return  edge[y][1];
	}

	public float getRightZ(int y) {
		if(y >= edge.length)
			throw new IndexOutOfBoundsException();
		return  edge[y][3];
	}
}

// code for comp261 assignments
