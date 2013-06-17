package se.ewpettersson.admissiblefpg.util;

public class CircularListNode<E> {

	public E data;
	CircularListNode<E> next;
	CircularListNode<E> prev;
	
	public CircularListNode(E e) {
		data = e;
	}
	
	public CircularListNode<E> getNext() {
		return next;
	}
	public void setNext(CircularListNode<E> next) {
		this.next = next;
	}
	public CircularListNode<E> getPrev() {
		return prev;
	}
	public void setPrev(CircularListNode<E> prev) {
		this.prev = prev;
	}
	
	public String toString() {
		return data.toString();
	}
}
