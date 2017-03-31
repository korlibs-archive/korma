package org.poly2tri {
	//import de.polygonal.ds.PriorityQueue;
	import com.signalsondisplay.datastructs.graphs.Graph;
	import flash.display.Graphics;
	import flash.display.Sprite;
	import com.soywiz.korma.geom.triangle.Edge;
	import org.poly2tri.Point;
	import com.soywiz.korma.geom.triangle.Sweep;
	import com.soywiz.korma.geom.triangle.SweepContext;
	import org.poly2tri.Triangle;

	public class VisiblePolygon {
		protected var sweepContext:SweepContext;
		protected var sweep:Sweep;
		protected var triangulated:Boolean;

		public function VisiblePolygon() {
			this.reset();
			//new PriorityQueue
		}
		
		public function addPolyline(polyline:Vector.<Point>):void {
			if (this.triangulated) throw('Shape already triangulated');
			this.sweepContext.addPolyline(polyline);
		}

		public function addHole(polyline:Vector.<Point>):void {
			addPolyline(polyline);
		}
		
		public function addBox(p1:Point, p2:Point):void {
			var polyline:Vector.<Point> = new Vector.<Point>();
			polyline.push(new Point(p1.x, p1.y));
			polyline.push(new Point(p2.x, p1.y));
			polyline.push(new Point(p2.x, p2.y));
			polyline.push(new Point(p1.x, p2.y));
			addPolyline(polyline);
		}
		
		public function addPolylineString(pStr:String, dx:Number = 0.0, dy:Number = 0.0, pointsSeparator:String = ',', componentSeparator:String = ' '):void {
			addPolyline(parseVectorPoints(pStr, dx, dy, pointsSeparator, componentSeparator));
		}
		
		public function addRectangle(x:Number, y:Number, width:Number, height:Number):void {
			addBox(new Point(x, y), new Point(x + width, y + height));
		}
		
		public function addBoxString(pStr:String):void {
			var polyline:Vector.<Point> = parseVectorPoints(pStr);
			if (polyline.length != 2) throw("Box should contain exactly two points");
			addBox(polyline[0], polyline[1]);
		}

		public function reset():void {
			this.sweepContext = new SweepContext();
			this.sweep = new Sweep(sweepContext);
			this.triangulated = false;
		}
		
		static public function parseVectorPoints(str:String, dx:Number = 0.0, dy:Number = 0.0, pointsSeparator:String = ',', componentSeparator:String = ' '):Vector.<Point> {
			var points:Vector.<Point> = new Vector.<Point>();
			for each (var xy_str:String in str.split(pointsSeparator)) {
				var xyl:Array = xy_str.replace(/^\s+/, '').replace(/\s+$/, '').split(componentSeparator);
				points.push(new Point(parseFloat(xyl[0]) + dx, parseFloat(xyl[1]) + dy));
				//trace(xyl);
			}
			return points;
		}
		
		protected function _triangulateOnce():void {
			if (!this.triangulated) {
				this.triangulated = true;
				this.sweep.triangulate();
			}
		}

		/**
		 * Performs triangulation if it hasn't done yet.
		 *
		 * NOTE: With the current implementation and to retriangulate once it has been already triangulated
		 * you should first call reset() and then add a set of points again.
		 *
		 * @return List of the triangles.
		 */
		public function get triangles():Vector.<Triangle> {
			_triangulateOnce();
			return this.sweepContext.triangles;
		}
		
		public function get edge_list():Vector.<Edge> {
			_triangulateOnce();
			return this.sweepContext.edge_list;
		}
		
		public function getTriangleAtPoint(p:Point):Triangle {
			for each (var triangle:Triangle in triangles) {
				if (triangle.pointInsideTriangle(p)) return triangle;
			}
			return null;
		}
		
		static public function drawTriangleHighlight(t:Triangle, g:Graphics):void {
			var pl:Vector.<Point> = t.points;
			g.lineStyle(0, 0xFFFFFF, 0.4);
			g.beginFill(0xFFFFFF, 0.4);
			{
				g.moveTo(pl[0].x, pl[0].y);
				g.lineTo(pl[1].x, pl[1].y);
				g.lineTo(pl[2].x, pl[2].y);
				g.lineTo(pl[0].x, pl[0].y);
			}
			g.endFill();
		}
		
		public function drawShape(g:Graphics, drawTriangleFill:Boolean = true, drawTriangleEdges:Boolean = true, drawShapeEdges:Boolean = true, drawTriangleCenters:Boolean = true):void {
			var t:Triangle;
			var pl:Vector.<Point>;

			if (drawTriangleFill) {
				for each (t in this.triangles) {
					pl = t.points;
					g.beginFill(0xFF0000);
					{
						g.moveTo(pl[0].x, pl[0].y);
						g.lineTo(pl[1].x, pl[1].y);
						g.lineTo(pl[2].x, pl[2].y);
						g.lineTo(pl[0].x, pl[0].y);
					}
					g.endFill();
				}
			}

			if (drawTriangleEdges) {
				g.lineStyle(1, 0x0000FF, 1);
				for each (t in this.triangles) {
					pl = t.points;
					g.moveTo(pl[0].x, pl[0].y);
					g.lineTo(pl[1].x, pl[1].y);
					g.lineTo(pl[2].x, pl[2].y);
					g.lineTo(pl[0].x, pl[0].y);
				}
			}

			if (drawShapeEdges) {
				g.lineStyle(2, 0x00FF00, 1);
				for each (var e:Edge in this.edge_list) {
					g.moveTo(e.p.x, e.p.y);
					g.lineTo(e.q.x, e.q.y);
				}
			}

			if (drawTriangleCenters) {
				g.lineStyle(2, 0xFFFFFF, 1);
				for each (t in this.triangles) {
					pl = t.points;
					var x:Number = (pl[0].x + pl[1].x + pl[2].x) / 3.0;
					var y:Number = (pl[0].y + pl[1].y + pl[2].y) / 3.0;
					g.moveTo(x - 2, y - 2); g.lineTo(x + 2, y + 2);
					g.moveTo(x - 2, y + 2); g.lineTo(x + 2, y - 2);
					//g.moveTo(x, y); g.drawCircle(x, y, 1);
				}
			}
		}
	}

}