/*
  Copyright 2011 James Humphreys. All rights reserved.

Redistribution and use in source and binary forms, with or without modification, are
permitted provided that the following conditions are met:

   1. Redistributions of source code must retain the above copyright notice, this list of
      conditions and the following disclaimer.

   2. Redistributions in binary form must reproduce the above copyright notice, this list
      of conditions and the following disclaimer in the documentation and/or other materials
      provided with the distribution.

THIS SOFTWARE IS PROVIDED BY James Humphreys ``AS IS'' AND ANY EXPRESS OR IMPLIED
WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> OR
CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

The views and conclusions contained in the software and documentation are those of the
authors and should not be interpreted as representing official policies, either expressed
or implied, of James Humphreys.
 */

package be.humphreys.simplevoronoi;

import java.util.ArrayList;
import java.util.List;

import org.newdawn.slick.geom.Vector2f;

public class GraphEdge
{
    public double x1, y1, x2, y2;

    public int site1;
    public int site2;

	public int c=0;

	public boolean intersection(int x, int y, int size) {
		List<Vector2f> pts = new ArrayList<Vector2f>();
		int left = x, top = y, right = x + size, bottom = y + size;
		
		Vector2f l = null;
		l = intersectsLine(left,top,left,bottom);
		if (l != null) pts.add(l);
		l = intersectsLine(left,bottom,right,bottom);
		if (l != null) pts.add(l);
		
		l = intersectsLine(left,top,right,top);
		if (l != null) pts.add(l);
		l = intersectsLine(right,top,right,bottom);
		if (l != null) pts.add(l);
		
		if (x1 > left && x1 < right && y1 > top && y1 < bottom) pts.add(new Vector2f(x1));
		if (x2 > left && x2 < right && y2 > top && y2 < bottom) pts.add(new Vector2f(x2));
		
		if (pts.size()>0)return true;
		return false;
	}
	
	public Vector2f intersectsLine(float ox1, float oy1, float ox2, float oy2) {
		Vector2f result = null;

		float s1_x = (float) (x2 - x1), s1_y = (float) (y2 - y1),

				s2_x = ox2 - ox1, s2_y = oy2 - oy1,

				s = (float) ((-s1_y * (x1 - ox1) + s1_x * (y1 - oy1)) / (-s2_x * s1_y + s1_x * s2_y)),
				t = (float) ((s2_x * (y1 - oy1) - s2_y * (x1 - ox1)) / (-s2_x * s1_y + s1_x * s2_y));

		if (s >= 0 && s <= 1 && t >= 0 && t <= 1) {
			// Collision detected
			result = new Vector2f((int) (x1 + (t * s1_x)), (int) (y1 + (t * s1_y)));
		}

		return result;
	}
}
