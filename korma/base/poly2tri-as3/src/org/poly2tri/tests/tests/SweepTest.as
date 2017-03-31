package org.poly2tri.tests.tests {
	import asunit.framework.TestCase;
	import org.poly2tri.Edge;
	import org.poly2tri.Point;
	import org.poly2tri.Sweep;
	import org.poly2tri.SweepContext;
	import org.poly2tri.Triangle;

	public class SweepTest extends TestCase {
		protected var initialPoints:Vector.<Point>;
		protected var holePoints:Vector.<Point>;
		protected var sweepContext:SweepContext;
		protected var sweep:Sweep;
		
		protected function setUpPoints():void {
			this.initialPoints = new Vector.<Point>();
			this.initialPoints.push(new Point(  0,   0));
			this.initialPoints.push(new Point(100,   0));
			this.initialPoints.push(new Point(100, 100));
			this.initialPoints.push(new Point(  0, 100));

			this.holePoints = new Vector.<Point>();
			this.holePoints.push(new Point(10, 10));
			this.holePoints.push(new Point(10, 90));
			this.holePoints.push(new Point(90, 90));
			this.holePoints.push(new Point(90, 10));
		}
		
		protected override function setUp():void {
			setUpPoints();
			this.sweepContext = new SweepContext(initialPoints);
			//this.sweepContext.addHole(holePoints);
			this.sweep = new Sweep(this.sweepContext);
		}
		
		public function testBoxTriangulate():void {
			//Edge.traceList(this.sweepContext.edge_list);
			this.sweep.triangulate();
			assertEquals(2, this.sweepContext.triangles.length);
			assertEquals("Triangle(Point(0, 100), Point(100, 0), Point(100, 100)),Triangle(Point(0, 100), Point(0, 0), Point(100, 0))", String(this.sweepContext.triangles))
		}

		public function testBoxWithHoleTriangulate():void {
			this.sweepContext.addHole(holePoints);
			this.sweep.triangulate();
			assertEquals(8, this.sweepContext.triangles.length);
			assertEquals("Triangle(Point(0, 100), Point(10, 90), Point(100, 100)),Triangle(Point(10, 90), Point(90, 90), Point(100, 100)),Triangle(Point(90, 90), Point(100, 0), Point(100, 100)),Triangle(Point(90, 90), Point(90, 10), Point(100, 0)),Triangle(Point(90, 10), Point(10, 10), Point(100, 0)),Triangle(Point(10, 10), Point(0, 0), Point(100, 0)),Triangle(Point(0, 0), Point(10, 10), Point(10, 90)),Triangle(Point(0, 100), Point(0, 0), Point(10, 90))", String(this.sweepContext.triangles));
		}
	}

}