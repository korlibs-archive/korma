package org.poly2tri.tests.tests {
	import asunit.framework.TestCase;
	import org.poly2tri.*;

	public class EdgeTest extends TestCase {
		protected var e1:Edge;
		protected var p1:Point;
		protected var p2:Point;
		
  		protected override function setUp():void {
			p1 = new Point(0, 0);
			p2 = new Point(-1, 0);
			e1 = new Edge(p1, p2);
   		}

  		public function testInstantiated():void {
   			assertTrue(e1 is Edge);
   		}

  		public function testValues():void {
			// Order of points was reversed.
			assertEquals(e1.p, p2);
			assertEquals(e1.q, p1);
   		}
		
		public function testInEdgeList():void {
			assertFalse(e1.q.edge_list === null);
			// q contains the edge_list
			assertTrue(e1.q.edge_list.indexOf(e1) != -1);
		}
	}

}