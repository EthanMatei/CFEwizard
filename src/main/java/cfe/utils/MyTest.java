package cfe.utils;

import java.util.regex.Pattern;

public class MyTest {
    
    public static final Pattern FLOAT_PATTERN = Pattern.compile("^-?[0-9]+[.][0-9]*([eE][-]?[0-9]+)?$");
    
    public static void main(String[] args) {
        System.out.println("Test");
        String[] data = {"1.2", "0.", "0.3", "0.4E-10", "abc", "1.0e2", "-1.0e-2", "1a2"};
        
        for (String value: data) {
            System.out.print("String \"" + value + "\" ");
            if (FLOAT_PATTERN.matcher(value).matches()) {
                System.out.print("is a floating point number.");
            }
            else {
                System.out.print("is NOT a floating point number.");
            }
            System.out.println();
        }
    }
}
