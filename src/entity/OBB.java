package entity;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import console.Console;

public class OBB {
	public Vector3f position;
	public Vector3f X;
	public Vector3f Y;
	public Vector3f Z;
	public Vector3f bounds;
	
	public PlacedEntity entity;
	
	public OBB(PlacedEntity entity, Vector3f bounds) {
		this.entity = entity;
		this.position = entity.position;
		this.X = new Vector3f(1,0,0);
		this.Y = new Vector3f(0,1,0);
		this.Z = new Vector3f(0,0,1);
		this.bounds = bounds;
	}
	
	public Vector3f checkForSnap(OBB box) {
		Vector4f axis = new Vector4f(0, 0, 0, 10000);
		
		if ((axis = axisTest(axis, box.X, box)) == null)
			return null;
		if ((axis = axisTest(axis, box.Y, box)) == null)
			return null;
		if ((axis = axisTest(axis, box.Z, box)) == null)
			return null;

		float l = (float)Math.sqrt(axis.x*axis.x + axis.y*axis.y + axis.z*axis.z);
		return new Vector3f(axis.x / l, axis.y / l, axis.z / l);
	}
	
	public Vector3f intersection(OBB box) {
		Vector4f axis = new Vector4f(0, 0, 0, 10000);
		
		if ((axis = axisTest(axis, X, box)) == null)
			return null;
		if ((axis = axisTest(axis, Y, box)) == null)
			return null;
		if ((axis = axisTest(axis, Z, box)) == null)
			return null;
		if ((axis = axisTest(axis, box.X, box)) == null)
			return null;
		if ((axis = axisTest(axis, box.Y, box)) == null)
			return null;
		if ((axis = axisTest(axis, box.Z, box)) == null)
			return null;
		if ((axis = axisTest(axis, Vector3f.cross(X, box.X), box)) == null)
			return null;
		if ((axis = axisTest(axis, Vector3f.cross(X, box.Y), box)) == null)
			return null;
		if ((axis = axisTest(axis, Vector3f.cross(X, box.Z), box)) == null)
			return null;
		if ((axis = axisTest(axis, Vector3f.cross(Y, box.X), box)) == null)
			return null;
		if ((axis = axisTest(axis, Vector3f.cross(Y, box.Y), box)) == null)
			return null;
		if ((axis = axisTest(axis, Vector3f.cross(Y, box.Z), box)) == null)
			return null;
		if ((axis = axisTest(axis, Vector3f.cross(Z, box.X), box)) == null)
			return null;
		if ((axis = axisTest(axis, Vector3f.cross(Z, box.Y), box)) == null)
			return null;
		if ((axis = axisTest(axis, Vector3f.cross(Z, box.Z), box)) == null)
			return null;

		/*float l = (float)Math.sqrt(axis.x*axis.x + axis.y*axis.y + axis.z*axis.z);
		axis.x /= l;
		axis.y /= l;
		axis.z /= l;*/
		//axis.w = (float)Math.sqrt(axis.w);
		return new Vector3f(axis.x, axis.y, axis.z);
	}
	
	private Vector4f axisTest(Vector4f overlapAxis, Vector3f axis, OBB other) {
		if (axis.isZero())
			return overlapAxis;
		
		float dot, rad;

		// Calculate extremes for this OBB
		dot = Vector3f.dot(axis, position);
		rad = 	  (Math.abs(Vector3f.dot(axis, X)) * bounds.x*entity.scale)
				+ (Math.abs(Vector3f.dot(axis, Y)) * bounds.y*entity.scale)
				+ (Math.abs(Vector3f.dot(axis, Z)) * bounds.z*entity.scale);

		float selfMin = dot - rad;
		float selfMax = dot + rad;

		// Calculate extremes for other OBB
		dot = Vector3f.dot(axis, other.position);
		rad =	  (Math.abs(Vector3f.dot(axis, other.X)) * other.bounds.x*other.entity.scale)
				+ (Math.abs(Vector3f.dot(axis, other.Y)) * other.bounds.y*other.entity.scale)
				+ (Math.abs(Vector3f.dot(axis, other.Z)) * other.bounds.z*other.entity.scale);

		float otherMin = dot - rad;
		float otherMax = dot + rad;

		if (selfMin > otherMax || selfMax < otherMin) {
			// Axis is not overlapping
			return null;
		}

		// Axis overlaps

		float d0 = selfMax - otherMin;
		float d1 = otherMax - selfMin;

		float overlap = (d0 < d1) ? -d0 : d1;
		float axisLengthSquared = axis.x * axis.x + axis.y * axis.y + axis.z * axis.z;
		float overlapSquared = (overlap * overlap) / axisLengthSquared;

		if (overlapSquared < overlapAxis.w) {
			overlapAxis.w = overlapSquared;
			Vector3f xyz = Vector3f.mul(axis, (overlap / axisLengthSquared));
			overlapAxis.x = xyz.x;
			overlapAxis.y = xyz.y;
			overlapAxis.z = xyz.z;
		}

		return overlapAxis;
	}
	
	public void setRotation(Vector3f rotation) {
		this.setRotation(rotation.x, rotation.y, rotation.z);
	}

	public void setRotation(float rx, float ry, float rz) {
		Matrix4f matrix = new Matrix4f();

		matrix.rotateZ(rz);
		matrix.rotateY(ry);
		matrix.rotateX(rx);

		X.set(Vector3f.X_AXIS);
		Y.set(Vector3f.Y_AXIS);
		Z.set(Vector3f.Z_AXIS);

		matrix.transform(X);
		matrix.transform(Y);
		matrix.transform(Z);
	}
}
