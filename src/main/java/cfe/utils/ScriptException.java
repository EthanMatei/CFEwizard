package cfe.utils;

public class ScriptException extends Exception {
    private static final long serialVersionUID = 1L;
    
    private String scriptCommand;
    private String scriptOutput;
    
    public String getScriptCommand() {
        return scriptCommand;
    }
    
    public void setScriptCommand(String scriptCommand) {
        this.scriptCommand = scriptCommand;
    }
    
    public String getScriptOutput() {
        return scriptOutput;
    }
    
    public void setScriptOutput(String scriptOutput) {
        this.scriptOutput = scriptOutput;
    }

}
