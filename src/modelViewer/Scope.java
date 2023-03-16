package modelViewer;

public class Scope {
	public double iX, fX, iY, fY, iZ, fZ;
	
	public Scope(double iX, double fX, double iY, double fY, double iZ, double fZ) {
		this.iX = iX;
		this.fX = fX;
		this.iY = iY;
		this.fY = fY;
		this.iZ = iZ;
		this.fZ = fZ;
	}
	
	public Scope(double iX, double fX, double iY, double fY) {
		this.iX = iX;
		this.fX = fX;
		this.iY = iY;
		this.fY = fY;
		this.iZ = 0;
		this.fZ = 0;
	}
	
	public Scope(Scope anotherScope) {
		this.iX = anotherScope.iX;
		this.fX = anotherScope.fX;
		this.iY = anotherScope.iY;
		this.fY = anotherScope.fY;
		this.iZ = anotherScope.iZ;
		this.fZ = anotherScope.fZ;
	}
}
