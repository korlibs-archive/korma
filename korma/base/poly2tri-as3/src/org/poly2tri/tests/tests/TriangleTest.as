package org.poly2tri.tests.tests {
	import asunit.framework.TestCase;
	import asunit.framework.TestMethod;
	import org.poly2tri.*;

	public class TriangleTest extends TestCase {
		protected var p1:Point, p2:Point, p3:Point, p4:Point, p5:Point, p6:Point;
		protected var pInside:Point, pOutside:Point;
		protected var t1:Triangle, t2:Triangle, t3:Triangle, t4:Triangle;
		
		/**
		 *    p5
		 *     |\
		 *     | \
		 *     |   \ p4
		 *   p3|____\____.p6
		 *    / \ t2|   /
		 *   / t1\  |  /
		 *  /_____\.|/
		 * p1      p2
		 *
		 */
  		protected override function setUp():void {
			p1 = new Point(-1,  0);
			p2 = new Point(+1,  0);
			p3 = new Point( 0, +1);
			p4 = new Point(+1, +1);
			p5 = new Point( 0, +2);
			p6 = new Point(+2, +1);
			pInside = new Point(0.3, 0.3);
			pOutside = new Point(+1, +1);
			t1 = new Triangle(p1, p2, p3, true); // CCW
			t2 = new Triangle(p3, p4, p2, true); // CW
			t3 = new Triangle(p4, p5, p3, true); // CCW
			t4 = new Triangle(p2, p4, p6, true); // CW
   		}

  		public function testInstantiated():void {
   			assertTrue(t1 is Triangle);
   		}
		
		public function testContainsPoint():void {
			assertTrue(t1.containsPoint(p1));
			assertTrue(t1.containsPoint(p2));
			assertTrue(t1.containsPoint(p3));
			assertFalse(t1.containsPoint(pInside));
			assertFalse(t1.containsPoint(pOutside));
		}
		
		public function testContainsEdge():void {
			assertTrue(t1.containsEdge(new Edge(p1, p2)));
			assertTrue(t1.containsEdge(new Edge(p2, p3)));
			assertTrue(t1.containsEdge(new Edge(p3, p1)));
			assertFalse(t1.containsEdge(new Edge(pInside, pOutside)));
			assertFalse(t1.containsEdge(new Edge(p1, pOutside)));
			assertFalse(t1.containsEdge(new Edge(pInside, p3)));
		}
		
		public function testMarkNeighbor():void {
			t1.markNeighborTriangle(t2);
			assertTrue(t1.neighbors[0] == t2 || t1.neighbors[1] == t2 || t1.neighbors[2] == t2);
			assertTrue(t2.neighbors[0] == t1 || t2.neighbors[1] == t1 || t2.neighbors[2] == t1);

			t1.markNeighborTriangle(t3);
			assertTrue(t1.neighbors[0] != t3 && t1.neighbors[1] != t3 && t1.neighbors[2] != t3);
		}
		
		public function testGetPointIndexOffset():void {
			// CCW
			assertEquals(0, t1.getPointIndexOffset(p1));
			assertEquals(1, t1.getPointIndexOffset(p2));
			assertEquals(2, t1.getPointIndexOffset(p3));

			assertEquals(2, t1.getPointIndexOffset(p1, -1));
			assertEquals(0, t1.getPointIndexOffset(p2, -1));
			assertEquals(1, t1.getPointIndexOffset(p3, -1));

			assertEquals(1, t1.getPointIndexOffset(p1, +1));
			assertEquals(2, t1.getPointIndexOffset(p2, +1));
			assertEquals(0, t1.getPointIndexOffset(p3, +1));
		}
		
		public function testPointCW():void {
			assertTrue(t1.pointCW(p1).equals(p3));
			assertTrue(t1.pointCCW(p1).equals(p2));
			assertTrue(t1.pointCW(p2).equals(p1));
			
			assertTrue(t2.pointCW(p3).equals(p4));
		}
		
		public function testNeighborCW():void {
			assertEquals(null, t2.neighborCW(p3));

			t2.markNeighborTriangle(t1);
			t2.markNeighborTriangle(t3);
			t2.markNeighborTriangle(t4);

			assertEquals(t3, t2.neighborCW(p3));
			assertEquals(t1, t2.neighborCCW(p3));
			
			assertEquals(t3, t2.neighborCCW(p4));
			assertEquals(t4, t2.neighborCW(p4));

			assertEquals(t4, t2.neighborCCW(p2));
			assertEquals(t1, t2.neighborCW(p2));
		}
		
		public function testConstrainedEdge():void {
			t1.markConstrainedEdgeByPoints(p1, p3);
			t1.markConstrainedEdgeByPoints(p1, p2);
			assertTrue(t1.getConstrainedEdgeCW(p1));
			assertFalse(t1.getConstrainedEdgeCW(p3));
			assertTrue(t1.getConstrainedEdgeCCW(p1));
			t1.markConstrainedEdgeByIndex(t1.edgeIndex(p3, p2));
			assertTrue(t1.getConstrainedEdgeCW(p3));

			// Constraints not propagated to other triangles.
			assertFalse(t2.getConstrainedEdgeCCW(p3));
		}
		
		public function testSetEdgeSide():void {
			t2.markNeighborTriangle(t1);
			t2.markNeighborTriangle(t3);
			t2.markNeighborTriangle(t4);
			
			//t2.setEdgeSide(p3, p2);
			t2.isEdgeSide(p3, p2);
			
			assertTrue(t2.getConstrainedEdgeCCW(p3));
			assertTrue(t1.getConstrainedEdgeCW(p3));
		}
		
		/*
		public function testRotateTrianglePair():void {
			Triangle.rotateTrianglePair(t1, p3, t2, p2);
		}
		*/
	}

}