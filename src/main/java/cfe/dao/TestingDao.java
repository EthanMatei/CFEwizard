package cfe.dao;

import org.hibernate.Session;
import org.hibernate.Transaction;

import cfe.model.Testing;

/**
 * Testing Data Access Object class.
 * 
 * @author Jim Mullen
 *
 */
public class TestingDao extends AbstractDao<Testing> {
	
	public TestingDao( Session sess, Transaction tx) {
		super(Testing.class, sess, tx);
	}

}
