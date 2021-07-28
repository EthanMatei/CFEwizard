package cfe.dao;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import cfe.model.CfeResults;


public class CfeResultsDao extends AbstractDao<CfeResults> {
	
	private static final Log log = LogFactory.getLog(CfeResultsDao.class);
	
	
	public CfeResultsDao( Session sess, Transaction tx) {
		super(CfeResults.class, sess, tx);
	}
	
	public List<CfeResults> getAllMetadata() {
	    List<CfeResults> results = null;
	    
	    String queryString = "Select new CfeResults(cfeResultsId, resultsType, generatedTime, phene, lowCutoff, highCutoff)"
	        + " from CfeResults";
	    
        Query<CfeResults> query = sess.createQuery(queryString);
        results = query.list();
	    return results;
	}

	/**
	 * Gets the metadata for the results with the specified results type.
	 * 
	 * @param resultsType
	 * @return
	 */
    public List<CfeResults> getMetadata(String resultsType) {
        List<CfeResults> results = null;
        
        String queryString = "Select new CfeResults(cfeResultsId, resultsType, generatedTime, phene, lowCutoff, highCutoff)"
            + " from CfeResults where resultsType = :resultsType";
        
        Query<CfeResults> query = sess.createQuery(queryString);
        query.setParameter("resultsType", resultsType);
        results = query.list();
        return results;
    }
    
    public List<CfeResults> getMetadata(String resultsType1, String resultsType2) {
        List<CfeResults> results = null;
        
        String queryString = "Select new CfeResults(cfeResultsId, resultsType, generatedTime, phene, lowCutoff, highCutoff)"
            + " from CfeResults where resultsType = :resultsType1 or resultsType = :resultsType2";
        
        Query<CfeResults> query = sess.createQuery(queryString);
        query.setParameter("resultsType1", resultsType1);
        query.setParameter("resultsType2", resultsType2);
        results = query.list();
        return results;
    }
}
