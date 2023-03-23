package cfe.utils;

import java.util.Comparator;

public class TemporaryFileInfoComparator implements Comparator<TemporaryFileInfo> {

    public int compare(TemporaryFileInfo tfi1, TemporaryFileInfo tfi2) {
        int cmp = tfi1.getAgeInDays().compareTo( tfi2.getAgeInDays() );
        return cmp;     
    }
    
    public boolean equals(TemporaryFileInfo tfi1, TemporaryFileInfo tfi2) {
        return tfi1.getAgeInDays().equals(tfi2.getAgeInDays());
    }
}
