package cfg.model;

import java.util.Comparator;

public class DisorderComparator implements Comparator<Disorder> {
	
	public int compare(Disorder d1, Disorder d2) {
		int cmp = d1.getDomain().trim().toLowerCase().compareTo( d2.getDomain().trim().toLowerCase() );
		if (cmp == 0) {
			cmp = d1.getSubdomain().trim().toLowerCase().compareTo( d2.getSubdomain().trim().toLowerCase() );
			if (cmp == 0) {
				cmp = d1.getRelevantDisorder().trim().toLowerCase().compareTo( d2.getRelevantDisorder().trim().toLowerCase() );
			}
		}
		
		return cmp;		
	}
	
	public boolean equals(Disorder d1, Disorder d2) {
		return d1.getDomain().trim().equalsIgnoreCase( d2.getDomain().trim() )
				&& d1.getSubdomain().trim().equalsIgnoreCase( d2.getSubdomain().trim() )
				&& d1.getRelevantDisorder().trim().equalsIgnoreCase( d2.getRelevantDisorder().trim() );
	}
	
}