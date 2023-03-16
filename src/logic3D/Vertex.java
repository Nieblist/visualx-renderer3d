package logic3D;

import java.util.ArrayList;

import renderer.Paintable;
import renderer.Renderer;

public class Vertex implements Paintable {
	public Vec3 position;
	public Vec3 normal;
	public ArrayList< Face > faces;
	
	public Vertex(Vec3 pos) {
		position = pos;
		normal = Vec3.versor(position);
		faces = new ArrayList< Face >();
	}
	
	public Vertex(double x, double y, double z) {
		position = new Vec3 (x, y, z);
		normal = Vec3.versor(position);
		faces = new ArrayList< Face >();
	}

	public void recalculateNormal() {
		normal.x = 0;
		normal.y = 0;
		normal.z = 0;
		for (Face f : faces) {
			normal.add(f.normal);
		}
		normal.normalize();
	}
	
	@Override
	public void paint(Renderer renderer) {
		renderer.drawPoint(this);
	}
}
