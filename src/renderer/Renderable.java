package renderer;

public interface Renderable {
	public void addToScene(Renderer renderer, Transformation toCamera, Transformation cameraToClip);
}
