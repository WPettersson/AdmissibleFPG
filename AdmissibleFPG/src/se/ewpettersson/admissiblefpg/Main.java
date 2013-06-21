package se.ewpettersson.admissiblefpg;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import nl.uu.cs.treewidth.input.InputException;

import se.ewpettersson.admissiblefpg.fpg.FacePairingGraph;
import se.ewpettersson.admissiblefpg.fpg.TreeDecomp;

public class Main {
	public static void main(String[] args) {
	
		Scanner stdin = null;
		if (args!=null && args.length > 0) {
			try {
				stdin = new Scanner(new File(args[0]));
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			stdin = new Scanner(System.in);
		}
		while(stdin.hasNextLine()) {
			String s = stdin.nextLine();
			FacePairingGraph f = new FacePairingGraph(s);
			try {
				TreeDecomp t = new TreeDecomp(f);
				if (t.isAdmissible()) {
					System.out.println(s);
				}
			} catch (InputException e) {
				System.err.println("Bad face pairing graph given");
			}

		}
	}
}
