package cfe.model;

import java.util.Comparator;

public class CfeResultsNewestFirstComparator implements Comparator<CfeResults> {

        public int compare(CfeResults c1, CfeResults c2) {
            int cmp = c2.getGeneratedTime().compareTo( c1.getGeneratedTime() );
            return cmp;     
        }
        
        public boolean equals(CfeResults c1, CfeResults c2) {
            return c1.getGeneratedTime().equals(c2.getGeneratedTime());
        }
}
