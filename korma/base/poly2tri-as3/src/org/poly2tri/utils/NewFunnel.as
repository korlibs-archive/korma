package org.poly2tri.utils {
	import org.poly2tri.Point;

	public class NewFunnel {
		private var _portals:Vector.<Portal>;
		private var _path:Vector.<Point>;
		
		public function get portals():Vector.<Portal> { return _portals; }
		public function get path():Vector.<Point> { return _path; }
		
		static protected function triarea2(a:Point, b:Point, c:Point):Number {
			var ax:Number = b.x - a.x;
			var ay:Number = b.y - a.y;
			var bx:Number = c.x - a.x;
			var by:Number = c.y - a.y;
			return bx * ay - ax * by;
		}
		
		static protected function vdistsqr(a:Point, b:Point):Number {
			var x:Number = b.x - a.x;
			var y:Number = b.y - a.y;
			return Math.sqrt(x * x + y * y);
		}
		
		static protected function vequal(a:Point, b:Point):Boolean {
			return vdistsqr(a, b) < (0.001 * 0.001);
		}
		
		function NewFunnel() {
			this._portals = new Vector.<Portal>();
		}
		
		public function push(p1:Point, p2:Point = null):void {
			if (p2 === null) {
				p2 = p1;
			}
			this._portals.push(new Portal(p1, p2));
			/*if (p2 == p1) {
				trace('channel.push(' + p1 + ');');
			} else {
				trace('channel.push(' + p1 + ', ' + p2 + ');');
			}*/
		}
		
		public function stringPull():Vector.<Point> {
			var pts:Vector.<Point> = new Vector.<Point>();
			// Init scan state
			var portalApex:Point, portalLeft:Point, portalRight:Point;
			var apexIndex:int = 0, leftIndex:int = 0, rightIndex:int = 0;

			portalApex  = _portals[0].left;
			portalLeft  = _portals[0].left;
			portalRight = _portals[0].right;

			// Add start point.
			pts.push(portalApex);

			for (var i:uint = 1; i < _portals.length; i++) {
				var left:Point  = _portals[i].left;
				var right:Point = _portals[i].right;

				// Update right vertex.
				if (triarea2(portalApex, portalRight, right) <= 0.0) {
					if (vequal(portalApex, portalRight) || triarea2(portalApex, portalLeft, right) > 0.0) {
						// Tighten the funnel.
						portalRight = right;
						rightIndex = i;
					} else {
						// Right over left, insert left to path and restart scan from portal left point.
						pts.push(portalLeft);
						// Make current left the new apex.
						portalApex = portalLeft;
						apexIndex = leftIndex;
						// Reset portal
						portalLeft = portalApex;
						portalRight = portalApex;
						leftIndex = apexIndex;
						rightIndex = apexIndex;
						// Restart scan
						i = apexIndex;
						continue;
					}
				}

				// Update left vertex.
				if (triarea2(portalApex, portalLeft, left) >= 0.0) {
					if (vequal(portalApex, portalLeft) || triarea2(portalApex, portalRight, left) < 0.0) {
						// Tighten the funnel.
						portalLeft = left;
						leftIndex = i;
					} else {
						// Left over right, insert right to path and restart scan from portal right point.
						pts.push(portalRight);
						// Make current right the new apex.
						portalApex = portalRight;
						apexIndex = rightIndex;
						// Reset portal
						portalLeft = portalApex;
						portalRight = portalApex;
						leftIndex = apexIndex;
						rightIndex = apexIndex;
						// Restart scan
						i = apexIndex;
						continue;
					}
				}
			}

			if ((pts.length == 0) || (!vequal(pts[pts.length - 1], _portals[_portals.length - 1].left))) {
				// Append last point to path.
				pts.push(_portals[_portals.length - 1].left);
			}

			this._path = pts;
			return pts;
		}
	}

}

import org.poly2tri.Point;

class Portal {
	public var left:Point;
	public var right:Point;
	
	function Portal(left:Point, right:Point) {
		this.left = left;
		this.right = right;
	}
}