package org.poly2tri.tests.tests {
	import asunit.framework.TestCase;
	import org.poly2tri.Point;

	public class PointTest extends TestCase {
		static private const P1X:Number = 1, P1Y:Number = 3;
		static private const P2X:Number = 7, P2Y:Number = 2;
		static private const P3X:Number = 5, P3Y:Number = 5;
		static private const SCALAR:Number = 3;
		static private const DELTA_FLOAT:Number = 0.0001;
		
		protected var p1:Point;
		protected var p2:Point;
		protected var p3:Point;
		
  		protected override function setUp():void {
   			p1 = new Point(P1X, P1Y);
			p2 = new Point(P2X, P2Y);
			p3 = new Point(P3X, P3Y);
   		}

  		public function testInstantiated():void {
   			assertTrue(p1 is Point);
   		}

  		public function testValues():void {
			assertEquals(p1.x, P1X);
			assertEquals(p1.y, P1Y);
   		}

  		public function testSum():void {
			p1.add(p2);
			assertEquals(p1.x, P1X + P2X);
			assertEquals(p1.y, P1Y + P2Y);
   		}

  		public function testSub():void {
			p1.sub(p2);
			assertEquals(p1.x, P1X - P2X);
			assertEquals(p1.y, P1Y - P2Y);
   		}
		
		public function testNeg():void {
			p1.neg();
			assertEquals(p1.x, -P1X);
			assertEquals(p1.y, -P1Y);
   		}

		public function testMul():void {
			p1.mul(SCALAR);
			assertEquals(p1.x, P1X * SCALAR);
			assertEquals(p1.y, P1Y * SCALAR);
   		}

		public function testLength():void {
			assertEqualsFloat(p1.length, Math.sqrt(P1X * P1X + P1Y * P1Y), DELTA_FLOAT);
   		}
		
		public function testNormalize():void {
			assertNotSame(p3.length, 1.0);
			p3.normalize(); assertEqualsFloat(p3.length, 1.0, DELTA_FLOAT);
			p1.normalize(); assertEqualsFloat(p1.length, 1.0, DELTA_FLOAT);
			p2.normalize(); assertEqualsFloat(p2.length, 1.0, DELTA_FLOAT);
		}
		
		public function testEquals():void {
			assertTrue(p1.equals(p1));
			assertTrue(p1.equals(new Point(P1X, P1Y)));
			assertFalse(p1.equals(p2));
		}
		
		public function testToString():void {
			assertEquals("Point(" + P2X + ", " + P2Y + ")", p2);
		}
	}

}