<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
 
<tiles:insertTemplate template="/pages/template.jsp" flush="true">

<tiles:putAttribute name="header">
    <title>CFE Wizard - Database Upload</title>
    <link rel="stylesheet" href="/js/jquery-ui.min.css">
    <link rel="stylesheet" href="/js/jquery-ui.structure.min.css">
    <link rel="stylesheet" href="/js/jquery-ui.theme.min.css">
    <script src="<s:url includeParams='none' value='/js/jquery-3.6.0.min.js'/>"></script> 
    <script src="<s:url includeParams='none' value='/js/jquery-ui.min.js'/>"></script> 
    <script src="<s:url includeParams='none' value='/js/diseases.js'/>"></script> 
    <script>
        $( function() {
            var discoveryPhenes = [
                <s:set var="isFirst" value="true"/>   
                <s:iterator value="discoveryPheneList" var="phene">
                    <s:if test="isFirst">
                        "<s:property value="phene"/>"
                        <s:set var="isFirst" value="false"/>
                    </s:if>
                    <s:else>
                        , "<s:property value="phene"/>"
                    </s:else>
                </s:iterator>
            ];
            /*
            $( "#discoveryPhenes" ).autocomplete({
                source: discoveryPhenes
            });
 
            */
            $( "#phenomicDbCheckDialog" ).dialog({
            	title: "Phenomic Database Status Details",
            	dialogClass: "phenomic-db-check-dialog",
                autoOpen: false,
                width: 940,
                height: 540
            });
            
            $( "#phenomicDbCheckDetails" ).on( "click", function() {
                $( "#phenomicDbCheckDialog" ).dialog( "open" );
                return false;
            });
            
            $( "#manualDiseasesSpecification" ).accordion({
            	active: false,
                collapsible: true
            });
        }); 
        

    </script>
    <s:head />
</tiles:putAttribute>
<tiles:putAttribute name="content">

<s:include value="/pages/batch/steps.jsp"/>

<s:include value="/pages/error_include.jsp"/>
        
<%-- <s:actionerror /> --%>


<s:form id ="dataInputForm" theme="simple" name="dataInputForm"
        action="BatchCalculate"
        method="post" enctype="multipart/form-data">

    <script>
    $( function() {
    	$("#dataInputForm").on('submit', function(event) {
    	    $('input[type=submit]').prop('disabled', true);
            $("body").css("cursor", "wait");
    	});
    });
    </script>
        
    <s:hidden name="testingDbTempFileName"/>
    <s:hidden name="testingDbFileName"/>
    
    <s:hidden name="startingCfeResultsId"/>
    
    <s:hidden name="startingResultsType"/>
    <s:hidden name="endingResultsType"/>
    
    <s:hidden name="skipValidationSteps"/>
    
    <%-- PHENOMIC (TESTING) DATABASE STATUS ====================================================== --%>
    <fieldset class=dataInput>
        <legend>Phenomic Database Status</legend>
        <p>
        Database File: <s:property value="testingDbFileName"/>
        </p>
        
        <div style="float: left;">
            <table class="dataTable">
                <tr>
                    <td>
                       <s:if test="phenomicDatabaseWarningCount > 0">
                           <span style="font-weight: bold; color: #FF8000;">Warnings:</span>
                       </s:if>
                       <s:else>
                           Warnings:
                       </s:else>
                    </td>
                    <s:if test="phenomicDatabaseWarningCount > 0">
                        <td style="text-align: right; font-weight: bold; color:  #FF8000;"> <s:property value="phenomicDatabaseWarningCount"/> </td>
                    </s:if>
                    <s:else>
                        <td style="text-align: right;"> <s:property value="phenomicDatabaseWarningCount"/> </td>
                    </s:else>
                </tr>
                <tr>
                    <td>
                        <s:if test="phenomicDatabaseErrorCount > 0">
                            <span style="font-weight: bold; color: red;">Errors:</span>
                        </s:if>
                        <s:else>
                            Errors:
                        </s:else> 
                    </td>
                    <s:if test="phenomicDatabaseErrorCount > 0">
                        <td style="text-align: right; font-weight: bold; color: red;"> <s:property value="phenomicDatabaseErrorCount"/> </td>
                    </s:if>
                    <s:else>
                        <td style="text-align: right;"> <s:property value="phenomicDatabaseErrorCount"/> </td>
                    </s:else>
                </tr>
            </table> 
        </div>
        
        <div style="float: left; margin-left: 1em; margin-top: 16px;">
            <button id="phenomicDbCheckDetails">Details</button>
        </div>
        
        <div style="clear: both;"></div>

        
        <div id="phenomicDbCheckDialog">
			<s:iterator value="tableCheckInfos">
			    <hr/>
			    <div>
			        <span style="font-weight: bold;">TABLE "<s:property value="name"/>"</span>
			        <ul>
			            <s:if test="!columns.isEmpty()">
			                <li><span style="font-weight: bold;">COLUMNS:</span> <s:property value="columnsString"/></li>
			            </s:if>
			
			            <s:iterator value="errors" var="error">
			                <li style="color: red;"><span style="color: red;"><i class="fa fa-exclamation-triangle"></i> ERROR: <s:property value="error"/></span></li>
			            </s:iterator>
			            
			            <s:iterator value="warnings" var="error">
			                <li style="color: #FF8000;"><span><i class="fa fa-exclamation-triangle"></i> WARNING: <s:property value="error"/></span></li>
			            </s:iterator>
			        </ul>
			    </div>
			
			</s:iterator>
        </div>
    </fieldset>
    
    <div>&nbsp;</div>
        
    <%--GENE EXPRESSION FILE (NON-DISCOVERY) ====================================================== --%>
    <s:if test="showValidationScores || showTestingScores"> 
        <fieldset class=dataInput>
            <legend>Data Files</legend>
        
            <table class="dataTable">
                <tr> <th>Data</th> <th>File</th> </tr>
                <tr>
                     <td> Gene Expression CSV File</td>
                    <td> <s:file name="geneExpressionCsv" label="Gene Expression CSV" /> </td>
                </tr>          
            </table>
        </fieldset>
        <div>&nbsp;</div>
    </s:if>
    

    
    <%-- DISCOVERY COHORTS ========================================================================= --%>
    <s:if test="showDiscoveryCohort">
        <fieldset class="dataInput">
            <legend>Discovery Cohort</legend>

            <p>
            Phene: <s:select name="discoveryPheneInfo" list="discoveryPheneList"/>
            </p>
        
            <p>
            Low Cutoff (&le;): <s:textfield name="discoveryPheneLowCutoff" size="6" style="text-align: right;"/>
            <span style="margin-left: 1em;">Comparison threshold:</span>
            <s:textfield size="8" style="text-align: right; margin-left: 1em;" name="discoveryCohortComparisonThreshold"/>
            </p>
        
            <p>
            High Cutoff (&ge;): <s:textfield name="discoveryPheneHighCutoff" size="6" style="text-align: right;"/>
            </p>
        
            <p>
            Genomics table <s:select name="genomicsTable" list="genomicsTables"/>
            </p>

            <%-- DIAGNOSIS --%>        
        </fieldset>
    </s:if>

    <%-- DISCOVERY SCORES ============================================================================= --%>
    <s:if test="showDiscoveryScores">
        <div>&nbsp;</div>
        
        <fieldset class="dataInput">
            <legend>Discovery Scoring</legend>
        
            <table class="dataTable">
                <tr> <th>Data</th> <th>File</th> </tr>
                <tr>
                    <td> Probeset to Gene Mapping Database </td>
                    <td> <s:file name="probesetToGeneMappingDb" label="Probeset to Gene Mapping Database" /> </td>
                </tr>
                <tr>
                    <td> Discovery Gene Expression CSV File</td>
                    <td> <s:file name="discoveryGeneExpressionCsv" label="Discovery Gene Expression CSV" /> </td>
                </tr>            
            </table>
        
            <p>
            Diagnosis: <s:select name="diagnosisCode" list="diagnosisCodesList"/>
            </p>

            <table class="dataTable" id="scoreTable">
                <thead>
                    <tr>
                        <th>Percentile Range</th> <th>Score</th>
                    </tr>
                </thead>
    
                <tbody>
                    <s:iterator value="discoveryPercentileScores.lowerBounds" var="lowerBound" status="status">
                        <tr>
                            <td>
                               <%--
                                <span id="lower<s:property value='#status.index'/>" style="text-align: right;margin-left: 1em; width: 10em; display: inline-block;">
                                    <s:property value="lowerBound" />
                                </span>
                
                                &le; 
                                
                                --%>
                                &nbsp; x &lt;
                
                                <s:set value="%{'upper' + #status.index}" var="upperId" />
                
                                <s:if test="#status.last">
                                    <s:textfield size="8" cssStyle="text-align: right;margin-left: 1em" readonly="true"
                                        name="discoveryPercentileScores.upperBounds[%{#status.index}]"
                                        id="%{upperId}"
                                    />
                                </s:if>
                                <s:else>
                                    <s:textfield size="8" cssStyle="text-align: right;margin-left: 1em"
                                                 name="discoveryPercentileScores.upperBounds[%{#status.index}]"
                                                 id="%{upperId}"
                                    />
                                </s:else>
                            </td>
                            <td>
                                <s:textfield size="7" cssStyle="text-align: right;margin-left: 1em"
                                             name="discoveryPercentileScores.scores[%{#status.index}]"
                                />
                            </td>
                        </tr>
                    </s:iterator>
                </tbody>
            </table>
            
            <%--         
            <table class="dataTable">
                <thead>
                    <tr>
                        <th>Percentile Range</th><th>Score</th>
                    </tr>
                </thead>
                <tbody>
                    <s:iterator value="discoveryPercentileScores.lowerBounds" var="lowerBound" status="status">
                        <tr>
                            <td>
                                <span style="text-align: right;margin-left: 1em; width: 10em; display: inline-block;">
                                    <s:property value="lowerBound"/>
                                </span>
                                &le; x &lt;
                                <span style="text-align: right;margin-left: 1em; width: 10em; display: inline-block;">
                                    <s:property value="discoveryPercentileScores.upperBounds[#status.index]"/>
                                </span>
                            </td>
                            <td>
                                <s:textfield size="7" cssStyle="text-align: right;margin-left: 1em"
                                    name="discoveryPercentileScores.scores[%{#status.index}]"
                                />
                            </td>
                        </tr>
                    </s:iterator>            
                </tbody>
            </table>
            --%>
            
            <p>
            <s:checkbox name="debugDiscoveryScoring"/> Debug
            </p>
            
        </fieldset>
    </s:if>
    
    
    
    <%-- PRIORITIZATION ===================================================================================== --%> 
    <s:if test="showPrioritizationScores">
 
        <div>&nbsp;</div>
   
        <fieldset class="dataInput">
            <legend>Prioritization</legend>
        
            <fieldset class="dataInputLevel2">
                <legend>Gene List Specification</legend>
                    <p>
                    <s:radio name="geneListSpecification" list="{'All'}"/>
                    </p>
                
                    <p>
                    <s:radio name="geneListSpecification" list="{'Upload File:'}"/>
                    <s:file name="geneListUpload" style="margin-left: 1em;"/>
                    </p>
                
                    <p>
                    <s:radio name="geneListSpecification" list="{'Generate from Discovery:'}"/>
                    <span style="margin-left: 2em;">Score cutoff (&ge;)</span>
                    <s:textfield style="text-align: right;" name="prioritizationScoreCutoff" size="4"/>
                    <span style="margin-left: 1em;">Comparison threshold:</span>
                    <s:textfield size="8" style="text-align: right; margin-left: 1em;" name="prioritizationComparisonThreshold"/>
                    </p>
            </fieldset>
        
            <fieldset class="dataInputLevel2">
                <legend>Global Scoring Weights</legend>
                <table>
                    <s:iterator value="@cfe.enums.prioritization.ScoringWeights@values()" var="scoringWeight">
                        <tr>
                            <td>
                                <s:property value="%{#scoringWeight.label}"/> &nbsp;
                            </td>
                            <td>
	                            <s:textfield name="%{#scoringWeight.name}" value="%{#scoringWeight.score}"
	                                         cssStyle="text-align:right;"/>
                            </td>
                        </tr>
                    </s:iterator>
                </table>
            </fieldset>
        
            <fieldset class="dataInputLevel2">
                <legend>Diseases</legend>
                Diseases CSV File: <s:file style="margin-left: 1em;" name="diseasesCsv"/>
                    
                <s:a action="PrioritizationReport" style="margin-left: 4em;">
                    <s:param name="reportName" value="'diseases-with-coefficients'" />
                    <s:param name="reportFormat" value="'csv'" />
                    diseases.csv
                </s:a>
                
                <!-- <div id="manualDiseasesSpecification" style="margin-top: 7px;"> -->
                <!--
                <div style="margin-top: 7px;">
                    <h3 style="font-size: 90%;">Manual Diseases Specification (WORK IN PROGRESS)</h3>
                    <div>
                        <s:include value="/pages/diseases_include.jsp"/>
                    </div>
                </div>
                -->
                
            </fieldset>
        </fieldset>
    </s:if>
    

    
    
    <%-- VALIDATION COHORT ===================================================================================== --%> 

    <s:if test="showValidationCohort && !skipValidationSteps">    
        <div>&nbsp;</div>
    
        <fieldset class="dataInput">
            <legend>Validation Cohort</legend>
        
            <table class="dataTable">
                <tr> <th>Phene</th> <th>Relation</th> <th>Value</th> </tr>
                <tr>
                    <td> <s:select name="phene1" list="phenes" /> </td>
                    <td style="text-align: center"> <s:select name="operator1" list="operators" /> </td>
                    <td> <s:textfield name="value1" value="" size="4"  style="text-align: right;"/> </td>
                </tr>
                <tr>
                    <td> <s:select name="phene2" list="phenes" /> </td>
                    <td style="text-align: center"> <s:select name="operator2" list="operators" /> </td>
                    <td> <s:textfield name="value2" value="" size="4" style="text-align: right;"/> </td>
                </tr>
                <tr>
                    <td> <s:select name="phene3" list="phenes" /> </td>
                    <td style="text-align: center"> <s:select name="operator3" list="operators" /> </td>
                    <td> <s:textfield name="value3" value="" size="4" style="text-align: right;"/> </td>
                </tr>
            </table>
        
            <div style="margin-top: 14px; margin-bottom: 14px;">
                % Subjects in Validation Cohort:
                <s:textfield name="percentInValidationCohort" value="50" size="4" style="text-align: right;"/>
            </div>
        
            <p>
            Comparison threshold:
            <s:textfield size="8" style="text-align: right; margin-left: 1em;" name="validationCohortComparisonThreshold"/>
            </p>
        </fieldset>
    </s:if>
    
    
    <%-- VALIDATION SCORES ===================================================================================== --%>
    
    <s:if test="showValidationScores && !skipValidationSteps">    
 
    <div>&nbsp;</div>
    <fieldset class="dataInput">
        <legend>Validation Scores</legend>
             
        <p>
        Diagnosis type: <s:select name="validationDiagnosisType" list="diagnosisTypes" />
        </p>
                  
        <p>
        Score cutoff (&ge;): <s:textfield style="text-align: right;" name="validationScoreCutoff" size="4"/>
        <span style="margin-left: 1em;">Comparison threshold:</span>
        <s:textfield size="8" style="text-align: right; margin-left: 1em;" name="validationScoresComparisonThreshold"/>
        </p>
        
        <table class="dataTable" style="margin-top: 17px;">
            <tr>
                <td>Bonferroni Score</td>
                <td> <s:textfield style="text-align: right;" name="bonferroniScore" size="4"/> </td> 
            </tr>
            <tr>
                <td>Nominal Score</td>
                <td> <s:textfield  style="text-align: right;" name="nominalScore"  size="4"/></td>
            </tr>
            <tr>
                <td>Stepwise Score</td>
                <td> <s:textfield  style="text-align: right;" name="stepwiseScore"  size="4"/></td>
            </tr>
            <tr>
                <td>Non-Stepwise Score</td>
                <td> <s:textfield  style="text-align: right;" name="nonStepwiseScore"  size="4"/></td>
            </tr>
        </table>        
         
        <p>
        Updated Master Sheet CSV File (optional): <s:file name="updatedValidationMasterSheet"/>
        </p>

        <p>
        Updated Predictor List CSV File (optional): <s:file name="updatedValidationPredictorList"/>
        </p>
          
    </fieldset> 
    </s:if>
 
    <%-- TESTING INPUTS (for starting at Testing Phase) ================================================= --%>
    <%--
    <s:if test="showTestingInputs">    
    
        <div>&nbsp;</div>
        <fieldset class="dataInput">
            <legend>Testing Inputs</legend>

            <p>
            Testing Input Spreadsheet: <s:file name="testingInputSpreadsheet" />
            </p>
              
            <p>
            Phene: <s:select name="discoveryPheneInfo" list="discoveryPheneList"/>
            </p>
        </fieldset>
    </s:if>   
    --%>
        
    <%-- TESTING COHORTS ================================================================================= --%>
    <s:if test="showTestingCohorts">    
    
        <div>&nbsp;</div>
        <fieldset class="dataInput">
            <legend>Testing Cohorts</legend>

            <p>
            Follow-Up Database: <s:file name="followUpDb" label="Follow-Up Database" />
            </p>
              
            <p>
            Admission phene: <s:select name="admissionPhene" list="admissionReasons"/>
            </p>
        </fieldset>
    </s:if>
    

        
    <%-- TESTING SCORES ================================================================================= --%>
    <s:if test="showTestingScores">    
                             

        <div>&nbsp;</div>
        <fieldset class="dataInput">
            <legend>Testing Scores</legend> 
            
            <p>
            Diagnosis type: <s:select name="testingDiagnosisType" list="diagnosisTypes" />
            </p>
                               
            <p>
            Score cutoff (&ge;): <s:textfield style="text-align: right;" name="testingScoreCutoff" />
            <span style="margin-left: 1em;">Comparison threshold:</span>
            <s:textfield size="8" style="text-align: right; margin-left: 1em;" name="testingComparisonThreshold"/>
            </p>
        
            <p>
            Updated Predictor List CSV File (optional)
            <s:file name="updatedTestingPredictorList" />
            </p>

            <p>
            Updated Master Sheet CSV File (optional)
            <s:file name="updatedTestingMasterSheet" />
            </p>
    
            <table class="dataTable">
                <tr>
                    <td>
                        <span style="font-weight: bold;">State</span>
                    </td>
                    <td>
                        <p>
                        <s:checkbox name="stateCrossSectional"/>Cross-Sectional
                        </p>
                
                        <p>
                        <s:checkbox name="stateLongitudinal"/>Longitudinal
                        </p>
                
                        <p>
                        Phene: <s:select name="predictionPhene" list="phenes" />
                        </p>
                
                        <p>
                        High Cutoff (&ge;):
                        <s:textfield size="5" style="text-align: right;" name="predictionPheneHighCutoff"/>
                
                        <span style="margin-left: 2em;">Comparison Threshold:</span>
                        <s:textfield size="10" style="text-align: right;" name="predictionPheneComparisonThreshold"/>
                        </p>
                    </td>
                </tr>
                <tr>
                    <td>
                        <span style="font-weight: bold;">First Year</span>
                    </td>
                    <td>
                        <p>
                        <s:checkbox name="firstYearCrossSectional"/>Cross-Sectional
                        </p>
                        <p>
                        <s:checkbox name="firstYearLongitudinal"/>Longitudinal
                        </p>
                    </td>
                </tr>
                <tr>
                    <td>
                        <span style="font-weight: bold;">All Future</span>
                    </td>
                    <td>
                        <p>
                        <s:checkbox name="futureCrossSectional"/>Cross-Sectional
                        </p>
                        <p>
                        <s:checkbox name="futuretLongitudinal"/>Longitudinal
                        </p>
                    </td>
                </tr>
            </table>     
        </fieldset>
    </s:if>
        
    <!-- <s:submit value="Calculate" id="calculateButton" onclick="submitForm();" /> -->
    <p>
    <s:submit value="Run" id="calculateButton" style="font-size: 125%; font-weight: bold; padding-left: 1em; padding-right: 1em;"/>
    </p>
    
    <s:token />

</s:form>

<s:iterator value="validationMsgs" status="vstat">
    <s:property value="validationMsgs[%{#vstat.index}]" />
</s:iterator>

</tiles:putAttribute>
</tiles:insertTemplate>
