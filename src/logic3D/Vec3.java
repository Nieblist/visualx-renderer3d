package logic3D;

public class Vec3 {
	public double x, y, z;
	
	//CONSTRUCTORS
	
	public Vec3() {
		x = 0;
		y = 0;
		z = 0;
	}
	
	public Vec3(Vec3 v) {
		this.x = v.x;
		this.y = v.y;
		this.z = v.z;
	}
	
	public Vec3(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	//OPERATIONS
	
	public Vec3 add(Vec3 v) {
		x += v.x;
		y += v.y;
		z += v.z;
		
		return this;
	}
	
	public Vec3 add(double x, double y, double z) {
		this.x += x;
		this.y += y;
		this.z += z;
		
		return this;
	}
	
	public Vec3 add3D(Vec4 v) {
		x += v.x;
		y += v.y;
		z += v.z;
		
		return this;
	}
	
	public Vec3 sub(Vec3 v) {
		x -= v.x;
		y -= v.y;
		z -= v.z;
		
		return this;
	}
	
	public Vec3 sub3D(Vec4 v) {
		x -= v.x;
		y -= v.y;
		z -= v.z;
		
		return this;
	}
	
	public Vec3 mul(Vec3 v) {
		x *= v.x;
		y *= v.y;
		z *= v.z;
		
		return this;
	}
	
	public Vec3 scale(double s) {
		x *= s;
		y *= s;
		z *= s;
		
		return this;
	}
	
	public Vec3 negate() {
		x = -x;
		y = -y;
		z = -z;

		return this;
	}
	
	public Vec3 normalize() {
		double norm = norm();
		
		x = x/norm;
		y = y/norm;
		z = z/norm;
		
		return this;
	}

	public double norm() {
		return Math.sqrt(x*x + y*y + z*z);
	}
	
	/**STATIC OPERATIONS**/
	
	public static Vec3 add(Vec3 v1, Vec3 v2) {
		Vec3 result = new Vec3(v1);
		
		return result.add(v2);
	}
	
	public static Vec3 sub(Vec3 v1, Vec3 v2) {
		Vec3 result = new Vec3(v1);
		
		return result.sub(v2);
	}
	
	public static Vec3 add3D(Vec4 v1, Vec4 v2) {
		Vec3 result = new Vec3(v1.x, v1.y, v1.z);
		
		return result.add3D(v2);
	}
	
	public static Vec3 sub3D(Vec4 v1, Vec4 v2) {
		Vec3 result = new Vec3(v1.x, v1.y, v1.z);
		
		return result.sub3D(v2);
	}
	
	public static Vec3 mul(Vec3 v1, Vec3 v2) {	
		Vec3 res = new Vec3(v1);
		
		return res.mul(v2);
	}
	
	public static Vec3 scale(Vec3 v, double s) {
		Vec3 res = new Vec3(v);
		
		return res.scale(s);
	}
	
	public static Vec3 negate(Vec3 v) {
		Vec3 res = new Vec3(v);
		
		return res.negate();
	}
	
	public static Vec3 cross(Vec3 v1, Vec3 v2) {
		return new Vec3(v1.y*v2.z - v1.z*v2.y, 
                  		v1.z*v2.x - v1.x*v2.z, 
                  		v1.x*v2.y - v1.y*v2.x);
	}
	
	public static double dot(Vec3 v1, Vec3 v2) {
		return v1.x*v2.x + v1.y * v2.y + v1.z * v2.z;
	}

	public static Vec3 versor(Vec3 v) {
		Vec3 result = new Vec3(v);
		
		return result.normalize();
	}
}
