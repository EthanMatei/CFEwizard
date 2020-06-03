package cfe.model.disease;



public class Disease {
	String domain;
	String subdomain;
	String relevantDisorder;
	
	public Disease(String domain, String subdomain, String relevantDisorder) {
		this.domain = domain;
		this.subdomain = subdomain;
		this.relevantDisorder = relevantDisorder;
	}

	public boolean equals(Object obj) {
	    boolean equal = true;
		if (obj == null) {
			equal = false;
		}
		else if (!(obj instanceof Disease)) {
			equal = false;
		}
		else {
			Disease d = (Disease) obj;
			
		    if (domain == null && d.domain != null) equal = false;
		    else if (domain != null && !domain.equals(d.domain)) equal = false;
		    else if (subdomain == null && d.subdomain != null) equal = false;
		    else if (subdomain != null && !subdomain.equals(d.subdomain)) equal = false;
		    else if (relevantDisorder == null && d.relevantDisorder != null) equal = false;
		    else if (relevantDisorder != null && !relevantDisorder.equals(d.relevantDisorder)) equal = false;
		}
	    return equal;
	}
	
	public int hashCode() {
		return (this.domain + this.subdomain + this.relevantDisorder).hashCode();
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String getSubdomain() {
		return subdomain;
	}

	public void setSubdomain(String subdomain) {
		this.subdomain = subdomain;
	}

	public String getRelevantDisorder() {
		return relevantDisorder;
	}

	public void setRelevantDisorder(String relevantDisorder) {
		this.relevantDisorder = relevantDisorder;
	}
	
}