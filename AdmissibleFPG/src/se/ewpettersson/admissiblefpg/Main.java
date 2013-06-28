package se.ewpettersson.admissiblefpg;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import nl.uu.cs.treewidth.input.InputException;

import se.ewpettersson.admissiblefpg.fpg.FacePairingGraph;
import se.ewpettersson.admissiblefpg.fpg.TreeDecomp;
import se.ewpettersson.admissiblefpg.util.Timer;

public class Main {
	static long totalTime;
	static int count;
	public static void main(String[] args) {
		totalTime = 0;
		count = 0;
		Scanner stdin = null;
		if (args!=null && args.length > 0) {
			for( String fname : args) {
				System.out.println(fname);
				try {
					stdin = new Scanner(new File(fname));
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
				checkGraphs(stdin);
			}
		} else {
			stdin = new Scanner(System.in);
		}
		checkGraphs(stdin);
		System.err.println();
		System.err.println(""+count+" graphs took "+totalTime+"ms total, "+(totalTime/count)+"ms per graph on average");
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
				
			} catch (InputException e) {
				System.err.println("Bad face pairing graph given");
				ok = false;
			}
			totalTime+=timer.getTime();
			if(ok) {
				System.out.println(s+","+adm+","+treewidth+","+decompTime+","+timer.getTime());
			}

			count+=1;
			System.err.print(".");
		}
	}
}
