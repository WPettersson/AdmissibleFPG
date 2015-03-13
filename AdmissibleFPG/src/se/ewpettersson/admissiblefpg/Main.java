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

	enum OutputStyle {
		OUTPUT_ORDER, OUTPUT_WIDTH_ONLY, OUTPUT_PYTHON, OUTPUT_REGINA
	};
	
	public static void main(String[] args) {
		totalTime = 0;
		Scanner stdin = null;
		if (args!=null && args.length > 0) {
			if (args[0].equals("--show-decomp")) {
				stdin = new Scanner(System.in);
				getWidths(stdin, OutputStyle.OUTPUT_ORDER);
				System.exit(0);
			}

			if (args[0].equals("--treewidth-only")) {
				stdin = new Scanner(System.in);
				getWidths(stdin, OutputStyle.OUTPUT_WIDTH_ONLY);
				System.exit(0);
			}

			if (args[0].equals("--python")) {
				stdin = new Scanner(System.in);
				getWidths(stdin, OutputStyle.OUTPUT_PYTHON);
				System.exit(0);
			}

			if (args[0].equals("--regina")) {
				stdin = new Scanner(System.in);
				getWidths(stdin, OutputStyle.OUTPUT_REGINA);
				System.exit(0);
			}

			if (args[0].startsWith("-")) {
				System.err.println("Unknown option: " + args[0]);
				System.exit(1);
			}

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
	
	private static void getWidths(Scanner input, OutputStyle output) {
//		int lines=0;
//		int outputs=0;
		while(input.hasNextLine()) {
			String s = input.nextLine();
//			lines+=1;
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
//				outputs+=1;
				switch (output) {
					case OUTPUT_ORDER:
						System.out.println(s);
						System.out.println(t.vertexOrder());
						break;
					case OUTPUT_WIDTH_ONLY:
						System.out.println(t.getTW());
						break;
					case OUTPUT_PYTHON:
						System.out.println(t.toPython());
						break;
					case OUTPUT_REGINA:
						System.out.print(t.toRegina());
						break;
				}
			} else {
				System.err.println("Failed on "+s);
			}
		}
		// System.err.println(""+lines+" lines read, "+outputs+" orders found");
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
			} catch (OutOfMemoryError e) {
				System.err.println("Out of memory on "+s);
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
