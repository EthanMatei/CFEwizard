package cfe.model;

import java.util.Comparator;

public class CfeResultsFileComparator implements Comparator<CfeResultsFile> {

        public int compare(CfeResultsFile c1, CfeResultsFile c2) {
            int cmp = c1.getFileType().compareTo( c2.getFileType() );
            return cmp;     
        }
        
        public boolean equals(CfeResultsFile c1, CfeResultsFile c2) {
            return c1.getFileType().equals(c2.getFileType());
        }
}
