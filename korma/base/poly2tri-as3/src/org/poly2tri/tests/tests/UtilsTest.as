package org.poly2tri.tests.tests {
	import asunit.framework.TestCase;
	import org.poly2tri.*;

	public class UtilsTest extends TestCase {
		protected var p1:Point, p2:Point, p3:Point, p4:Point, p5:Point;
		
  		protected override function setUp():void {
			p1 = new Point( 0, +1);
			p2 = new Point(-1,  0);
			p3 = new Point(+1,  0);
			p4 = new Point( 0, -0.01);
			p5 = new Point( 0, -1);
   		}

  		public function testInsideCircleTrue():void {
			assertTrue(Utils.insideIncircle(p1, p2, p3, p4));
   		}
		
		public function testInsideCircleFalse():void {
			assertFalse(Utils.insideIncircle(p1, p2, p3, p5));
		}
	}

}