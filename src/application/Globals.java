package application;

import java.awt.Color;

import org.joml.Vector3f;

import assets.TextureAsset;
import opengl.tex.Texture;

public class Globals {
	public static Tool tool = Tool.NOTOOL;
	public static ToolShape toolShape = ToolShape.CIRCLE;
	public static Texture defaultTexture = null;
	public static int brushWidth = 5;
	public static boolean smoothBrush;
	public static boolean yPlaneVisible = false;
	public static boolean dragTerrain = false;
	public static float yPlane = 0f;
	public static int gridSize = 12;
	public static TextureAsset selectedTexture = null;
	public static Vector3f dragInitHeight = new Vector3f();
	public static String mapName, mapVersion;
	
	public static final Color COLOR_GENERAL_LIGHT = Color.decode("#BB7ABC");
	public static final Color COLOR_GENERAL_DARK = Color.decode("#884489");
}
