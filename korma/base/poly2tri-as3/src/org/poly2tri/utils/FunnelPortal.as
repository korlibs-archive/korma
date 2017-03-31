package org.poly2tri.utils {
	import org.poly2tri.Point;

	public class FunnelPortal {
		public var left:Point;
		public var right:Point;
		
		public function FunnelPortal(left:Point, right:Point) {
			this.left = left;
			this.right = right;
		}
		
		public function toString():String {
			return "FunnelPortal(" + left + ", " + right + ")";
		}
	}
}