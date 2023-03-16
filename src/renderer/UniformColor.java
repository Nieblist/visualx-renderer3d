package renderer;

import logic3D.Vec3;

public class UniformColor extends Shader {

	int color;
	
	public UniformColor(int color) {
		this.color = color;
	}
	
	public void setColor(int color) {
		this.color = color;
	}
	
	@Override
	public int shade(Vec3 camera, Vec3 position, Vec3 normal) {
		return color;
	}

}
