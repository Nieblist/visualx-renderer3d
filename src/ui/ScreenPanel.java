package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import logic3D.Vec3;
import modelViewer.Model;
import modelViewer.World;
import renderer.Renderer;
import renderer.Transformation;

public class ScreenPanel extends JPanel implements ComponentListener {

	private static final long serialVersionUID = 5719299850133183903L;
	
	private static final int I_SIZE_DEFAULT_W = 640;
	private static final int I_SIZE_DEFAULT_H = 480;
	
	private static final double D_RZARCH_RADIUS = 0.25;
	
	public static final double D_ZOOM =  1.3;
	
	private BufferedImage screenImage;
	private BufferedImage renderImage;
	
	private World world;
		
	boolean moving, rotating, rotatingZ;
	
	Rectangle selectionRect;
	Point selectionStart, selectionEnd;
	Point clickStart, clickEnd;
	
    public ScreenPanel(World world) {
       this.world = world;
       
       screenImage  = new BufferedImage(I_SIZE_DEFAULT_W, I_SIZE_DEFAULT_H, BufferedImage.TYPE_INT_ARGB);
       renderImage = new BufferedImage(I_SIZE_DEFAULT_W, I_SIZE_DEFAULT_H, BufferedImage.TYPE_INT_ARGB);
       this.world.setViewport(renderImage);
       
       selectionRect  = null;
       selectionStart = new Point();
       selectionEnd   = new Point();
       
       clickStart = new Point();
       clickEnd   = new Point();
       
       moving  = false;
       rotating = false;
       rotatingZ = false;
       
       setLayout(new BorderLayout(0, 0));
       
       KeyListener keyboardActions = new KeyListener() {
			@Override
			public void keyPressed(KeyEvent ke) {
				int key = ke.getKeyCode();
				switch (key) {
					case KeyEvent.VK_ADD:
						zoomView(D_ZOOM);
					break;
					case KeyEvent.VK_SUBTRACT:
						zoomView(-D_ZOOM);
					break;
				}
				
			}
	
			@Override
			public void keyReleased(KeyEvent ke) { }
	
			@Override
			public void keyTyped(KeyEvent ke) { }
    	   
       };
       
       MouseAdapter mouseActions = new MouseAdapter() {
           @Override
           public void mousePressed(MouseEvent me) {
        	   clickStart = me.getPoint();
           }
    	   
           @Override
           public void mouseMoved(MouseEvent me) {
               clickStart = me.getPoint();
           }

           @Override
           public void mouseDragged(MouseEvent me) {
               clickEnd = me.getPoint();
               
               if (clickStart.x < clickEnd.x) {
            	   selectionStart.x = clickStart.x;
            	   selectionEnd  .x =   clickEnd.x;
               } else {
            	   selectionStart.x =   clickEnd.x;
            	   selectionEnd  .x = clickStart.x;
               }
               
               if (clickStart.y < clickEnd.y) {
            	   selectionStart.y = clickStart.y;
            	   selectionEnd  .y =   clickEnd.y;
               } else {
            	   selectionStart.y =   clickEnd.y;
            	   selectionEnd  .y = clickStart.y;
               }
               
               if (!rotating && SwingUtilities.isLeftMouseButton(me)) {
            	   moving = true;
            	   repaint();
               }
               
        	   if (!moving && (SwingUtilities.isRightMouseButton(me) || SwingUtilities.isMiddleMouseButton(me))) {
        		   rotating = true;
        		   if (SwingUtilities.isMiddleMouseButton(me)) {
        			   rotatingZ = true;
        			   
	        		   double sX = Renderer.screenToClipX(clickStart.x,  screenImage.getWidth());
	        		   double sY = Renderer.screenToClipY(clickStart.y, screenImage.getHeight());
	        		   double eX = Renderer.screenToClipX(  clickEnd.x,  screenImage.getWidth());
	        		   double eY = Renderer.screenToClipY(  clickEnd.y, screenImage.getHeight());
	        		      
	        		   rotateZ1 = new Vec3(sX, sY, 0.0);
	        		   rotateZ1 = Vec3.versor(rotateZ1).scale(0.25);
	        		   rotateZ2 = new Vec3(eX, eY, 0.0);
	        		   rotateZ2 = Vec3.versor(rotateZ2).scale(0.25);
        		   } else {
        			   rotatingZ = false;
        		   }
                   repaint();
    	       }
           }
           
           @Override
           public void mouseReleased(MouseEvent me) {
        	   if (moving && SwingUtilities.isLeftMouseButton(me)) {
        		   moving = false;
        		   moveObject(clickStart, clickEnd);
        		   refreshView();
        	   } else if (rotating) {
        		   if (SwingUtilities.isRightMouseButton(me)) {
	        		   rotateXYObject(clickStart, clickEnd);
        		   } else if (rotatingZ) {
        			   rotateZObject(clickStart, clickEnd);
        		   }
        		   rotating = false;
        		   rotatingZ = false;
        		   refreshView();
        	   }
           }
       };
       
       MouseWheelListener mouseWheelActions = new MouseWheelListener() {
    	      public void mouseWheelMoved(MouseWheelEvent we) {
    	    	  int notches = we.getWheelRotation();
    	          if (notches < 0) {
    	        	  zoomView(D_ZOOM);
    	          } else {
    	        	  zoomView(-D_ZOOM);
    	          }
    	      }
    	 };
       
       addKeyListener(keyboardActions);
       addMouseListener(mouseActions);
       addMouseMotionListener(mouseActions);
       addMouseWheelListener(mouseWheelActions);
       
       this.addComponentListener(this);
       
       setFocusable(true);
       requestFocusInWindow();
    }
    
    public void setWorld(World world) {
    	this.world = world;
    	refreshView();
    }
    
	public BufferedImage getRenderImage() {
		return renderImage;
	}
    
    public void zoomView(double d) {
    	if (d < 0) {
    		d = 1/Math.abs(d);
    	}
    	
    	double z = world.getZoom();
    	world.setZoom(z*d);
		refreshView();
	}

	public void resetView() {
		world.setZoom(1.0);
		world.setObjectPosition(0.0, 0.0, -world.getObjectModel().getMeanRadius()*2);
		world.setScope(world.getObjectModel().getMeanRadius());
		world.setObjectRotation(new Transformation());
    	refreshView();
	}
    
	public void moveObject(Point start, Point end) {
		double sX = Renderer.screenToClipX(start.x,  screenImage.getWidth());
		double sY = Renderer.screenToClipY(start.y, screenImage.getHeight());
		double eX = Renderer.screenToClipX(  end.x,  screenImage.getWidth());
		double eY = Renderer.screenToClipY(  end.y, screenImage.getHeight());
		
		double xChange = eX - sX;
		double yChange = eY - sY;
	   
		world.moveObject(xChange*world.getObjectModel().getMeanRadius()*0.5, -yChange*world.getObjectModel().getMeanRadius()*0.5, 0);
    }
	
	public void rotateXYObject(Point start, Point end) {
		double sX = Renderer.screenToClipX(start.x,  screenImage.getWidth());
		double sY = Renderer.screenToClipY(start.y, screenImage.getHeight());
		double eX = Renderer.screenToClipX(  end.x,  screenImage.getWidth());
		double eY = Renderer.screenToClipY(  end.y, screenImage.getHeight());
		
		double xChange = eX - sX;
		double yChange = eY - sY;
	   
	   world.rotateObject(-yChange * 50.0, xChange * 50.0, 0.0);
	}
    
	private Vec3 rotateZ1 = new Vec3(0.0, 0.0, 0.0);
	private Vec3 rotateZ2 = new Vec3(0.0, 0.0, 0.0);
	
	public void rotateZObject(Point start, Point end) {
		Vec3 r1 = Vec3.versor(rotateZ1);
		Vec3 r2 = Vec3.versor(rotateZ2);
		double sign = Vec3.cross(r1, r2).z;
		sign /= Math.abs(sign);
	   
		world.rotateObject(0.0, 0.0, Math.acos(Vec3.dot(r1, r2))*sign*(180/Math.PI));
	}
	
    public void refreshView() {
		world.setViewport(renderImage);
		long startTime = System.nanoTime();
		world.render();
		long estimatedTime = System.nanoTime() - startTime;
		System.out.print("Elapsed time: " + estimatedTime/1000000 + "ms\n");
		repaint();
    }
 
    
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(renderImage.getWidth(), renderImage.getHeight());
    }
    
	@Override
    protected void paintComponent(Graphics g) {
		super.paintComponent(g);   
        Graphics2D sG = screenImage.createGraphics();
        sG.drawImage(renderImage, 0, 0, null);
        if (moving || rotating) {
        	
        	if (rotatingZ) {
        		int cX = Renderer.clipToScreenX(0,  getWidth());
        		int cY = Renderer.clipToScreenY(0, getHeight());
        		
        		double aspect = (double) getHeight() / (double) getWidth();
        		
        		int v1X = Renderer.clipToScreenX(rotateZ1.x * aspect,  getWidth());
        		int v1Y = Renderer.clipToScreenY(rotateZ1.y		    , getHeight());
        		int v2X = Renderer.clipToScreenX(rotateZ2.x * aspect,  getWidth());
        		int v2Y = Renderer.clipToScreenY(rotateZ2.y		    , getHeight());
        		
        		int x0 = Renderer.clipToScreenX(-D_RZARCH_RADIUS * aspect,  getWidth());
        		int y0 = Renderer.clipToScreenY( D_RZARCH_RADIUS		 , getHeight());
        		int x1 = Renderer.clipToScreenX( D_RZARCH_RADIUS * aspect,  getWidth());
        		int y1 = Renderer.clipToScreenY(-D_RZARCH_RADIUS	     , getHeight());
        		
        		int width  = Math.abs(x1 - x0);
        		int height = Math.abs(y1 - y0);        		
        		
        		sG.setColor(Color.RED);
            	sG.drawLine(cX, cY, v2X, v2Y);
        		
        		sG.setColor(Color.BLUE);
        		sG.drawOval(x0, y0, width, height);
        		sG.drawLine(cX, cY, v1X, v1Y);
        		
        	} else {
        		sG.setColor(Color.RED);
        		sG.drawLine(clickStart.x, clickStart.y, clickEnd.x, clickEnd.y);
        	}
        }
        sG.dispose();
        g.drawImage(screenImage, 0, 0, null);
    }

	@Override
	public void componentResized(ComponentEvent c) {
		if (getWidth() <= 0 || getHeight() <= 0) {
			return;
		}
    	screenImage = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
		renderImage = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
		refreshView();
	}

	@Override
	public void componentHidden(ComponentEvent c) { }

	@Override
	public void componentMoved(ComponentEvent c) { }

	@Override
	public void componentShown(ComponentEvent c) { }

	public void setModel(Model model) {
		world.setObjectModel(model);
		resetView();
		refreshView();
	}

}