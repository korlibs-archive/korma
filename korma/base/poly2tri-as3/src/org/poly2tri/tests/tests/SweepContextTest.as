package org.poly2tri.tests.tests {
	import asunit.framework.TestCase;
	import org.poly2tri.Point;
	import org.poly2tri.SweepContext;

	public class SweepContextTest extends TestCase {
		protected var initialPoints:Vector.<Point>;
		protected var holePoints:Vector.<Point>;
		protected var sweepContext:SweepContext;
		
		protected override function setUp():void {
			this.initialPoints = new Vector.<Point>();
			this.initialPoints.push(new Point(  0,   0));
			this.initialPoints.push(new Point(  0, 100));
			this.initialPoints.push(new Point(100, 100));
			this.initialPoints.push(new Point(100,   0));

			this.holePoints = new Vector.<Point>();
			this.holePoints.push(new Point(10, 10));
			this.holePoints.push(new Point(10, 90));
			this.holePoints.push(new Point(90, 90));
			this.holePoints.push(new Point(90, 10));

			this.sweepContext = new SweepContext(initialPoints);
			this.sweepContext.addHole(holePoints);
		}
		
		public function testInitTriangulation():void {
			assertEquals("Point(0, 0),Point(0, 100),Point(100, 100),Point(100, 0),Point(10, 10),Point(10, 90),Point(90, 90),Point(90, 10)", String(this.sweepContext.points));
			this.sweepContext.initTriangulation();
			assertEquals("Point(0, 0),Point(100, 0),Point(10, 10),Point(90, 10),Point(10, 90),Point(90, 90),Point(0, 100),Point(100, 100)", String(this.sweepContext.points))
		}
	}

}