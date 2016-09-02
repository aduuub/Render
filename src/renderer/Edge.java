package renderer;

public class Edge {

	public Vector3D a;
	public Vector3D b;

	public Edge(Vector3D a, Vector3D b){
		this.a = a;
		this.b = b;
	}

	public Vector3D getMinY(){
		if(a.y < b.y)
			return a;
		else
			return b;
	}
	
	public Vector3D getMaxY(){
		if(a.y > b.y)
			return a;
		else
			return b;
	}
}
