package logic3D;

import renderer.Paintable;
import renderer.Renderer;

public class Face implements Paintable {
	public int v1, v2, v3;
	public Vec3 normal;
	
	public Face(int v1, int v2, int v3, Vec3 normal) {
		this.v1 = v1;
		this.v2 = v2;
		this.v3 = v3;
		this.normal = normal;
	}

	@Override
	public void paint(Renderer renderer) {
		renderer.drawFace(this);
	}
}
