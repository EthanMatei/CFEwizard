<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
 
<tiles:insertTemplate template="/pages/template.jsp" flush="true">

<tiles:putAttribute name="header">
    <title>CFE Wizard - Prioritization and Discovery Scores Merge</title>
    <s:head />
    <script src="<s:url includeParams='none' value='/js/jquery-3.6.0.min.js'/>"></script> 
    <script src="<s:url includeParams='none' value='/js/jquery.fancytree-all-deps.min.js'/>"></script> 
</tiles:putAttribute>
<tiles:putAttribute name="content">

<h2>Prioritization and Discovery Scores Merge</h2>

<p>
This page merges Prioritization Scores calculated without Discovery Scores
("prioritization scores only")
with previously calculated Discovery Scores to create a result that
can be used as an starting point in the CFE Pipeline.
</p>


<s:actionerror />

<s:if test="errorMessage != null && errorMessage != ''">
    <div class="cfeError">
        <span style="font-weight: bold;">ERROR:</span> <s:property value="errorMessage" />
    </div>
</s:if>


<s:form action="ValidationDataMerge" theme="simple" method="post" enctype="multipart/form-data">

<p>
<s:submit value="Merge Data" class="submit"
          style="margin-top: 12px; margin-bottom: 12px;"/>
</p>

<div>
<div style="float: left; margin-right: 2em; margin-bottom: 17px;">

<table class="dataTable">
    <caption style="font-weight: bold; color: #000063;">Prioritization Only Scores</caption>
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

</table>
</div>


<div style="float: left;">
            
<table class="dataTable">
    <caption style="font-weight: bold; color: #000063;">Discovery Scores</caption>
    <tr> 
        <th>ID</th>
        <th>Results</th>
        <th>Results Type</th>
        <th>Time Generated</th>
        <th>Phene</th>
        <th>Phene<br/>Low Cutoff</th>
        <th>Phene<br/>High Cutoff</th>
    </tr>

    <s:iterator value="discoveryScores" var="result">
        <tr>
            <td>
                 <s:radio name="discoveryId" list="{cfeResultsId}"/>
            </td>
            <td>
                <s:a action="CfeResultsXlsxDisplay" title="Discovery Scores">
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
</div>
</div>

<div style="clear: both;"></div>




<s:token/>
</s:form>

</tiles:putAttribute>
</tiles:insertTemplate>
