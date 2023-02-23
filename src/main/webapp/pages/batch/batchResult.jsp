<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
 
<tiles:insertTemplate template="/pages/template.jsp" flush="true">

<tiles:putAttribute name="header">
    <title>CFE Wizard - Results</title>
    <s:head />
</tiles:putAttribute>
<tiles:putAttribute name="content">

<script>
    function submitForm() {
    	document.getElementById("uploadButton").disabled = true;
    	document.body.style.cursor='wait';
    	document.uploadForm.submit();
    }
</script>

<h1>CFE Results</h1>

        
<s:if test="!errorMessage.trim().isEmpty()">
    <div class="cfeError">
        <span style="font-weight: bold;">ERROR:</span> <s:property value="errorMessage" />
        <div style="margin-top: 14px;">
            <span style="font-weight: bold;">STACK TRACE:</span>
            <pre><s:property value="exceptionStack"/></pre>
        </div>
    </div>
</s:if>
        
<s:actionerror />

<s:iterator value="validationMsgs" status="vstat">
    <s:property value="validationMsgs[%{#vstat.index}]" />
</s:iterator>

<table class="dataTable">
    <tr>
        <th>Input</th> <th>Value</th>
    </tr>
    <tr>
        <td>Discovery Phene Table</td>
        <td>&quot;<s:property value="discoveryPheneTable"/>&quot;</td>
    </tr>
    <tr>
        <td>Discovery Phene</td>
        <td>&quot;<s:property value="discoveryPhene"/>&quot;</td>
    </tr>
    <tr>
        <td>Discovery Phene Low Cutoff</td>
        <td style="text-align: right;">
            <s:property value="discoveryPheneLowCutoff"/>
        </td>
    </tr>
    <tr>
        <td>Discovery Phene High Cutoff</td>
        <td style="text-align: right;">
            <s:property value="discoveryPheneHighCutoff"/>
        </td>
    </tr>
</table>

<table class="dataTable" style="margin-top: 27px;">
    <tr>
        <th>Name</th>
        <th>ID</th>
        <th>Results</th>
        <th>Script Command File</th>
        <th>Script Log File</th></tr>
    <tr>
        <td> Discovery Cohort </td>
        <s:if test="discoveryResultsCohortId != null">
            <td> <s:property value="discoveryCohortResultsId"/> </td>
            <td>
                <s:a action="CfeResultsXlsxDisplay" title="Discovery Cohort Results">
                    <s:param name="cfeResultsId" value="discoveryCohortResultsId" />
                    <s:param name="fileName" value="'discovery-cohort-results.xlsx'" />
                    discovery-cohort-results.xlsx
                </s:a>
            </td>
            <td> N/A </td>
            <td> N/A </td>
        </s:if>
        <s:else>
            <td> &nbsp; </td>
            <td> No Results </td>
            <td> N/A </td>
            <td> N/A </td>        
        </s:else>
    </tr>
    
    <tr>
        <td> Discovery Scores </td>
	        <s:if test="discoveryScoresResultsId != null">
	        <td> <s:property value="discoveryScoresResultsId"/> </td>
	        <td>
	            <s:a action="CfeResultsXlsxDisplay" title="Discovery Scores Results">
	                <s:param name="cfeResultsId" value="discoveryScoresResultsId" />
	                <s:param name="fileName" value="'discovery-scores-results.xlsx'" />
	                discovery-scores-results.xlsx
	            </s:a>
	        </td>
	        <td>
	            <s:a action="CfeResultsFileDisplay" title="Discovery R Script Command">
	                <s:param name="cfeResultsId" value="discoveryScoresResultsId" />
	                <s:param name="fileType" value="@cfe.model.CfeResultsFileType@DISCOVERY_R_SCRIPT_COMMAND" />
	                <s:property value="'discovery-r-script-command'" />
	            </s:a>  
	        </td>
	        <td>
	            <s:a action="CfeResultsFileDisplay" title="Discovery R Script Log">
	                <s:param name="cfeResultsId" value="discoveryScoresResultsId" />
	                <s:param name="fileType" value="@cfe.model.CfeResultsFileType@DISCOVERY_R_SCRIPT_LOG" />
	                <s:property value="'discovery-r-script-log'" />
	            </s:a> 
	        </td>
        </s:if>
        <s:else>
            <td> &nbsp; </td>
            <td> No Results </td>
            <td> &nbsp; </td>
            <td> &nbsp; </td>
        </s:else>
    </tr>
    
    <tr>
        <td> Prioritization Scores </td>
        
        <s:if test="prioritizationScoresResultsId != null">
            <td>
                <s:property value="prioritizationScoresResultsId"/>
            </td>
            <td>
                <s:a action="CfeResultsXlsxDisplay" title="Prioritization Scores Results">
                    <s:param name="cfeResultsId" value="prioritizationScoresResultsId" />
                    <s:param name="fileName" value="'prioritization-scores-results.xlsx'" />
                    prioritization-scores-results.xlsx
                </s:a>
            </td>
            <td> N/A </td>
            <td> N/A </td>            
        </s:if>
        <s:else>
            <td> &nbsp; </td>
            <td> No Results </td>
            <td> N/A </td>
            <td> N/A </td>
        </s:else>
    </tr>  
    
    
    <tr>
        <td> Validation Cohort </td>
        
        <s:if test="validationCohortResultsId != null">
            <td>
                <s:property value="validationCohortResultsId"/>
            </td>
            <td>
                <s:a action="CfeResultsXlsxDisplay" title="Validation Cohort Results">
                    <s:param name="cfeResultsId" value="validationCohortResultsId" />
                    <s:param name="fileName" value="'validation-cohort-results.xlsx'" />
                    validation-cohort-results.xlsx
                </s:a>
            </td>
            <td> N/A </td>
            <td> N/A </td>
        </s:if>
        <s:else>
            <td> &nbsp; </td>
            <td> No Results </td>
            <td> N/A </td>
            <td> N/A </td>
        </s:else>

    </tr>
    
    <tr>
        <td> Validation Scores </td>
        
        <s:if test="validationScoresResultsId != null">
            <td>
                <s:property value="validationScoresResultsId"/>
            </td>
            <td>
                <s:a action="CfeResultsXlsxDisplay" title="Validation Scores Results">
                    <s:param name="cfeResultsId" value="validationScoresResultsId" />
                    <s:param name="fileName" value="'validarion-scores-results.xlsx'" />
                    validation-scores-results.xlsx
                </s:a>
            </td>
	        <td>
	            <s:a action="CfeResultsFileDisplay" title="Validation R Script Command">
	                <s:param name="cfeResultsId" value="validationScoresResultsId" />
	                <s:param name="fileType" value="@cfe.model.CfeResultsFileType@VALIDATION_R_SCRIPT_COMMAND" />
	                <s:property value="'validation-r-script-command'" />
	            </s:a>  
	        </td>
	        <td>
	            <s:a action="CfeResultsFileDisplay" title="Validation R Script Log">
	                <s:param name="cfeResultsId" value="validationScoresResultsId" />
	                <s:param name="fileType" value="@cfe.model.CfeResultsFileType@VALIDATION_R_SCRIPT_LOG" />
	                <s:property value="'validation-r-script-log'" />
	            </s:a> 
	        </td>          
        </s:if>
        <s:else>
            <td> &nbsp; </td>
            <td> No Results </td>
            <td> &nbsp; </td>
            <td> &nbsp; </td>
        </s:else>
    </tr>
    
    <tr>
        <td> Testing Cohorts </td>
        
        <s:if test="testingCohortsResultsId != null">
            <td>
                <s:property value="testingCohortsResultsId"/>
            </td>
            <td>
                <s:a action="CfeResultsXlsxDisplay" title="Testing Cohorts Results">
                    <s:param name="cfeResultsId" value="testingCohortsResultsId" />
                    <s:param name="fileName" value="'testing-cohorts-results.xlsx'" />
                    testing-cohorts-results.xlsx
                </s:a>
            </td>
	        <td>
	            <s:a action="CfeResultsFileDisplay" title="Testing Cohorts Python Script Command">
	                <s:param name="cfeResultsId" value="testingCohortsResultsId" />
	                <s:param name="fileType" value="@cfe.model.CfeResultsFileType@TESTING_COHORTS_PYTHON_SCRIPT_COMMAND" />
	                <s:property value="'testing-cohorts-python-script-command'" />
	            </s:a>  
	        </td>
	        <td>
	            <s:a action="CfeResultsFileDisplay" title="Testing Cohorts Python Script Log">
	                <s:param name="cfeResultsId" value="testingCohortsResultsId" />
	                <s:param name="fileType" value="@cfe.model.CfeResultsFileType@TESTING_COHORTS_PYTHON_SCRIPT_LOG" />
	                <s:property value="'testing-chorts-python-script-log'" />
	            </s:a> 
	        </td> 
        </s:if>
        <s:else>
            <td> &nbsp; </td>
            <td> No Results </td>
            <td> &nbsp; </td>
            <td> &nbsp; </td>
        </s:else>
    </tr>

    
    <tr>
        <td> Testing Scores </td>
        
        <s:if test="testingScoresResultsId != null">
            <td>
                <s:property value="testingScoresResultsId"/>
            </td>
            <td>
                <s:a action="CfeResultsXlsxDisplay" title="Testing Scores Results">
                    <s:param name="cfeResultsId" value="testingScoresResultsId" />
                    <s:param name="fileName" value="'testing-scores-results.xlsx'" />
                    testing-scores-results.xlsx
                </s:a>
            </td>
	        <td> 
	        
	        <!--
	        PREDICTION_STATE_CROSS_SECTIONAL_R_SCRIPT_COMMAND
            PREDICTION_STATE_CROSS_SECTIONAL_R_SCRIPT_LOG 
  
            PREDICTION_STATE_LONGITUDINAL_R_SCRIPT_COMMAND
            PREDICTION_STATE_LONGITUDINAL_R_SCRIPT_LOG
            
            PREDICTION_FIRST_YEAR_CROSS_SECTIONAL_R_SCRIPT_COMMAND
            PREDICTION_FIRST_YEAR_CROSS_SECTIONAL_R_SCRIPT_LOG
 
            PREDICTION_FIRST_YEAR_LONGITUDINAL_R_SCRIPT_COMMAND
            PREDICTION_FIRST_YEAR_LONGITUDINAL_R_SCRIPT_LOG

            PREDICTION_FUTURE_CROSS_SECTIONAL_R_SCRIPT_COMMAND
            PREDICTION_FUTURE_CROSS_SECTIONAL_R_SCRIPT_LOG

            PREDICTION_FUTURE_LONGITUDINAL_R_SCRIPT_COMMAND
            PREDICTION_FUTURE_LONGITUDINAL_R_SCRIPT_LOG
            -->
                <!-- STATE -->
	            <s:a action="CfeResultsFileDisplay" title="Testing State Cross-Sectional R Script Command">
	                <s:param name="cfeResultsId" value="testingScoresResultsId" />
	                <s:param name="fileType" value="@cfe.model.CfeResultsFileType@PREDICTION_STATE_CROSS_SECTIONAL_R_SCRIPT_COMMAND" />
	                <s:property value="'testing-state-cross-sectional-r-script-command'" />
	            </s:a>
	            <br/>
	            
	            <s:a action="CfeResultsFileDisplay" title="Testing State Longitudinal R Script Command">
	                <s:param name="cfeResultsId" value="testingScoresResultsId" />
	                <s:param name="fileType" value="@cfe.model.CfeResultsFileType@PREDICTION_STATE_LONGITUDINAL_R_SCRIPT_COMMAND" />
	                <s:property value="'testing-state-longitudinal-r-script-command'" />
	            </s:a>
	            <br/>
	           
	            <!-- FIRST YEAR --> 
	            <s:a action="CfeResultsFileDisplay" title="Testing First Year Cross-Sectional R Script Command">
	                <s:param name="cfeResultsId" value="testingScoresResultsId" />
	                <s:param name="fileType" value="@cfe.model.CfeResultsFileType@PREDICTION_FIRST_YEAR_CROSS_SECTIONAL_R_SCRIPT_COMMAND" />
	                <s:property value="'testing-first-year-cross-sectional-r-script-command'" />
	            </s:a>
	            <br/>
	            
	            <s:a action="CfeResultsFileDisplay" title="Testing First Year Longitudinal R Script Command">
	                <s:param name="cfeResultsId" value="testingScoresResultsId" />
	                <s:param name="fileType" value="@cfe.model.CfeResultsFileType@PREDICTION_FIRST_YEAR_LONGITUDINAL_R_SCRIPT_COMMAND" />
	                <s:property value="'testing-first-year-longitudinal-r-script-command'" />
	            </s:a>
	            <br/>

	            <!-- FUTURE -->
	            <s:a action="CfeResultsFileDisplay" title="Testing Future Cross-Sectional R Script Command">
	                <s:param name="cfeResultsId" value="testingScoresResultsId" />
	                <s:param name="fileType" value="@cfe.model.CfeResultsFileType@PREDICTION_FUTURE_CROSS_SECTIONAL_R_SCRIPT_COMMAND" />
	                <s:property value="'testing-future-cross-sectional-r-script-command'" />
	            </s:a>
	            <br/>
	            
	            <s:a action="CfeResultsFileDisplay" title="Testing Future Longitudinal R Script Command">
	                <s:param name="cfeResultsId" value="testingScoresResultsId" />
	                <s:param name="fileType" value="@cfe.model.CfeResultsFileType@PREDICTION_FUTURE_LONGITUDINAL_R_SCRIPT_COMMAND" />
	                <s:property value="'testing-future-longitudinal-r-script-command'" />
	            </s:a>
	            <br/>
	            	            
	        </td>
	        <td>
	            <!-- STATE -->
	            <s:a action="CfeResultsFileDisplay" title="Testing State Cross-Sectional R Script Log">
	                <s:param name="cfeResultsId" value="testingScoresResultsId" />
	                <s:param name="fileType" value="@cfe.model.CfeResultsFileType@REDICTION_STATE_CROSS_SECTIONAL_R_SCRIPT_LOG" />
	                <s:property value="'testing-state-cross-sectional-r-script-log'" />
	            </s:a>
	            <br/>
	            
	            <s:a action="CfeResultsFileDisplay" title="Testing State Longitudinal R Script Log">
	                <s:param name="cfeResultsId" value="testingScoresResultsId" />
	                <s:param name="fileType" value="@cfe.model.CfeResultsFileType@REDICTION_STATE_LONGITUDINAL_R_SCRIPT_LOG" />
	                <s:property value="'testing-state-longitudinal-r-script-log'" />
	            </s:a> 
                <br/>
                
                <!-- FIRST YEAR -->
	            <s:a action="CfeResultsFileDisplay" title="Testing First Year Cross-Sectional R Script Log">
	                <s:param name="cfeResultsId" value="testingScoresResultsId" />
	                <s:param name="fileType" value="@cfe.model.CfeResultsFileType@REDICTION_FIRST_YEAR_CROSS_SECTIONAL_R_SCRIPT_LOG" />
	                <s:property value="'testing-first-year-cross-sectional-r-script-log'" />
	            </s:a>
	            <br/>
	            
	            <s:a action="CfeResultsFileDisplay" title="Testing First Year Longitudinal R Script Log">
	                <s:param name="cfeResultsId" value="testingScoresResultsId" />
	                <s:param name="fileType" value="@cfe.model.CfeResultsFileType@REDICTION_FIRST_YEAR_LONGITUDINAL_R_SCRIPT_LOG" />
	                <s:property value="'testing-first-year-longitudinal-r-script-log'" />
	            </s:a>
	            <br/> 
	            
                <!-- FUTURE -->
	            <s:a action="CfeResultsFileDisplay" title="Testing Future Cross-Sectional R Script Log">
	                <s:param name="cfeResultsId" value="testingScoresResultsId" />
	                <s:param name="fileType" value="@cfe.model.CfeResultsFileType@REDICTION_FUTURE_CROSS_SECTIONAL_R_SCRIPT_LOG" />
	                <s:property value="'testing-future-cross-sectional-r-script-log'" />
	            </s:a>	            
	            <br/>
	            
	            <s:a action="CfeResultsFileDisplay" title="Testing Future Longitudinal R Script Log">
	                <s:param name="cfeResultsId" value="testingScoresResultsId" />
	                <s:param name="fileType" value="@cfe.model.CfeResultsFileType@REDICTION_FUTURE_LONGITUDINAL_R_SCRIPT_LOG" />
	                <s:property value="'testing-future-longitudinal-r-script-log'" />
	            </s:a>
	            <br/> 
	            		            	            
	        </td>          
        </s:if>
        <s:else>
            <td> &nbsp; </td>
            <td> No Results </td>
            <td> &nbsp; </td>
            <td> &nbsp; </td>
        </s:else>
    </tr>
        
</table>

<%--
<p>
R Script LOG: <s:property value="@cfe.model.CfeResultsFileType@DISCOVERY_R_SCRIPT_LOG" />
</p>
--%>
         
<%-- FOR DEBUGGING:
<table class="dataTable">
    <tr>
        <th>Number</th> <th>Probeset</th> <th>Gene</th>
    </tr>
    <s:iterator value="probesetToGeneMap" var="pgmap" status="pstat">
        <tr>
            <td>
                <s:property value="#pstat.index"/>
            </td>
            <td>
                <s:property value="key"/>
            </td>
            <td>
                <s:property value="value"/>
            </td>
        </tr>
    </s:iterator>
</table>
--%>

</tiles:putAttribute>
</tiles:insertTemplate>
