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
		Scanner stdin = null;
		if (args!=null && args.length > 0) {
			if (args[0].equals("--treewidth-only")) {
				tw_only = true;
			}
			
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
				TreeDecomp t = new TreeDecomp(f);
				timer.stop();
				decompTime = timer.getTime();
				timer.start();
				adm = t.isAdmissible();
				timer.stop();
				if(!adm) {
					System.err.println("Bad");
				}
				treewidth=t.getTW();
				maxConfigs = t.getMaxConfigs();
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
