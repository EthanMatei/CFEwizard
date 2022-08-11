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

 <!-- http://stackoverflow.com/questions/5633949/jquery-solution-for-dynamically-uploading-multiple-files -->
 <!-- http://stackoverflow.com/questions/8906910/struts2-dynamically-add-remove-list-of-objects-from-page -->
<s:actionerror />

<s:if test="includeNonDiscoveryOptions == null || includeNonDiscoveryOptions">
    <hr/>

    <s:form id ="uploadGeneForm" theme="simple" action="PrioritizationAllGenesProcess"
            method="post" enctype="multipart/form-data">

        <p>
	    <s:submit value="Use All Genes" name="allGenesButton" style="font-weight: bold;"/>
        </p>
        <s:token/>
    </s:form>

    <hr />

    <s:form id ="uploadGeneForm" theme="simple" action="PrioritizationGeneListUploadProcess"
        method="post" enctype="multipart/form-data">

        <p>
        <s:submit value="Upload Gene List File" name="geneListButton" style="font-weight: bold;"/>
        <s:file style="margin-left: 1em;" name="upload" label="Gene List File" />
        </p>
        <s:token/>
    </s:form>

    <hr />
</s:if>

<s:form id ="processDiscoveryGeneForm" theme="simple" action="PrioritizationDiscoveryGeneListProcess" method="post">    
    <p>
    <s:submit value="Generate Gene List from Discovery Results" name="geneListButton"
          style="font-weight: bold;"/>
    
    </p>
    
    <p>
    Discovery score cutoff: <s:textfield style="text-align: right;" name="discoveryScoreCutoff" size="4"/>

    <span style="margin-left: 1em;">Comparison Threshold:</span>
    <s:textfield size="8" style="text-align: right;" name="comparisonThreshold"/>


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
