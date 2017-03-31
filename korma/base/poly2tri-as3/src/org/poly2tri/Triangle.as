package org.poly2tri {
	import flash.utils.Dictionary;

	public class Triangle {
		// Triangle points
		public var points:Vector.<Point> = new Vector.<Point>(3, true); // [null, null, null]
		
		// Neighbor list
		public var neighbors:Vector.<Triangle> = new Vector.<Triangle>(3, true); // [null, null, null]

		// Has this triangle been marked as an interior triangle?
		public var interior:Boolean = false;

		// Flags to determine if an edge is a Constrained edge
		public var constrained_edge:Vector.<Boolean> = new Vector.<Boolean>(3, true); // [false, false, false]

		// Flags to determine if an edge is a Delauney edge
		public var delaunay_edge:Vector.<Boolean> = new Vector.<Boolean>(3, true); // [false, false, false]

		public function Triangle(p1:Point, p2:Point, p3:Point, fixOrientation:Boolean = false, checkOrientation:Boolean = true) {
			if (fixOrientation) {
				if (Orientation.orient2d(p1, p2, p3) == Orientation.CW) {
					var pt:Point = p3;
					p3 = p2;
					p2 = pt;
					//trace("Fixed orientation");
				}
			}
			if (checkOrientation && Orientation.orient2d(p3, p2, p1) != Orientation.CW) throw(new Error("Triangle must defined with Orientation.CW"));
			this.points[0] = p1;
			this.points[1] = p2;
			this.points[2] = p3;
		}
		
		/**
		 * Test if this Triangle contains the Point object given as parameter as its vertices.
		 *
		 * @return <code>True</code> if the Point objects are of the Triangle's vertices,
		 *         <code>false</code> otherwise.
		 */
		public function containsPoint(point:Point):Boolean {
			return point.equals(points[0]) || point.equals(points[1]) || point.equals(points[2]);
		}
		
		/**
		 * Test if this Triangle contains the Edge object given as parameters as its bounding edges.
		 * @return <code>True</code> if the Edge objects are of the Triangle's bounding
		 *         edges, <code>false</code> otherwise.
		 */
		public function containsEdge(edge:Edge):Boolean {
			// In a triangle to check if contains and edge is enough to check if it contains the two vertices.
			return containsEdgePoints(edge.p, edge.q);
		}

		public function containsEdgePoints(p1:Point, p2:Point):Boolean {
			// In a triangle to check if contains and edge is enough to check if it contains the two vertices.
			return containsPoint(p1) && containsPoint(p2);
		}

		/**
		 * Update neighbor pointers.<br>
		 * This method takes either 3 parameters (<code>p1</code>, <code>p2</code> and
		 * <code>t</code>) or 1 parameter (<code>t</code>).
		 * @param   t   Triangle object.
		 * @param   p1  Point object.
		 * @param   p2  Point object.
		 */
		public function markNeighbor(t:Triangle, p1:Point, p2:Point):void {
			if ((p1.equals(this.points[2]) && p2.equals(this.points[1])) || (p1.equals(this.points[1]) && p2.equals(this.points[2]))) { this.neighbors[0] = t; return; }
			if ((p1.equals(this.points[0]) && p2.equals(this.points[2])) || (p1.equals(this.points[2]) && p2.equals(this.points[0]))) { this.neighbors[1] = t; return; }
			if ((p1.equals(this.points[0]) && p2.equals(this.points[1])) || (p1.equals(this.points[1]) && p2.equals(this.points[0]))) { this.neighbors[2] = t; return; }
			throw(new Error('Invalid markNeighbor call (1)!'));
		}
		
		public function markNeighborTriangle(that:Triangle):void {
			// exhaustive search to update neighbor pointers
			if (that.containsEdgePoints(this.points[1], this.points[2])) {
				this.neighbors[0] = that;
				that.markNeighbor(this, this.points[1], this.points[2]);
				return;
			}
			
			if (that.containsEdgePoints(this.points[0], this.points[2])) {
				this.neighbors[1] = that;
				that.markNeighbor(this, this.points[0], this.points[2]);
				return;
			}
			
			if (that.containsEdgePoints(this.points[0], this.points[1])) {
				this.neighbors[2] = that;
				that.markNeighbor(this, this.points[0], this.points[1]);
				return;
			}
		}
		
		/*public function getPointIndexOffset(p:Point, offset:int = 0):uint {
			for (var n:uint = 0; n < 3; n++) if (p.equals(this.points[n])) return (n + offset) % 3;
			throw(new Error("Point not in triangle"));
		}*/
		
		// Optimized?
		public function getPointIndexOffset(p:Point, offset:int = 0):uint {
			var no:int = offset;
			for (var n:uint = 0; n < 3; n++, no++) {
				while (no < 0) no += 3;
				while (no > 2) no -= 3;
				if (p.equals(this.points[n])) return no;
			}
			throw(new Error("Point not in triangle"));
		}
		
		/**
		 * Alias for containsPoint
		 *
		 * @param	p
		 * @return
		 */
		public function isPointAVertex(p:Point):Boolean {
			return containsPoint(p);
			//for (var n:uint = 0; n < 3; n++) if (p.equals[this.points[n]]) return true;
			//return false;
		}
		
		static public function getNotCommonVertexIndex(t1:Triangle, t2:Triangle):int {
			var sum:int = 0, index:int = -1;
			if (!t2.containsPoint(t1.points[0])) { index = 0; sum++; }
			if (!t2.containsPoint(t1.points[1])) { index = 1; sum++; }
			if (!t2.containsPoint(t1.points[2])) { index = 2; sum++; }
			if (sum != 1) throw(new Error("Triangles are not contiguous"));
			return index;
		}
		
		static public function getNotCommonVertex(t1:Triangle, t2:Triangle):Point {
			return t1.points[getNotCommonVertexIndex(t1, t2)];
		}
		
		static public function getCommonEdge(t1:Triangle, t2:Triangle):Edge {
			var commonIndexes:Vector.<Point> = new Vector.<Point>();
			for each (var point:Point in t1.points) {
				if (t2.containsPoint(point)) commonIndexes.push(point);
			}
			if (commonIndexes.length != 2) throw(new Error("Triangles are not contiguous"));
			return new Edge(commonIndexes[0], commonIndexes[1]);
		}

		/**
		 * Return the point clockwise to the given point.
		 * Return the point counter-clockwise to the given point.
		 *
		 * Return the neighbor clockwise to given point.
		 * Return the neighbor counter-clockwise to given point.
		 */
		
		private const CW_OFFSET:int = +1;
		private const CCW_OFFSET:int = -1;
		
		//private const CCW_OFFSET:int = +1;
		//private const CW_OFFSET:int = -1;
		
		public function pointCW (p:Point):Point { return this.points[getPointIndexOffset(p, CCW_OFFSET)]; }
		public function pointCCW(p:Point):Point { return this.points[getPointIndexOffset(p, CW_OFFSET)]; }
		
		public function neighborCW(p:Point):Triangle { return this.neighbors[getPointIndexOffset(p, CW_OFFSET)]; }
		public function neighborCCW(p:Point):Triangle { return this.neighbors[getPointIndexOffset(p, CCW_OFFSET)]; }

		public function getConstrainedEdgeCW(p:Point):Boolean              { return this.constrained_edge[getPointIndexOffset(p, CW_OFFSET)]; }
		public function setConstrainedEdgeCW(p:Point, ce:Boolean):Boolean  { return this.constrained_edge[getPointIndexOffset(p, CW_OFFSET)] = ce; }

		public function getConstrainedEdgeCCW(p:Point):Boolean             { return this.constrained_edge[getPointIndexOffset(p, CCW_OFFSET)]; }
		public function setConstrainedEdgeCCW(p:Point, ce:Boolean):Boolean { return this.constrained_edge[getPointIndexOffset(p, CCW_OFFSET)] = ce; }

		public function getDelaunayEdgeCW(p:Point):Boolean                 { return this.delaunay_edge[getPointIndexOffset(p, CW_OFFSET)]; }
		public function setDelaunayEdgeCW(p:Point, e:Boolean):Boolean      { return this.delaunay_edge[getPointIndexOffset(p, CW_OFFSET)] = e; }
		
		public function getDelaunayEdgeCCW(p:Point):Boolean                { return this.delaunay_edge[getPointIndexOffset(p, CCW_OFFSET)]; }
		public function setDelaunayEdgeCCW(p:Point, e:Boolean):Boolean     { return this.delaunay_edge[getPointIndexOffset(p, CCW_OFFSET)] = e; }

		/**
		 * The neighbor across to given point.
		 */
		public function neighborAcross(p:Point):Triangle { return this.neighbors[getPointIndexOffset(p, 0)]; }
		public function oppositePoint(t:Triangle, p:Point):Point { return this.pointCW(t.pointCW(p)); }

		/**
		 * Legalize triangle by rotating clockwise.<br>
		 * This method takes either 1 parameter (then the triangle is rotated around
		 * points(0)) or 2 parameters (then the triangle is rotated around the first
		 * parameter).
		 */
		public function legalize(opoint:Point, npoint:Point = null):void {
			if (npoint == null) return this.legalize(this.points[0], opoint);

			if (opoint.equals(this.points[0])) {
				this.points[1] = this.points[0];
				this.points[0] = this.points[2];
				this.points[2] = npoint;
			} else if (opoint.equals(this.points[1])) {
				this.points[2] = this.points[1];
				this.points[1] = this.points[0];
				this.points[0] = npoint;
			} else if (opoint.equals(this.points[2])) {
				this.points[0] = this.points[2];
				this.points[2] = this.points[1];
				this.points[1] = npoint;
			} else {
				throw(new Error('Invalid js.poly2tri.Triangle.Legalize call!'));
			}
		}
		
		/**
		 * Alias for getPointIndexOffset
		 *
		 * @param	p
		 */
		public function index(p:Point):int {
			try {
				return this.getPointIndexOffset(p, 0);
			} catch (e:*) {
			}
			return -1;
		}

		public function edgeIndex(p1:Point, p2:Point):int {
			if (p1.equals(this.points[0])) {
				if (p2.equals(this.points[1])) return 2;
				if (p2.equals(this.points[2])) return 1;
			} else if (p1.equals(this.points[1])) {
				if (p2.equals(this.points[2])) return 0;
				if (p2.equals(this.points[0])) return 2;
			} else if (p1.equals(this.points[2])) {
				if (p2.equals(this.points[0])) return 1;
				if (p2.equals(this.points[1])) return 0;
			}
			return -1;
		}


		/**
		 * Mark an edge of this triangle as constrained.<br>
		 * This method takes either 1 parameter (an edge index or an Edge instance) or
		 * 2 parameters (two Point instances defining the edge of the triangle).
		 */
		public function markConstrainedEdgeByIndex(index:uint):void {
			this.constrained_edge[index] = true;
		}

		public function markConstrainedEdgeByEdge(edge:Edge):void {
			this.markConstrainedEdgeByPoints(edge.p, edge.q);
		}

		public function markConstrainedEdgeByPoints(p:Point, q:Point):void {
			if ((q.equals(this.points[0]) && p.equals(this.points[1])) || (q.equals(this.points[1]) && p.equals(this.points[0]))) {
				this.constrained_edge[2] = true;
				return;
			}

			if ((q.equals(this.points[0]) && p.equals(this.points[2])) || (q.equals(this.points[2]) && p.equals(this.points[0]))) {
				this.constrained_edge[1] = true;
				return;
			}
			
			if ((q.equals(this.points[1]) && p.equals(this.points[2])) || (q.equals(this.points[2]) && p.equals(this.points[1]))) {
				this.constrained_edge[0] = true;
				return;
			}
		}
		
		// isEdgeSide
		/**
		 * Checks if a side from this triangle is an edge side.
		 * If sides are not marked they will be marked.
		 *
		 * @param	ep
		 * @param	eq
		 * @return
		 */
		public function isEdgeSide(ep:Point, eq:Point):Boolean {
			var index:int = this.edgeIndex(ep, eq);
			if (index == -1) return false;
			
			this.markConstrainedEdgeByIndex(index);
			var that:Triangle = this.neighbors[index];
			if (that != null) that.markConstrainedEdgeByPoints(ep, eq);
			return true;
		}
		
		/**
		 * Rotates a triangle pair one vertex CW
		 *<pre>
		 *       n2                    n2
		 *  P +-----+             P +-----+
		 *    | t  /|               |\  t |
		 *    |   / |               | \   |
		 *  n1|  /  |n3           n1|  \  |n3
		 *    | /   |    after CW   |   \ |
		 *    |/ oT |               | oT \|
		 *    +-----+ oP            +-----+
		 *       n4                    n4
		 * </pre>
		 */
		static public function rotateTrianglePair(t:Triangle, p:Point, ot:Triangle, op:Point):void {
			var n1:Triangle =  t.neighborCCW( p);
			var n2:Triangle =  t.neighborCW ( p);
			var n3:Triangle = ot.neighborCCW(op);
			var n4:Triangle = ot.neighborCW (op);

			var ce1:Boolean =  t.getConstrainedEdgeCCW( p);
			var ce2:Boolean =  t.getConstrainedEdgeCW ( p);
			var ce3:Boolean = ot.getConstrainedEdgeCCW(op);
			var ce4:Boolean = ot.getConstrainedEdgeCW (op);

			var de1:Boolean =  t.getDelaunayEdgeCCW( p);
			var de2:Boolean =  t.getDelaunayEdgeCW ( p);
			var de3:Boolean = ot.getDelaunayEdgeCCW(op);
			var de4:Boolean = ot.getDelaunayEdgeCW (op);

			 t.legalize( p, op);
			ot.legalize(op,  p);

			// Remap delaunay_edge
			ot.setDelaunayEdgeCCW( p, de1);
			 t.setDelaunayEdgeCW ( p, de2);
			 t.setDelaunayEdgeCCW(op, de3);
			ot.setDelaunayEdgeCW (op, de4);

			// Remap constrained_edge
			ot.setConstrainedEdgeCCW( p, ce1);
			 t.setConstrainedEdgeCW ( p, ce2);
			 t.setConstrainedEdgeCCW(op, ce3);
			ot.setConstrainedEdgeCW (op, ce4);

			// Remap neighbors
			// XXX: might optimize the markNeighbor by keeping track of
			//      what side should be assigned to what neighbor after the
			//      rotation. Now mark neighbor does lots of testing to find
			//      the right side.
			 t.clearNeigbors();
			ot.clearNeigbors();
			if (n1) ot.markNeighborTriangle(n1);
			if (n2)  t.markNeighborTriangle(n2);
			if (n3)  t.markNeighborTriangle(n3);
			if (n4) ot.markNeighborTriangle(n4);
			t.markNeighborTriangle(ot);
		}
		
		public function clearNeigbors():void {
			this.neighbors[0] = null;
			this.neighbors[1] = null;
			this.neighbors[2] = null;
		}

		public function clearDelunayEdges():void {
			this.delaunay_edge[0] = false;
			this.delaunay_edge[1] = false;
			this.delaunay_edge[2] = false;
		}
		
		public function equals(that:Triangle):Boolean {
			for (var n:uint = 0; n < 3; n++) if (!this.points[n].equals(that.points[n])) return false;
			return true;
		}
		
		static public function getUniquePointsFromTriangles(triangles:Vector.<Triangle>):Vector.<Point> {
			var triangle:Triangle;
			var point:Point;
			var points:Vector.<Point> = new Vector.<Point>();
			for each (triangle in triangles) for each (point in triangle.points) points.push(point);
			return Point.getUniqueList(points);
		}
		
		static public function traceList(triangles:Vector.<Triangle>):void {
			var point:Point;
			var triangle:Triangle;
			var pointsList:Vector.<Point> = Triangle.getUniquePointsFromTriangles(triangles);
			var pointsMap:Dictionary = new Dictionary();
			
			var points_length:uint = 0;
			for each (point in pointsList) pointsMap[String(point)] = ++points_length;
			
			function getPointName(point:Point):String {
				return "p" + pointsMap[String(point)];
			}
			
			trace("Points:");
			for each (point in pointsList) {
				trace("  " + getPointName(point) + " = " + point);
			}
			trace("Triangles:");
			for each (triangle in triangles) {
				trace("  Triangle(" + getPointName(triangle.points[0]) + ", " + getPointName(triangle.points[1]) + ", " + getPointName(triangle.points[2]) + ")");
			}
		}
		
		public function pointInsideTriangle(pp:Point):Boolean {
			var p1:Point = points[0];
			var p2:Point = points[1];
			var p3:Point = points[2];
			if (_product(p1, p2, p3) >= 0) {
				return (_product(p1, p2, pp) >= 0) && (_product(p2, p3, pp)) >= 0 && (_product(p3, p1, pp) >= 0);
			} else {
				return (_product(p1, p2, pp) <= 0) && (_product(p2, p3, pp)) <= 0 && (_product(p3, p1, pp) <= 0);
			}
		}

		static private function _product(p1:Point, p2:Point, p3:Point):Number {
			return (p1.x - p3.x) * (p2.y - p3.y) - (p1.y - p3.y) * (p2.x - p3.x);
		}

		public function toString():String {
			return "Triangle(" + this.points[0] + ", " + this.points[1] + ", " + this.points[2] + ")";
		}
	}

}