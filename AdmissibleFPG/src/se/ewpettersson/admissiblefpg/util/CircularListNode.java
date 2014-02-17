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
package se.ewpettersson.admissiblefpg.util;

import se.ewpettersson.admissiblefpg.TVE;

public class CircularListNode {

	public TVE data;
	CircularListNode next;
	CircularListNode prev;
	
	public CircularListNode(TVE e) {
		data = e;
	}
	
	public CircularListNode(CircularListNode c) {
		next = null;
		prev = null;
		data = new TVE(c.getData());
	}
	
	public CircularListNode getNext() {
		return next;
	}
	public void setNext(CircularListNode next) {
		this.next = next;
	}
	public CircularListNode getPrev() {
		return prev;
	}
	public void setPrev(CircularListNode prev) {
		this.prev = prev;
	}
	
	public int size() {
		int i=0;
		CircularListNode temp = getNext();
		CircularListNode now = this;
		while((!now.equals(temp)) && temp != null && i < 10) {
			temp=temp.getNext();
			i++;
		}
		return i;
	}
	
	public String toString() {
		String s="[";
		s+=data.toString()+", ";
		CircularListNode temp = getNext();
		CircularListNode now = this;
		int i=0;
		while(temp != null && (!now.equals(temp)) && i < 101) {
			s+=temp.getData().toString()+", ";
			temp=temp.getNext();
			i++;
		}
		s=s.substring(0, s.length()-2); 
		if (temp == null) {
			s+="*"; // Denote a not-circular node.
		}
		s+="]";
		return s;
	}
	
	public TVE getData() {
		return data;
	}
	
	public boolean equals(CircularListNode other) {
		return data.equals(other.getData());
	}
}
