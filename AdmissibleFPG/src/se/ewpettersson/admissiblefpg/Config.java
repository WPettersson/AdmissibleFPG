package se.ewpettersson.admissiblefpg;

//import java.util.ArrayList;
import java.util.Iterator;
//import java.util.List;



public class Config implements Iterable<Config>{
	VertexConfig vc;
	EdgeConfig ec;
//	List<String> description;
	

//	public List<String> getDescriptions() {
//		return description;
//	}

	public Config(Config c) {
		ec = new EdgeConfig(c.getEC());
		vc = new VertexConfig(c.getVC());
//		description = new ArrayList<String>();
//		for( String desc: c.getDescriptions()) {
//			description.add(new String(desc));
//		}
	}
	
	public Config() {
		ec = new EdgeConfig();
		vc = new VertexConfig();
//		description = new ArrayList<String>();
	}
	
	public void mergeWith(Config d) {
//		description.addAll(d.getDescriptions());
		vc.mergeWith(d.getVC());
		ec.mergeWith(d.getEC());
	}
	public VertexConfig getVC() {
		return vc;
	}
	public EdgeConfig getEC() {
		return ec;
	}
	public void addTetrahedra(Integer tetToAdd) {
		ec.addTetrahedra(tetToAdd);
		vc.addTetrahedra(tetToAdd);

	}
	
	public boolean addGluing(Gluing g) {
		TFE[][] tfe = g.getTFEPairs();
		for(TFE[] pair: tfe) {
			//System.out.println("Before: "+ec);
			//System.out.println("Gluing TFE "+ pair[0]+ " to " + pair[1]);
			if (!ec.addGluing(pair)) {
			//	System.out.println("Failed: "+ec);
				return false;
			}

			//System.out.println("After: "+ec);
		}
		TVE[][] tve = g.getTVEPairs();
		for(TVE[] pair: tve) {
			if (!vc.addGluing(pair)) {
				//System.out.println("Gluing TVE "+ pair[0] + " to " + pair[1]);
				//System.out.println("Failed: "+vc);
				return false;
			}
		}
		return true;
	}

	public boolean equals(Config other) {
		return (ec.equals(other.getEC()) && vc.equals(other.getVC()));

	}
//
//	public void addDescription(String desc) {
//		description.add(desc);
//		
//	}
//	
//	public String toString() {
//		return description.toString();
//	}

	@Override
	public Iterator<Config> iterator() {
		return null;
	}


}
