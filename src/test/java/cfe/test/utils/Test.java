package cfe.test.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Test {
    public static void main(String[] args) {
        //SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MMM-yyyy");
        //String dateString = "7-Jun-2013";
        
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("M/d/yy");
        String dateString = "06/07/2020";
        
        SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("MM/dd/yyyy");
        SimpleDateFormat simpleDateFormat3 = new SimpleDateFormat("yyyy-MM-dd");
        
        try {
            Date dateObj = simpleDateFormat.parse( dateString );        //String to Date
            String dateStr = simpleDateFormat3.format( dateObj ); 
            System.out.println(dateStr);
        }
        catch (ParseException exception) {
            System.out.println(exception.getLocalizedMessage());
        }
    }
}
