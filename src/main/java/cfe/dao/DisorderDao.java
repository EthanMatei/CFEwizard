package cfe.dao;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import cfe.model.Disorder;


public class DisorderDao extends AbstractDao<Disorder> {
	
	private static final Log log = LogFactory.getLog(DisorderDao.class);
	
	
	public DisorderDao( Session sess, Transaction tx) {
		super(Disorder.class, sess, tx);
	}
	
	public void update() {
		String queryString = "";
	}
}
