package org.poly2tri.utils {
	import flash.display.Sprite;
	import org.poly2tri.Point;
	import org.poly2tri.Triangle;
	import org.poly2tri.utils.SpatialNode;

	/**
	 * ...
	 * @author Carlos Ballesteros Velasco
	 */
	public class Drawing {
		static public function drawLines(sp:Sprite, lines:Vector.<Point>, color:uint = 0xFF0000FF, thickness:int = 4):void {
			sp.graphics.lineStyle(thickness, color);

			sp.graphics.moveTo(lines[0].x, lines[0].y);
			for each (var p:Point in lines.slice(1)) sp.graphics.lineTo(p.x, p.y);
		}
		
		static public function drawCrossHair(sp:Sprite, point:Point, size:int = 5, color:uint = 0xFF000000, thickness:int = 2):void {
			sp.graphics.lineStyle(thickness, color);
			sp.graphics.moveTo(point.x, point.y - size);
			sp.graphics.lineTo(point.x, point.y + size);
			sp.graphics.moveTo(point.x - size, point.y);
			sp.graphics.lineTo(point.x + size, point.y);
		}
		
		static public function drawTriangles(sp:Sprite, pathNodes:Vector.<SpatialNode>, backgroundColor:uint = 0xFFFFFF, borderColor:uint = 0xca0a76, thickness:int = 2, borderAlpha:Number = 1.0, fillAlpha:Number = 0.5):void {
			sp.graphics.lineStyle(thickness, borderColor, borderAlpha);
			
			for each (var spatialNode:SpatialNode in pathNodes) {
				var triangle:Triangle = spatialNode.triangle;
				var p1:Point = triangle.points[0], p2:Point = triangle.points[1], p3:Point = triangle.points[2];
				sp.graphics.beginFill(backgroundColor, fillAlpha);
				{
					sp.graphics.moveTo(p1.x, p1.y);
					sp.graphics.lineTo(p2.x, p2.y);
					sp.graphics.lineTo(p3.x, p3.y);
					sp.graphics.lineTo(p1.x, p1.y);
				}
				sp.graphics.endFill();
			}
		}
	}

}