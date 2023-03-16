package renderer;

import logic3D.Vec3;

public class FlatLight extends Shader {

	Vec3 lightDirection;
	int lightColor;
	
	public FlatLight(Vec3 lightDirection, int lightColor) {
		this.lightDirection = Vec3.versor(lightDirection);
		this.lightColor = lightColor;
	}
	
	public void setLight(Vec3 direction, int color) {
		lightDirection = direction;
		lightColor = color;
	}
	
	@Override
	public int shade(Vec3 camera, Vec3 position, Vec3 normal) {
		double incidence = Vec3.dot(lightDirection, normal);
		
		if (incidence < 0) {
			incidence = 0;
		}
		
		return scaleColor(lightColor, incidence);
	}

}
