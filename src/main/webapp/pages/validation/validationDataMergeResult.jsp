<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
 
<tiles:insertTemplate template="/pages/template.jsp" flush="true">

<tiles:putAttribute name="header">
    <title>CFE Wizard - Prioritization and Discovery Scores Merge Results</title>
    <s:head />
    <script src="<s:url includeParams='none' value='/js/jquery-3.6.0.min.js'/>"></script> 
    <script src="<s:url includeParams='none' value='/js/jquery.fancytree-all-deps.min.js'/>"></script> 
</tiles:putAttribute>
<tiles:putAttribute name="content">

<h2>Prioritization and Discovery Scores Merge Results</h2>


<s:actionerror />

<s:if test="errorMessage != null && errorMessage != ''">
    <div class="cfeError">
        <span style="font-weight: bold;">ERROR:</span> <s:property value="errorMessage" />
    </div>
</s:if>


<s:set var="mergeFileName" value="'discovery-prioritization-merge.xlsx'"/>

<%--
<table class="dataTable">
    <tr>
        <th>ID</th> <th>File</th>
    </tr>
    <tr>
        <td> <s:property value="cfeResultsId"/> </td>
        <td>
            <s:a action="CfeResultsXlsxDisplay" title="Discovery Prioritization Merge">
                <s:param name="cfeResultsId" value="cfeResultsId" />
                <s:param name="fileName" value="mergeFileName" />
                <s:property value="mergeFileName"/>
            </s:a>
        </td>
    </tr>
</table>
--%>

<table class="dataTable" style="margin-top: 12px;">
    <tr>
        <th>ID</th>
        <th>Results</th> <th>Results Type</th> <th>Details</th>
        <th>Time Generated</th>
        <th>Discovery Phene</th> <th>Discovery Phene<br/>Low Cutoff</th> <th>Discovery Phene<br/>High Cutoff</th>
    </tr>
    
    <tr>
        <td> <s:property value="cfeResults.cfeResultsId"/> </td>
        
        <td>
            <s:a action="CfeResultsXlsxDisplay" title="CFE Results">
                <s:param name="cfeResultsId" value="cfeResults.cfeResultsId" />
                results.xlsx
             </s:a>
        </td>
        
        <td> <s:property value="cfeResults.resultsType"/> </td>
            
        <td>
            <s:a action="CfeResultsDetailAction" title="CFE Results Detail">
                <s:param name="cfeResultsId" value="cfeResults.cfeResultsId" />
                details
             </s:a>
        </td>
        
        <td> <s:date name="cfeResults.generatedTime" format="MM/dd/yyyy hh:mm"/> </td>

        <td> <s:property value="cfeResults.phene"/> </td>
            
        <td style="text-align: right;"> <s:property value="cfeResults.lowCutoff"/> </td>
        
        <td style="text-align: right;"> <s:property value="cfeResults.highCutoff"/> </td>

    </tr>
</table>

<%--
<s:if test="true">
    <br/>
    <s:a action="ValidationCohortSpecification" title="Validation Cohort Creation" class="linkButton" style="margin-left: 2em;">
        <s:param name="discoveryId" value="cfeResultsId" />
        Validation Cohort Creation
    </s:a>
    <br/>
</s:if>
--%>
                          
</tiles:putAttribute>
</tiles:insertTemplate>
