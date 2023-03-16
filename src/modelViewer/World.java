package modelViewer;

import java.awt.image.BufferedImage;

import logic3D.Vec3;
import renderer.AmbDifLight;
import renderer.FlatLight;
import renderer.Renderer;
import renderer.Shader;
import renderer.Transformation;
import renderer.UniformColor;

public class World {	

	public static Vec3 V3_LIGHT_DIRECTION = new Vec3(-1.0, -0.2, -0.5);
	public static int I_LIGHT_COLOR = 0xFFEECC;
	public static int I_AMBIENTLIGHT_COLOR = 0xDD9999;
	
	public static double D_AMBIENTLIGHT_INTENSITY = 0.2;
	public static double D_PERSPECTIVE_FOV = 45;
	public static double D_ZOOM_MIN = 0.1;
	public static double D_ZOOM_MAX = 20.0;
	public static double D_MOVE_MAX = 1.0;
	
	Renderer renderer;
	Scope viewScope;
	Vec3 cameraPos;
	Vec3 objectPos;
	Model model;
	Transformation objectRot;
	
	Transformation orthographic;
	Transformation perspective;
	
	Shader uniform;
	Shader flatShader;
	Shader ambdifShader;
	
	double objectZoom;
	double perspectiveFOV = D_PERSPECTIVE_FOV;
	boolean perspectiveView = true;
	
	double ambientI = D_AMBIENTLIGHT_INTENSITY;
	int ambientColor = I_AMBIENTLIGHT_COLOR;
	int lightColor = I_LIGHT_COLOR;
	Vec3 lightDirection = V3_LIGHT_DIRECTION;
	
	public World() {
		renderer = new Renderer();
		viewScope = new Scope(-400.0, 400.0, -400.0, 400.0, Renderer.ZNEAR, -1000.0);
		cameraPos = new Vec3(0, 0, 0);
		objectPos = new Vec3(0, 0, 0);
		objectRot = new Transformation();
		objectZoom = 1;
		model = new CubeModel(1.0);
		
		updateShaders();
	}
	
	public String getRenderMode() {
		return renderer.getMode();
	}
	
	public boolean getBackfaceCullingState() {
		return renderer.getBackfaceCullingState();
	}
	
	public double getPerspectiveFOV() {
		return perspectiveFOV;
	}
	
	public double getZoom() {
		return objectZoom;
	}
	
	public double getAmbientLightIntensity() {
		return ambientI;
	}
	
	public int getAmbientLightColor() {
		return ambientColor;
	}
	
	public int getLightColor() {
		return lightColor;
	}

	public Vec3 getLightDirection() {
		return lightDirection;
	}
	
	public void setRenderMode(String mode) {
		renderer.setMode(mode);
	}
	
	public void setScope(double range) {
		viewScope = new Scope(-range, range, -range, range, Renderer.ZNEAR, -1000.0);
	}
	
	public void setBackfaceCulling(boolean culling) {
		renderer.setBackfaceCulling(culling);
	}
	
	public void setPerspectiveFOV(double fovY) {
		perspectiveFOV = fovY;
		
		double aspectRatio = (double) renderer.getViewportWidth() / (double) renderer.getViewportHeight();
		perspective = new Transformation();
		perspective.projectPerspective(perspectiveFOV, aspectRatio, viewScope.iZ, viewScope.fZ);
	}
	
	public void setViewport(BufferedImage viewport) {
		renderer.setViewport(viewport);
		
		double aspectRatio = (double) renderer.getViewportWidth() / (double) renderer.getViewportHeight();
		
		orthographic = new Transformation();
		orthographic.projectOrthographic(Math.abs(viewScope.fY - viewScope.iY), aspectRatio, viewScope.iZ, viewScope.fZ);
		
		perspective = new Transformation();
		perspective.projectPerspective(perspectiveFOV, aspectRatio, viewScope.iZ, viewScope.fZ);
	}
	
	public void setZoom(double zoom) {
		if (zoom < D_ZOOM_MIN || zoom > D_ZOOM_MAX) {
			return;
		}
		objectZoom = zoom;
	}
	
	public void setAmbientLightIntensity(double intensity) {
		ambientI = intensity;
		
		updateShaders();
	}
	
	public void setAmbientLightColor(int color) {
		ambientColor = color;
		
		updateShaders();
	}
	
	public void setLightColor(int color) {
		lightColor = color;
		
		updateShaders();
	}
	
	public void setLightDirection(Vec3 direction) {
		lightDirection = direction;
		
		updateShaders();
	}
	
	public void setPerspectiveView(boolean perspective) {
		perspectiveView = perspective;
		if (perspectiveView) {
			renderer.setZNearClipping(true);
		} else {
			renderer.setZNearClipping(false);
		}
	}
	
	public BufferedImage render() {
		renderer.clearRender();
		renderer.clearScene();
		
		Transformation toCamera = new Transformation();
		
		Vec3 meanPoint = model.getMeanPoint();
		toCamera.translate(-meanPoint.x, -meanPoint.y, -meanPoint.z);
		objectRot.applyTo(toCamera);
		toCamera.scale(objectZoom);
		toCamera.translate(objectPos.x, objectPos.y, objectPos.z);
		
		if (perspectiveView) {
			model.addToScene(renderer, toCamera, perspective);
		} else {
			model.addToScene(renderer, toCamera, orthographic);
		}
		
		return renderer.drawScene();
	}

	public void moveObject(double dX, double dY, int dZ) {
		objectPos.add(dX, -dY, dZ);
		
		if (objectPos.x > model.getRadius() * D_MOVE_MAX) {
			objectPos.x = model.getRadius() * D_MOVE_MAX;
		} else if (objectPos.x < -model.getRadius() * D_MOVE_MAX) {
				objectPos.x = -model.getRadius() * D_MOVE_MAX;
		}
		
		if (objectPos.y > model.getRadius() * D_MOVE_MAX) {
			objectPos.y = model.getRadius() * D_MOVE_MAX;
		} else if (objectPos.y < -model.getRadius() * D_MOVE_MAX) {
				objectPos.y = -model.getRadius() * D_MOVE_MAX;
		}
	}
	
	public void rotateObject(double x, double y, double z) {
		objectRot.rotateOverZ(z);
		objectRot.rotateOverY(y);
		objectRot.rotateOverX(x);
	}
	
	public Model getObjectModel() {
		return model;
	}
	
	public void setObjectModel(Model model) {
		this.model = model;
	}

	public void setObjectPosition(double x, double y, double z) {
		objectPos.x = x;
		objectPos.y = y;
		objectPos.z = z;
	}

	public void setObjectRotation(Transformation t) {
		objectRot = t;
	}
	
	public void setNormalsOrientation(boolean clockwise) {
		renderer.setOrientation(clockwise);
	}
	
	private void updateShaders() {
		uniform		 = new UniformColor(lightColor);
		flatShader   = new FlatLight(lightDirection, lightColor);
		ambdifShader = new AmbDifLight(lightDirection, lightColor, ambientColor, ambientI);
		renderer.setShader(ambdifShader);
	}
}
