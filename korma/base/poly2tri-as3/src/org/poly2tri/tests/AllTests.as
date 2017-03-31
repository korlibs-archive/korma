package org.poly2tri.tests {
    import asunit.framework.TestSuite;
	import org.poly2tri.tests.tests.*;

    public class AllTests extends TestSuite {
        public function AllTests() {
			/*
            addTest(new PointTest());
			addTest(new OrientationTest());
			addTest(new EdgeTest());
			addTest(new TriangleTest());
			addTest(new SweepContextTest());
			addTest(new UtilsTest());
			addTest(new SweepTest());
			addTest(new SpatialMeshTest());
			addTest(new PriorityQueueTest());
			*/
			addTest(new PathFindTest());
			//addTest(new NewFunnelTest());
			/*
			addTest(new FunnelTest());
			*/
        }
    }
}