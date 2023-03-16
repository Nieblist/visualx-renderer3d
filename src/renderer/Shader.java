package renderer;

import logic3D.Vec3;

public abstract class Shader {
	public abstract int shade(Vec3 camera, Vec3 position, Vec3 normal);
	
	public static int addColors(int c1, int c2) {
		int r1 = (int) ((c1 >> 16) & 0xFF);
		int g1 = (int) ((c1 >> 8) & 0xFF);
		int b1 = (int) (c1 & 0xFF);
		
		int r2 = (int) ((c2 >> 16) & 0xFF);
		int g2 = (int) ((c2 >> 8) & 0xFF);
		int b2 = (int) (c2 & 0xFF);
		
		r1 += r2; if (r1 > 0xFF) r1 = 0xFF;
		g1 += g2; if (g1 > 0xFF) g1 = 0xFF;
		b1 += b2; if (b1 > 0xFF) b1 = 0xFF;
		
		int res;
		res = 0;
		res += (r1 & 0xFF);
		res = (res << 8) + (g1 & 0xFF);
		res = (res << 8) + (b1 & 0xFF);
		
		return res;
	}
	
	public static int scaleColor(int color, double s) {
		int r = (int) ((color >> 16) & 0xFF);
		int g = (int) ((color >> 8) & 0xFF);
		int b = (int) (color & 0xFF);
		
		r *= s;
		g *= s;
		b *= s;
		
		int res;
		res = 0;
		res += (r & 0xFF);
		res = (res << 8) + (g & 0xFF);
		res = (res << 8) + (b & 0xFF);
		
		return res;
	}
	
	public static int getColor(int r, int g, int b) {		
		int color;
		color = 0;
		color += (r & 0xFF);
		color = (color << 8) + (g & 0xFF);
		color = (color << 8) + (b & 0xFF);
		
		return color;
	}
	
	public static int getR(int color) {
		return (int) ((color >> 16) & 0xFF);
	}
	
	public static int getG(int color) {
		return (int) ((color >> 8) & 0xFF);
	}
	
	public static int getB(int color) {
		return (int) (color & 0xFF);
	}
}
