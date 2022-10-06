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
            

        } ); 
        

    </script>
    <s:head />
</tiles:putAttribute>
<tiles:putAttribute name="content">


<h1>Data Input</h1>

        
<s:if test="!errorMessage.trim().isEmpty()">
    <div class="cfeError">
        <span style="font-weight: bold;">ERROR:</span> <s:property value="errorMessage" />
    </div>
</s:if>
        
<s:actionerror />

<s:form id ="dataInputForm" theme="simple" name="dataInputForm"
        action="BatchCalculate"
        method="post" enctype="multipart/form-data">
        
    <s:hidden name="testingDbTempFileName"/>

    <fieldset class=dataInput>
        <legend>Data Files</legend>
        
        <table class="dataTable">
            <tr> <th>Data</th> <th>File</th> </tr>
            <tr>
                <td> Probeset to Gene Mapping Database </td>
                <td> <s:file name="probesetToGeneMappingDb" label="Probeset to Gene Mapping Database" /> </td>
            </tr>
            <tr>
                <td> Follow-Up Database </td>
                <td> <s:file name="followUpDb" label="Follow-Up Database" /> </td>
            </tr>
            <tr>
                <td> Discovery Gene Expression CSV File</td>
                <td> <s:file name="discoveryGeneExpressionCsv" label="Discovery Gene Expression CSV" /> </td>
            </tr>   
            <tr>
                <td> Gene Expression CSV File</td>
                <td> <s:file name="geneExpressionCsv" label="Gene Expression CSV" /> </td>
            </tr>           
        </table>
    </fieldset>
    
    <div>&nbsp;</div>
    
    <fieldset class="dataInput">
        <legend>Discovery</legend>

        <p>
            Phene: <s:select name="discoveryPheneInfo" list="discoveryPheneList"/>
        </p>
        
        <p>
        Low Cutoff (&le;): <s:textfield name="discoveryPheneLowCutoff"/>
        </p>
        <p>
        High Cutoff (&ge;): <s:textfield name="discoveryPheneHighCutoff"/>
        </p>
        <p>
        Genomics table <s:select name="genomicsTable" list="genomicsTables"/>
        </p>

        <%-- DIAGNOSIS --%>        
        
        <p>
        Diagnosis: <s:select name="diagnosisCode" list="diagnosisCodesList"/>
        </p>
        
        <%--
        <h3>Diagnosis</h3>
        <table class="dataTable" style="margin-top: 1em;">
            <tr>
                <th> Diagnosis&nbsp;Code </th>
                <th> Examples </th>
            </tr>
            <tr>
                <td> <s:radio name="diagnosisCode" list="{'All'}" checked="true" style="fontweight:bold;"/> </td>
                <td> (All diagnosis codes will be processed) </td>
            </tr>
            <s:iterator value="diagnosisCodes">
                <tr>
                    <td> <s:radio name="diagnosisCode" list="{key}" /> </td>
                    <td> <s:property value="value"/> </td>
                </tr>
            </s:iterator>
        </table>
        --%>
        
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
    </fieldset>
    
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
                <span style="margin-left: 2em;">Discovery score cutoff</span>
                <s:textfield style="text-align: right;" name="discoveryScoreCutoff" size="4"/>
                <span style="margin-left: 1em;">Comparison threshold</span>
                <s:textfield size="8" style="text-align: right; margin-left: 1em;" name="comparisonThreshold"/>
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
            Diseases CSV File: <s:file style="margin-left: 1em;" name="diseasesCsvFile"/>
                    
            <s:a action="PrioritizationReport" style="margin-left: 4em;">
                <s:param name="reportName" value="'diseases-with-coefficients'" />
                <s:param name="reportFormat" value="'csv'" />
                diseases.csv
            </s:a>
        </fieldset>
        
    </fieldset>
 
    <div>&nbsp;</div>
    
    
    <%-- VALIDATION ===================================================================================== --%>
    <fieldset class="dataInput">
        <legend>Validation</legend>
        
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
        Score cutoff (&ge;): <s:textfield style="text-align: right;" name="validationScoreCutoff" />
        </p>
        
        <table class="dataTable" style="margin-top: 17px;">
            <tr>
                <td>Bonferroni Score</td>
                <td> <s:textfield style="text-align: right;" name="bonferroniScore" /> </td> 
            </tr>
            <tr>
                <td>Nominal Score</td>
                <td> <s:textfield  style="text-align: right;" name="nominalScore" /></td>
            </tr>
            <tr>
                <td>Stepwise Score</td>
                <td> <s:textfield  style="text-align: right;" name="stepwiseScore" /></td>
            </tr>
            <tr>
                <td>Non-Stepwise Score</td>
                <td> <s:textfield  style="text-align: right;" name="nonStepwiseScore" /></td>
            </tr>
        </table>        
          
    </fieldset> 
 
    <div>&nbsp;</div>
        
    <%-- TESTING ===================================================================================== --%>
    <fieldset class="dataInput">
        <legend>Testing</legend>
  
        <p>
        Admission phene: <s:select name="admissionPhene" list="admissionReasons"/>
        </p>
        
                        
        <p>
        Score cutoff (&ge;): <s:textfield style="text-align: right;" name="testingScoreCutoff" />
        </p>
        
    <p>
    Updated predictor list CSV file (optional):
    <s:file name="specialPredictorListCsv" style="margin-left: 1em;"/>
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
                <s:textfield size="10" style="text-align: right;" name="comparisonThreshold"/>
                </p>
            </td>
        </tr>
        <tr>
            <td>
                <span style="font-weight: bold;">First Year Hospitalization</span>
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
                <span style="font-weight: bold;">Future Hospitalization</span>
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
        
    <!-- <s:submit value="Calculate" id="calculateButton" onclick="submitForm();" /> -->
    <p>
    <s:submit value="Calculate" id="calculateButton" style="font-size: 125%; font-weight: bold;"/>
    </p>
    
    <s:token />

</s:form>

<s:iterator value="validationMsgs" status="vstat">
    <s:property value="validationMsgs[%{#vstat.index}]" />
</s:iterator>

</tiles:putAttribute>
</tiles:insertTemplate>
