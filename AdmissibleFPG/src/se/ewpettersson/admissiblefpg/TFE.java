package se.ewpettersson.admissiblefpg;

public class TFE {
	public TFE(int tet, int face, int edge) {
		super();
		this.tet = tet;
		this.face = face;
		this.edge = edge;
	}



	public int tet;
	public int edge;
	public int face;
	
	/**
	 * 
	 * @param tfe
	 * @return 0 if the two pairs do not match up
	 *         1 if they match up, and orientations agree
	 *         -1 if they match up but orientations disagree
	 */
	public int orientTo(TFE tfe) {
		if (tet == tfe.tet && face == tfe.face) {
			if (edge == tfe.edge) {
				return 1;
			}
			if (edge == -tfe.edge) {
				return -1;
			}
		}
		return 0;
	}
	
	
}
