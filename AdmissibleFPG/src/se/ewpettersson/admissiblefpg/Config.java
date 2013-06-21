package se.ewpettersson.admissiblefpg;



public class Config {
	VertexConfig vc;
	EdgeConfig ec;
	

	public Config(Config c) {
		ec = new EdgeConfig(c.getEC());
		vc = new VertexConfig(c.getVC());
	}
	
	public Config() {
		ec = new EdgeConfig();
		vc = new VertexConfig();
	}
	
	public void mergeWith(Config d) {
		vc.mergeWith(d.getVC());
		ec.mergeWith(d.getEC());
	}
	private VertexConfig getVC() {
		return vc;
	}
	private EdgeConfig getEC() {
		return ec;
	}
	public void addTetrahedra(Integer tetToAdd) {
		ec.addTetrahedra(tetToAdd);
		vc.addTetrahedra(tetToAdd);

	}
	
	public boolean addGluing(Gluing g) {
		TFE[][] tfe = g.getTFEPairs();
		for(TFE[] pair: tfe) {
			if (!ec.addGluing(pair)) {
				return false;
			}
		}
		TVE[][] tve = g.getTVEPairs();
		for(TVE[] pair: tve) {
			if (!vc.addGluing(pair)) {
				return false;
			}
		}
		return true;
	}

	public boolean equals(Config other) {
		return (ec.equals(other.getEC()) && vc.equals(other.getVC()));

	}
}
