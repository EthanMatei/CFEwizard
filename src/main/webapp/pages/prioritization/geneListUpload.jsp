<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
 
<tiles:insertTemplate template="/pages/template.jsp" flush="true">

<tiles:putAttribute name="header">
    <title>Prioritization - Gene List Specification</title>
    <s:head />
</tiles:putAttribute>
<tiles:putAttribute name="content">

<h1>Prioritization - Gene List Specification</h1>


<s:if test="errorMessage != null && errorMessage != ''">
    <div class="cfeError">
        ERROR: <s:property value="errorMessage" />
        <p>
        <s:if test="exceptionStack != ''">
            <s:property value="exceptionStack" />
        </s:if>
        </p>
    </div>
</s:if>

<p>
(Optional) Upload the Gene List File:
</p>
<p>
To process all genes, don't specify a file and click on the Upload button.
</p>
 <!-- http://stackoverflow.com/questions/5633949/jquery-solution-for-dynamically-uploading-multiple-files -->
 <!-- http://stackoverflow.com/questions/8906910/struts2-dynamically-add-remove-list-of-objects-from-page -->
<s:actionerror />

<s:form id ="uploadGeneForm" theme="simple" action="PrioritizationGeneListUploadProcess"
        method="post" enctype="multipart/form-data">

    <p>
    <s:file name="upload" label="Gene List File" />
	<s:submit value="Upload Gene List File" name="geneListButton" style="font-weight: bold;"/>
    </p>
    
    <hr />
</s:form>

<s:form id ="processDiscoveryGeneForm" theme="simple" action="PrioritizationDiscoveryGeneListProcess" method="post">    
    <p>
    Discovery score cutoff: <s:textfield style="text-align: right;" name="discoveryScoreCutoff" size="4"/>

    <span style="margin-left: 1em;">Comparison Threshold:</span>
    <s:textfield size="8" style="text-align: right;" name="comparisonThreshold"/>

    <s:submit value="Generate Gene List from Discovery Results" name="geneListButton"
              style="font-weight: bold; margin-left: 2em;"/>
    </p>
    
    Discovery Scoring Results:
    
    <table class="dataTable">
    <tr> 
        <th>ID</th>
        <th>Results</th>
        <th>Results Type</th>
        <th>Time Generated</th>
        <th>Phene</th>
        <th>Phene Low Cutoff</th>
        <th>Phene High Cutoff</th>
    </tr>
        <s:iterator value="discoveryScoringResultsList" var="result">
        <tr>
            <td>
                 <s:radio name="discoveryId" list="{cfeResultsId}"/>
            </td>
            <td>
                <s:a action="CfeResultsXlsxDisplay" title="Discovery Results">
                    <s:param name="cfeResultsId" value="cfeResultsId" />
                    discovery-results.xlsx
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
    <s:token />
</s:form>

</tiles:putAttribute>
</tiles:insertTemplate>
