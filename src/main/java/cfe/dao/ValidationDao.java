package cfe.dao;

import org.hibernate.Session;
import org.hibernate.Transaction;

import cfe.model.Validation;

/**
 * Validation Data Access Object class.
 * 
 * @author Jim Mullen
 *
 */
public class ValidationDao extends AbstractDao<Validation> {
	
	public ValidationDao( Session sess, Transaction tx) {
		super(Validation.class, sess, tx);
	}

}
