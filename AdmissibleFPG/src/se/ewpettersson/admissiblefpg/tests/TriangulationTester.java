package se.ewpettersson.admissiblefpg.tests;

import static org.junit.Assert.*;
import nl.uu.cs.treewidth.input.InputException;
import se.ewpettersson.admissiblefpg.fpg.FacePairingGraph;
import se.ewpettersson.admissiblefpg.fpg.TreeDecomp;
import org.junit.Test;

public class TriangulationTester {
	
	@Test
	public void gLMzQbcdefffhhhhhxq() throws InputException {
		String s = "1 0 1 1 2 0 2 1 0 0 0 1 3 0 3 1 0 2 0 3 4 0 4 1 1 2 1 3 5 0 5 1 2 2 2 3 5 2 5 3 3 2 3 3 4 2 4 3";
		FacePairingGraph f = new FacePairingGraph(s);
	
		TreeDecomp t = new TreeDecomp(f);
		t.makeConfig(6);
		
		// t1 f1 t2 f2 s
		// The symmetries used in this gluing are numbered as follows.
		// 0 = 012->012
		// 1 = 012->021
		// 2 = 012->102
		// 3 = 012->120
		// 4 = 012->201
		// 5 = 012->210
		// In the above, we are mapping vertices to vertex, where 0,1,2 represent the three vertices, in natural ordering (so triangle 230 is of type "120" since 0<2<3).
		int gluings[][] = { {0,0,1,0,0}, {0,2,1,3,2}, {0,1,2,1,0}, {0,3,2,2,2}, {1,1,3,1,0}, {1,2,3,3,2}, {2,0,4,0,0}, {2,3,4,2,2}, {3,0,5,0,0}, {3,2,5,3,2}, {4,1,5,2,5}, {4,3,5,1,3}};
		
		for (int g[] : gluings) {
			assertTrue(t.glue(g[0],g[1],g[2],g[3],g[4]));
		}
		
	}
	
	@Test
	public void eLAkbcbddhhwhr() throws InputException {
		String s = "0 1 0 0 1 0 1 1 0 2 0 3 2 0 2 1 1 2 1 3 3 0 3 1 2 2 2 3 3 3 3 2";
		FacePairingGraph f = new FacePairingGraph(s);
	
		TreeDecomp t = new TreeDecomp(f);
		t.makeConfig(4);
		
		// t1 f1 t2 f2 s
		// The symmetries used in this gluing are numbered as follows.
		// 0 = 012->012
		// 1 = 012->021
		// 2 = 012->102
		// 3 = 012->120
		// 4 = 012->201
		// 5 = 012->210
		// In the above, we are mapping vertices to vertex, where 0,1,2 represent the three vertices, in natural ordering (so triangle 230 is of type "120" since 0<2<3).
		int gluings[][] = { {0,0,1,0,0}, {0,2,1,3,2}, {0,1,2,1,0}, {0,3,2,2,2}, {1,1,1,2,4}, {2,0,3,0,0}, {2,3,3,2,2}, {3,3,3,1,5} };
		
		for (int g[] : gluings) {
			assertTrue(t.glue(g[0],g[1],g[2],g[3],g[4]));
		}
	}
}
