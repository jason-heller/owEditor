package application;

import java.awt.Color;

import org.joml.Vector3f;

import assets.TextureAsset;
import opengl.tex.Texture;

public class Globals {
	public static Tool tool = Tool.ENTITYTOOL;
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
	public static boolean showUtilities = false;
	
	public static boolean gridSnap = false;
	public static boolean objectSnap = false;
	public static boolean objectPlacementCollision = false;
	
	public static final Color COLOR_GENERAL_LIGHT = Color.decode("#BB7ABC");
	public static final Color COLOR_GENERAL_DARK  = Color.decode("#884489");
	public static final Color COLOR_DEV			  = Color.decode("#803fa3");
	public static final Color COLOR_DONT_USE	  = Color.decode("#ce0606");
}
