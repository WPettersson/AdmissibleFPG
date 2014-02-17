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
package se.ewpettersson.admissiblefpg.fpg;

public class Arc {
	public Arc(int t1, int t2) {
		this.t1 = t1;
		this.t2 = t2;
	}

	public int t1,t2,f1,f2;

	public boolean isLoop() {
		return t1==t2;
	}
	
	public String toString() {
		String s = "["+t1+", "+t2+"]";
		return s;
	}
	
	public boolean parallel(Arc a) {
		return (((t1 == a.t1)&&(t2 == a.t2))||((t1 == a.t2)&&(t2 == a.t1))) ;
	}

	public void setF1(Integer f1) {
		this.f1 = f1;
	}
	public void setF2(Integer f2) {
		this.f2 = f2;
	}
}
