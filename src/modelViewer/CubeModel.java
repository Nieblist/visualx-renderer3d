package modelViewer;

import logic3D.Face;
import logic3D.Vec3;
import logic3D.Vertex;

public class CubeModel extends Model {
	

	
	public CubeModel(double sideLength) {
		
		double s = sideLength/2;
		
		//Vertices
			//Front
			vertices.add(new Vertex(-s,  s, -s));
			vertices.add(new Vertex( s,  s, -s));
			vertices.add(new Vertex( s, -s, -s));
			vertices.add(new Vertex(-s, -s, -s));
			
			//Back
			vertices.add(new Vertex(-s,  s, s));
			vertices.add(new Vertex( s,  s, s));
			vertices.add(new Vertex( s, -s, s));
			vertices.add(new Vertex(-s, -s, s));
			
		//Faces
			//Front
			faces.add(new Face(0, 3, 2, new Vec3(0.0, 0.0, 1.0)));
			faces.add(new Face(0, 2, 1, new Vec3(0.0, 0.0, 1.0)));
			
			//Back
			faces.add(new Face(4, 6, 7, new Vec3(0.0, 0.0, -1.0)));
			faces.add(new Face(4, 5, 6, new Vec3(0.0, 0.0, -1.0)));
		
			//Left
			faces.add(new Face(4, 7, 3, new Vec3(-1.0, 0.0, 0.0)));
			faces.add(new Face(4, 3, 0, new Vec3(-1.0, 0.0, 0.0)));

			//Right
			faces.add(new Face(1, 2, 6, new Vec3(1.0, 0.0, 0.0)));
			faces.add(new Face(1, 6, 5, new Vec3(1.0, 0.0, 0.0)));
			
			//Bottom
			faces.add(new Face(3, 7, 6, new Vec3(0.0, -1.0, 0.0)));
			faces.add(new Face(3, 6, 2, new Vec3(0.0, -1.0, 0.0)));
		
			//Top
			faces.add(new Face(0, 5, 4, new Vec3(0.0, 1.0, 0.0)));
			faces.add(new Face(0, 1, 5, new Vec3(0.0, 1.0, 0.0)));
			
			meanPoint = new Vec3(0.0, 0.0, 0.0);
			centerPoint = new Vec3(0.0, 0.0, 0.0);;
			boundingBoxStart = new Vec3(-s, -s, -s);;
			boundingBoxEnd = new Vec3(s, s, s);;;
			radius = s*3;
			meanRadius = s*3;
	}

}
