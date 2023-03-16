package ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.filechooser.FileFilter;

import logic3D.Vec3;
import modelViewer.Model;
import modelViewer.World;
import renderer.Renderer;
import renderer.Transformation;
import fileFormats.SURMeshFormat;

public class MainWindow extends JFrame implements SelectorListener {

	private static final long serialVersionUID = 7926568576715024627L;

	private static final String S_APP_TITLE                    = "3D Renderer";
	private static final String S_MENU_FILE		               = "File";
	private static final String S_MENU_FILE_OPEN               = "Open model...";
	private static final String S_MENU_FILE_SAVE               = "Save image...";
	private static final String S_MENU_FILE_EXIT               = "Exit";
	private static final String S_MENU_VIEW		               = "View";
	private static final String S_MENU_ROTATE_X		           = "Rotate over X... (right-click)";
	private static final String S_MENU_ROTATE_X_WINDOW         = "Rotate over X";
	private static final String S_MENU_ROTATE_Y		           = "Rotate over Y... (right-click)";
	private static final String S_MENU_ROTATE_Y_WINDOW         = "Rotate over Y";
	private static final String S_MENU_ROTATE_Z		           = "Rotate over Z... (middle-click)";
	private static final String S_MENU_ROTATE_Z_WINDOW         = "Rotate over Z";
	private static final String S_MENU_VIEW_ZOOMIN             = "Zoom In";
	private static final String S_MENU_VIEW_ZOOMOUT            = "Zoom Out";
	private static final String S_MENU_VIEW_RESET              = "Reset";
	private static final String S_MENU_RENDERING               = "Rendering";
	private static final String S_MENU_RENDERING_FILL          = "Fill";
	private static final String S_MENU_RENDERING_WIREFRAME     = "Wireframe";
	private static final String S_MENU_RENDERING_CULLING	   = "Back-face culling";
	private static final String S_MENU_RENDERING_LIGHTS        = "Lights...";
	private static final String S_MENU_RENDERING_LIGHTS_WINDOW = "Lights";
	private static final String S_MENU_RENDERING_NORMALS_CCW   = "Counterclockwise normals";
	private static final String S_MENU_RENDERING_NORMALS_CW    = "Clockwise normals";
	private static final String S_MENU_RENDERING_PROJECTION    = "Projection...";
	private static final String S_MENU_RENDERING_PROJ_WINDOW   = "Projection";
	private static final String S_MENU_HELP	 	               = "Help";
	private static final String S_MENU_HELP_ABOUT              = "About";
	private static final String S_MENU_HELP_ABOUT_TEXT         = "By Leonardo Fernández Esteberena\nFor UNCPBA - Visualización Computacional I\nFrom Argentina";
	private static final String S_PROJECTION_ORTHOGRAPHIC      = "Orthographic";
	private static final String S_PROJECTION_PERSPECTIVE       = "Perspective";
	
	public static final String FIELD_ROTATION_X        = "mainwindow.rotationX";
	public static final String FIELD_ROTATION_Y        = "mainwindow.rotationY";
	public static final String FIELD_ROTATION_Z        = "mainwindow.rotationZ";
	public static final String FIELD_PROJECTION        = "mainwindow.projection";
	public static final String FIELD_PERSPECTIVE_FOV   = "mainwindow.projection.perspective.fov";
	public static final String FIELD_LIGHTS_AMBIENT_I  = "mainwindow.lights.ambient.intensity";
	public static final String FIELD_LIGHTS_AMBIENT_C  = "mainwindow.lights.ambient.color";
	public static final String FIELD_LIGHTS_INTENSITY  = "mainwindow.lights.main.intensity";
	public static final String FIELD_LIGHTS_COLOR      = "mainwindow.lights.main.color";
	public static final String FIELD_LIGHTS_NDIRECTION = "mainwindow.lights.main.nominaldirection";
	public static final String FIELD_LIGHTS_DIRECTION  = "mainwindow.lights.main.direction";
	
	public static final String VALUE_PROJECTION_ORTHO = "mainwindow.projection.orthographic";
	public static final String VALUE_PROJECTION_PERSP = "mainwindow.projection.perspective";
	public static final String VALUE_LDIR_FR  = "Behind-East";
	public static final String VALUE_LDIR_NTR = "Front top right";
	public static final String VALUE_LDIR_TOP = "Top";
	public static final String VALUE_LDIR_VECTOR = "Other:";
	
	public static final Double D_PERSPECTIVE_FOV_MIN  = 5.0;
	public static final Double D_PERSPECTIVE_FOV_MAX  = 85.0;
	public static final Double D_LIGHTS_AMBIENT_I_MIN = 0.0;
	public static final Double D_LIGHTS_AMBIENT_I_MAX = 1.0;
	
	private World world;
	private ScreenPanel worldPanel;
	
	private JToggleButton orthographicButton;
	private JToggleButton perspectiveButton;
	boolean perspectiveProjection = true;
	
	private JToggleButton clockwiseButton;
	private JToggleButton counterClockwiseButton;
	private JCheckBoxMenuItem ccwRenderingItem;
	private JCheckBoxMenuItem cwRenderingItem;
	boolean clockwiseNormals = false;
	private JCheckBoxMenuItem fillRenderingItem;
	private JCheckBoxMenuItem wireframeRenderingItem;
	boolean fill = true;
	boolean wireframe = false;
	private JCheckBoxMenuItem cullingRenderingItem;
	
	boolean nominalLightDirection = true;
	private String lightDirection = VALUE_LDIR_VECTOR;
	
	/*ACTIONS*/
	
	ActionListener openModelFileAction = new ActionListener() {
	      public void actionPerformed(ActionEvent ae) {
		        JFileChooser chooser = new JFileChooser();
		        chooser.setMultiSelectionEnabled(true);
				chooser.setAcceptAllFileFilterUsed(false);
				chooser.addChoosableFileFilter(new FileFilter() {
					public boolean accept(File f) {
					  if (f.isDirectory()) {
						  return true;
					  }
					  
					  String s = f.getName();
					  return s.endsWith(".sur")||s.endsWith(".SUR");
					}
					
					public String getDescription() {
						return "*.sur,*.SUR";
					}
				});
		        int option = chooser.showOpenDialog(MainWindow.this);
		        if (option == JFileChooser.APPROVE_OPTION) {
		          File[] sf = chooser.getSelectedFiles();
		          
		          if (sf.length > 0) {
		        	  openSURFile(sf[0]);
		          }
		        }
	      }
	};
	
	ActionListener saveImageFileAction = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			JFileChooser chooser = new JFileChooser();
			chooser.setAcceptAllFileFilterUsed(false);
			chooser.addChoosableFileFilter(new FileFilter() {
				public boolean accept(File f) {
				  if (f.isDirectory()) {
					  return true;
				  }
				  
				  String s = f.getName();
				  return s.endsWith(".png")||s.endsWith(".PNG");
				}
				
				public String getDescription() {
					return "*.png,*.PNG";
				}
			});
			chooser.setSelectedFile(new File( "./render.png"));
			int actionDialog = chooser.showSaveDialog(MainWindow.this);
			if (actionDialog == JFileChooser.APPROVE_OPTION) {
				File sf = chooser.getSelectedFile();
		          
		        try {
					saveRenderImage(sf);
				} catch (Exception ex) {
					JOptionPane.showMessageDialog(new JFrame(), "Error saving image file", "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		}
	};
	
	ActionListener exitFileAction = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			System.exit(0);
		}
	};
	
	ActionListener zoomInViewAction = new ActionListener() {
	      public void actionPerformed(ActionEvent ae) {
	    	  worldPanel.zoomView(ScreenPanel.D_ZOOM);
	      }
	};
	
	ActionListener rotateXViewAction = new ActionListener() {
	      public void actionPerformed(ActionEvent ae) {
	    	  selectRotationX();
	      }
	};
	
	ActionListener rotateYViewAction = new ActionListener() {
	      public void actionPerformed(ActionEvent ae) {
	    	  selectRotationY();
	      }
	};
	
	ActionListener rotateZViewAction = new ActionListener() {
	      public void actionPerformed(ActionEvent ae) {
	    	  selectRotationZ();
	      }
	};
	
	ActionListener zoomOutViewAction = new ActionListener() {
	      public void actionPerformed(ActionEvent ae) {
	    	  worldPanel.zoomView(-ScreenPanel.D_ZOOM);
	      }
	};
	
	ActionListener resetViewAction = new ActionListener() {
	      public void actionPerformed(ActionEvent ae) {
	    	  worldPanel.resetView();
	      }
	};
	
	ActionListener orthographicRenderingAction = new ActionListener() {
	      public void actionPerformed(ActionEvent ae) {
	    	  perspectiveView(false);
	      }
	};
	
	ActionListener perspectiveRenderingAction = new ActionListener() {
	      public void actionPerformed(ActionEvent ae) {
	    	  perspectiveView(true);
	      }
	};
	
	ActionListener lightsRenderingAction = new ActionListener() {
	      public void actionPerformed(ActionEvent ae) {
	    	  selectLightsOptions();
	      }
	};
	
	ActionListener fillRenderingAction = new ActionListener() {
	      public void actionPerformed(ActionEvent ae) {
	    	  fill = !fill;
	    	  setRenderMode();
	      }
	};
	
	ActionListener wireframeRenderingAction = new ActionListener() {
	      public void actionPerformed(ActionEvent ae) {
	    	  wireframe = !wireframe;
	    	  setRenderMode();
	      }
	};
	
	ActionListener cullingRenderingAction = new ActionListener() {
	      public void actionPerformed(ActionEvent ae) {
	    	  world.setBackfaceCulling(!world.getBackfaceCullingState());
	    	  worldPanel.refreshView();
	      }
	};
	
	ActionListener counterClockwiseRenderingAction = new ActionListener() {
	      public void actionPerformed(ActionEvent ae) {
	    	  clockwiseNormalOrientation(false);
	      }
	};
	
	ActionListener clockwiseRenderingAction = new ActionListener() {
	      public void actionPerformed(ActionEvent ae) {
	    	  clockwiseNormalOrientation(true);
	      }
	};
	
	ActionListener projectionRenderingAction = new ActionListener() {
	      public void actionPerformed(ActionEvent ae) {
	    	  selectProjectionOptions();
	      }
	};
	
	ActionListener aboutAction = new ActionListener() {
	      public void actionPerformed(ActionEvent ae) {
	    	  showAboutDialog();
	      }
	};
	
	private void showAboutDialog() {
		JOptionPane.showMessageDialog(this,
				S_MENU_HELP_ABOUT_TEXT,
				S_MENU_HELP_ABOUT,
			    JOptionPane.DEFAULT_OPTION);
	}

	private void saveRenderImage(File file) throws IOException {
		ImageIO.write(worldPanel.getRenderImage(), "png", file);
	}
	
	private void selectRotationX() {
		setEnabled(false);
		ValuesSelectorWindow angleXWindow = new ValuesSelectorWindow(S_MENU_ROTATE_X_WINDOW);
		
		angleXWindow.addSelector(new AngleSelector(FIELD_ROTATION_X, 35, -360, 360));
		angleXWindow.subscribe(this);
		angleXWindow.displayWindow();
		angleXWindow.requestFocus();
	}
	
	private void selectRotationY() {
		setEnabled(false);
		ValuesSelectorWindow angleYWindow = new ValuesSelectorWindow(S_MENU_ROTATE_Y_WINDOW);
		
		angleYWindow.addSelector(new AngleSelector(FIELD_ROTATION_Y, 35, -360, 360));
		angleYWindow.subscribe(this);
		angleYWindow.displayWindow();
		angleYWindow.requestFocus();
	}
	
	private void selectRotationZ() {
		setEnabled(false);
		ValuesSelectorWindow angleZWindow = new ValuesSelectorWindow(S_MENU_ROTATE_Z_WINDOW);
		
		angleZWindow.addSelector(new AngleSelector(FIELD_ROTATION_Z, 35, -360, 360));
		angleZWindow.subscribe(this);
		angleZWindow.displayWindow();
		angleZWindow.requestFocus();
	}
	
	private void selectProjectionOptions() {
		setEnabled(false);
		ValuesSelectorWindow projectionWindow = new ValuesSelectorWindow(S_MENU_RENDERING_PROJ_WINDOW);
		
		SingleOptionSelector projSelector;
		
		if (perspectiveProjection) {
			projSelector = new SingleOptionSelector(FIELD_PROJECTION, S_PROJECTION_PERSPECTIVE);
		} else {
			projSelector = new SingleOptionSelector(FIELD_PROJECTION, S_PROJECTION_ORTHOGRAPHIC);
		}
		 
		projSelector.addOption(S_PROJECTION_ORTHOGRAPHIC, VALUE_PROJECTION_ORTHO);
		projSelector.addOption(S_PROJECTION_PERSPECTIVE , VALUE_PROJECTION_PERSP);

		projectionWindow.addSelector(projSelector);
		projectionWindow.addSelector(new AngleSelector(FIELD_PERSPECTIVE_FOV, world.getPerspectiveFOV(), D_PERSPECTIVE_FOV_MIN, D_PERSPECTIVE_FOV_MAX, "Perspective field of view:"));
		
		projectionWindow.subscribe(this);
		projectionWindow.displayWindow();
		projectionWindow.requestFocus();
	}
	
	private void selectLightsOptions() {
		setEnabled(false);
		ValuesSelectorWindow lightsWindow = new ValuesSelectorWindow(S_MENU_RENDERING_LIGHTS_WINDOW);
		
		JLabel colorTitle = new JLabel("Light color");
		colorTitle.setAlignmentX(JComponent.CENTER_ALIGNMENT);
		
		Font font = colorTitle.getFont();
		Font boldFont = new Font(font.getFontName(), Font.BOLD, font.getSize());
		
		colorTitle.setFont(boldFont);
		lightsWindow.getContentPane().add(colorTitle);
		
		lightsWindow.addSelector(new ColorSelector(FIELD_LIGHTS_COLOR, world.getLightColor()));
		
		JLabel dirTitle = new JLabel("Light direction");
		dirTitle.setAlignmentX(JComponent.CENTER_ALIGNMENT);
		dirTitle.setFont(boldFont);
		lightsWindow.getContentPane().add(dirTitle);
		
		SingleOptionSelector nldirSelector = new SingleOptionSelector(FIELD_LIGHTS_NDIRECTION, lightDirection);
		nldirSelector.addOption(   VALUE_LDIR_NTR,    VALUE_LDIR_NTR);
		nldirSelector.addOption(   VALUE_LDIR_TOP,    VALUE_LDIR_TOP);
		nldirSelector.addOption(   VALUE_LDIR_FR ,    VALUE_LDIR_FR );
		nldirSelector.addOption(VALUE_LDIR_VECTOR, VALUE_LDIR_VECTOR);
		
		lightsWindow.addSelector(nldirSelector);
		lightsWindow.addSelector(new VectorSelector(FIELD_LIGHTS_DIRECTION, world.getLightDirection()));
		
		JLabel ambientTitle = new JLabel("Ambient light");
		ambientTitle.setAlignmentX(JComponent.CENTER_ALIGNMENT);
		ambientTitle.setFont(boldFont);
		lightsWindow.getContentPane().add(ambientTitle);
		
		lightsWindow.addSelector(new DoubleSelector(FIELD_LIGHTS_AMBIENT_I, world.getAmbientLightIntensity(), D_LIGHTS_AMBIENT_I_MIN, D_LIGHTS_AMBIENT_I_MAX, "Intensity:"));
		lightsWindow.addSelector(new ColorSelector(FIELD_LIGHTS_AMBIENT_C, world.getAmbientLightColor(), "Color:", 15));
		
		lightsWindow.subscribe(this);
		lightsWindow.displayWindow();
		lightsWindow.requestFocus();
	}
	
	private void openSURFile(File file) {
		try {
			RandomAccessFile SURFile;
			SURFile = new RandomAccessFile(file.getPath(), "r");
			Model loadedModel = SURMeshFormat.openFile(SURFile);
			worldPanel.setModel(loadedModel);
			setTitle(S_APP_TITLE + " - " + file.getName());
			SURFile.close();
		} catch (IOException e) {
		    JOptionPane.showMessageDialog(new JFrame(), "Error reading SUR file", "Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	private void setRenderMode() {
		if (fill) {
			if (wireframe) {
				world.setRenderMode(Renderer.MODE_FILL_WIREFRAME);
			} else {
				world.setRenderMode(Renderer.MODE_FILL);
			}
		} else if(wireframe) {
			world.setRenderMode(Renderer.MODE_WIREFRAME);
		} else {
			world.setRenderMode(Renderer.MODE_NONE);
		}
		fillRenderingItem.setSelected(fill);
		wireframeRenderingItem.setSelected(wireframe);
		worldPanel.refreshView();
	}
	
	private void perspectiveView(boolean perspective) {
		perspectiveProjection = perspective;
		orthographicButton.setSelected(!perspectiveProjection);
		perspectiveButton.setSelected(perspectiveProjection);
		world.setPerspectiveView(perspectiveProjection);
		worldPanel.refreshView();
	}
	
	private void clockwiseNormalOrientation(boolean clockwise) {
		clockwiseNormals = clockwise;
		clockwiseButton.setSelected(clockwiseNormals);
		cwRenderingItem.setSelected(clockwiseNormals);
		counterClockwiseButton.setSelected(!clockwiseNormals);
		ccwRenderingItem.setSelected(!clockwiseNormals);
		world.setNormalsOrientation(clockwiseNormals);
		worldPanel.refreshView();
	}
	
	public MainWindow() {
		world = new World();
		worldPanel = new ScreenPanel(world);
		
		world.setZoom(1.0);
		world.setObjectPosition(0.0, 0.0, -world.getObjectModel().getMeanRadius()*2.5);
		world.setScope(world.getObjectModel().getMeanRadius());
		world.setObjectRotation(new Transformation().rotateOverY(20).rotateOverX(15));
		
    	try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException
				| IllegalAccessException
				| UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
	
    	/* MENU BAR */
		
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);	
		
		//File
		JMenu fileMenu = new JMenu(S_MENU_FILE);
		menuBar.add(fileMenu);
		
			JMenuItem openModelFileItem = new JMenuItem(S_MENU_FILE_OPEN);
			fileMenu.add(openModelFileItem);
			openModelFileItem.addActionListener(openModelFileAction);
		
			JMenuItem saveImageFileItem = new JMenuItem(S_MENU_FILE_SAVE);
			fileMenu.add(saveImageFileItem);
			saveImageFileItem.addActionListener(saveImageFileAction);


			fileMenu.addSeparator();
			
			JMenuItem exitFileItem = new JMenuItem(S_MENU_FILE_EXIT);
			fileMenu.add(exitFileItem);
			exitFileItem.addActionListener(exitFileAction);
			
		//View
		JMenu viewMenu = new JMenu(S_MENU_VIEW);
		menuBar.add(viewMenu);
		
			JMenuItem zoomInViewItem = new JMenuItem(S_MENU_VIEW_ZOOMIN);
			viewMenu.add(zoomInViewItem);
			zoomInViewItem.addActionListener(zoomInViewAction);
			zoomInViewItem.setAccelerator(KeyStroke.getKeyStroke("PLUS")); 

			JMenuItem zoomOutViewItem = new JMenuItem(S_MENU_VIEW_ZOOMOUT);
			viewMenu.add(zoomOutViewItem);
			zoomOutViewItem.addActionListener(zoomOutViewAction);
			zoomOutViewItem.setAccelerator(KeyStroke.getKeyStroke("MINUS")); 
			
			viewMenu.addSeparator();
			
			JMenuItem rotateXViewItem = new JMenuItem(S_MENU_ROTATE_X);
			viewMenu.add(rotateXViewItem);
			rotateXViewItem.addActionListener(rotateXViewAction);
			rotateXViewItem.setAccelerator(KeyStroke.getKeyStroke("X"));
			
			JMenuItem rotateYViewItem = new JMenuItem(S_MENU_ROTATE_Y);
			viewMenu.add(rotateYViewItem);
			rotateYViewItem.addActionListener(rotateYViewAction);
			rotateYViewItem.setAccelerator(KeyStroke.getKeyStroke("Y"));
			
			JMenuItem rotateZViewItem = new JMenuItem(S_MENU_ROTATE_Z);
			viewMenu.add(rotateZViewItem);
			rotateZViewItem.addActionListener(rotateZViewAction);
			rotateZViewItem.setAccelerator(KeyStroke.getKeyStroke("Z"));
			
			viewMenu.addSeparator();
			
			JMenuItem resetViewItem = new JMenuItem(S_MENU_VIEW_RESET);
			viewMenu.add(resetViewItem);
			resetViewItem.addActionListener(resetViewAction);
			resetViewItem.setAccelerator(KeyStroke.getKeyStroke("ESCAPE"));
			
		//Rendering
			JMenu renderingMenu = new JMenu(S_MENU_RENDERING);
			menuBar.add(renderingMenu);
				
				fillRenderingItem = new JCheckBoxMenuItem(S_MENU_RENDERING_FILL);
				renderingMenu.add(fillRenderingItem);
				fillRenderingItem.addActionListener(fillRenderingAction);
			
				wireframeRenderingItem = new JCheckBoxMenuItem(S_MENU_RENDERING_WIREFRAME);
				renderingMenu.add(wireframeRenderingItem);
				wireframeRenderingItem.addActionListener(wireframeRenderingAction);
				
				renderingMenu.addSeparator();
				
				cullingRenderingItem = new JCheckBoxMenuItem(S_MENU_RENDERING_CULLING);
				renderingMenu.add(cullingRenderingItem);
				cullingRenderingItem.addActionListener(cullingRenderingAction);
				
				cullingRenderingItem.setSelected(world.getBackfaceCullingState());
				
				renderingMenu.addSeparator();
				
				ccwRenderingItem = new JCheckBoxMenuItem(S_MENU_RENDERING_NORMALS_CCW);
				renderingMenu.add(ccwRenderingItem);
				ccwRenderingItem.addActionListener(counterClockwiseRenderingAction);
			
				cwRenderingItem = new JCheckBoxMenuItem(S_MENU_RENDERING_NORMALS_CW);
				renderingMenu.add(cwRenderingItem);
				cwRenderingItem.addActionListener(clockwiseRenderingAction);
				
				renderingMenu.addSeparator();
				
				JMenuItem projectionRenderingItem = new JMenuItem(S_MENU_RENDERING_PROJECTION);
				renderingMenu.add(projectionRenderingItem);
				projectionRenderingItem.addActionListener(projectionRenderingAction);
				
				JMenuItem lightsRenderingItem = new JMenuItem(S_MENU_RENDERING_LIGHTS);
				renderingMenu.add(lightsRenderingItem);
				lightsRenderingItem.addActionListener(lightsRenderingAction);
				
		//Help
		JMenu helpMenu = new JMenu(S_MENU_HELP);
		menuBar.add(helpMenu);
			
			JMenuItem aboutItem = new JMenuItem(S_MENU_HELP_ABOUT);
			aboutItem.addActionListener(aboutAction);
			helpMenu.add(aboutItem);
		
		/*TOOLBAR*/
		JToolBar toolBar = new JToolBar();
		toolBar.setFloatable(false);
		
		JButton resetViewButton = new JButton(new ImageIcon(getClass().getResource("reset.png")));
		resetViewButton.setToolTipText("Reset view (scape)");
		resetViewButton.addActionListener(resetViewAction);
		toolBar.add(resetViewButton);
		
		toolBar.addSeparator();
		
		orthographicButton = new JToggleButton(new ImageIcon(getClass().getResource("orthographic.png")));
		orthographicButton.setToolTipText("Orthographic view");
		orthographicButton.addActionListener(orthographicRenderingAction);
		toolBar.add(orthographicButton);
		
		perspectiveButton = new JToggleButton(new ImageIcon(getClass().getResource("perspective.png")));
		perspectiveButton.setToolTipText("Perspective view");
		perspectiveButton.addActionListener(perspectiveRenderingAction);
		toolBar.add(perspectiveButton);
		
		toolBar.addSeparator();
		
		counterClockwiseButton = new JToggleButton(new ImageIcon(getClass().getResource("ccw.png")));
		counterClockwiseButton.setToolTipText(S_MENU_RENDERING_NORMALS_CCW);
		counterClockwiseButton.addActionListener(counterClockwiseRenderingAction);
		toolBar.add(counterClockwiseButton);
		
		clockwiseButton = new JToggleButton(new ImageIcon(getClass().getResource("cw.png")));
		clockwiseButton.setToolTipText(S_MENU_RENDERING_NORMALS_CW);
		clockwiseButton.addActionListener(clockwiseRenderingAction);
		toolBar.add(clockwiseButton);
		
		clockwiseNormalOrientation(false);
		
		toolBar.addSeparator();
		
		JButton lightsButton = new JButton(new ImageIcon(getClass().getResource("lights.png")));
		lightsButton.setToolTipText(S_MENU_RENDERING_LIGHTS);
		lightsButton.addActionListener(lightsRenderingAction);
		toolBar.add(lightsButton);
		
		perspectiveView(true);
		setRenderMode();
		
		getContentPane().add(toolBar, BorderLayout.NORTH);
		getContentPane().add(worldPanel, BorderLayout.CENTER);

		setTitle(S_APP_TITLE);
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(true);
		pack();
		
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation(dim.width/2-this.getSize().width/2, dim.height/2-this.getSize().height/2);
		
		setVisible(true);
	}
	
	public static void main(String[] args) {
		new MainWindow();
	}

	@Override
	public void valuesSelected(Object mainSelector, ArrayList<Selector> valueSelectors) {
		Vec3 ldir = world.getLightDirection();
		
		for (Selector s : valueSelectors) {
			switch (s.getField()) {
				case FIELD_ROTATION_X:
					world.rotateObject((double) s.getValue(), 0.0, 0.0);
				break;
				case FIELD_ROTATION_Y:
					world.rotateObject(0.0, (double) s.getValue(), 0.0);
				break;
				case FIELD_ROTATION_Z:
					world.rotateObject(0.0, 0.0, (double) s.getValue());
				break;
				case FIELD_PERSPECTIVE_FOV:
					world.setPerspectiveFOV((double) s.getValue()); 
				break;
				case FIELD_PROJECTION:
					if (s.getValue().equals(VALUE_PROJECTION_ORTHO)) {
						perspectiveView(false);
					} else if (s.getValue().equals(VALUE_PROJECTION_PERSP)) {
						perspectiveView(true);
					}
				break;
				case FIELD_LIGHTS_NDIRECTION:
					switch((String) s.getValue()) {
						case VALUE_LDIR_FR:
							nominalLightDirection = true;
							world.setLightDirection(new Vec3(-1.0, -0.5, 1.0));
						break;
						case VALUE_LDIR_TOP:
							nominalLightDirection = true;
							world.setLightDirection(new Vec3(0.0, -1.0, 0.0));
						break;
						case VALUE_LDIR_NTR:
							nominalLightDirection = true;
							world.setLightDirection(new Vec3(-1.0, -1.0, -1.0));
						break;
						case VALUE_LDIR_VECTOR:
							nominalLightDirection = false;
						break;
					}
					lightDirection = (String) s.getValue();
				break;
				case FIELD_LIGHTS_DIRECTION:
					ldir = (Vec3) s.getValue();
				break;
				case FIELD_LIGHTS_COLOR:
					world.setLightColor((int) s.getValue());
				break;
				case FIELD_LIGHTS_AMBIENT_I:
					world.setAmbientLightIntensity((double) s.getValue());
				break;
				case FIELD_LIGHTS_AMBIENT_C:
					world.setAmbientLightColor((int) s.getValue());
				break;
			}
		}

		if (!nominalLightDirection) {
			world.setLightDirection(ldir);
		}
		
		worldPanel.refreshView();
		setEnabled(true);
		requestFocus();
	}

	@Override
	public void selectorClosed() {
		setEnabled(true);
		requestFocus();
	}

}



