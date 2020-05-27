package cfg.services;

import org.hibernate.Session;
import org.hibernate.Transaction;

public class Services extends AbstractServices {

	protected Services(Session session, Transaction tx) {
		super(session, tx);			
		
	}
	
}
