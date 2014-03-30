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
package se.ewpettersson.admissiblefpg;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import nl.uu.cs.treewidth.input.InputException;

import se.ewpettersson.admissiblefpg.fpg.FacePairingGraph;
import se.ewpettersson.admissiblefpg.fpg.TreeDecomp;
import se.ewpettersson.admissiblefpg.util.Timer;

public class Main {
	static long totalTime;
	
	static Map<Integer,Long> runTimes;
	static Map<Integer,Integer> configCounts;
	static Map<Integer,Integer> count;

	
	public static void main(String[] args) {
		totalTime = 0;
		boolean tw_only = false;
		boolean decomp = false;
		Scanner stdin = null;
		if (args!=null && args.length > 0) {
			if (args[0].equals("--treewidth-only")) {
				tw_only = true;
			}
			
		}
		if (args!=null && args.length > 0) {
			if (args[0].equals("--show-decomp")) {
				decomp = true;
			}
		}
		if (decomp) {
			stdin = new Scanner(System.in);
//			try { // Eclipse can't redirect stdin as part of a run configuration.
//				stdin = new Scanner(new File("/home/enigma/tmp/AdmissibleResults/6.pairs"));
//			} catch (FileNotFoundException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
			getOrder(stdin);
			System.exit(0);
		}
		if (tw_only) {
			stdin = new Scanner(System.in);
			getWidths(stdin);
			System.exit(0);
		}
		if (args!=null && args.length > 0) {
			for( String fname : args) {
				System.out.println(fname);
				try {
					stdin = new Scanner(new File(fname));
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
				runTimes = new HashMap<Integer,Long>();
				configCounts = new HashMap<Integer,Integer>();
				count = new HashMap<Integer,Integer>();
				checkGraphs(stdin);
				for(Integer key: runTimes.keySet()) {
					System.err.println("tw: "+key+" graphs took " + runTimes.get(key)/count.get(key) + " avg, maxConfigs = "+configCounts.get(key));;
				}
				
			}
		} else {
			stdin = new Scanner(System.in);
			runTimes = new HashMap<Integer,Long>();
			configCounts = new HashMap<Integer,Integer>();
			count = new HashMap<Integer,Integer>();
	
			checkGraphs(stdin);
			System.err.println();
			for(Integer key: runTimes.keySet()) {
				System.err.println("tw: "+key+" graphs took " + runTimes.get(key)/count.get(key) + " avg, maxConfigs = "+configCounts.get(key));;
			}
		}
		//System.err.println();
		//System.err.println(""+count+" graphs took "+totalTime+"ms total, "+(totalTime/count)+"ms per graph on average");
	}
	
	private static void getWidths(Scanner input) {
		while(input.hasNextLine()) {
			String s = input.nextLine();
			FacePairingGraph f = new FacePairingGraph(s);
			boolean ok = true;
			int treewidth=-1;
			try {
				TreeDecomp t = new TreeDecomp(f);
				treewidth=t.getTW();
			} catch (InputException e) {
				System.err.println("Bad face pairing graph given");
				ok = false;
			}
			//totalTime+=timer.getTime();
			if(ok) {
				System.out.println(treewidth);
			}

		}
	}
	
	private static void getOrder(Scanner input) {
		while(input.hasNextLine()) {
			String s = input.nextLine();
			FacePairingGraph f = new FacePairingGraph(s);
			boolean ok = true;
			TreeDecomp t = null;
			try {
				t = new TreeDecomp(f);
			} catch (InputException e) {
				System.err.println("Bad face pairing graph given");
				ok = false;
			}
			//totalTime+=timer.getTime();
			if(ok) {
				System.out.println(t.vertexOrder());
			}

		}
	}
	private static void checkGraphs(Scanner input) {
		while(input.hasNextLine()) {
			String s = input.nextLine();
			Timer timer = new Timer();
			FacePairingGraph f = new FacePairingGraph(s);


			boolean adm = false;
			boolean ok = true;
			int treewidth=-1;
			long decompTime = -1;
			int maxConfigs=-1;
			try {
				timer.start();
				TreeDecomp t=null;
				if (!f.badGraph()) {
					t = new TreeDecomp(f);
					treewidth=t.getTW();
				}
				timer.stop();
				decompTime = timer.getTime();
				timer.start();
				if (!f.badGraph()) {
					adm = t.isAdmissible();
					maxConfigs = t.getMaxConfigs();
				}
				timer.stop();
				if(!runTimes.containsKey(treewidth)  )
					runTimes.put(treewidth, 0L);
				runTimes.put(treewidth, runTimes.get(treewidth)+timer.getTime());
				if(!configCounts.containsKey(treewidth))
					configCounts.put(treewidth, maxConfigs);
				else
					if ( maxConfigs > configCounts.get(treewidth))
						configCounts.put(treewidth, maxConfigs);
				if(!count.containsKey(treewidth)) 
					count.put(treewidth, 1);
				else
					count.put(treewidth, count.get(treewidth)+1);
				
			} catch (InputException e) {
				System.err.println("Bad face pairing graph given");
				ok = false;
			}
			//totalTime+=timer.getTime();
			if(ok) {
				System.out.println(s+","+adm+","+treewidth+","+decompTime+","+timer.getTime()+","+maxConfigs);
			}

			System.err.print(".");
		}
	}
}
