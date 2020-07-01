package cfe.dao;

import org.hibernate.Session;
import org.hibernate.Transaction;

import cfe.model.Discovery;

/**
 * Discovery Data Access Object class.
 * 
 * @author Jim Mullen
 *
 */
public class DiscoveryDao extends AbstractDao<Discovery> {
	
	public DiscoveryDao( Session sess, Transaction tx) {
		super(Discovery.class, sess, tx);
	}

}
