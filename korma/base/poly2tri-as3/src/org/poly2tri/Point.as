package org.poly2tri {
	import flash.utils.Dictionary;

	/**
	 *     x
	 *   +----->
	 * y |
	 *   |
	 *   V
	 */
	public class Point {
		public var x:Number, y:Number;

		/// The edges this point constitutes an upper ending point
		private var _edge_list:Vector.<Edge>;

		// Lazy loading.
		public function get edge_list():Vector.<Edge> {
			if (this._edge_list == null) this._edge_list = new Vector.<Edge>();
			return this._edge_list;
		}

		/// Default constructor does nothing (for performance).
		public function Point(x:Number = 0, y:Number = 0) {
			this.x = x;
			this.y = y;
		}

		/// Set this point to all zeros.
		public function set_zero():void {
			this.x = 0.0;
			this.y = 0.0;
		}

		/// Set this point to some specified coordinates.
		public function set(x:Number, y:Number):void {
			this.x = x;
			this.y = y;
		}

		/// Negate this point.
		public function neg():void {
			this.x = -this.x;
			this.y = -this.y;
		}

		/// Add a point to this point.
		public function add(v:Point):void {
			this.x += v.x;
			this.y += v.y;
		}

		/// Subtract a point from this point.
		public function sub(v:Point):void {
			this.x -= v.x;
			this.y -= v.y;
		}

		/// Multiply this point by a scalar.
		public function mul(s:Number):void {
			this.x *= s;
			this.y *= s;
		}
		
		public function get length():Number {
			return Math.sqrt(x * x + y * y);
		}

		/// Convert this point into a unit point. Returns the Length.
		public function normalize():Number {
			var cachedLength:Number = this.length;
			this.x /= cachedLength;
			this.y /= cachedLength;
			return cachedLength;
		}
		
		public function equals(that:Point):Boolean {
			return (this.x == that.x) && (this.y == that.y);
		}
		
		static public function getUniqueList(nonUniqueList:Vector.<Point>):Vector.<Point> {
			var point:Point;
			var pointsMap:Dictionary = new Dictionary();
			var uniqueList:Vector.<Point> = new Vector.<Point>();

			for each (point in nonUniqueList) {
				var hash:String = String(point);
				if (pointsMap[hash] === undefined) {
					pointsMap[hash] = true;
					uniqueList.push(point);
				}
			}
			
			return uniqueList;
		}
		
		static public function middle(a:Point, b:Point):Point {
			return new Point((a.x + b.x) / 2, (a.y + b.y) / 2);
		}
		
		static public function sortPoints(points:Vector.<Point>):void {
			points.sort(cmpPoints);
		}
		
		static protected function cmpPoints(l:Point, r:Point):int {
			var ret:Number = l.y - r.y;
			if (ret == 0) ret = l.x - r.x;
			if (ret <  0) return -1;
			if (ret >  0) return +1;
			return 0;
		}
		
		public function toString():String {
			return "Point(" + x + ", " + y + ")";
		}
	}

}