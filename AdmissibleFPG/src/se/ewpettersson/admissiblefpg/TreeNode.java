package se.ewpettersson.admissiblefpg;

import java.util.ArrayList;
import java.util.List;

import se.ewpettersson.admissiblefpg.fpg.Edge;
import se.ewpettersson.admissiblefpg.fpg.FPG;
import se.ewpettersson.admissiblefpg.fpg.Vertex;

public class TreeNode {
	List<Integer> contents;
	List<TreeNode> children;
	TreeNode parent;
	FPG fpg;
	Integer id;
	
	
	public TreeNode(Vertex v, FPG f, TreeNode parent) {
		this.parent = parent;
		this.fpg = f;
		id = v.id;
		contents = new ArrayList<Integer>(v.contents);
		contents.removeAll(fpg.seen);
		fpg.seen.addAll(contents);
	}


	public void addChildren() {
		List<Edge> toRemove = new ArrayList<Edge>();
		for( Edge e: fpg.getEdges()) {
			Vertex v1 = e.get(1);
			Vertex v2 = e.get(2);
			if (v1.id == id) {
				addChild(v2);
				toRemove.add(e);
			}
			if (v2.id == id) {
				addChild(v1);
				toRemove.add(e);
			}
		}
		fpg.getEdges().removeAll(toRemove);
		for( TreeNode child : children) {
			child.addChildren();
		}
	}


	private void addChild(Vertex v) {
		TreeNode t = new TreeNode(v,fpg,this);
		children.add(t);
	}
	
}
