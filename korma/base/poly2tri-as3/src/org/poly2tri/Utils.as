package org.poly2tri {
	public class Utils {
		/**
		 * <b>Requirement</b>:<br>
		 * 1. a, b and c form a triangle.<br>
		 * 2. a and d is know to be on opposite side of bc<br>
		 * <pre>
		 *                a
		 *                +
		 *               / \
		 *              /   \
		 *            b/     \c
		 *            +-------+
		 *           /    d    \
		 *          /           \
		 * </pre>
		 * <b>Fact</b>: d has to be in area B to have a chance to be inside the circle formed by
		 *  a,b and c<br>
		 *  d is outside B if orient2d(a,b,d) or orient2d(c,a,d) is CW<br>
		 *  This preknowledge gives us a way to optimize the incircle test
		 * @param pa - triangle point, opposite d
		 * @param pb - triangle point
		 * @param pc - triangle point
		 * @param pd - point opposite a
		 * @return true if d is inside circle, false if on circle edge
		 */
		static public function insideIncircle(pa:Point, pb:Point, pc:Point, pd:Point):Boolean {
			var adx:Number    = pa.x - pd.x;
			var ady:Number    = pa.y - pd.y;
			var bdx:Number    = pb.x - pd.x;
			var bdy:Number    = pb.y - pd.y;

			var adxbdy:Number = adx * bdy;
			var bdxady:Number = bdx * ady;
			var oabd:Number   = adxbdy - bdxady;

			if (oabd <= 0) return false;

			var cdx:Number    = pc.x - pd.x;
			var cdy:Number    = pc.y - pd.y;

			var cdxady:Number = cdx * ady;
			var adxcdy:Number = adx * cdy;
			var ocad:Number   = cdxady - adxcdy;

			if (ocad <= 0) return false;

			var bdxcdy:Number = bdx * cdy;
			var cdxbdy:Number = cdx * bdy;

			var alift:Number  = adx * adx + ady * ady;
			var blift:Number  = bdx * bdx + bdy * bdy;
			var clift:Number  = cdx * cdx + cdy * cdy;

			var det:Number = alift * (bdxcdy - cdxbdy) + blift * ocad + clift * oabd;
			return det > 0;
		}
		
		static public function inScanArea(pa:Point, pb:Point, pc:Point, pd:Point):Boolean {
			var pdx:Number = pd.x;
			var pdy:Number = pd.y;
			var adx:Number = pa.x - pdx;
			var ady:Number = pa.y - pdy;
			var bdx:Number = pb.x - pdx;
			var bdy:Number = pb.y - pdy;

			var adxbdy:Number = adx * bdy;
			var bdxady:Number = bdx * ady;
			var oabd:Number = adxbdy - bdxady;

			if (oabd <= Constants.EPSILON) return false;

			var cdx:Number = pc.x - pdx;
			var cdy:Number = pc.y - pdy;

			var cdxady:Number = cdx * ady;
			var adxcdy:Number = adx * cdy;
			var ocad:Number = cdxady - adxcdy;

			if (ocad <= Constants.EPSILON) return false;

			return true;
		}
	}
}