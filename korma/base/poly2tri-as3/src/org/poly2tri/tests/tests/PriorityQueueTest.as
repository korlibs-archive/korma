package org.poly2tri.tests.tests {
	import asunit.framework.TestCase;
	import org.poly2tri.utils.PriorityQueue;

	public class PriorityQueueTest extends TestCase {
		protected var pq:PriorityQueue;

		// Items inserted on setUp.
		protected var o1:QueueItem, o2:QueueItem, o3:QueueItem, o4:QueueItem, o5:QueueItem, o6:QueueItem;
		
		// Items not inserted on setUp.
		protected var n1:QueueItem;
		
		override protected function setUp():void {
			pq = new PriorityQueue('priority');
			pq.push(o1 = new QueueItem('o1',  1));
			pq.push(o2 = new QueueItem('o2', -5));
			pq.push(o3 = new QueueItem('o3',  7));
			pq.push(o4 = new QueueItem('o4',  4));
			pq.push(o5 = new QueueItem('o5',  9));
			pq.push(o6 = new QueueItem('o6',  0));
			n1 = new QueueItem('n1',  3);
		}
		
		public function testInitialOrder():void {
			assertEquals('o2', pq.head);
			assertEquals('o2,o6,o1,o4,o3,o5', pq.sortedList.toString());
		}
		
		public function testUpdateOrder():void {
			o3.priority = -6;
			pq.updateObject(o3);
			assertEquals('o3', pq.head);
			assertEquals('o3,o2,o6,o1,o4,o5', pq.sortedList.toString());
		}
		
		public function testPostUpdateInsert():void {
			o3.priority = -6;
			pq.updateObject(o3);
			pq.push(n1);
			assertEquals('o3,o2,o6,o1,n1,o4,o5', pq.sortedList.toString());
		}
		
		public function testContains():void {
			assertTrue(pq.contains(o4));
			assertFalse(pq.contains(n1));
		}
		
	}

}

internal class QueueItem {
	public var priority:int;
	public var name:String;
	
	public function QueueItem(name:String, priority:int) {
		this.name     = name;
		this.priority = priority;
	}
	
	public function toString():String {
		return name;
	}
}
