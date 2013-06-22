package se.ewpettersson.admissiblefpg;

public class Gluing {
	
	// Edges are, in order, 01, 02, 03, 12, 13, 23.
	
	public Gluing(int sym, int t1, int f1, int t2, int f2) {
		super();
		this.sym = sym;
		this.t1 = t1;
		this.t2 = t2;
		this.f1 = f1;
		this.f2 = f2;
	}

	public int getT1() {
		return t1;
	}

	public int getT2() {
		return t2;
	}

	public int getF1() {
		return f1;
	}

	public int getF2() {
		return f2;
	}

	// The three faces about a face, such that FACE_EDGES[f] give the three edges about face "f"
	public static int[][] FACE_EDGES = {{4,5,6},{2,3,6},{1,3,5},{1,2,4}};
	
	// The three vertices about a face, as above.
	public static int[][] FACE_VERTICES = {{1,2,3},{0,2,3},{0,1,3},{0,1,2}};
	
	
	// The symmetries used in this gluing are numbered as follows.
	// 0 = 012->012
	// 1 = 012->021
	// 2 = 012->102
	// 3 = 012->120
	// 4 = 012->201
	// 5 = 012->210
	// In the above, we are mapping vertices to vertex, where 0,1,2 represent the three vertices, in natural ordering (so triangle 230 is of type "120" since 0<2<3).
	int sym;
	
	// Given face f, and symmetry s, the vertex v is glued to VERT_SYM_MAP[s][v]
	public static int[][] VERT_SYM_MAP = {{0,1,2},{0,2,1},{1,0,2},{1,2,0},{2,0,1},{2,1,0}};
	
	// Given face f, and symmetry s, the edge e is glued to EDGE_SYM_MAP[s][f], where this is a value in 0,1,2. To get an actual edge, use FACE_EDGES[ EDGE_SYM_MAP ...
	public static int[][] EDGE_SYM_MAP = {{0,1,2},{1,0,2},{0,2,1},{2,0,1},{1,2,0},{2,1,0}};
	// Given the above edge gluing, EDGE_ORIENT_MAP is +-1 based on whether the orientations will agree (+1) or disagree (-1) 
	public static int[][] EDGE_ORIENT_MAP = {{1,1,1},{1,1,-1},{-1,1,1},{1,-1,-1},{-1,-1,1},{-1,-1,-1}};
	
	// Given a face f, the edge opposite vertex v is OPP_EDGE[f][v]
	public static int[][] OPP_EDGE = {{-1,6,5,4},{6,-1,3,2},{5,3,-1,1},{4,2,1,-1}};
	
	int t1,t2;
	int f1,f2;
	
	
	public TVE[][] getTVEPairs() {
		TVE[][] pairs = new TVE[3][2];
		int i=0;
		// For each vertex v
		for(int vert = 0 ; vert < 4; vert++) {
			if (vert == f1) {
				continue;
			}
			
			// Find the edge on f1 opposite vert
			int edge1 = OPP_EDGE[f1][vert];
			pairs[i][0] = new TVE(t1,vert, edge1);
			
			// Find the index of vert in the FACE_VERTICES array for this face 
			// We do this so we can apply the symmetry-gluing to vert
			int vertIndex = 0;
			while (FACE_VERTICES[f1][vertIndex] != vert) {
				vertIndex += 1;
			}
			
			// Get the second vertex by using VERT_SYM_MAP
			int vert2 = FACE_VERTICES[f2][VERT_SYM_MAP[sym][vertIndex]];
			
			// Find the index of the first edge in FACE_EDGES
			int edgeIndex = 0;
			while (FACE_EDGES[f1][edgeIndex] != edge1) {
				edgeIndex += 1;
			}
			
			// Translate through the gluing and don't forget orientation.
			int edge2 = EDGE_ORIENT_MAP[sym][edgeIndex]*FACE_EDGES[f2][EDGE_SYM_MAP[sym][edgeIndex]];
				
			// Create second TVE.
		    pairs[i][1] = new TVE(t2,vert2,edge2);
		    i+=1;
		}
		return pairs;
		
	}
	
	public TFE[][] getTFEPairs() {
		TFE[][] pairs = new TFE[3][2];
		
		pairs[0][0] = new TFE(t1,f1,FACE_EDGES[f1][0]);
		pairs[1][0] = new TFE(t1,f1,FACE_EDGES[f1][1]);
	    pairs[2][0] = new TFE(t1,f1,FACE_EDGES[f1][2]);
	    
	    pairs[0][1] = new TFE(t2,f2,EDGE_ORIENT_MAP[sym][0]*FACE_EDGES[f2][EDGE_SYM_MAP[sym][0]]);
	    pairs[1][1] = new TFE(t2,f2,EDGE_ORIENT_MAP[sym][1]*FACE_EDGES[f2][EDGE_SYM_MAP[sym][1]]);
	    pairs[2][1] = new TFE(t2,f2,EDGE_ORIENT_MAP[sym][2]*FACE_EDGES[f2][EDGE_SYM_MAP[sym][2]]);
		
		return pairs;
	}
	

	public String toString() {
		String s = "[g "+t1+".";
		s+=String.valueOf(FACE_VERTICES[f1][0]) + String.valueOf(FACE_VERTICES[f1][1]) + String.valueOf(FACE_VERTICES[f1][2])+ " to "+t2+".";
		s+=String.valueOf(FACE_VERTICES[f2][VERT_SYM_MAP[sym][0]])+String.valueOf(FACE_VERTICES[f2][VERT_SYM_MAP[sym][1]])+String.valueOf(FACE_VERTICES[f2][VERT_SYM_MAP[sym][2]]);
		s+="]";
		//+f1+ " - " + t2+"."+f2+"]";
		return s;
	}
}

