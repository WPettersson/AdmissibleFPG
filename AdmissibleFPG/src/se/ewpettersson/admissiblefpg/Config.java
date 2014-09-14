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
//				System.out.println("Failed EC: "+ec);
				return false;
			}

			//System.out.println("After: "+ec);
		}
		TVE[][] tve = g.getTVEPairs();
		for(TVE[] pair: tve) {
//			String vcs = "VC: "+vc;
			if (!vc.addGluing(pair)) {
//				System.out.println("Gluing TVE "+ pair[0] + " to " + pair[1]);
//				System.out.println("Failed VC: "+vcs);
				return false;
			}
		}
		return true;
	}

	public boolean equals(Config other) {
		return (ec.equals(other.getEC()) && vc.equals(other.getVC()));

	}

//	public void addDescription(String desc) {
//		description.add(desc);
//		
//	}
	
	public String toString() {
		return "";
//		return description.toString();
	}

	@Override
	public Iterator<Config> iterator() {
		return null;
	}
	
	public boolean onBoundary(int tet, int face) {
		return ec.onBoundary(tet, face);
	}


}
