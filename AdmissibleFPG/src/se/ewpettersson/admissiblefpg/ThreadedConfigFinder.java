package se.ewpettersson.admissiblefpg;

import java.util.Map;

import nl.uu.cs.treewidth.input.InputException;
import se.ewpettersson.admissiblefpg.fpg.FacePairingGraph;
import se.ewpettersson.admissiblefpg.fpg.TreeDecomp;
import se.ewpettersson.admissiblefpg.util.Timer;

public class ThreadedConfigFinder implements Runnable{

	private String fpg;
	private Map<Integer, Long> runTimes;
	private Map<Integer, Integer> configCounts;
	private Map<Integer, Integer> count;
	private int treewidth;
	private long decompTime;
	private long configTime;
	private int maxConfigs;
	private boolean adm;

	public ThreadedConfigFinder(int id, String fpg, Map<Integer, Long> runTimes2, Map<Integer, Integer> configCounts2, Map<Integer, Integer> count2) {
		this.fpg = fpg;
		this.runTimes = runTimes2;
		this.configCounts = configCounts2;
		this.count = count2;
		this.treewidth = -1;
		this.decompTime = -1;
		this.configTime = -1;
		this.maxConfigs = -1;
		this.adm = false;
	}

	@Override
	public void run() {
		Timer timer = new Timer();
		FacePairingGraph f = new FacePairingGraph(fpg);
		boolean ok = true;
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
			configTime = timer.getTime();
		} catch (InputException e) {
			System.err.println("Bad face pairing graph given");
			ok = false;
		} catch (OutOfMemoryError e) {
			System.err.println("Out of memory on "+fpg);
			ok = false;
		}
		if (ok) {
			store();
		}
	}
	
	public synchronized void store() {
		if(!runTimes.containsKey(treewidth)  )
			runTimes.put(treewidth, 0L);
		runTimes.put(treewidth, runTimes.get(treewidth)+configTime);
		if(!configCounts.containsKey(treewidth))
			configCounts.put(treewidth, maxConfigs);
		else
			if ( maxConfigs > configCounts.get(treewidth))
				configCounts.put(treewidth, maxConfigs);
		if(!count.containsKey(treewidth)) 
			count.put(treewidth, 1);
		else
			count.put(treewidth, count.get(treewidth)+1);
		System.out.println(fpg+","+adm+","+treewidth+","+decompTime+","+configTime+","+maxConfigs);
	}
}
