package logic3D;

public class Mat4 {
	private double matrix[];
	
	public Mat4(double diagonal) {
		matrix = new double[16];
		
		matrix[0] = diagonal;
		matrix[5] = diagonal;
		matrix[10] = diagonal;
		matrix[15] = diagonal;
	}

	public Mat4(Mat4 mat) {
		matrix = new double[16];
		
		System.arraycopy(mat.matrix, 0, matrix, 0, 16);
	}

	public double get(int row, int column) {
		return matrix[row * 4 + column];
	}
	
	public void set(int row, int column, double value) {
		matrix[row * 4 + column] = value;
	}
	
	public static Vec3 mul3D(Mat4 mat, Vec3 v) {
		Vec3 res = new Vec3();
		
		for (int i = 0; i < 3; i++) {
			double temp = 0;
			
			switch (i) {
				case 0:
					temp = v.x;
				break;
				case 1:
					temp = v.y;
				break;
				case 2:
					temp = v.z;
				break;
			}
			
			res.x += mat.matrix[     i] * temp;
			res.y += mat.matrix[4  + i] * temp;
			res.z += mat.matrix[8  + i] * temp;
		}

		return res;
	}
	
	public static Vec4 mul(Mat4 mat, Vec4 v) {
		Vec4 res = new Vec4();
		
		for (int i = 0; i < 4; i++) {
			double temp = 0;
			
			switch (i) {
				case 0:
					temp = v.x;
				break;
				case 1:
					temp = v.y;
				break;
				case 2:
					temp = v.z;
				break;
				case 3:
					temp = v.w;
				break;
			}
			
			res.x += mat.matrix[     i] * temp;
			res.y += mat.matrix[4  + i] * temp;
			res.z += mat.matrix[8  + i] * temp;
			res.w += mat.matrix[12 + i] * temp;
		}

		return res;
	}
	
	public Mat4 mul(Mat4 mat) {
		double[] res = new double[16];
		double[] m1 = matrix;
		double[] m2 = mat.matrix;

		for (int r = 0; r < 4; r++) {
			for (int c = 0; c < 4; c++) { 
				double sum = 0;

				for (int k = 0; k < 4; k++) {
					double a = m1[r * 4 + k];
					double b = m2[k * 4 + c];
					sum += a * b;
				}

				res[r * 4 + c] = sum;
			}
		}

		System.arraycopy(res, 0, matrix, 0, 16);

		return this;
	}
}
