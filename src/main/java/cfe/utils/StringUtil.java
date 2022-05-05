package cfe.utils;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

public class StringUtil {

    private static Pattern INT_PATTERN   = Pattern.compile("^[+-]?\\d+$");
    private static Pattern FLOAT_PATTERN = Pattern.compile("^[+-]?([0-9]+([.][0-9]*)?|[.][0-9]+)$");
    private static Pattern DATE_MDY_PATTERN = Pattern.compile("\\d{1,2}/\\d{1,2}/\\d{2}(\\d{2})?$");
    
    private static Pattern DATE_PATTERN = Pattern.compile(
            "^\\d{4}-\\d{2}-\\d{2}$");


    public boolean matches(String date) {
        return DATE_PATTERN.matcher(date).matches();
    }
          
	public static boolean isInt(String value) {
	    boolean isInt = INT_PATTERN.matcher(value).matches(); 
	    return isInt;
	}
	
	public static boolean isFloat(String value) {
	    boolean isFloat = FLOAT_PATTERN.matcher(value).matches();
	    return isFloat;
	}
	
	public static boolean isMdyDate(String value) {
	   boolean isMdyDate = DATE_MDY_PATTERN.matcher(value).matches();
	   return isMdyDate;
	}

    public static String mdyDateToTimestampDate(String value) {
        SimpleDateFormat inputDateFormat = new SimpleDateFormat("M/d/yy");
        SimpleDateFormat outputDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        
        try {
            Date date = inputDateFormat.parse( value );
            value = outputDateFormat.format( date ); 
        }
        catch (ParseException exception) {
            ;
        }
        return value;
    }
    
    public static String mdyDateToTimestamp(String value) {
        SimpleDateFormat inputDateFormat = new SimpleDateFormat("M/d/yy");
        SimpleDateFormat outputDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
        
        try {
            Date date = inputDateFormat.parse( value );
            value = outputDateFormat.format( date ); 
        }
        catch (ParseException exception) {
            ;
        }
        return value;
    }    
 
    
    public static String timestampToMdyDate(String value) {
        SimpleDateFormat inputDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
        SimpleDateFormat outputDateFormat = new SimpleDateFormat("M/d/yy");

        try {
            Date date = inputDateFormat.parse( value );
            value = outputDateFormat.format( date ); 
        }
        catch (ParseException exception) {
            ;
        }
        return value;
    }
    
    
    public static String expandMdyDate(String value) {
        SimpleDateFormat inputDateFormat = new SimpleDateFormat("M/d/yy");
        SimpleDateFormat outputDateFormat = new SimpleDateFormat("MM/dd/yyyy");
        
        try {
            Date date = inputDateFormat.parse( value );
            value = outputDateFormat.format( date ); 
        }
        catch (ParseException exception) {
            ;
        }
        return value;
    }

    
    public static int compareTo(String value1, String value2) {
        int compare = 0;
        if (isInt(value1) && isInt(value2)) {
            try {
                Integer ival1 = Integer.parseInt(value1);
                Integer ival2 = Integer.parseInt(value2);
                compare = ival1.compareTo(ival2);
            } catch (NumberFormatException exception) {
                // this should never happen
                compare = value1.compareTo(value2);
            }
        }
        else if ( (isFloat(value1) && isFloat(value2))
                || (isFloat(value1) && isInt(value2)) 
                || (isInt(value1) && isFloat(value2))
                ) {
            try {
                Double dval1 = Double.parseDouble(value1);
                Double dval2 = Double.parseDouble(value2);
                compare = dval1.compareTo(dval2);
            } catch (NumberFormatException exception) {
                // this should never happen
                compare = value1.compareTo(value2);
            }
        }
        else if (isMdyDate(value1) && isMdyDate(value2)) {
            String val1 = mdyDateToTimestampDate(value1);
            String val2 = mdyDateToTimestampDate(value2);
            compare = val1.compareTo(val2);
        }
        else {
            compare = value1.compareTo(value2);
        }
        
        return compare;
    }
}
