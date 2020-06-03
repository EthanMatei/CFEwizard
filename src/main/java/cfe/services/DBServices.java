package cfe.services;

import org.hibernate.Session;
import org.hibernate.Transaction;

public class DBServices extends AbstractServices {
	
	protected DBServices(Session session, Transaction tx)
	{
		super(session, tx);	
	}
}
