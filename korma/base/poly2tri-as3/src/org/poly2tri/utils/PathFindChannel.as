package org.poly2tri.utils {
	import org.poly2tri.Edge;
	import org.poly2tri.Point;
	import org.poly2tri.Triangle;

	public class PathFindChannel {
		public function PathFindChannel() {
		}
		
		static public function channelToPortals(startPoint:Point, endPoint:Point, channel:Vector.<SpatialNode>):NewFunnel {
			var portals:NewFunnel = new NewFunnel();

			portals.push(startPoint);

			if (channel.length >= 2) {
				var firstTriangle:Triangle = channel[0].triangle;
				var secondTriangle:Triangle = channel[1].triangle;
				var lastTriangle:Triangle  = channel[channel.length - 1].triangle;
				var startVertex:Point;
				
				assert(firstTriangle.pointInsideTriangle(startPoint));
				assert(lastTriangle.pointInsideTriangle(endPoint));
				
				startVertex = Triangle.getNotCommonVertex(firstTriangle, secondTriangle);
				
				var vertexCW0:Point = startVertex;
				var vertexCCW0:Point = startVertex;
				
				//trace(startVertex);
				
				for (var n:int = 0; n < channel.length - 1; n++) {
					var triangleCurrent:Triangle = channel[n + 0].triangle;
					var triangleNext:Triangle    = channel[n + 1].triangle;
					var commonEdge:Edge  = Triangle.getCommonEdge(triangleCurrent, triangleNext);
					var vertexCW1:Point  = triangleCurrent.pointCW (vertexCW0 );
					var vertexCCW1:Point = triangleCurrent.pointCCW(vertexCCW0);
					if (!commonEdge.hasPoint(vertexCW0)) {
						vertexCW0 = vertexCW1;
					}
					if (!commonEdge.hasPoint(vertexCCW0)) {
						vertexCCW0 = vertexCCW1;
					}
					portals.push(vertexCW0, vertexCCW0);
					//trace(vertexCW0, vertexCCW0);
				}
			}
			
			portals.push(endPoint);
			
			portals.stringPull();
			
			return portals;
		}
		
		static public function channelToPortals2(startPoint:Point, endPoint:Point, channel:Vector.<SpatialNode>):NewFunnel {
			/*
			var nodeStart:SpatialNode   = spatialMesh.getNodeFromTriangle(vp.getTriangleAtPoint(new Point(50, 50)));
			//var nodeEnd:SpatialNode     = spatialMesh.getNodeFromTriangle(vp.getTriangleAtPoint(new Point(73, 133)));
			//var nodeEnd:SpatialNode     = spatialMesh.getNodeFromTriangle(vp.getTriangleAtPoint(new Point(191, 152)));
			//var nodeEnd:SpatialNode     = spatialMesh.getNodeFromTriangle(vp.getTriangleAtPoint(new Point(316, 100)));
			var nodeEnd:SpatialNode     = spatialMesh.getNodeFromTriangle(vp.getTriangleAtPoint(new Point(300, 300)));
			channel[0].triangle.pointInsideTriangle();
			channel[0].triangle.points[0]
			*/

			var portals:NewFunnel = new NewFunnel();
			var firstTriangle:Triangle = channel[0].triangle;
			var secondTriangle:Triangle = channel[1].triangle;
			var lastTriangle:Triangle  = channel[channel.length - 1].triangle;
			
			assert(firstTriangle.pointInsideTriangle(startPoint));
			assert(lastTriangle.pointInsideTriangle(endPoint));

			var startVertexIndex:int = Triangle.getNotCommonVertexIndex(firstTriangle, secondTriangle);
			//firstTriangle.containsPoint(firstTriangle.points[0]);

			// Add portals.
			
			var currentVertexCW:Point  = firstTriangle.points[startVertexIndex];
			var currentVertexCCW:Point = firstTriangle.points[startVertexIndex];
			//var currentTriangle:Triangle = firstTriangle;
			
			portals.push(startPoint);
			
			for (var n:uint = 1; n < channel.length; n++) {
				var edge:Edge = Triangle.getCommonEdge(channel[n - 1].triangle, channel[n].triangle);
				portals.push(edge.p, edge.q);
				//trace(edge);
			}
			
			/*
			for (var n:uint = 0; n < channel.length; n++) {
				trace(currentVertexCW + " | " + currentVertexCCW);
				currentVertexCW = channel[n].triangle.pointCW(currentVertexCW);
				currentVertexCCW = channel[n].triangle.pointCCW(currentVertexCCW);
				portals.push(new FunnelPortal(currentVertexCW, currentVertexCCW));
				//firstTriangle.pointCW();
			}
			*/

			portals.push(endPoint);
			
			portals.stringPull();

			return portals;
		}
		
		static private function assert(test:Boolean):void {
			if (!test) throw(new Error("Assert error"));
		}
	}

}