package cfg.utils;

/**
 * Work in progress (does NOT work)- trying to create stand alone program that will generate the createTables.sql script.
 * 
 * @author Jim Mullen
 *
 */
public class CreateSchema {

	public static void main(String[] args) {
		HibernateUtils.setCreateSchema( true );
	}
}

