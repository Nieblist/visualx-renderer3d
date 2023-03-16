package renderer;

import logic3D.Vec3;

public class AmbDifLight extends Shader {

	Vec3 lightDirection;
	int lightColor;
	double ambientIntensity;
	int ambientColor;
	int ambient;
	
	public AmbDifLight(Vec3 lightDirection, int lightColor, int ambientColor, double ambientIntensity) {
		this.lightDirection = Vec3.versor(lightDirection);
		this.lightColor = lightColor;
		this.ambientColor = ambientColor;
		this.ambientIntensity = ambientIntensity;
		ambient = scaleColor(ambientColor, ambientIntensity);
	}
	
	public void setLight(Vec3 direction, int color) {
		lightDirection = direction;
		lightColor = color;
	}
	
	public void setAmbient(int color, double intensity) {
		ambientIntensity = intensity;
		ambientColor = color;
		ambient = scaleColor(ambientColor, ambientIntensity);
	}
	
	@Override
	public int shade(Vec3 camera, Vec3 position, Vec3 normal) {
		double incidence = Vec3.dot(lightDirection, normal);
		
		if (incidence < 0) {
			incidence = 0;
		}
		
		return addColors(scaleColor(lightColor, incidence), ambient);
	}

}
