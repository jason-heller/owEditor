package utils;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import entity.PlacedEntity;

public class MathUtils {
	public static float barycentric(float x, float y, Vector3f p1, Vector3f p2, Vector3f p3) {
		float det = (p2.z - p3.z) * (p1.x - p3.x) + (p3.x - p2.x) * (p1.z - p3.z);
		float l1 = ((p2.z - p3.z) * (x - p3.x) + (p3.x - p2.x) * (y - p3.z)) / det;
		float l2 = ((p3.z - p1.z) * (x - p3.x) + (p1.x - p3.x) * (y - p3.z)) / det;
		float l3 = 1.0f - l1 - l2;
		return l1 * p1.y + l2 * p2.y + l3 * p3.y;
	}

	public static Vector3f getDirection(Matrix4f matrix) {
		Matrix4f inverse = new Matrix4f();
		matrix.invert(inverse);
		
		return new Vector3f(inverse.m20, inverse.m21, inverse.m22);
	}

	public static boolean entityBroadphaseIntersection(Vector3f orig, Vector3f dir, PlacedEntity e) {
		Vector3f aabbPos = e.position;
		Vector3f max = Vector3f.mul(e.getModel().getMax(), e.scale);
		Vector3f min = Vector3f.mul(e.getModel().getMin(), e.scale);
		
		Vector3f invDir = new Vector3f(1.0f / dir.x, 1.0f / dir.y, 1.0f / dir.z);
		Vector3f tOrig = Vector3f.sub(orig, aabbPos);
		
		float t1 = (min.x - tOrig.x)*invDir.x;
		float t2 = (max.x - tOrig.x)*invDir.x;
		float t3 = (min.y - tOrig.y)*invDir.y;
		float t4 = (max.y - tOrig.y)*invDir.y;
		float t5 = (min.z - tOrig.z)*invDir.z;
		float t6 = (max.z - tOrig.z)*invDir.z;

		float tmin = Math.max(Math.max(Math.min(t1, t2), Math.min(t3, t4)), Math.min(t5, t6));
		float tmax = Math.min(Math.min(Math.max(t1, t2), Math.max(t3, t4)), Math.max(t5, t6));

		if (tmax < 0) return false;
		if (tmin > tmax) return false;
		
		return true;
	}

	public static boolean rayIntersectsMesh(Vector3f orig, Vector3f dir, Vector3f meshPos, Vector3f rot, float scale, float[] v) {
		Matrix4f transform = new Matrix4f();
		transform.translate(meshPos);
		transform.rotateX(rot.x);
		transform.rotateY(rot.y);
		transform.rotateZ(rot.z);
		transform.scale(scale);
		
		for(int i = 0; i < v.length; i += 9) {
			Vector3f p1 = new Vector3f(v[i+0], v[i+1], v[i+2]);
			Vector3f p2 = new Vector3f(v[i+3], v[i+4], v[i+5]);
			Vector3f p3 = new Vector3f(v[i+6], v[i+7], v[i+8]);
			//p1.sub(meshPos);
			//p2.sub(meshPos);
			//p3.sub(meshPos);
			p1.mul(transform);
			p2.mul(transform);
			p3.mul(transform);
			if (rayTriIntersection(orig, dir, p1, p2, p3) != Float.POSITIVE_INFINITY) {
				return true;
			}
		}
		
		return false;
	}
	
	public static float rayMeshIntersection(Vector3f orig, Vector3f dir, Vector3f meshPos, Vector3f rot, float scale, float[] v) {
		Matrix4f transform = new Matrix4f();
		transform.translate(meshPos);
		transform.rotateX(rot.x);
		transform.rotateY(rot.y);
		transform.rotateZ(rot.z);
		transform.scale(scale);
		
		float dist = Float.POSITIVE_INFINITY;
		
		for(int i = 0; i < v.length; i += 9) {
			Vector3f p1 = new Vector3f(v[i+0], v[i+1], v[i+2]);
			Vector3f p2 = new Vector3f(v[i+3], v[i+4], v[i+5]);
			Vector3f p3 = new Vector3f(v[i+6], v[i+7], v[i+8]);
			//p1.sub(meshPos);
			//p2.sub(meshPos);
			//p3.sub(meshPos);
			p1.mul(transform);
			p2.mul(transform);
			p3.mul(transform);
			float d = rayTriIntersection(orig, dir, p1, p2, p3);
			if (d < dist) {
				dist = d;
			}
		}
		
		return dist;
	}
	
	public static float rayTriIntersection(Vector3f origin, Vector3f normal, Vector3f p1, Vector3f p2, Vector3f p3) {
		Vector3f    u, v, n;
        Vector3f    dir, w0;
        float     r, a, b;
        
        u = new Vector3f(p2);
        u.sub(p1);
        v = new Vector3f(p3);
        v.sub(p1);
        n = Vector3f.cross(u, v);
        
        if (n.length() == 0) {
            return Float.POSITIVE_INFINITY;
        }
        
        dir = new Vector3f(normal);
        w0 = new Vector3f(origin);
        w0.sub(p1);
        a = -(new Vector3f(n).dot(w0));
        b = new Vector3f(n).dot(dir);
        
        if ((float)Math.abs(b) < .0001f) {
            return Float.POSITIVE_INFINITY;
        }
        
        r = a / b;
        if (r < 0.0) {
            return Float.POSITIVE_INFINITY;
        }
        
        return r;
    }

	public static Vector3f rayPlaneIntersectPt(Vector3f origin, Vector3f direction, float dist, float nx, float ny, float nz) {
		float normalDotRayDir = nx*direction.x + ny*direction.y + nz*direction.z;
		
		if (normalDotRayDir == 0.0f) {
	        return null;
	    }

	    float t = (dist - (nx*origin.x + ny*origin.y + nz*origin.z)) / normalDotRayDir;
	    return Vector3f.add(origin,direction.mul(t));
	}

	public static long cantorReal(Integer x, Integer y) {
		long n = cantor(x, y);
		
		if (n >= 0) {
			return 2 * n;
		} else {
			return -2 * 2 - 1;
		}
	}

	public static long cantor(Integer x, Integer y) {
		return (((long)(x + y) * (x + y + 1))/2) + y;
	}
}
