package renderer;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import logic3D.Face;
import logic3D.Vec3;
import logic3D.Vec4;
import logic3D.Vertex;

public class Renderer {
	
	public final static double SPACE_CLIP_X_I = -1.0;
	public final static double SPACE_CLIP_X_F =  1.0;
	public final static double SPACE_CLIP_Y_I = -1.0;
	public final static double SPACE_CLIP_Y_F =  1.0;
	public final static double SPACE_CLIP_Z_I = -1.0;
	public final static double SPACE_CLIP_Z_F =  1.0;
	public final static double SPACE_CLIP_WIDTH  = Math.abs(SPACE_CLIP_X_F - SPACE_CLIP_X_I);
	public final static double SPACE_CLIP_HEIGHT = Math.abs(SPACE_CLIP_Y_F - SPACE_CLIP_Y_I);
	public final static double SPACE_CLIP_DEPTH  = Math.abs(SPACE_CLIP_Z_F - SPACE_CLIP_Z_I);
	
	public final static double ZNEAR = -0.1;
	
	public final static int POINT_WIDTH = 3;
	public final static int WIREFRAME_WIDTH = 1;
	public final static int WIREFRAME_COLOR = 0xCCCCCC;
	
	public final static String MODE_WIREFRAME      = "renderer.mode.wireframe";
	public final static String MODE_FILL 	       = "renderer.mode.fill";
	public final static String MODE_FILL_WIREFRAME = "renderer.mode.fillAndWireframe";
	public static final String MODE_NONE 		   = "renderer.mode.none";
	public static final String LIGHTING_FLAT   	   = "renderer.lighting.flat";
	public static final String LIGHTING_PHONG 	   = "renderer.lighting.phong";
	
	
	private BufferedImage viewport;
	private Shader currentShader;
	
	private ArrayList< PainterElement > painterElements = new ArrayList< PainterElement >();
	
	private String currentMode = MODE_FILL;
	private String lightingMode = LIGHTING_PHONG;
	private int pointRadius = POINT_WIDTH;
	private Color wireframeColor = new Color(WIREFRAME_COLOR);
	private Stroke wireframeStroke = new BasicStroke(WIREFRAME_WIDTH);;
	
	private boolean clockwiseOrientation = false;
	private boolean clipZNear = true;
	private boolean backfaceCulling = true;
	
	private Graphics2D sG;
	
	public Renderer() {
		viewport = null;
	}
	
	public Renderer(BufferedImage viewport) {
		this.viewport = viewport;
	}
	
	public static double screenToClipX(int x, int width) {
		return ((double) x / (double) width) * SPACE_CLIP_WIDTH + SPACE_CLIP_X_I;	
	}
	
	public static double screenToClipY(int y, int height) {	
		return ((1.0-(double) y / (double) height)) * SPACE_CLIP_HEIGHT + SPACE_CLIP_Y_I;
	}
	
	public static int clipToScreenX(double x, int width) {		
		return (int) (((x - SPACE_CLIP_X_I) / SPACE_CLIP_WIDTH) * (double) (width));	
	}
	
	public static int clipToScreenY(double y, int height) {	
		return (int) (((1.0-(y - SPACE_CLIP_Y_I) / SPACE_CLIP_HEIGHT)) * (double) (height));
	}
	
	public BufferedImage getViewport() {
		return viewport;
	}
	
	public int getViewportWidth() {
		if (viewport == null) {
			return 0;
		}
		return viewport.getWidth();
	}
	
	public int getViewportHeight() {
		if (viewport == null) {
			return 0;
		}
		return viewport.getHeight();
	}
	
	public String getMode() {
		return currentMode;
	}
	
	public boolean getBackfaceCullingState() {
		return backfaceCulling;
	}
	
	public void setMode(String mode) {
		currentMode = mode;
	}
	
	public void setBackfaceCulling(boolean culling) {
		backfaceCulling = culling;
	}
	
	public void setViewport(BufferedImage viewport) {
		this.viewport = viewport;
		if (sG != null) {
			sG.dispose();
		}
        sG = viewport.createGraphics();
	}
	
	public void setShader(Shader shader) {
		currentShader = shader;
	}

	public void setZNearClipping(boolean clip) {
		clipZNear = clip;
	}
	
	void setPointRadius(int radius) {
		pointRadius = radius;
	}
	
	void setWireframeThickness(int thickness) {
		wireframeStroke = new BasicStroke(thickness);
	}
	
	public void setOrientation(boolean clockWise) {
		clockwiseOrientation = clockWise;
	}
	
	public void addVertexToScene(Vertex v, Transformation toCamera, Transformation cameraToClip) {
		Vec4 vF = new Vec4(v.position, 1.0);
		vF = toCamera.applyTo(vF);
		vF = cameraToClip.applyTo(vF);
		vF = vF.scale(1/vF.w);
		
		PainterElement pE = new PainterElement(new Vertex(vF.x, vF.y, vF.z), vF.z);
		pE.addToList(painterElements);
	}
	
	private ArrayList< Vec4 > sceneVertices = new ArrayList< Vec4 >();
	private ArrayList< Vec3 > sceneVertexNormals = new ArrayList< Vec3 >();
	
	public void setData(ArrayList< Vertex > vertices, ArrayList< Face > faces, Transformation toCamera, Transformation cameraToClip, Transformation normals) {
		sceneVertices.clear();
		sceneVertexNormals.clear();
		
		for (Vertex v : vertices) {
			Vec4 vertex = toCamera.applyTo(new Vec4(v.position, 1.0));
			Vec3 normal = toCamera.applyTo(new Vec3(v.normal));
			
    		if (clockwiseOrientation) {
    			
    		}
    		sceneVertexNormals.add(normal);
    		
    		vertex = cameraToClip.applyTo(vertex);
    		vertex = vertex.scale(1/vertex.w);
    		sceneVertices.add(vertex);
		}
		
		for (Face f : faces) {
			Vec3 faceNormal = normals.applyTo(new Vec3(f.normal));
			if (clockwiseOrientation) {
				faceNormal.negate().normalize();
			}
			Vec3 viewNormal = cameraToClip.applyTo(new Vec3(faceNormal));
			
			if (backfaceCulling && Vec3.dot(viewNormal, new Vec3(0, 0, -1.0)) <= 0) {
				continue;
			}
			
			Face newFace = new Face(f.v1, f.v2, f.v3, faceNormal);
			
			double meanZ = (vertices.get(newFace.v1).position.z + vertices.get(newFace.v2).position.z + vertices.get(newFace.v3).position.z)/3.0;
			
			PainterElement pE = new PainterElement(newFace, meanZ);
			pE.addToList(painterElements);
			/*
			if (clipZNear) {
				if (sceneVertices.get(newFace.v1).z < -1.0) { return; }
				if (sceneVertices.get(newFace.v1).z < -1.0) { return; }
				if (sceneVertices.get(newFace.v1).z < -1.0) { return; }
			}*/
		}
	}
	
	public void clearRender() {
		if (viewport == null) {
			return;
		}
		
        sG.setColor(Color.BLACK);
        sG.fillRect(0, 0, viewport.getWidth(), viewport.getHeight());
	}
	
	public void clearScene() {
		painterElements.clear();
	}
	
	public BufferedImage drawScene() {
		if (viewport == null || currentMode.equals(MODE_NONE)) {
			return null;
		}
		
		for (int i = 0; i < painterElements.size(); i++) {
			painterElements.get(i).getElement().paint(this);
		}
		return viewport;
	}
	
	public BufferedImage drawPoint(Vertex v) {
		if (viewport == null) {
			return null;
		}
		
		int i = clipToScreenX(v.position.x, viewport.getWidth());
		int j = clipToScreenY(v.position.y, viewport.getHeight());
		
		int color = currentShader.shade(null, null, v.normal);
		
		if (i >= 0 && i < viewport.getWidth() && j >= 0 && j < viewport.getHeight()) {
			viewport.setRGB(i, j, color);
		}
		
        sG.setColor(new Color(color));
		sG.drawOval(i - pointRadius, j - pointRadius, pointRadius*2, pointRadius*2);
		
		return viewport;
	}
	
	public BufferedImage drawFace(Face f) {
		if (viewport == null) {
			return null;
		}
		
		switch (currentMode) {
			case MODE_WIREFRAME:
				return drawFaceWireframe(f);
			case MODE_FILL:
				if (lightingMode.equals(LIGHTING_FLAT)) {
					return drawFlatFace(f);
				} else if (lightingMode.equals(LIGHTING_PHONG)) {
					return drawPhongFace(f);
				}
			case MODE_FILL_WIREFRAME:
				return drawFilledAndWireframedFace(f);
			case MODE_NONE:
				return viewport;
		}
		
		return null;
	}
	
	private BufferedImage drawFilledAndWireframedFace(Face f) {
		int[] pI = new int[3];
		int[] pJ = new int[3];
		
		pI[0] = clipToScreenX(sceneVertices.get(f.v1).x, viewport.getWidth());
		pJ[0] = clipToScreenY(sceneVertices.get(f.v1).y, viewport.getHeight());
		pI[1] = clipToScreenX(sceneVertices.get(f.v2).x, viewport.getWidth());
		pJ[1] = clipToScreenY(sceneVertices.get(f.v2).y, viewport.getHeight());
		pI[2] = clipToScreenX(sceneVertices.get(f.v3).x, viewport.getWidth());
		pJ[2] = clipToScreenY(sceneVertices.get(f.v3).y, viewport.getHeight());
		
        sG.setColor(new Color(currentShader.shade(null, null, f.normal)));
        sG.fillPolygon(pI, pJ, 3);
        if (wireframeColor != null) {
        	sG.setColor(wireframeColor);
        }
        sG.setStroke(wireframeStroke);
		sG.drawLine(pI[0], pJ[0], pI[1], pJ[1]);
		sG.drawLine(pI[1], pJ[1], pI[2], pJ[2]);
		sG.drawLine(pI[2], pJ[2], pI[0], pJ[0]);
		
		return viewport;
	}
	
	private BufferedImage drawPhongFace(Face f) {
		int[] pI = new int[3];
		int[] pJ = new int[3];
		
		int w = viewport.getWidth();
		int h = viewport.getHeight();
		
		Vec4 v1 = sceneVertices.get(f.v1);
		Vec4 v2 = sceneVertices.get(f.v2);
		Vec4 v3 = sceneVertices.get(f.v3);
		
		pI[0] = clipToScreenX(v1.x, w);
		pJ[0] = clipToScreenY(v1.y, h);
		pI[1] = clipToScreenX(v2.x, w);
		pJ[1] = clipToScreenY(v2.y, h);
		pI[2] = clipToScreenX(v3.x, w);
		pJ[2] = clipToScreenY(v3.y, h);

		int minX = (pI[0] < pI[1]) ? pI[0] : pI[1];
		if (minX > pI[2]) { minX = pI[2]; }
		if (minX < 0) { minX = 0; }
		
		int maxX = (pI[0] > pI[1]) ? pI[0] : pI[1];
		if (maxX < pI[2]) { maxX = pI[2]; }
		if (maxX >= w) { maxX = w - 1; }
		
		int minY = (pJ[0] < pJ[1]) ? pJ[0] : pJ[1];
		if (minY > pJ[2]) { minY = pJ[2]; }
		if (minY < 0) { minY = 0; }
		
		int maxY = (pJ[0] > pJ[1]) ? pJ[0] : pJ[1];
		if (maxY < pJ[2]) { maxY = pJ[2]; }
		if (maxY >= h) { maxY = h - 1; }

        for (int j = minY; j <= maxY; j++) {
        	for (int i = minX; i <= maxX; i++) {
        		double x = screenToClipX(i, w);
        		double y = screenToClipY(j, h);
        		Vec4 p = new Vec4(x, y, 1.0f, 1.0f);
        		
        		double A, B, C;
        		if (clockwiseOrientation) {
        			A = formedArea(v1, v2, p);
        			B = formedArea(v2, v3, p);
        			C = formedArea(v3, v1, p);
        		} else {
        			A = formedArea(v2, v1, p);
        			B = formedArea(v1, v3, p);
        			C = formedArea(v3, v2, p);
        		}
        		
        		Vec3 normal = f.normal;
        		
                int c = currentShader.shade(null, null, normal);
        		
        		if (A >= 0 && B >= 0 && C >= 0)  {
            		viewport.setRGB(i, j, c);
            	}	
        			
        		
        	}
        }
		
		return viewport;
	}
	
	private double formedArea(Vec4 v1, Vec4 v2, Vec4 v3) {
	    return (v2.x-v1.x)*(v3.y-v1.y) - (v2.y-v1.y)*(v3.x-v1.x);
	}
	
	private BufferedImage drawFlatFace(Face f) {
		int[] pI = new int[3];
		int[] pJ = new int[3];
		
		pI[0] = clipToScreenX(sceneVertices.get(f.v1).x, viewport.getWidth());
		pJ[0] = clipToScreenY(sceneVertices.get(f.v1).y, viewport.getHeight());
		pI[1] = clipToScreenX(sceneVertices.get(f.v2).x, viewport.getWidth());
		pJ[1] = clipToScreenY(sceneVertices.get(f.v2).y, viewport.getHeight());
		pI[2] = clipToScreenX(sceneVertices.get(f.v3).x, viewport.getWidth());
		pJ[2] = clipToScreenY(sceneVertices.get(f.v3).y, viewport.getHeight());

        sG.setColor(new Color(currentShader.shade(null, null, f.normal)));
        sG.fillPolygon(pI, pJ, 3);
		
		return viewport;
	}
	
	private BufferedImage drawFaceWireframe(Face f) {	
		int i1 = clipToScreenX(sceneVertices.get(f.v1).x, viewport.getWidth());
		int j1 = clipToScreenY(sceneVertices.get(f.v1).y, viewport.getHeight());
		int i2 = clipToScreenX(sceneVertices.get(f.v2).x, viewport.getWidth());
		int j2 = clipToScreenY(sceneVertices.get(f.v2).y, viewport.getHeight());
		int i3 = clipToScreenX(sceneVertices.get(f.v3).x, viewport.getWidth());
		int j3 = clipToScreenY(sceneVertices.get(f.v3).y, viewport.getHeight());
		
        sG.setColor(new Color(currentShader.shade(null, null, f.normal)));
        if (wireframeColor != null) {
        	sG.setColor(wireframeColor);
        }
        sG.setStroke(wireframeStroke);
		sG.drawLine(i1, j1, i2, j2);
		sG.drawLine(i2, j2, i3, j3);
		sG.drawLine(i3, j3, i1, j1);
		
		return viewport;
	}
	
}
