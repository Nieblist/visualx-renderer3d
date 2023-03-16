package renderer;

import java.util.ArrayList;

import logic3D.Mat4;
import logic3D.Vec3;
import logic3D.Vec4;

public class Transformation {

	Mat4 currentMatrix;
	ArrayList< Mat4 > tStack;
	
	public Transformation() {
		tStack = new ArrayList< Mat4 >();
		currentMatrix = new Mat4(1.0);
	}
	
	public Vec3 applyTo(Vec3 v) {
		return Mat4.mul3D(currentMatrix, v);
	}

	public Vec4 applyTo(Vec4 v) {
		return Mat4.mul(currentMatrix, v);
	}
	
	public void applyTo(Transformation t) {
		Mat4 res = new Mat4(currentMatrix);
		t.currentMatrix = res.mul(t.currentMatrix);
	}
	
	public void clear() {
		tStack.clear();
		currentMatrix = new Mat4(1.0);
	}
	
	public void push() {
		tStack.add(new Mat4(currentMatrix));
	}
	
	public Mat4 pop() {
		Mat4 res = currentMatrix;
		if (tStack.size() > 0) {
			currentMatrix = tStack.get(tStack.size() - 1);
		}
		return res;
	}

	public Mat4 top() {
		return tStack.get(tStack.size() - 1);
	}
	
	public void setMatrix(Mat4 mat) {
		currentMatrix = mat;
	}
	
	public void setIndentity() {
		currentMatrix = new Mat4(1.0);
	}
	
	public Transformation scale(double s) {
		Mat4 t = new Mat4(1.0);

		//Matrix
		t.set(0, 0, s); /* 		   0 */ /*		   0 */ /* 0 */
		/*		   0 */ t.set(1, 1, s); /*		   0 */ /* 0 */
		/* 		   0 */ /* 		   0 */ t.set(2, 2, s); /* 0 */
		
		/* 		   0 */ /*		   0 */ /*		   0 */ /* 1 */
		currentMatrix = t.mul(currentMatrix);
		
		return this;
	}
	
	public Transformation translate(double x, double y, double z) {
		Mat4 t = new Mat4(1.0);

		//Matrix
		/* 1 */ /* 0 */ /* 0 */ t.set(0, 3, x);
		/* 0 */ /* 1 */ /* 0 */ t.set(1, 3, y);
		/* 0 */ /* 0 */ /* 1 */ t.set(2, 3, z);
		/* 0 */ /* 0 */ /* 0 */ /*	       1 */

		currentMatrix = t.mul(currentMatrix);
		
		return this;
	}
	
	public Transformation rotateOverX(double angle) {
		double angRad = (float) Math.toRadians(angle);
		double cos = (float) Math.cos(angRad);
		double sin = (float) Math.sin(angRad);

		Mat4 t = new Mat4(1.0);

		//Matrix
		/* 1 */ /*           0 */ /*            0 */ /* 0 */
		/* 0 */ t.set(1, 1, cos); t.set(1, 2, -sin); /* 0 */
		/* 0 */ t.set(2, 1, sin); t.set(2, 2,  cos); /* 0 */
		/* 0 */ /*           0 */ /*            0 */ /* 1 */

		currentMatrix = t.mul(currentMatrix);
		
		return this;
	}
	
	public Transformation rotateOverY(double angle) {
		double angRad = (float) Math.toRadians(angle);
		double cos = (float) Math.cos(angRad);
		double sin = (float) Math.sin(angRad);

		Mat4 t = new Mat4(1.0);

		//Matrix
		t.set(0, 0,  cos); /* 0 */ t.set(0, 2, sin); /* 0 */
		/*            0 */ /* 1 */ /*           0 */ /* 0 */
		t.set(2, 0, -sin); /* 0 */ t.set(2, 2, cos); /* 0 */
		/*            0 */ /* 0 */ /*           0 */ /* 1 */
		
		currentMatrix = t.mul(currentMatrix);
		
		return this;
	}
	
	public Transformation rotateOverZ(double angle) {
		double angRad = (float) Math.toRadians(angle);
		double cos = (float) Math.cos(angRad);
		double sin = (float) Math.sin(angRad);

		Mat4 t = new Mat4(1.0);

		//Matrix
		t.set(0, 0, cos); t.set(0, 1, -sin); /* 0 */ /* 0 */
		t.set(1, 0, sin); t.set(1, 1,  cos); /* 0 */ /* 0 */  
		/*           0 */ /*            0 */ /* 1 */ /* 0 */
		/*           0 */ /*            0 */ /* 0 */ /* 1 */
		
		currentMatrix = t.mul(currentMatrix);
		
		return this;
	}
	
	public Transformation projectOrthographic(double side, double aspect, double iZ, double fZ) {
		double range = side/2;
		
		double iX = -range * aspect;
		double fX =  range * aspect;
		double iY = -range;
		double fY =  range;
		
		Mat4 t = new Mat4(1.0);
		
		//Matrix
		t.set(0,0,2/(fX-iX)); /*               0 */ /*                0 */ t.set(0,3,-(fX+iX)/(fX-iX));
		/*               0 */ t.set(1,1,2/(fY-iY)); /*                0 */ t.set(1,3,-(fY+iY)/(fY-iY));
		/*               0 */ /*               0 */ t.set(2,2,-2/(fZ-iZ)); t.set(2,3,-(fZ+iZ)/(fZ-iZ));
		/*               0 */ /*               0 */ /*                0 */ /*                      1 */

		currentMatrix = t.mul(currentMatrix);
		
		return this;
	}
	
	public Transformation projectPerspective(double fovY, double aspect, double iZ, double fZ) {
		double range = Math.tan(Math.toRadians(fovY / 2.0f)) * iZ;
		
		double iX = -range * aspect;
		double fX =  range * aspect;
		double iY = -range;
		double fY =  range;
		
		Mat4 t = new Mat4(0.0);
		
		//Matrix
		t.set(0,0,(2.0*iZ)/(fX-iX)); /*               	     0 */ /*               	      0 */ /*                 	       0 */
		/*        			    0 */ t.set(1,1,(2.0*iZ)/(fY-iY)); /*                 	  0 */ /*                     	   0 */
		/*         				0 */ /*         			 0 */ t.set(2,2,-(fZ+iZ)/(fZ-iZ)); t.set(2,3,-(2.0*fZ*iZ)/(fZ-iZ));
		/*                 		0 */ /*                		 0 */ t.set(3,2,		    -1.0); /*                	       0 */
																					
		currentMatrix = t.mul(currentMatrix);
		
		return this;
	}

}
