package overhead;

import java.awt.Point;

public class OverheadInstance {
	
	public OverheadItem item;
	private int radius = -1;
	private OverheadInstance next = null, prev = null;
	public Point point;
	
	public OverheadInstance(OverheadItem item, Point point) {
		this.item = item;
		this.point = point;
	}
	
	public OverheadInstance(OverheadItem item, Point point, int radius) {
		this.item = item;
		this.point = point;
		this.radius = radius;
	}
	
	public OverheadInstance(OverheadItem item, Point point, OverheadInstance prev) {
		this.item = item;
		this.point = point;
		this.prev = prev;
		if (prev != null) prev.next = this;
	}
	
	public int getRadius() {
		return radius;
	}
	
	public OverheadInstance getPrev() {
		return prev;
	}
	
	public OverheadInstance getNext() {
		return next;
	}
	
	public String toString() {
		return item.toString();
	}
	
	public boolean hasNext() {
		return (next != null);
	}

	public void detach() {
		if (next == null && prev == null) return;
		
		// End of path
		if (next == null) {
			prev.next = null;
		}
		// Start of path
		else if (prev == null) {
			next.prev = null;
		}
		// In middle of path
		else {
			prev.next = next;
			next.prev = prev;
		}
	}
}
