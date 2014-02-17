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
