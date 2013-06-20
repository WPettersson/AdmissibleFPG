package se.ewpettersson.admissiblefpg.tests;



import org.junit.Test;

import se.ewpettersson.admissiblefpg.EdgeConfig;
import se.ewpettersson.admissiblefpg.TFE;

public class TestEdgeConfig {
	@Test
	public void doTestCombine(){
		TFE[] pair = new TFE[2];

		
		EdgeConfig ec = new EdgeConfig();
		
		ec.addTetrahedra(0);
		
		assert(ec.numPairs() == 6);

		pair = new TFE[2];
		pair[0] = new TFE(0,1,2);
		pair[1] = new TFE(0,2,1);
		
		ec.addGluing(pair);
		
		TFE p1 = new TFE(0,3,1);
		TFE p2 = new TFE(0,3,2);
		assert(ec.pairs(p1,p2));

		
	}
	
	@Test
	public void doTestDropPair() {
		TFE[] pair = new TFE[2];
		EdgeConfig ec = new EdgeConfig();
		ec.addTetrahedra(0);
		assert(ec.numPairs() == 6);
		
		pair = new TFE[2];
		pair[0] = new TFE(0,1,1);
		pair[1] = new TFE(0,1,2);
		ec.addGluing(pair);
		assert(ec.numPairs() == 5);
	}
}
