package org.poly2tri.utils {
	import com.wirelust.as3zlib.ZStreamException;
	import flash.geom.Point;
	import flash.utils.Dictionary;
	import org.poly2tri.Triangle;
	public class SpatialNode {
		public var x:Number, y:Number, z:Number;
		public var triangle:Triangle;
		public var neighbors:Vector.<SpatialNode>;
		public function get F():uint { return G + H; } // F = G + H
		public var G:uint; // Cost
		public var H:uint; // Heuristic
		
		public var parent:SpatialNode;
		public var closed:Boolean = false;
		
		public function SpatialNode() {
			neighbors = new Vector.<SpatialNode>(3);
		}
		
		static private function poly(x:Number, y:Number):Number {
			return Math.sqrt(x * x + y * y);
		}
		
		public function distanceToSpatialNode(that:SpatialNode):uint {
			return poly(this.x - that.x, this.y - that.y);
		}
		
		public function toString():String {
			return 'SpatialNode(' + x + ', ' + y + ')';
		}
	}
}