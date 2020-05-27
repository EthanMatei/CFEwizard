package cfg.utils;


public class Filter {
	
	public static String filterNonAlphaNumeric(String value) {
		String filteredValue = null;
		if (value != null) filteredValue = value.replaceAll("[^0-9a-zA-Z]*", "");
		return filteredValue;
	}

}
