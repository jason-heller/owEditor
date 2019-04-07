package utils;

import static org.lwjgl.input.Keyboard.*;
import static java.awt.event.KeyEvent.*;

public class Keys {
	
	public static int W = VK_W;
	public static int A = VK_A;
	public static int S = VK_S;
	public static int D = VK_D;
	public static int UP = VK_UP;
	public static int DOWN = VK_DOWN;
	public static int LEFT = VK_LEFT;
	public static int RIGHT = VK_RIGHT;
	public static int CTRL = VK_CONTROL;
	public static int SHIFT = VK_SHIFT;
	public static int ALT = VK_ALT;
	public static int X = VK_X;
	public static int Y = VK_Y;
	public static int Z = VK_Z;
	public static int T = VK_T;
	public static int R = VK_R;
	public static int E = VK_E;
	public static int F = VK_F;
	public static int DELETE = VK_DELETE;
	public static int C = VK_C;
	public static int V = VK_V;
	
	
	public static int fromLWJGL(int keyCode) {
		switch(keyCode) {
		case KEY_W: return W;
		case KEY_A: return A;
		case KEY_S: return S;
		case KEY_D: return D;
		case KEY_UP: return UP;
		case KEY_DOWN: return DOWN;
		case KEY_LEFT: return LEFT;
		case KEY_RIGHT: return RIGHT;
		case KEY_LCONTROL:
		case KEY_RCONTROL: return CTRL;
		case KEY_LSHIFT:
		case KEY_RSHIFT: return SHIFT;
		case KEY_LMENU:
		case KEY_RMENU: return ALT;
		case KEY_X: return X;
		case KEY_Y: return Y;
		case KEY_Z: return Z;
		case KEY_T: return T;
		case KEY_R: return R;
		case KEY_E: return E;
		case KEY_F: return F;
		case KEY_C: return C;
		case KEY_V: return V;
		case KEY_DELETE: return DELETE;
		}
		
		return -1;
	}
}
