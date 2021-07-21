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
	    
	    String queryString = "Select new CfeResults(cfeResultsId, generatedTime, phene, lowCutoff, highCutoff)"
	        + " from CfeResults";
	    
        Query<CfeResults> query = sess.createQuery(queryString);
        results = query.list();
	    return results;
	}

}
