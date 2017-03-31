package org.poly2tri.tests.tests {
	import asunit.framework.TestCase;
	import org.poly2tri.Point;
	import org.poly2tri.utils.NewFunnel;

	/**
	 * ...
	 * @author Carlos Ballesteros Velasco
	 */
	public class NewFunnelTest extends TestCase {
		
		public function NewFunnelTest() {
		}
		
		public function testStringPull():void {
			var channel:NewFunnel = new NewFunnel();

			channel.push(new Point( 1,   0));
			channel.push(new Point( 0,   4),   new Point( 4,  3));
			channel.push(new Point( 4,   7),   new Point( 4,  3));
			channel.push(new Point(16,   0),   new Point(10,  1));
			channel.push(new Point(16,   0),   new Point( 9, -5));
			channel.push(new Point(12, -11));
			channel.stringPull();

			assertEquals("Point(1, 0),Point(4, 3),Point(10, 1),Point(12, -11)", channel.path.toString());
		}
		
	}

}