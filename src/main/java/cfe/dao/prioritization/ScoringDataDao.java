package cfe.dao.prioritization;



import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;
import org.hibernate.Transaction;


import cfe.dao.AbstractDao;

import cfe.model.prioritization.ScoringData;

public class ScoringDataDao extends AbstractDao<ScoringData> {
	
	private static final Log log = LogFactory.getLog(ScoringDataDao.class);
	
	
	public ScoringDataDao( Session sess, Transaction tx) {
		super(ScoringData.class, sess, tx);
	}

	
	
}
