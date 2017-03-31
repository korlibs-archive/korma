package org.poly2tri.utils {
	/**
	 * @TODO Optimize!!
	 */
	public class PriorityQueue {
		protected var dirtyList:Vector.<Object>;
		protected var compare:Function;
		protected var dirty:Boolean;
		protected var reversed:Boolean;
		
		public function PriorityQueue(_compare:* = null, _reversed:Boolean = false) {
			this.dirtyList = new Vector.<Object>();
			this.reversed = _reversed;
			if (_compare is Function) {
				this.compare = _compare;
			} else {
				this.compare = function(a:*, b:*):* {
					//trace('CMP');
					return a[_compare] - b[_compare];
				};
			}
		}
		
		public function updateObject(object:Object):void {
			dirty = true;
		}

		public function contains(object:Object):Boolean {
			return this.dirtyList.indexOf(object) != -1;
		}
		
		public function push(object:Object):void {
			dirtyList.push(object);
			dirty = true;
		}
		
		public function get sortedList():Vector.<Object> {
			if (dirty) {
				dirtyList = dirtyList.sort(compare);
				dirty = false;
			}
			return dirtyList;
		}
		
		public function get length():uint {
			return dirtyList.length;
		}
		
		public function get head():Object {
			return sortedList[this.reversed ? (sortedList.length - 1) : 0];
		}
		
		public function removeHead():Object {
			if (this.reversed) {
				return sortedList.pop();
			} else {
				return sortedList.shift();
			}
		}
	}

}