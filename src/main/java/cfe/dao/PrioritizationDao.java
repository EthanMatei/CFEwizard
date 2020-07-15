package cfe.dao;

import org.hibernate.Session;
import org.hibernate.Transaction;

import cfe.model.Prioritization;

/**
 * Prioritization Data Access Object class.
 * 
 * @author Jim Mullen
 *
 */
public class PrioritizationDao extends AbstractDao<Prioritization> {
	
	public PrioritizationDao( Session sess, Transaction tx) {
		super(Prioritization.class, sess, tx);
	}

}
