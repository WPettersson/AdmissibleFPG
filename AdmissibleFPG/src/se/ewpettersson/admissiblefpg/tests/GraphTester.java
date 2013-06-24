package se.ewpettersson.admissiblefpg.tests;

import nl.uu.cs.treewidth.input.InputException;
import se.ewpettersson.admissiblefpg.fpg.FacePairingGraph;
import se.ewpettersson.admissiblefpg.fpg.TreeDecomp;

public class GraphTester {
	public static void main(String[] args) throws InputException {
		String s = "0 1 0 0 1 0 1 1 0 2 0 3 2 0 3 0 1 2 4 0 4 1 4 2 1 3 5 0 5 1 5 2 2 1 2 2 2 3 5 3 3 1 3 2 3 3 4 3";
		FacePairingGraph f = new FacePairingGraph(s);
	
		TreeDecomp t = new TreeDecomp(f);
		t.makeConfig(6);
		
		
		t.glue(0,3,3,3,0);
		t.glue(0,2,2,3,2);
		t.glue(0,1,2,1,0);
		t.glue(0,0,1,0,0);
		
		t.glue(1,3,4,3,0);
		t.glue(1,2,4,2,0);
		t.glue(1,1,4,1,0);
		
		t.glue(2,2,2,0,1);
	}
}
