package opengl;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_CULL_FACE;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_LEQUAL;
import static org.lwjgl.opengl.GL11.GL_NICEST;
import static org.lwjgl.opengl.GL11.GL_PERSPECTIVE_CORRECTION_HINT;
import static org.lwjgl.opengl.GL11.GL_SMOOTH;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glDepthFunc;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glHint;
import static org.lwjgl.opengl.GL11.glLoadIdentity;
import static org.lwjgl.opengl.GL11.glMatrixMode;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glShadeModel;
import static org.lwjgl.opengl.GL11.glViewport;

import java.awt.Canvas;
import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import javax.swing.UIManager;

import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

import application.Globals;
import application.swing.SwingControl;
import heightmap.Heightmap;

public class GLWindow {
	
	public static int viewportWidth = 640;
	public static int viewportHeight = 360;
	public int fps = 60;
	private static boolean isPoppedOut = false;
	public static Canvas parent = null;
	
	private static ByteBuffer buffer;
	private static byte[] asBytes = new byte[] {0,0};
	
	public static Camera camera = new Camera();
	
	/** Sets up openGL Display */
	public void init() {
		try {
			viewportWidth = SwingControl.fullscreenCanvas.getWidth();
			viewportHeight = SwingControl.fullscreenCanvas.getHeight();
			parent = SwingControl.fullscreenCanvas;

			Display.setDisplayMode(new DisplayMode(viewportWidth, viewportHeight));
			Display.create();
			
		}
		catch (LWJGLException e) {
			e.printStackTrace();
			System.exit(0);
		}
		
		glViewport(0, 0, viewportWidth, viewportHeight);
		glMatrixMode(GL11.GL_PROJECTION);
		glLoadIdentity();
		
		GLU.gluPerspective( Camera.FOV, (float)viewportWidth/(float)viewportHeight, Camera.NEAR_PLANE, Camera.FAR_PLANE); 
		glMatrixMode(GL11.GL_MODELVIEW);
		
		glEnable(GL_DEPTH_TEST);
		glDepthFunc(GL_LEQUAL);
		glEnable(GL_CULL_FACE);
		glEnable(GL_TEXTURE_2D);
		glEnable(GL11.GL_LIGHTING);
		glEnable(GL11.GL_LIGHT0);  
		//GL11.glLightModeli(GL11.GL_LIGHT_MODEL_LOCAL_VIEWER, GL11.GL_TRUE); 
		FloatBuffer buff = BufferUtils.createFloatBuffer(4);
		buff.put(0.7f).put(0.7f).put(0.7f).put(1.0f).flip();
		GL11.glLightModel(GL11.GL_LIGHT_MODEL_AMBIENT, buff); 
		glShadeModel (GL_SMOOTH);
		glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST);
		
		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		} catch (Exception e) {
			System.err.println("look and feel failed whatev");
		}
	}
	
	public void step() {
		grabScreen();
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		
		camera.step();
		
		if (isPoppedOut && Display.getParent() != null) {
			popOut();
		} else if (!isPoppedOut && Display.getParent() == null) {
			popIn();
		}
		
		if (!isPoppedOut) {
			if (Display.getParent() != parent) {
				try {
					Display.setParent(parent);
					viewportWidth = parent.getWidth();
					viewportHeight = parent.getHeight();
					glViewport(0, 0, viewportWidth, viewportHeight);
					glMatrixMode(GL11.GL_PROJECTION);
					glLoadIdentity();
					GLU.gluPerspective( Camera.FOV, (float)viewportWidth/(float)viewportHeight, Camera.NEAR_PLANE, Camera.FAR_PLANE);
					glMatrixMode(GL11.GL_MODELVIEW);
				} catch (LWJGLException e) {
					e.printStackTrace();
				}
			} else if (viewportWidth != parent.getWidth() && viewportHeight != parent.getHeight()) {
				viewportWidth = parent.getWidth();
				viewportHeight = parent.getHeight();
				glViewport(0, 0, viewportWidth, viewportHeight);
				glMatrixMode(GL11.GL_PROJECTION);
				glLoadIdentity();
				GLU.gluPerspective( Camera.FOV, (float)viewportWidth/(float)viewportHeight, Camera.NEAR_PLANE, Camera.FAR_PLANE);
				glMatrixMode(GL11.GL_MODELVIEW);
			}
		}
		
		glLoadIdentity();
		GL11.glRotatef(camera.getPitch(), 1, 0, 0);
		GL11.glRotatef(camera.getYaw(), 0, 1, 0);
		GL11.glTranslatef(-camera.getPosition().x, -camera.getPosition().y, -camera.getPosition().z);
		
		Globals.defaultTexture.bind();
		
		render();
		
		Display.update();
		Display.sync(fps);
	}
	
	public static void togglePoppedInOrOut() {
		isPoppedOut = !isPoppedOut;
	}
	
	private static void render() {
		glPushMatrix();
		Heightmap.draw();
		glPopMatrix();
	}

	public static void popOut() {
		try {
			Display.setParent(null);
		} catch (LWJGLException e) {
			e.printStackTrace();
		}
	}
	
	public static void popIn() {
		try {
			Display.setParent(parent);
			
		} catch (LWJGLException e) {
			e.printStackTrace();
		}
	}
	
	public void clean() {
		Display.destroy();
	}
	
	public int getViewportWidth() {
		return viewportWidth;
	}

	public void setViewportWidth(int viewportWidth) {
		GLWindow.viewportWidth = viewportWidth;
	}

	public int getViewportHeight() {
		return viewportHeight;
	}

	public void setViewportHeight(int viewportHeight) {
		GLWindow.viewportHeight = viewportHeight;
	}

	public static boolean isPoppedOut() {
		return isPoppedOut;
	}
	
	private static void grabScreen() {
		if (Display.getParent() == null) return;
		
		int w = Display.getWidth()-2;
		int h = Display.getHeight()-2;
		
		buffer = BufferUtils.createByteBuffer(w * h * 3);
		asBytes = new byte[w * h * 3];
		
		//GL11.glReadPixels(0, 0, w, h, GL11.GL_RGB, GL11.GL_UNSIGNED_BYTE, buffer); //Copy the image to the array imageData

	    buffer.get(asBytes, 0, asBytes.length);
	}
	
	public static byte[] asBytes() {
		return asBytes;
	}

	public static BufferedImage asImage() {
		int w = Display.getWidth()-2;
		int h = Display.getHeight()-2;
		if (asBytes.length <= 4000 || w <= 0 || h <= 0)
			return null;

		DataBuffer buffer = new DataBufferByte(asBytes, w * h);

		int pixelStride = 3; // assuming r, g, b, skip, r, g, b, skip...
		int scanlineStride = 3 * w; // no extra padding
		int[] bandOffsets = { 0, 1, 2 }; // r, g, b
		WritableRaster raster = Raster.createInterleavedRaster(buffer, w, h, scanlineStride, pixelStride, bandOffsets,
				null);

		ColorSpace colorSpace = ColorSpace.getInstance(ColorSpace.CS_sRGB);
		boolean hasAlpha = false;
		boolean isAlphaPremultiplied = true;
		int transparency = Transparency.TRANSLUCENT;
		int transferType = DataBuffer.TYPE_BYTE;
		ColorModel colorModel = new ComponentColorModel(colorSpace, hasAlpha, isAlphaPremultiplied, transparency,
				transferType);

		BufferedImage image = new BufferedImage(colorModel, raster, isAlphaPremultiplied, null);

		AffineTransform flip;
		AffineTransformOp op;
		flip = AffineTransform.getScaleInstance(1, -1);
		flip.translate(0, -image.getHeight());
		op = new AffineTransformOp(flip, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
		image = op.filter(image, null);

		return image;
	}
}
