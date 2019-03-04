package io.format;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;

import assets.Model;

public class ObjFileParser {
	public static Model loadObj(String path, boolean saveData) {
		
		List<float[]> vertices = new ArrayList<float[]>();
		List<Integer> vertexOrder = new ArrayList<Integer>();
		List<float[]> uvs = new ArrayList<float[]>();
		List<float[]> normals = new ArrayList<float[]>();
		
		List<int[]> indices = new ArrayList<int[]>();
		Vector3f max = new Vector3f(-1000000,-1000000,-1000000);
		Vector3f min = new Vector3f(1000000,1000000,1000000);
		
		BufferedReader reader;
		
		Model model = Model.create();
		GL11.glNewList(model.id, GL11.GL_COMPILE);
		GL11.glBegin(GL11.GL_TRIANGLES);
		
		try {
			try {
				InputStreamReader isr = new InputStreamReader(new FileInputStream(path));
				reader = new BufferedReader(isr);
				
			} catch (Exception e) {
				System.err.println("Failed to reader for "+path);
				throw e;
			}
			String line = reader.readLine();
			
			while (line != null) {
				String[] data = line.split(" ");
				
				if (data.length > 2) {
					if (data[0].equals("v")) {
						float[] vertex = new float[] {Float.parseFloat(data[1]), Float.parseFloat(data[2]), Float.parseFloat(data[3])};
						vertices.add(vertex);
						min.x = Math.min(min.x, vertex[0]);
						min.y = Math.min(min.y, vertex[1]);
						min.z = Math.min(min.z, vertex[2]);
						max.x = Math.max(max.x, vertex[0]);
						max.y = Math.max(max.y, vertex[1]);
						max.z = Math.max(max.z, vertex[2]);
					}
					else if (data[0].equals("vt")) {
						float[] uvCoord = new float[] {Float.parseFloat(data[1]), Float.parseFloat(data[2])};
						uvs.add(uvCoord);
					}
					else if (data[0].equals("vn")) {
						float[] normal = new float[] {Float.parseFloat(data[1]), Float.parseFloat(data[2]), Float.parseFloat(data[3])};
						normals.add(normal);
					}
					else if (data[0].equals("f")) {
						if (data.length > 4) { 
							int[][] tempIndices = new int[4][3];
							
							for(byte i = 1; i < data.length; i++) {
								String[] faceData = data[i].split("/");
								int[] index = new int[] {Integer.parseInt(faceData[0])-1, Integer.parseInt(faceData[1])-1, Integer.parseInt(faceData[2])-1};
								tempIndices[i-1] = index;
							}
							
							indices.add(tempIndices[0]);
							indices.add(tempIndices[1]);
							indices.add(tempIndices[3]);
							indices.add(tempIndices[3]);
							indices.add(tempIndices[1]);
							indices.add(tempIndices[2]);
							
							vertexOrder.add(tempIndices[0][0]);
							vertexOrder.add(tempIndices[1][0]);
							vertexOrder.add(tempIndices[3][0]);
							vertexOrder.add(tempIndices[3][0]);
							vertexOrder.add(tempIndices[1][0]);
							vertexOrder.add(tempIndices[2][0]);
							
							GL11.glVertex3f(vertices.get(tempIndices[0][0])[0], vertices.get(tempIndices[0][0])[1], vertices.get(tempIndices[0][0])[2]);
							GL11.glTexCoord2f(uvs.get(tempIndices[0][1])[0], uvs.get(tempIndices[0][1])[1]);
							GL11.glNormal3f(normals.get(tempIndices[0][2])[0], normals.get(tempIndices[0][2])[1], normals.get(tempIndices[0][2])[2]);
							GL11.glVertex3f(vertices.get(tempIndices[1][0])[0], vertices.get(tempIndices[1][0])[1], vertices.get(tempIndices[1][0])[2]);
							GL11.glTexCoord2f(uvs.get(tempIndices[1][1])[0], uvs.get(tempIndices[1][1])[1]);
							GL11.glNormal3f(normals.get(tempIndices[1][2])[0], normals.get(tempIndices[1][2])[1], normals.get(tempIndices[1][2])[2]);
							GL11.glVertex3f(vertices.get(tempIndices[3][0])[0], vertices.get(tempIndices[3][0])[1], vertices.get(tempIndices[3][0])[2]);
							GL11.glTexCoord2f(uvs.get(tempIndices[3][1])[0], uvs.get(tempIndices[3][1])[1]);
							GL11.glNormal3f(normals.get(tempIndices[3][2])[0], normals.get(tempIndices[3][2])[1], normals.get(tempIndices[3][2])[2]);
							GL11.glVertex3f(vertices.get(tempIndices[3][0])[0], vertices.get(tempIndices[3][0])[1], vertices.get(tempIndices[3][0])[2]);
							GL11.glTexCoord2f(uvs.get(tempIndices[3][1])[0], uvs.get(tempIndices[3][1])[1]);
							GL11.glNormal3f(normals.get(tempIndices[3][2])[0], normals.get(tempIndices[3][2])[1], normals.get(tempIndices[3][2])[2]);
							GL11.glVertex3f(vertices.get(tempIndices[1][0])[0], vertices.get(tempIndices[1][0])[1], vertices.get(tempIndices[1][0])[2]);
							GL11.glTexCoord2f(uvs.get(tempIndices[1][1])[0], uvs.get(tempIndices[1][1])[1]);
							GL11.glNormal3f(normals.get(tempIndices[1][2])[0], normals.get(tempIndices[1][2])[1], normals.get(tempIndices[1][2])[2]);
							GL11.glVertex3f(vertices.get(tempIndices[2][0])[0], vertices.get(tempIndices[2][0])[1], vertices.get(tempIndices[2][0])[2]);
							GL11.glTexCoord2f(uvs.get(tempIndices[2][1])[0], uvs.get(tempIndices[2][1])[1]);
							GL11.glNormal3f(normals.get(tempIndices[2][2])[0], normals.get(tempIndices[2][2])[1], normals.get(tempIndices[2][2])[2]);
						}
						else {
							for(byte i = 1; i < data.length; i++) {
								String[] faceData = data[i].split("/");
								int[] index = new int[] {Integer.parseInt(faceData[0])-1, Integer.parseInt(faceData[1])-1, Integer.parseInt(faceData[2])-1};
								indices.add(index);
								vertexOrder.add(index[0]);
								
								float tx = uvs.get(index[1])[0];
								float ty = 1f-uvs.get(index[1])[1];
								GL11.glTexCoord2f(tx, ty);
								GL11.glNormal3f(normals.get(index[2])[0], normals.get(index[2])[1], normals.get(index[2])[2]);
								GL11.glVertex3f(vertices.get(index[0])[0], vertices.get(index[0])[1], vertices.get(index[0])[2]);
							}
						}
					}
				}
				
				line = reader.readLine();
			}
			
			reader.close();
			
			GL11.glEnd();
			GL11.glEndList();
			
			model.setMax(max);
			model.setMin(min);
			model.setVertexData(vertexOrder, vertices);
			
			return model;
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
}
