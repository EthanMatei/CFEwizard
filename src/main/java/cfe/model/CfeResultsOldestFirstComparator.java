package cfe.model;

import java.util.Comparator;

public class CfeResultsOldestFirstComparator implements Comparator<CfeResults> {

        public int compare(CfeResults c1, CfeResults c2) {
            int cmp = c1.getGeneratedTime().compareTo( c2.getGeneratedTime() );
            return cmp;     
        }
        
        public boolean equals(CfeResults c1, CfeResults c2) {
            return c1.getGeneratedTime().equals(c2.getGeneratedTime());
        }
}
