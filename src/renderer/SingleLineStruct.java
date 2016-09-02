package renderer;

public class SingleLineStruct {

	 // an addRow(y, xLeft, xRight, zLeft, zRight) method.

	private int y;
	private float xLeft;
	private float xRight;
	private float zLeft;
	private float zRight;
	
	public SingleLineStruct(){
	}
	
	public int gety(){return this.y;}
	public float getXLeft(){return this.xLeft;}
	public float getXRight(){return this.xRight;}
	public float getZLeft(){return this.zLeft;}
	public float getZRight(){return this.zRight;}

	public void setXLeft(float x){this.xLeft = x;}
	public void setZLeft(float x){this.zLeft = x;}
	public void setXRight(float x){this.xRight = x;}
	public void setZRight(float x){this.zRight = x;}
	
	public void init(){
		this.xLeft = Float.MAX_VALUE;
		this.zLeft = Float.MAX_VALUE;
		this.xRight = Float.MIN_VALUE;
		this.zRight = Float.MAX_VALUE;
	}
}
