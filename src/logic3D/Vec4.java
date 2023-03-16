package logic3D;

public class Vec4 {
	public double x, y, z, w;
	
	//CONSTRUCTORS
	
	public Vec4() {
		x = 0;
		y = 0;
		z = 0;
		w = 0;
	}
	
	public Vec4(Vec3 v, double w) {
		this.x = v.x;
		this.y = v.y;
		this.z = v.z;
		this.w =   w;
	}
	
	public Vec4(Vec4 v) {
		this.x = v.x;
		this.y = v.y;
		this.z = v.z;
		this.w = v.w;
	}
	
	public Vec4(double x, double y, double z, double w) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
	}

	//OPERATIONS
	
	public Vec4 add(Vec4 v) {
		x += v.x;
		y += v.y;
		z += v.z;
		w += v.w;
		
		return this;
	}
	
	public Vec4 sub(Vec4 v) {
		x -= v.x;
		y -= v.y;
		z -= v.z;
		w -= v.w;
		
		return this;
	}
	
	public Vec4 mul(Vec4 v) {
		x *= v.x;
		y *= v.y;
		z *= v.z;
		w *= v.w;
		
		return this;
	}
	
	public Vec4 scale(double s) {
		x *= s;
		y *= s;
		z *= s;
		w *= s;
		
		return this;
	}
	
	public Vec4 negate() {
		x = -x;
		y = -y;
		z = -z;
		w = -w;

		return this;
	}
	
	public Vec4 normalize() {
		double norm = norm();
		
		x = x/norm;
		y = y/norm;
		z = z/norm;
		w = w/norm;
		
		return this;
	}

	public double norm() {
		return Math.sqrt(x*x + y*y + z*z + w*w);
	}
	
	/**STATIC OPERATIONS**/
	
	public static Vec4 add(Vec4 v1, Vec4 v2) {
		Vec4 result = new Vec4(v1);
		
		return result.add(v2);
	}
	
	public static Vec4 sub(Vec4 v1, Vec4 v2) {
		Vec4 result = new Vec4(v1);
		
		return result.sub(v2);
	}
	
	public static Vec4 mul(Vec4 v1, Vec4 v2) {	
		Vec4 res = new Vec4(v1);
		
		return res.mul(v2);
	}
	
	public static Vec4 scale(Vec4 v, double s) {
		Vec4 res = new Vec4(v);
		
		return res.scale(s);
	}
	
	public static Vec4 negate(Vec4 v) {
		Vec4 res = new Vec4(v);
		
		return res.negate();
	}
	
	public static Vec4 cross3D(Vec4 v1, Vec4 v2) {
	    Vec3 vA = new Vec3(v1.x, v1.y, v1.z);
	    Vec3 vB = new Vec3(v2.x, v2.y, v2.z);
	    Vec3 result = Vec3.cross(vA, vB);
	    
	    return new Vec4(result.x, result.y, result.z, 1.0);
	}
	
	public static double dot(Vec4 v1, Vec4 v2) {
		return v1.x*v2.x + v1.y * v2.y + v1.z * v2.z + v1.w * v2.w;
	}

	public static Vec4 versor(Vec4 v) {
		Vec4 result = new Vec4(v);
		
		return result.normalize();
	}
}
