package org.poly2tri {
	import flash.utils.Dictionary;
	public class Edge {
		public var p:Point;
		public var q:Point;

		/// Constructor
		public function Edge(p1:Point, p2:Point) {
			if (p1 === null) throw(new Error("p1 is null"));
			if (p2 === null) throw(new Error("p2 is null"));
			
			var swap:Boolean = false;
			
			if (p1.y > p2.y) {
				swap = true;
			} else if (p1.y == p2.y) {
				if (p1.x == p2.x) throw(new Error("Repeat points"));

				swap = (p1.x > p2.x);
			} else {
				swap = false;
			}
			
			if (swap) {
				this.q = p1;
				this.p = p2;
			} else {
				this.p = p1;
				this.q = p2;
			}

			this.q.edge_list.push(this);
		}
		
		public function hasPoint(point:Point):Boolean {
			return p.equals(point) || q.equals(point);
		}
		
		static public function getUniquePointsFromEdges(edges:Vector.<Edge>):Vector.<Point> {
			var edge:Edge;
			var point:Point;
			var points:Vector.<Point> = new Vector.<Point>();
			for each (edge in edges) { points.push(edge.p); points.push(edge.q); }
			return Point.getUniqueList(points);
		}
		
		static public function traceList(edges:Vector.<Edge>):void {
			var point:Point;
			var edge:Edge;
			var pointsList:Vector.<Point> = Edge.getUniquePointsFromEdges(edges);
			var pointsMap:Dictionary = new Dictionary();
			
			var points_length:uint = 0;
			for each (point in pointsList) pointsMap[String(point)] = ++points_length;
			
			function getPointName(point:Point):String {
				return "p" + pointsMap[String(point)];
			}
			
			trace("Points:");
			for each (point in pointsList) {
				trace("  " + getPointName(point) + " = " + point);
			}
			trace("Edges:");
			for each (edge in edges) {
				trace("  Edge(" + getPointName(edge.p) + ", " + getPointName(edge.q) + ")");
			}
		}
		
		public function toString():String {
			return "Edge(" + this.p + ", " + this.q + ")";
		}
	}

}