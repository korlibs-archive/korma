package org.poly2tri {
	public class Basin {
		public var left_node:Node;
		public var bottom_node:Node;
		public var right_node:Node;
		public var width:Number = 0.0;
		public var left_highest:Boolean = false;
		
		public function clear():void {
			this.left_node    = null ;
			this.bottom_node  = null ;
			this.right_node   = null ;
			this.width        = 0.0  ;
			this.left_highest = false;
		}
	}

}