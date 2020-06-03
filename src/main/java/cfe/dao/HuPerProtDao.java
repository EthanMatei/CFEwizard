package cfe.dao;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import cfe.model.Disorder;
import cfe.model.HuPerProt;

public class HuPerProtDao extends AbstractDao<HuPerProt> {
	
	public HuPerProtDao( Session sess, Transaction tx) {
		super(HuPerProt.class, sess, tx);
	}
	
	public List<Disorder> getDisorders() {
		String queryString = "SELECT DISTINCT new Disorder(psychiatricDomain, subDomain, relevantDisorder) FROM " 
				             + this.getClass().getSimpleName().replace("Dao",  "")
				             + " WHERE psychiatricDomain IS NOT NULL AND subDomain IS NOT NULL AND relevantDisorder IS NOT NULL";				             
		Query query = sess.createQuery( queryString );
		List<Disorder> disorders = (List<Disorder>) query.list();
		return disorders;
	}
}