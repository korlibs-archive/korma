package org.poly2tri.tests.tests {
	import asunit.framework.TestCase;
	import flash.display.MovieClip;
	import flash.events.Event;
	import flash.events.MouseEvent;
	import org.poly2tri.Point;
	import org.poly2tri.Triangle;
	import org.poly2tri.utils.Drawing;
	import org.poly2tri.utils.FunnelPortal;
	import org.poly2tri.utils.NewFunnel;
	import org.poly2tri.utils.PathFind;
	import org.poly2tri.utils.PathFindChannel;
	import org.poly2tri.utils.PriorityQueue;
	import org.poly2tri.utils.SpatialMesh;
	import org.poly2tri.utils.SpatialNode;
	import org.poly2tri.VisiblePolygon;

	public class PathFindTest extends TestCase {
		protected var vp:VisiblePolygon;
		protected var spatialMesh:SpatialMesh;
		protected var pathNodes:Vector.<SpatialNode>;
		protected var pathFind:PathFind;
		
		public function get triangles():Vector.<Triangle> {
			return vp.triangles;
		}
		
		protected override function setUp():void {
			vp = new VisiblePolygon();
			vp.addRectangle(0, 0, 400, 400);
			vp.addRectangle(200, 100, 80, 80);
			vp.addRectangle(100, 200, 80, 80);
			
			//vp.addRectangle(110, 210, 60, 60);
			
			vp.addRectangle(100, 20, 80, 150);
			vp.addRectangle(37, 140, 37, 104);
			var vec:Vector.<Point> = new Vector.<Point>();
			vec.push(new Point(10, 10));
			vec.push(new Point(40, 10));
			vec.push(new Point(60, 20));
			vec.push(new Point(40, 40));
			vec.push(new Point(10, 40));
			vp.addPolyline(vec);
			spatialMesh = SpatialMesh.fromTriangles(triangles);
			pathFind = new PathFind(spatialMesh);
		}
		
		public function debugDrawPath():void {
			var mc:MovieClip = new MovieClip();
			mc.graphics.clear();
			vp.drawShape(mc.graphics);
			for each (var node:SpatialNode in pathNodes) {
				VisiblePolygon.drawTriangleHighlight(node.triangle, mc.graphics);
			}
			/*
			VisiblePolygon.drawTriangleHighlight(nodeStart.triangle, mc.graphics);
			VisiblePolygon.drawTriangleHighlight(nodeEnd.triangle, mc.graphics);
			*/
			this.context.addChild(mc);
			context.stage.addEventListener(MouseEvent.CLICK, onClick);
		}
		
		private function onClick(e:MouseEvent):void {
			//trace(TrianglesToSpatialNodesConverter.convert(triangles));
			
			/*
			var pointStart:Point = new Point(50, 50);
			var pointEnd:Point = new Point(340, 200);
			//var pointEnd:Point = new Point(160, 260);

			var nodeStart:SpatialNode   = spatialMesh.getNodeFromTriangle(vp.getTriangleAtPoint(pointStart));
			var nodeEnd:SpatialNode     = spatialMesh.getNodeFromTriangle(vp.getTriangleAtPoint(pointEnd));
			
			pathNodes = pathFind.find(nodeStart, nodeEnd);
			
			assertTrue(pathNodes.toString(), "SpatialNode(45, 53),SpatialNode(70, 100),SpatialNode(91, 110),SpatialNode(82, 184),SpatialNode(91, 204),SpatialNode(126, 180),SpatialNode(153, 190),SpatialNode(186, 183),SpatialNode(186, 220),SpatialNode(220, 213),SpatialNode(286, 286)");
			
			var portals:NewFunnel = PathFindChannel.channelToPortals(pointStart, pointEnd, pathNodes)
			
			debugDrawPath(); var mc:MovieClip = new MovieClip(); Drawing.drawLines(mc, portals.path); addChild(mc);
			
			assertEquals("Point(50, 50),Point(100, 170),Point(180, 200),Point(300, 300)", portals.path);
			*/
			
			var pointStart:Point = new Point(50, 50);
			var pointEnd:Point = new Point(300, 300);

			var nodeStart:SpatialNode   = spatialMesh.getNodeFromTriangle(vp.getTriangleAtPoint(pointStart));
			var nodeEnd:SpatialNode     = spatialMesh.getNodeFromTriangle(vp.getTriangleAtPoint(pointEnd));
			
			pathNodes = pathFind.find(nodeStart, nodeEnd);
			
			var portals:NewFunnel = PathFindChannel.channelToPortals(pointStart, pointEnd, pathNodes)
			
			debugDrawPath(); var mc:MovieClip = new MovieClip(); Drawing.drawLines(mc, portals.path); addChild(mc);
		}
		
		public function testDemo():void {
			
			//trace(TrianglesToSpatialNodesConverter.convert(triangles));
			
			var pointStart:Point = new Point(50, 50);
			var pointEnd:Point = new Point(300, 300);

			var nodeStart:SpatialNode   = spatialMesh.getNodeFromTriangle(vp.getTriangleAtPoint(pointStart));
			var nodeEnd:SpatialNode     = spatialMesh.getNodeFromTriangle(vp.getTriangleAtPoint(pointEnd));
			
			pathNodes = pathFind.find(nodeStart, nodeEnd);
			
			assertTrue(pathNodes.toString(), "SpatialNode(45, 53),SpatialNode(70, 100),SpatialNode(91, 110),SpatialNode(82, 184),SpatialNode(91, 204),SpatialNode(126, 180),SpatialNode(153, 190),SpatialNode(186, 183),SpatialNode(186, 220),SpatialNode(220, 213),SpatialNode(286, 286)");
			
			var portals:NewFunnel = PathFindChannel.channelToPortals(pointStart, pointEnd, pathNodes)
			
			//debugDrawPath(); var mc:MovieClip = new MovieClip(); Drawing.drawLines(mc, portals.path); addChild(mc);
			
			assertEquals("Point(50, 50),Point(100, 170),Point(180, 200),Point(300, 300)", portals.path);
		}
	}
}