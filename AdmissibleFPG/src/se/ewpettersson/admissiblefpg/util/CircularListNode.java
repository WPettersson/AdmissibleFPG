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
		next = c.getNext();
		prev = c.getPrev();
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
	
	public String toString() {
		return data.toString();
	}
	
	public TVE getData() {
		return data;
	}
	
	public boolean equals(CircularListNode other) {
		return data.equals(other.getData());
	}
}
