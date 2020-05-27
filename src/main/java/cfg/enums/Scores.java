package cfg.enums;

public enum Scores {
	
	SUICIDE("Suicide"),
	MOOD("Mood"),
	BIPOLAR("Bipolar"),
	PSYCHOSIS("Psychosis"),
	OTHER("Other")
	;
	
	private String myLabel;
	private Scores (String label){ this.myLabel = label; }
	
	public String getLabel(){ return this.myLabel; }
	
	public String getDefault() { return SUICIDE.getLabel();}

}
