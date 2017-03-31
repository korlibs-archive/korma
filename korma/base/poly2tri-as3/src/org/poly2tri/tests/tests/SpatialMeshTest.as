package org.poly2tri.tests.tests {
	import asunit.framework.TestCase;
	import com.adobe.utils.StringUtil;
	import org.poly2tri.Point;
	import org.poly2tri.Triangle;
	import org.poly2tri.utils.PathFind;
	import org.poly2tri.utils.SpatialMesh;

	public class SpatialMeshTest extends TestCase {
		public var points:Vector.<Point>;
		public var triangles:Vector.<Triangle>;
		
		protected function addPoint(info:String):void {
			var result:Object = /^p(\d+):\s*(-?\d+)\s*,\s*(-?\d+)$/ig.exec(info);
			var pointId:int = parseInt(result[1]);
			var point:Point = new Point(parseInt(result[2]), parseInt(result[3]));
			if (points.length < pointId + 1) points.length = pointId + 1;
			points[pointId] = point;
		}
		
		protected function addTriangle(info:String):void {
			var result:Object = /^t(\d+):\s*p(\d+)\s*,\s*p(\d+)\s*,\s*p(\d+)$/ig.exec(info);
			var triangleId:int = parseInt(result[1]);
			var p1:Point = points[parseInt(result[1 + 1])];
			var p2:Point = points[parseInt(result[1 + 2])];
			var p3:Point = points[parseInt(result[1 + 3])];
			var triangle:Triangle = new Triangle(p1, p2, p3, true, false);
			if (triangles.length < triangleId + 1) triangles.length = triangleId + 1;
			triangles[triangleId] = triangle;
			//trace(triangleId);
		}
		
		protected function addNeighbors(info:String):void {
			var tt:Array = info.replace(/[t|\s]+/gi, '').split(':');
			var triangleId:int = parseInt(tt[0]);
			var neighborIds:Array = tt[1].split(',').map(function(v:*, index:int, arr:Array):int { return parseInt(v); });
			var n:int = 0;
			for each (var neighborId:int in neighborIds) {
				triangles[triangleId].neighbors[n++] = triangles[neighborId];
			}
			
		}
		
		override protected function setUp():void {
			points    = new Vector.<Point>();
			triangles = new Vector.<Triangle>();

			addPoint('p1: 1,  0');
			addPoint('p2: 2,  3');
			addPoint('p3: 3,  1');
			addPoint('p4: 4,  3');
			addPoint('p5: 2, -1');
			addPoint('p6: 4,  4');
			addPoint('p7: 0,  2');
			
			addTriangle('t1: p1, p2, p3');
			addTriangle('t2: p2, p3, p4');
			addTriangle('t3: p1, p3, p5');
			addTriangle('t4: p2, p6, p4');
			addTriangle('t5: p7, p2, p1');

			addNeighbors('t1: t2, t3, t5');
			addNeighbors('t2: t1, t4');
			addNeighbors('t3: t1');
			addNeighbors('t4: t2');
			addNeighbors('t5: t1');
		}
		
		public function testTest():void {
			var spatialMesh:SpatialMesh = SpatialMesh.fromTriangles(triangles.slice(1, triangles.length));
			assertTrue("SpatialMesh(SpatialNode(2, 1),SpatialNode(3, 2),SpatialNode(2, 0),SpatialNode(3, 3),SpatialNode(1, 1))", spatialMesh.toString());
		}
		
	}

}