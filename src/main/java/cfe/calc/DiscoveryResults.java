package cfe.calc;

import cfe.model.CfeResults;

/**
 * Discovery scoring results data.
 * 
 * @author Jim Mullen
 *
 */
public class DiscoveryResults {
    private CfeResults cfeResults;
    private String scriptCommand;
    private String scriptOutput;
    
    public CfeResults getCfeResults() {
        return cfeResults;
    }
    
    public void setCfeResults(CfeResults cfeResults) {
        this.cfeResults = cfeResults;
    }
    
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
    
    // Debug files...
    
    
}
