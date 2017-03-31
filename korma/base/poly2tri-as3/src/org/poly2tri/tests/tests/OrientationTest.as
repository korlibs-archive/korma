package org.poly2tri.tests.tests {
	import asunit.framework.TestCase;
	import org.poly2tri.Orientation;
	import org.poly2tri.Point;

	public class OrientationTest extends TestCase {
  		public function testOrientationCollinear():void {
			assertEquals(Orientation.orient2d(new Point(0, 0), new Point(1, 1), new Point(2, 2)), Orientation.COLLINEAR);
			assertFalse(Orientation.orient2d(new Point(-1, 0), new Point(0, 0), new Point(+1, 0)) != Orientation.COLLINEAR);
   		}
		
		public function testOrientationCW():void {
			assertEquals(Orientation.orient2d(new Point(0, 0), new Point(1, 1), new Point(2, 0)), Orientation.CW);
		}

		public function testOrientationCCW():void {
			assertEquals(Orientation.orient2d(new Point(0, 0), new Point(-1, 1), new Point(-2, 0)), Orientation.CCW);
		}
	}

}