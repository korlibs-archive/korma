package org.poly2tri.utils {
	import flash.utils.Dictionary;
	import org.poly2tri.Point;
	import org.poly2tri.Triangle;

	public class SpatialMesh {
		protected var mapTriangleToSpatialNode:Dictionary;
		public var nodes:Vector.<SpatialNode>;
		
		public function SpatialMesh() {
			nodes = new Vector.<SpatialNode>();
			mapTriangleToSpatialNode = new Dictionary();
		}
		
		public function spatialNodeFromPoint(point:Point):SpatialNode {
			for each (var node:SpatialNode in nodes) {
				if (node.triangle.pointInsideTriangle(point)) return node;
			}
			throw('Point not inside triangles');
		}
		
		public function getNodeFromTriangle(triangle:Triangle):SpatialNode {
			if (triangle === null) return null;
			
			if (mapTriangleToSpatialNode[triangle] === undefined) {
				var spatialNode:SpatialNode = mapTriangleToSpatialNode[triangle] = new SpatialNode();
				var tp:Vector.<Point> = triangle.points;
				spatialNode.x = int((tp[0].x + tp[1].x + tp[2].x) / 3);
				spatialNode.y = int((tp[0].y + tp[1].y + tp[2].y) / 3);
				spatialNode.z = 0.0;
				spatialNode.triangle = triangle;
				spatialNode.G = 0;
				spatialNode.H = 0;
				spatialNode.neighbors[0] = triangle.constrained_edge[0] ? null : getNodeFromTriangle(triangle.neighbors[0]);
				spatialNode.neighbors[1] = triangle.constrained_edge[1] ? null : getNodeFromTriangle(triangle.neighbors[1]);
				spatialNode.neighbors[2] = triangle.constrained_edge[2] ? null : getNodeFromTriangle(triangle.neighbors[2]);
			}
			return mapTriangleToSpatialNode[triangle];
		}
		
		static public function fromTriangles(triangles:Vector.<Triangle>):SpatialMesh {
			var sm:SpatialMesh = new SpatialMesh();
			for each (var triangle:Triangle in triangles) {
				sm.nodes.push(sm.getNodeFromTriangle(triangle));
			}
			return sm;
		}
		
		public function toString():String {
			return 'SpatialMesh(' + nodes.toString() + ')';
		}
	}

}