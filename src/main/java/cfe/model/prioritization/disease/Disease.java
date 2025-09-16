package cfe.model.prioritization.disease;



public class Disease {
	private final String psychiatricDomain;
	private final String subdomain;
	private final String relevantDisorder;
	
	public Disease(String psychiatricDomain, String subdomain, String relevantDisorder) {
	    this.psychiatricDomain = psychiatricDomain == null ? null : psychiatricDomain.trim().toUpperCase();
	    this.subdomain = subdomain == null ? null : subdomain.trim().toUpperCase();
	    this.relevantDisorder = relevantDisorder == null ? null : relevantDisorder.trim().toUpperCase();
	}

	@Override
	public boolean equals(Object obj) {
	    if (this == obj) return true;
	    if (obj == null || getClass() != obj.getClass()) return false;
	    Disease other = (Disease) obj;
	    return (psychiatricDomain == null ? other.psychiatricDomain == null : psychiatricDomain.equals(other.psychiatricDomain))
	        && (subdomain == null ? other.subdomain == null : subdomain.equals(other.subdomain))
	        && (relevantDisorder == null ? other.relevantDisorder == null : relevantDisorder.equals(other.relevantDisorder));
	}

	@Override
	public int hashCode() {
	    int result = 17;
	    result = 31 * result + (psychiatricDomain == null ? 0 : psychiatricDomain.hashCode());
	    result = 31 * result + (subdomain == null ? 0 : subdomain.hashCode());
	    result = 31 * result + (relevantDisorder == null ? 0 : relevantDisorder.hashCode());
	    return result;
	}

	public String getDomain() {
		return psychiatricDomain;
	}

	public String getSubdomain() {
		return subdomain;
	}

	public String getRelevantDisorder() {
		return relevantDisorder;
	}
	
}
