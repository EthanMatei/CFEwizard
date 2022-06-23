<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
 
<tiles:insertTemplate template="/pages/template.jsp" flush="true">

<tiles:putAttribute name="header">
    <title>CFE Wizard - Validation Cohort - Discovery Cohort Selection</title>
    <s:head />
    <script src="<s:url includeParams='none' value='/js/jquery-3.6.0.min.js'/>"></script> 
    <script src="<s:url includeParams='none' value='/js/jquery.fancytree-all-deps.min.js'/>"></script> 
</tiles:putAttribute>
<tiles:putAttribute name="content">

<h2>Testing Cohorts - Validation Cohort Selection</h2>

<s:actionerror />

<p style="font-weight: bold;">
Select a Validation Cohort:
</p>

<s:form action="TestingCohortsSpecification" theme="simple" style="margin-bottom: 24px;">
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

    <s:iterator value="validationResultsList" var="result">
        <tr>
            <td>
                 <s:radio name="validationId" list="{cfeResultsId}"/>
            </td>
            <td>
                <s:a action="CfeResultsXlsxDisplay" title="Validation Results">
                    <s:param name="cfeResultsId" value="cfeResultsId" />
                    cfe-results.xlsx
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
<s:submit value="Select" style="margin-top: 17px; padding-left: 2em; padding-right: 2em; font-weight: bold;"/>
</s:form>

</tiles:putAttribute>
</tiles:insertTemplate>
