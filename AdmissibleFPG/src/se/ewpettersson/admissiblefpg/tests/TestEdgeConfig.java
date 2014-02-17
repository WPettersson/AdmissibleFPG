/*
 *This file is part of AdmissibleFPG.
 *
 * Copyright (C) 2014 William Pettersson <william@ewpettersson.se>
 *
 *    AdmissibleFPG is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation, either version 3 of the License, or
 *    (at your option) any later version.
 *
 *    AdmissibleFPG is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with AdmissibleFPG.  If not, see <http://www.gnu.org/licenses/>.
 */
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
