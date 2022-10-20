<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
 
<tiles:insertTemplate template="/pages/template.jsp" flush="true">

<tiles:putAttribute name="header">
    <title>CFE Wizard - Validation Scoring - Validation Data Selection</title>
    <s:head />
    <script src="<s:url includeParams='none' value='/js/jquery-3.6.0.min.js'/>"></script> 
    <script src="<s:url includeParams='none' value='/js/jquery.fancytree-all-deps.min.js'/>"></script> 
</tiles:putAttribute>
<tiles:putAttribute name="content">

<h2>Validation Scoring - Validation Data Selection</h2>

<s:actionerror />

<s:if test="errorMessage != null && errorMessage != ''">
    <div class="cfeError">
        <span style="font-weight: bold;">ERROR:</span> <s:property value="errorMessage" />
    </div>
</s:if>


<s:form action="ValidationScoringSpecification" theme="simple" method="post" enctype="multipart/form-data">

<%--
<p>
Probeset to Gene Mapping Database: <s:file name="probesetMappingDb"/>
</p>
--%>

<p>
Gene Expression CSV File
<s:file name="geneExpressionCsv" />
</p>
        
<p>
Score cutoff (&ge;): <s:textfield style="text-align: right;" name="scoreCutoff" />
<span style="margin-left: 1em;">Comparison Threshold:</span>
<s:textfield size="8" style="text-align: right;" name="comparisonThreshold"/>
</p>


<p style="font-weight: bold;">
Select Validation Data:
</p>
            
<table class="dataTable">
    <caption>Validation Cohorts</caption>
    <tr> 
        <th>ID</th>
        <th>Results</th>
        <th>Results Type</th>
        <th>Time Generated</th>
        <th>Phene</th>
        <th>Phene Low Cutoff</th>
        <th>Phene High Cutoff</th>
    </tr>

    <s:iterator value="validationCohorts" var="result">
        <tr>
            <td>
                 <s:radio name="validationDataId" list="{cfeResultsId}"/>
            </td>
            <td>
                <s:a action="CfeResultsXlsxDisplay" title="Validation Cohorts">
                    <s:param name="cfeResultsId" value="cfeResultsId" />
                    results.xlsx
                 </s:a>
            </td>
            <td> <s:property value="resultsType"/>
            <td> <s:date name="generatedTime" format="MM/dd/yyyy hh:mm"/> </td>
            
            <td> <s:property value="phene"/> </td>
            <td style="text-align: right;"> <s:property value="lowCutoff"/> </td>
            <td style="text-align: right;"> <s:property value="highCutoff"/> </td>
        </tr>
    </s:iterator>

</table>

<%--
<table class="dataTable" style="margin-top: 17px;">
    <caption>Prioritization Scores</caption>
    <tr> 
        <th>ID</th>
        <th>Results</th>
        <th>Results Type</th>
        <th>Time Generated</th>
    </tr>

    <s:iterator value="prioritizationScores" var="result">
        <tr>
            <td>
                 <s:radio name="prioritizationId" list="{cfeResultsId}"/>
            </td>
            <td>
                <s:a action="CfeResultsXlsxDisplay" title="Discovery Results">
                    <s:param name="cfeResultsId" value="cfeResultsId" />
                    prioritization-results.xlsx
                 </s:a>
            </td>
            <td> <s:property value="resultsType"/>
            <td> <s:date name="generatedTime" format="MM/dd/yyyy hh:mm"/> </td>
        </tr>
    </s:iterator>
--%>

</table>

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

<s:submit value="Select" style="margin-top: 17px; margin-bottom: 17px; padding-left: 2em; padding-right: 2em; font-weight: bold;"/>

<s:token/>
</s:form>

</tiles:putAttribute>
</tiles:insertTemplate>
