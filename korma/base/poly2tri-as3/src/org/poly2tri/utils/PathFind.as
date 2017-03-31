package org.poly2tri.utils {
	import flash.utils.Dictionary;

	public class PathFind {
		protected var spatialMesh:SpatialMesh;
		protected var openedList:PriorityQueue;
		
		public function PathFind(spatialMesh:SpatialMesh) {
			this.spatialMesh = spatialMesh;
			reset();
		}
		
		protected function reset():void {
			openedList = new PriorityQueue('F');
			for each (var node:SpatialNode in this.spatialMesh.nodes) {
				node.parent = null;
				node.G = 0;
				node.H = 0;
				node.closed = false;
			}
		}
		
		public function find(startNode:SpatialNode, endNode:SpatialNode):Vector.<SpatialNode> {
			var returnList:Vector.<SpatialNode> = new Vector.<SpatialNode>();
			reset();
			var currentNode:SpatialNode = startNode;
			
			addToOpenedList(startNode);

			if (startNode !== null && endNode !== null) {
				while ((currentNode != endNode) && openedListHasItems()) {
					currentNode = getAndRemoveFirstFromOpenedList();
					addNodeToClosedList(currentNode);

					for each (var neighborNode:SpatialNode in getNodeNeighbors(currentNode)) {
						// Ignore invalid paths and the ones on the closed list.
						if (neighborNode === null) continue;
						if (inClosedList(neighborNode)) continue;
						
						var G:uint = currentNode.G + neighborNode.distanceToSpatialNode(currentNode);
						// Not in opened list yet.
						if (!inOpenedList(neighborNode)) {
							addToOpenedList(neighborNode);
							neighborNode.G = G;
							neighborNode.H = neighborNode.distanceToSpatialNode(endNode);
							neighborNode.parent = currentNode;
							updatedNodeOnOpenedList(neighborNode);
						}
						// In opened list but with a worse G than this one.
						else if (G < neighborNode.G) {
							neighborNode.G = G;
							neighborNode.parent = currentNode;
							updatedNodeOnOpenedList(neighborNode);
						}
					}
				}
			}
			
			if (currentNode != endNode) throw(new PathFindException("Can't find a path", 1));
			
			while (currentNode != startNode) {
				returnList.unshift(currentNode);
				//returnList.push(currentNode);
				currentNode = currentNode.parent;
			}
			
			returnList.unshift(startNode);
		
			return returnList;
		}
		
		protected function addToOpenedList(node:SpatialNode):void {
			openedList.push(node);
		}

		protected function openedListHasItems():Boolean {
			return openedList.length > 0;
		}
		
		protected function getAndRemoveFirstFromOpenedList():SpatialNode {
			return openedList.removeHead() as SpatialNode;
		}
		
		protected function addNodeToClosedList(node:SpatialNode):void {
			node.closed = true;
		}
		
		protected function inClosedList(node:SpatialNode):Boolean {
			return node.closed;
		}
		
		protected function getNodeNeighbors(node:SpatialNode):Vector.<SpatialNode> {
			return node.neighbors;
		}
		
		protected function inOpenedList(node:SpatialNode):Boolean {
			return openedList.contains(node);
		}
		
		protected function updatedNodeOnOpenedList(node:SpatialNode):void {
			openedList.updateObject(node);
		}
		
		/*public function inClosedList(node:SpatialNode):Boolean {
			return (closedList[node] !== undefined);
		}*/
	}

}