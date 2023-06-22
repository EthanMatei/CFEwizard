package cfe.utils;

import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Assert;

import cfe.utils.StringUtil;

public class StringUtilTest {


    @Test
    public void testIsInt() {
        boolean isInt = StringUtil.isInt("123");
        Assert.assertTrue(isInt);
    }
    
    @Test
    public void testIsFloat() {
        boolean isFloat = StringUtil.isFloat("123.456");
        Assert.assertTrue(isFloat);
        
        isFloat = StringUtil.isFloat("123.456a");
        Assert.assertFalse(isFloat);
    }
    
    @Test
    public void isMdyDate() {
        boolean isDate = StringUtil.isMdyDate("1/2/20");
        Assert.assertTrue(isDate);
        
        isDate = StringUtil.isMdyDate("01/02/2020");
        Assert.assertTrue(isDate);
    }
    
}