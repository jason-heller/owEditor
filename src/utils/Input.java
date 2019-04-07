package utils;

import static application.swing.SwingControl.isTyping;

import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import utils.Keys;

import javax.swing.JList;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

import application.swing.SwingControl;
import opengl.GLWindow;

public enum Input {

	INPUT;

	private int[] keys;
	private int[] states;
	private int[] mouse;

	private static int mouseScreenPosX;
	private static int mouseScreenPosY;

	private static float mouseDX, mouseDY;
	private static int mouseDWheel;

	private final int MAX_KEYS = 8;
	private final int PRESSED = 1, HELD = 2, NOT_PRESSED = 3;

	Input() {
		keys = new int[MAX_KEYS];
		states = new int[MAX_KEYS];
		mouse = new int[3];

		KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(new KeyEventDispatcher() {
			@Override
			public boolean dispatchKeyEvent(KeyEvent ke) {
				return onKeyInput(ke.getKeyCode(), ke.getID());
			}
		});
	}
	
	private boolean onKeyInput(int keyCode, int id) {
		synchronized (Input.class) {
			int key = -1;
			
			if (id == KeyEvent.KEY_PRESSED) {
				key = parse(keyCode);
				
				if (key == -1) return false;
				
				int freeInd = -1;
				
				for (int i = 0; i < INPUT.MAX_KEYS; i++) {
					if (freeInd == -1 && keys[i] == 0) {
						freeInd = i;
					}
					
					if (keys[i] == key) {
						return false;
					}
				}
				
				if (freeInd != -1) {
					keys[freeInd] = key;
					states[freeInd] = PRESSED;
				}
			}
			
			if (id == KeyEvent.KEY_RELEASED) {
				key = parse(keyCode);
				
				if (key == -1) return false;
				
				for (int i = 0; i < INPUT.MAX_KEYS; i++) {
					if (keys[i] == key) {
						states[i] = NOT_PRESSED;
						keys[i] = 0;
					}
				}
			}
			return false;
		}
	}
	
	public static int parse(int id) {
		
		boolean isInEditorList = SwingControl.jFrame.getFocusOwner() instanceof JList;
		
		switch (id) {
		case KeyEvent.VK_W: return when(KeyEvent.VK_W, !isTyping);
		case KeyEvent.VK_A: return when(KeyEvent.VK_A, !isTyping);
		case KeyEvent.VK_S: return when(KeyEvent.VK_S, !isTyping);
		case KeyEvent.VK_D: return when(KeyEvent.VK_D, !isTyping);
		
		case KeyEvent.VK_UP: 	return when(KeyEvent.VK_UP, !isInEditorList && !isTyping);
		case KeyEvent.VK_DOWN:	return when(KeyEvent.VK_DOWN, !isInEditorList && !isTyping);
		case KeyEvent.VK_LEFT: 	return when(KeyEvent.VK_LEFT, !isTyping);
		case KeyEvent.VK_RIGHT:	return when(KeyEvent.VK_RIGHT, !isTyping);

		}
		
		return id;
	}

	private static int when(int key, boolean condition) {
		return condition ? key : -1;
	}

	public static void poll() {
		int i = 0;

		int[] states = INPUT.states;
		int[] mouse = INPUT.mouse;

		for (; i < INPUT.MAX_KEYS; i++) {
			if (states[i] == INPUT.PRESSED)
				states[i] = INPUT.HELD;
		}

		i = 0;

		mouseDWheel = Mouse.getDWheel();
		mouseDX = Mouse.getDX();
		mouseDY = Mouse.getDY();

		mouseScreenPosX = (int) (((float) Mouse.getX() / Display.getWidth()) * GLWindow.viewportWidth);
		mouseScreenPosY = (int) (GLWindow.viewportHeight
				- ((float) Mouse.getY() / Display.getHeight()) * GLWindow.viewportHeight);

		for (i = 0; i < 3; i++) {
			if (Mouse.isButtonDown(i)) {
				if (mouse[i] == 0)
					mouse[i] = 1;
				else
					mouse[i] = 2;
			} else {
				if (mouse[i] == 1 || mouse[i] == 2)
					mouse[i] = 3;
				else
					mouse[i] = 0;
			}
		}
		
		// LWJGL Input
		while(Keyboard.next()) {
			int key = Keyboard.getEventKey();
			
			if (Keyboard.getEventKeyState()) {
				INPUT.onKeyInput(Keys.fromLWJGL(key), KeyEvent.KEY_PRESSED);
			}
			else {
				INPUT.onKeyInput(Keys.fromLWJGL(key), KeyEvent.KEY_RELEASED);
			}
		}
	}

	public static int getAny() {
		if (INPUT.keys[0] != 0) {
			return INPUT.keys[0];
		}
		return -1;
	}

	public static boolean isPressed(int key) {
		for (int i = 0; i < INPUT.MAX_KEYS; i++) {
			if (INPUT.keys[i] == key && INPUT.states[i] == INPUT.PRESSED)
				return true;
		}

		return false;
	}

	public static boolean isDown(int key) {
		for (int i = 0; i < INPUT.MAX_KEYS; i++) {
			if (INPUT.keys[i] == key && INPUT.states[i] != INPUT.NOT_PRESSED)
				return true;
		}

		return false;
	}

	public static int keyState(int key) {
		for (int i = 0; i < INPUT.MAX_KEYS; i++) {
			if (INPUT.keys[i] == key)
				return INPUT.states[i];
		}

		return INPUT.NOT_PRESSED;
	}

	public static boolean isMouseDown(int key) {
		return INPUT.mouse[key] == 2;
	}

	public static boolean isMousePressed(int key) {
		return INPUT.mouse[key] == 1;
	}

	public static boolean isMouseReleased(int key) {
		return INPUT.mouse[key] == 3;
	}

	public static List<Integer> getHeldKeys() {
		List<Integer> keys = new ArrayList<Integer>();

		if (Input.isDown(Keyboard.KEY_BACK)) {
			keys.add(Keyboard.KEY_BACK);
			return keys;
		}

		for (int i = 0; i < INPUT.MAX_KEYS; i++) {
			if (INPUT.keys[i] > 0 && INPUT.states[i] == INPUT.PRESSED || INPUT.states[i] == INPUT.HELD)
				keys.add(INPUT.keys[i]);
		}

		return keys;
	}

	public static float getMouseDX() {
		return mouseDX;
	}

	public static float getMouseDY() {
		return mouseDY;
	}

	public static int getMouseDWheel() {
		return mouseDWheel;
	}

	public static int getMouseX() {
		return mouseScreenPosX;
	}

	public static int getMouseY() {
		return mouseScreenPosY;
	}

	public static void clear() {
		for (int i = 0; i < INPUT.MAX_KEYS; i++) {
			INPUT.keys[i] = 0;
			INPUT.states[i] = 0;
		}

		for (int i = 0; i < 3; i++) {
			INPUT.mouse[i] = 0;
		}
	}
}
