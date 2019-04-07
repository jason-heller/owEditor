package application.swing;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyVetoException;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.OverlayLayout;
import javax.swing.UIManager;

import application.Globals;
import application.Profile;
import application.swing.menu.EditorMenuBar;
import assets.Entity;
import assets.Model;
import assets.TextureAsset;
import console.Console;
import opengl.GLWindow;
import overhead.OverheadControl;
import overhead.OverheadFrame;

public class SwingControl {
	public static JFrame jFrame;
	
	public static JPanel canvasPane;
	public static CanvasFrame glCanvas;
	private static OverheadControl overheadControl;
	public static OverheadFrame overheadCanvas;
	
	private static EditorToolbar toolbar;
	private static EditorButtonsTop btnsTop;
	private static EditorList assetLists;
	private static EditorMenuBar menuBar;
	
	public static boolean isTyping = false;
	
	private static int viewWidth, viewHeight;
	
	public static void init(int width, int height) {
		jFrame = new JFrame();
		
		viewWidth = width;
		viewHeight = height;
		
		try {
			initViews();
			overheadControl = new OverheadControl(overheadCanvas);
			
			frameColors();

			//jFrame.setSize(width, height + 130);
			jFrame.setTitle("Map Editor V2.0");
			jFrame.setVisible(true);
			
			btnsTop = new EditorButtonsTop();
			final EditorCmdBox cmdBox = new EditorCmdBox();
			menuBar = new EditorMenuBar();
			assetLists = new EditorList();
			toolbar = new EditorToolbar(cmdBox);

			jFrame.add(btnsTop, BorderLayout.NORTH);
			jFrame.add(toolbar, BorderLayout.WEST);
			jFrame.add(cmdBox, BorderLayout.SOUTH);
			jFrame.setJMenuBar(menuBar);
			jFrame.add(assetLists, BorderLayout.EAST);
			jFrame.getContentPane().setBackground(Color.DARK_GRAY);
			jFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			jFrame.setSize(width, height);

			jFrame.setExtendedState(Frame.MAXIMIZED_BOTH);
			
			jFrame.addWindowListener(new WindowAdapter() {
				@Override
				public void windowClosing(WindowEvent e) {
					Console.send("quit");
				}
			});
			
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static void frameColors() {
		/*UIManager.put( "control", new Color(120, 120, 120) );
		//UIManager.put( "nimbusBlueGrey", new Color(131, 99, 147) );
		UIManager.put( "nimbusBase", new Color(100, 100, 100) );
		UIManager.put( "nimbusLightBackground", new Color( 77, 77, 77) );
		UIManager.put("InternalFrame.activeTitleForeground", Globals.COLOR_GENERAL_LIGHT);
		UIManager.put("InternalFrame.activeTitleBackground", Globals.COLOR_GENERAL_LIGHT);
		UIManager.put("InternalFrame.inactiveTitleBackground", Globals.COLOR_GENERAL_DARK);
		UIManager.put("InternalFrame.inactiveTitleForeground", Globals.COLOR_GENERAL_DARK);
		UIManager.put( "text", new Color( 230, 230, 230) );
		UIManager.put( "nimbusDisabledText", new Color( 128, 128, 128) );
		UIManager.put( "nimbusSelectedText", new Color( 255, 255, 255) );
		UIManager.put( "info", new Color(100, 100, 100) );
		  try {
		    for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
		      if ("Nimbus".equals(info.getName())) {
		          UIManager.setLookAndFeel(info.getClassName());
		          break;
		      }
		    }
		  } catch (ClassNotFoundException e) {
		    e.printStackTrace();
		  } catch (InstantiationException e) {
		    e.printStackTrace();
		  } catch (IllegalAccessException e) {
		    e.printStackTrace();
		  } catch (javax.swing.UnsupportedLookAndFeelException e) {
		    e.printStackTrace();
		  } catch (Exception e) {
		    e.printStackTrace();
		  }*/
	}

	private static void initViews() {
		Dimension dimension = new Dimension(viewWidth, viewHeight);
		canvasPane = new JPanel();
		canvasPane.setPreferredSize(dimension);
		//canvasPanel.setLayout(new GridLayout(1,0));
		canvasPane.setLayout(new OverlayLayout(canvasPane));
		
		jFrame.add(canvasPane, BorderLayout.CENTER);
		
		/*
		int dWidth = canvasPanel.getWidth()/2;
		int dHeight = canvasPanel.getHeight()/2;
		int dx = canvasPanel.getX();
		int dy = canvasPanel.getY();
		*/
		
		canvasPane.revalidate();
		
		canvasPane.setLayout(new GridLayout(1,2));
		
		glCanvas = new CanvasFrame("3D Camera");
		overheadCanvas = new OverheadFrame("Overhead Camera");
		
		GLWindow.requestRefresh();
		
		canvasPane.add(glCanvas);
	}

	public static void populateAssetList() {
		assetLists.models.removeAll();
		assetLists.clear();
		for(Model model : Profile.models) {
			assetLists.add(model);
		}
		
		assetLists.textures.removeAll();
		for(TextureAsset texture : Profile.textures) {
			assetLists.add(texture);
		}
		
		assetLists.entities.removeAll();
		for(Entity entity : Profile.entities) {
			assetLists.add(entity);
		}
	}
	
	public static EditorList getAssetList() {
		return assetLists;
	}
	
	public static void updateSwing() {
		// NOTE: Here we need a workaround for the HW/LW Mixing feature to work
		// correctly due to yet unfixed bug 6852592.
		// The JTextField is a validate root, so the revalidate() method called
		// during the setText() operation does not validate the parent of the
		// component. Therefore, we need to force validating its parent here.
		Container parent = toolbar.getParent();
		if (parent instanceof JComponent) {
			((JComponent) parent).revalidate();
		}
		parent = btnsTop.getParent();
		if (parent instanceof JComponent) {
			((JComponent) parent).revalidate();
		}
		/*
		 * // ... and just in case, call validate() on the top-level window as well
		 * Window window = SwingUtilities.getWindowAncestor(toolbar); if (window !=
		 * null) { window.validate(); }
		 */
	}

	private static void copyCanvas(Canvas src, Canvas dest) {
		//dest.getGraphics().setColor(Color.WHITE);
		Image buffer = src.createImage(src.getWidth(), src.getHeight());
		dest.getGraphics().drawImage(buffer, 0, 0, dest.getWidth(), dest.getHeight(), jFrame);
		//dest.getGraphics().setColor(Color.RED);
		//dest.getGraphics().fillRect(0, 0, 500, 500);
	}

	public static void terminate() {
		jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		try {
			glCanvas.setClosed(true);
			overheadCanvas.setClosed(true);
		} catch (PropertyVetoException e) {
			e.printStackTrace();
		}
		
	}
	
	public static void glInit() {
		overheadCanvas.setup();
	}
	
	public static OverheadControl getOverheadControl() {
		return overheadControl;
	}
}
