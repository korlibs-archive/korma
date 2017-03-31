package org.poly2tri {
	public class Orientation {
		static public const CW:int        = +1;
		static public const CCW:int       = -1;
		static public const COLLINEAR:int =  0;
		
		static public function orient2d(pa:Point, pb:Point, pc:Point):int {
			var detleft:Number  = (pa.x - pc.x) * (pb.y - pc.y);
			var detright:Number = (pa.y - pc.y) * (pb.x - pc.x);
			var val:Number = detleft - detright;

			if ((val > -Constants.EPSILON) && (val < Constants.EPSILON)) return Orientation.COLLINEAR;
			if (val > 0) return Orientation.CCW;
			return Orientation.CW;
		}
	}

}