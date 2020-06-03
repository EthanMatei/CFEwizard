package cfe.parser;

import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.healthmarketscience.jackcess.Column;
import com.healthmarketscience.jackcess.DataType;
import com.healthmarketscience.jackcess.Database;
import com.healthmarketscience.jackcess.Table;

public class Parser<M> {
	
	private static final Log log = LogFactory.getLog(Parser.class);
	
	private List<String> validationMsgs = new ArrayList<String>(10);
	
	// http://www.mkyong.com/java/how-to-use-reflection-to-call-java-method-at-runtime/
	@SuppressWarnings("rawtypes")
	private static Class[] getParameterType(DataType dataType)
	{
		Class[] paramString = new Class[1];
		paramString[0] = Long.TYPE;
		
		if (dataType == DataType.TEXT)
			paramString[0] = String.class;
		if (dataType == DataType.DOUBLE)
			paramString[0] = Double.TYPE;
		if (dataType == DataType.SHORT_DATE_TIME)
			paramString[0] = Date.class;
		
		return paramString;
	}

	
	public void parseTable(Database db, String tablename, String modelName, List<M> hbgs) throws Exception {
		
		Table table = db.getTable(tablename);
		
		validateColumns(table);
		
		log.info("Processing " + table.getName());
		
		for(Map<String,Object> row : table) {
			
			@SuppressWarnings("unchecked")
			M hbg = (M)Class.forName(modelName).newInstance();
			
			Method getFieldName = hbg.getClass().getDeclaredMethod("getFieldName", String.class);
					
			for(Column column : table.getColumns()) {
			
			   String columnName = column.getName().trim();
			   
			   String fieldName = (String)getFieldName.invoke(hbg, columnName);
			      
				if (fieldName == null || fieldName.equalsIgnoreCase("ID")) continue;
								
				Object value = row.get(columnName);
				
				if (value == null) continue ; //{ value = ""; }
				
				Method m = hbg.getClass().getDeclaredMethod("set" + fieldName,  getParameterType(column.getType()));
				
				// Tue Apr 09 00:00:00 EDT 2013
				// EEE MMM dd hh:mm:ss z yyyy
				if (column.getType() == DataType.SHORT_DATE_TIME)	{
					
					if (value != "")	{
						
						String pattern = "EEE MMM dd hh:mm:ss z yyyy";
					
						DateFormat formatter = new SimpleDateFormat(pattern);
					
						Date d = formatter.parse(value.toString());
					
						m.invoke(hbg, d);
					}
					
				} else {
					
					m.invoke(hbg, value);
				}
			}
			hbgs.add(hbg);			
		}  	
	}
	
	// Need to protect the code from the users
	private void validateColumns(Table t) { //throws Exception{
		
		/**
		 *	Psychiatric domain
			Sub domain
			Relevant disorder
			Genecard symbol
			Pub Med ID
		 */
		
		String[] reqFields = {"Psychiatric domain","Sub domain","Relevant disorder", "Genecard symbol","Pub Med ID"};
		
		boolean res = false;
		
		for (String f: reqFields)	{
		
			res = false;
			for(Column c : t.getColumns()) {
			
				String column = c.getName();
				if (column.contentEquals(f)) 
					res = true;
			}
			
			if (res == false) {
				// throw new Exception ("Field " + f + " not found in table " + t.getName() + ". Parsing aborted.");
				log.warn("Field " + f + " not found in table " + t.getName() + ". Parsing aborted.");
				validationMsgs.add("Field " + f + " not found in table " + t.getName() + ".");
			}
		}
	}
	
	public List<String> getValidationMsgs() {
		return this.validationMsgs;
	}
}