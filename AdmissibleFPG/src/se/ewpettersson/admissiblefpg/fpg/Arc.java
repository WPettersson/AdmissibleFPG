package se.ewpettersson.admissiblefpg.fpg;

public class Arc {
	public Arc(int t1, int t2) {
		this.t1 = t1;
		this.t2 = t2;
	}

	public int t1,t2;

	public boolean isLoop() {
		// TODO Auto-generated method stub
		return t1==t2;
	}
	
	public String toString() {
		String s = "["+t1+", "+t2+"]";
		return s;
	}
	
}
