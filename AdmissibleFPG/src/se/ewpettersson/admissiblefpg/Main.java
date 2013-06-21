package se.ewpettersson.admissiblefpg;

import java.util.Scanner;

import se.ewpettersson.admissiblefpg.fpg.TreeDecomp;

public class Main {
	public static void main(String[] args) {
	
		Scanner stdin = new Scanner(System.in);
		while(stdin.hasNextLine()) {
			String s = stdin.nextLine();
			FPG f = new FPG(s);
			TreeDecomp t = new TreeDecomp();
			if (t.isAdmissible()) {
				System.out.println(s);
			}

		}
	}
}
