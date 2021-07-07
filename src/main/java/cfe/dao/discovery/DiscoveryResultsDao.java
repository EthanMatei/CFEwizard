package cfe.dao.discovery;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import cfe.dao.AbstractDao;
import cfe.model.discovery.DiscoveryResults;


public class DiscoveryResultsDao extends AbstractDao<DiscoveryResults> {
	
	private static final Log log = LogFactory.getLog(DiscoveryResultsDao.class);
	
	
	public DiscoveryResultsDao( Session sess, Transaction tx) {
		super(DiscoveryResults.class, sess, tx);
	}
	
	public List<DiscoveryResults> getAllMetadata() {
	    List<DiscoveryResults> results = null;
	    
	    String queryString = "Select new DiscoveryResults(discoveryResultsId, generatedTime, phene, lowCutoff, highCutoff)"
	        + " from DiscoveryResults";
	    
        Query<DiscoveryResults> query = sess.createQuery(queryString);
        results = query.list();
	    return results;
	}

}
