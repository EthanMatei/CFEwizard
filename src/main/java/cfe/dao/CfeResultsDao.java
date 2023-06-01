package cfe.dao;

import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import cfe.model.CfeResults;


public class CfeResultsDao extends AbstractDao<CfeResults> {
	
	private static final Logger log = Logger.getLogger(CfeResultsDao.class.getName());
	
	
	public CfeResultsDao( Session sess, Transaction tx) {
		super(CfeResults.class, sess, tx);
	}
	
	public List<CfeResults> getAllMetadata() {
	    List<CfeResults> results = null;
	    
	    String queryString = "Select new CfeResults(cfeResultsId, resultsType, generatedTime, phene, lowCutoff, highCutoff)"
	        + " from CfeResults";
	    
        Query<CfeResults> query = sess.createQuery(queryString, CfeResults.class);
        results = query.list();
	    return results;
	}

	
	/**
	 * Gets the metadata for the results with the specified results type.
	 * 
	 * @param resultsType
	 * @return
	 */
	/*
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
    */

    
    public List<CfeResults> getMetadata(String ... resultsTypes) {
        List<CfeResults> results = null;
        
        String queryString = "Select new CfeResults(cfeResultsId, resultsType, generatedTime, phene, lowCutoff, highCutoff)"
            + " from CfeResults where resultsType in (";
        
        for (int i = 0; i < resultsTypes.length; i++) {
            if (i > 0) {
                queryString += ", ";
            }
            queryString += ":resultsType" + i;
        }
        queryString += ")";
        
        Query<CfeResults> query = sess.createQuery(queryString, CfeResults.class);
        
        for (int i = 0; i < resultsTypes.length; i++) {
            String parameter = "resultsType" + i;
            query.setParameter(parameter, resultsTypes[i]);
        }
        
        results = query.list();
        return results;
    }
    
    public List<String> getPhenes() {
        List<String> results = null;
        
        String queryString = "Select distinct phene from CfeResults order by phene";
        
        Query<String> query = sess.createQuery(queryString, String.class);
        results = query.list();
        return results;
    }
    
    
    public Date getMinimumGeneratedTime() {
        Date results = null;
        
        String queryString = "Select min(generatedTime) from CfeResults";
        
        Query<Date> query = sess.createQuery(queryString, Date.class);
        results = query.getSingleResult();
        return results;
    }
}
