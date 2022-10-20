<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
 
<tiles:insertTemplate template="/pages/template.jsp" flush="true">

<tiles:putAttribute name="header">
    <title>CFE Wizard - Testing Scoring - Testing Data Selection</title>
    <s:head />
    <script src="<s:url includeParams='none' value='/js/jquery-3.6.0.min.js'/>"></script> 
    <script src="<s:url includeParams='none' value='/js/jquery.fancytree-all-deps.min.js'/>"></script> 
</tiles:putAttribute>
<tiles:putAttribute name="content">

<h2>Testing Scoring - Testing Data Selection</h2>

<s:actionerror />

<s:if test="errorMessage != null && errorMessage != ''">
    <div class="cfeError">
        <span style="font-weight: bold;">ERROR:</span> <s:property value="errorMessage" />
    </div>
</s:if>


<s:form action="TestingScoringSpecification" theme="simple" method="post" enctype="multipart/form-data">

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
Select Testing Data:
</p>    
<table class="dataTable">
    <tr> 
        <th>ID</th>
        <th>Results</th>
        <th>Results Type</th>
        <th>Time Generated</th>
        <th>Discovery Phene</th>
        <th>Discovery Phene Low Cutoff</th>
        <th>Discovery Phene High Cutoff</th>
    </tr>

    <s:iterator value="cfeResults" var="result">
        <tr>
            <td>
                 <s:radio name="testingDataId" list="{cfeResultsId}"/>
            </td>
            <td>
                <s:a action="CfeResultsXlsxDisplay" title="CFE Results">
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




<s:submit value="Select" style="margin-top: 17px; margin-bottom: 17px; padding-left: 2em; padding-right: 2em; font-weight: bold;"/>
<s:token/>
</s:form>

</tiles:putAttribute>
</tiles:insertTemplate>
