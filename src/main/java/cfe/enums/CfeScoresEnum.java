package cfe.enums;

public enum CfeScoresEnum {
	
	SUICIDE("Suicide"),
	MOOD("Mood"),
	BIPOLAR("Bipolar"),
	PSYCHOSIS("Psychosis"),
	OTHER("Other")
	;
	
	private String myLabel;
	private CfeScoresEnum (String label){ this.myLabel = label; }
	
	public String getLabel(){ return this.myLabel; }
	
	public String getDefault() { return SUICIDE.getLabel();}

}
