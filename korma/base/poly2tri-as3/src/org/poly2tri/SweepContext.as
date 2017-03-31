package org.poly2tri {
	import adobe.utils.CustomActions;
	import com.adobe.utils.DictionaryUtil;
	import flash.utils.Dictionary;

	public class SweepContext {
		public var triangles:Vector.<Triangle>;
		public var points:Vector.<Point>;
		public var edge_list:Vector.<Edge>;
		
		public var map:Dictionary;
		
		public var front:AdvancingFront;
		public var head:Point;
		public var tail:Point;
		
		public var af_head:Node;
		public var af_middle:Node;
		public var af_tail:Node;
		
		public var basin:Basin = new Basin();
		public var edge_event:EdgeEvent = new EdgeEvent();
		
		public function SweepContext(polyline:Vector.<Point> = null) {
			this.triangles = new Vector.<Triangle>();
			this.points = new Vector.<Point>();
			this.edge_list = new Vector.<Edge>();
			this.map = new Dictionary();
			
			this.addPolyline(polyline);
		}
		
		protected function addPoints(points:Vector.<Point>):void {
			for each (var point:Point in points) this.points.push(point);
		}
		
		public function addPolyline(polyline:Vector.<Point>):void {
			if (polyline == null) return;
			this.initEdges(polyline);
			this.addPoints(polyline);
		}

		/**
		 * An alias of addPolyline.
		 *
		 * @param	polyline
		 */
		public function addHole(polyline:Vector.<Point>):void {
			addPolyline(polyline);
		}

		protected function initEdges(polyline:Vector.<Point>):void {
			for (var n:uint = 0; n < polyline.length; n++) {
				this.edge_list.push(new Edge(polyline[n], polyline[(n + 1) % polyline.length]));
			}
		}

		public function addToMap(triangle:Triangle):void {
			this.map[String(triangle)] = triangle;
		}

		public function initTriangulation():void {
			var xmin:Number = this.points[0].x, xmax:Number = this.points[0].x;
			var ymin:Number = this.points[0].y, ymax:Number = this.points[0].y;
			
			// Calculate bounds
			for each (var p:Point in this.points) {
				if (p.x > xmax) xmax = p.x;
				if (p.x < xmin) xmin = p.x;
				if (p.y > ymax) ymax = p.y;
				if (p.y < ymin) ymin = p.y;
			}

			var dx:Number = Constants.kAlpha * (xmax - xmin);
			var dy:Number = Constants.kAlpha * (ymax - ymin);
			this.head = new Point(xmax + dx, ymin - dy);
			this.tail = new Point(xmin - dy, ymin - dy);

			// Sort points along y-axis
			Point.sortPoints(this.points);
			//throw(new Error("@TODO Implement 'Sort points along y-axis' @see class SweepContext"));
		}
		
		public function locateNode(point:Point):Node {
			return this.front.locateNode(point.x);
		}

		public function createAdvancingFront():void {
			// Initial triangle
			var triangle:Triangle = new Triangle(this.points[0], this.tail, this.head);

			addToMap(triangle);

			var head:Node    = new Node(triangle.points[1], triangle);
			var middle:Node  = new Node(triangle.points[0], triangle);
			var tail:Node    = new Node(triangle.points[2]);

			this.front  = new AdvancingFront(head, tail);

			head.next   = middle;
			middle.next = tail;
			middle.prev = head;
			tail.prev   = middle;
		}

		public function removeNode(node:Node):void {
			// do nothing
		}

		public function mapTriangleToNodes(triangle:Triangle):void {
			for (var n:uint = 0; n < 3; n++) {
				if (triangle.neighbors[n] == null) {
					var neighbor:Node = this.front.locatePoint(triangle.pointCW(triangle.points[n]));
					if (neighbor != null) neighbor.triangle = triangle;
				}
			}
		}
		
		public function removeFromMap(triangle:Triangle):void {
			delete this.map[String(triangle)];
		}

		public function meshClean(triangle:Triangle, level:int = 0):void {
			if (level == 0) {
				//for each (var mappedTriangle:Triangle in this.map) trace(mappedTriangle);
			}
			if (triangle == null || triangle.interior) return;
			triangle.interior = true;
			this.triangles.push(triangle);
			for (var n:uint = 0; n < 3; n++) {
				if (!triangle.constrained_edge[n]) {
					this.meshClean(triangle.neighbors[n], level + 1);
				}
			}
		}
	}
}