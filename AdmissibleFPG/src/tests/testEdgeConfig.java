package tests;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import se.ewpettersson.admissiblefpg.EdgeConfig;
import se.ewpettersson.admissiblefpg.TFE;

public class testEdgeConfig {
	@Test
	public void doTestCombine(){
		List<TFE[]> start = new ArrayList<TFE[]>();
		TFE[] pair = new TFE[2];
		pair[0] = new TFE(0,1,2);
		pair[1] = new TFE(0,3,2);
		start.add(pair);
		pair = new TFE[2];
		pair[0] = new TFE(0,2,1);
		pair[1] = new TFE(0,3,1);
		start.add(pair);
		
		EdgeConfig ec = new EdgeConfig(start);
		
		assert(ec.numPairs() == 2);

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
		List<TFE[]> start = new ArrayList<TFE[]>();
		TFE[] pair = new TFE[2];
		pair[0] = new TFE(0,1,2);
		pair[1] = new TFE(0,1,1);
		start.add(pair);
		EdgeConfig ec = new EdgeConfig(start);
		assert(ec.numPairs() == 1);
		
		pair = new TFE[2];
		pair[0] = new TFE(0,1,1);
		pair[1] = new TFE(0,1,2);
		ec.addGluing(pair);
		assert(ec.numPairs() == 0);
	}
}
