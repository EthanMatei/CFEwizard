package cfe.parser;


import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.healthmarketscience.jackcess.Database;
import com.healthmarketscience.jackcess.DatabaseBuilder;

import cfe.dao.*;
import cfe.model.*;
import cfe.utils.HibernateUtils;
import cfe.utils.ParseResult;
import cfe.utils.TableParseResult;
import cfe.utils.Util;

/**
 * Class for parsing MS Access database files.
 * 
 * @author Michel Tavares
 * @author Jim Mullen
 *
 */
public class MSAccessParser implements IParser {
	
	private static final Log log = LogFactory.getLog(MSAccessParser.class);
	
	private List<String> validationMsgs = new ArrayList<String>(10);
	private ParseResult parseResult;
	
	public void parse(String filename) throws Exception {
		
		log.info("Processing uploaded ms file " + filename);
		
		parseResult = new ParseResult(filename);
		
		File dbfile = new File(filename);
		
		Database db = DatabaseBuilder.open(dbfile);
		
		Set<String> tablenames = db.getTableNames();
				
		Session session = HibernateUtils.getSession();
		
		//ArrayList<String> tblnames = new ArrayList<String>(20);
		
		//--------------------------------------------------------------------
		// For each table in the database...
		//--------------------------------------------------------------------
		for (String tablename : tablenames)	{

			log.info("Found  table " + tablename);
			parseResult.addTableParseResult(tablename);

			try {
				
				if (tablename.contains(cfe.enums.Tables.DISCOVERY.getLabel())) {
					// DISCOVERY DATABASE TABLE
					Discovery entity = new Discovery();
					List<Discovery> discoveries = this.parseTable(db, tablename, entity.getClass().getCanonicalName());

					Transaction tx = session.beginTransaction();
					DiscoveryDao discoveryDao = new DiscoveryDao(session, tx);

					discoveryDao.deleteAll(cfe.enums.Tables.DISCOVERY.getTblName());
					log.info("*************** discoveries count: " + discoveries.size());
					discoveryDao.saveAll(discoveries);
				} else {
					log.warn("Ignored tablename " + tablename);
					parseResult.setTableStatus(tablename, TableParseResult.Status.IGNORED);
				}
			} catch (Exception exception) {
				this.parseResult.setTableStatus(tablename, TableParseResult.Status.ERROR);
				String errorMessage = exception.getLocalizedMessage();
				Throwable cause = exception.getCause();
				if (cause != null) {
					errorMessage += ": " + cause.getLocalizedMessage();
				}
				this.parseResult.addTableIssue(tablename, errorMessage);
				throw exception; // re-throw the exception
			}
		}
		db.close();
		
		session.close();		
		session = null;

		
		log.info("Done processing ms uploaded file " + filename);
		log.info(Util.getFreeMemory());
	}

	
	public <T> List<T> parseTable(Database db, String tableName, String className) throws Exception {
		List<T> entities = new ArrayList<T>();
		Parser<T> parser = new Parser<T>();
		
		validationMsgs.clear();
		
	    parser.parseTable(db, tableName, className, entities);

	    this.parseResult.setTableStatus(tableName, TableParseResult.Status.PROCESSED);
		validationMsgs.addAll(parser.getValidationMsgs());
		for (String issue: validationMsgs) {
			this.parseResult.addTableIssue(tableName, issue);
		}

		return entities;
	}
	
	/*
	public <T> List<T> parseTable(Database db, String tablename, cfe.enums.Tables table, Session session) {
		List<T> hbgs = new ArrayList<T>();
		
		Parser<T> parser = new Parser<T>();
		
		parser.parseTable(db, tablename, "cfe.model." + table.getClassname(), hbgs);
		
		validationMsgs.addAll(parser.getValidationMsgs());
		
		for (String issue: validationMsgs) {
			this.parseResult.addTableIssue(tablename, issue);
		}
		
		return hbgs;

		Transaction tx = session.beginTransaction();
		D hbgDao = new D(session, tx);
		
		hbgDao.deleteAll(table.getTblName());
		hbgDao.saveAll(hbgs);

		//tblnames.add(table.getClassname());
	}
	*/
	
	
	public List<String> getValidationMsgs() {
		return this.validationMsgs;
	}
	
	public ParseResult getParseResult() {
		return this.parseResult;
	}
}
