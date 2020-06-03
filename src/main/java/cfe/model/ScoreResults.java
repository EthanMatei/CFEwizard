package cfe.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ScoreResults	{
	private String score;
	private String pubMedId;
	private String tissue;
	private String directionChange;

	// Eliminate duplicates
	private Set<String> pubMedUrl = new HashSet<String>();

	public String getScore() {
		return score;
	}
	public void setScore(String score) {
		this.score = score;
	}

	public String getPubMedId() {
		return pubMedId;
	}

	public void setPubMedId(String pubMedId) {
		this.pubMedId = pubMedId;
	}


	public ScoreResults(String score,  String tissue, String directionChange, String pubMedId) {
		this.score = score;
		this.tissue = tissue;
		this.directionChange = directionChange;
		this.pubMedId = pubMedId;
		// log.info("pubMedId to URL: " + pubMedId);
		List<String> tmp = Arrays.asList(pubMedId.split("#"));
		for(String t: tmp)	{
			if (t.trim().equals("0")) continue;
			// log.info("Adding to URL: " + t);
			this.pubMedUrl.add(t.trim().toString());
		}
	}

	public Set<String> getPubMedUrl() {
		/*
			for(String t: pubMedUrl)	{
				log.info(">>>>>>>>>>>>>>PubMedID: " + t);
			}
		 */
		return pubMedUrl;
	}

	public List<String> getPubMedUrl2() {

		List<String> list = new ArrayList<String>(this.pubMedUrl);
		/*
			for(String t: list)	{
				log.info("PubMedID: " + t);
			}
		 */
		return list;
	}
	public void setPubMedUrl(Set<String> pubMedUrl) {
		this.pubMedUrl = pubMedUrl;
	}
	public String getTissue() {
		return tissue;
	}
	public void setTissue(String tissue) {
		this.tissue = tissue;
	}
	public String getDirectionChange() {
		return directionChange;
	}
	public void setDirectionChange(String directionChange) {
		this.directionChange = directionChange;
	}
}