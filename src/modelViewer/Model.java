package modelViewer;

import java.util.ArrayList;

import logic3D.Face;
import logic3D.Vec3;
import logic3D.Vertex;
import renderer.Renderable;
import renderer.Renderer;
import renderer.Transformation;

public class Model implements Renderable {
	
	protected ArrayList< Vertex > vertices = new ArrayList< Vertex >();
	protected ArrayList< Face > faces = new ArrayList< Face >();
	protected Vec3 meanPoint;
	protected Vec3 centerPoint;
	public Vec3 boundingBoxStart;
	public Vec3 boundingBoxEnd;
	protected double radius;
	protected double meanRadius;
	
	public Model() {
		meanPoint = new Vec3(0.0, 0.0, 0.0);
		centerPoint = new Vec3(0.0, 0.0, 0.0);
		boundingBoxStart = new Vec3(0.0, 0.0, 0.0);
		boundingBoxEnd = new Vec3(0.0, 0.0, 0.0);
		radius = 0;
	}
	
	public double getRadius() {
		return radius;
	}
	
	public void setVertices(ArrayList< Vertex> vertices) {
		this.vertices = vertices;
	}
	
	public void addVertex(Vertex v) {
		vertices.add(v);
	}
	
	public void addFace(Face f) {
		faces.add(f);
	}
	
	public void setBoundingBox(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
		boundingBoxStart.x = minX;
		boundingBoxStart.y = minY;
		boundingBoxStart.z = minZ;
		
		boundingBoxEnd.x = maxX;
		boundingBoxEnd.y = maxY;
		boundingBoxEnd.z = maxZ;
		
		double spanX = Math.abs(maxX - minX);
		double spanY = Math.abs(maxY - minY);
		double spanZ = Math.abs(maxZ - minZ);
		
		centerPoint.x = (maxX - minX)/2;
		centerPoint.y = (maxY - minY)/2;
		centerPoint.z = (maxZ - minZ)/2;
		
		radius = (spanX > spanY) ? spanX : spanY;
		if (radius < spanZ) { radius = spanZ; }
		radius /= 2;
	}
	
	public Vec3 getCenterPoint() {
		return centerPoint;
	}
	
	public Vec3 getMeanPoint() {
		return meanPoint;
	}
	
	public double getMeanRadius() {
		return meanRadius;
	}
	
	public void setMeanRadius(double mean) {
		meanRadius = mean;
	}
	
	public void setMeanPoint(double meanX, double meanY, double meanZ) {
		meanPoint.x = meanX;
		meanPoint.y = meanY;
		meanPoint.z = meanZ;
	}

	@Override
	public void addToScene(Renderer renderer, Transformation toCamera, Transformation cameraToClip) {
		renderer.setData(vertices, faces, toCamera, cameraToClip, toCamera);
	}
}
