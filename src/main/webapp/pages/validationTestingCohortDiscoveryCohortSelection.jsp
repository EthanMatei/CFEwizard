<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
 
<tiles:insertTemplate template="/pages/template.jsp" flush="true">

<tiles:putAttribute name="header">
    <title>CFE Wizard - Validation and Testing Cohorts - Discovery Cohort Selection</title>
    <s:head />
    <script src="<s:url includeParams='none' value='/js/jquery-3.6.0.min.js'/>"></script> 
    <script src="<s:url includeParams='none' value='/js/jquery.fancytree-all-deps.min.js'/>"></script> 
</tiles:putAttribute>
<tiles:putAttribute name="content">

<h2>Validation and Testing Cohorts - Discovery Cohort Selection</h2>

<s:actionerror />

<p style="font-weight: bold;">
Select a Discovery Cohort:
</p>

<s:form action="ValidationTestingCohortSpecification" theme="simple">
<table class="dataTable">
    <tr> 
        <th>ID</th>
        <th>Results</th>
        <th>Time Generated</th>
        <th>Phene</th>
        <th>Phene Low Cutoff</th>
        <th>Phene High Cutoff</th>
    </tr>

    <s:iterator value="discoveryResultsList" var="result">
        <tr>
            <td>
                 <s:radio name="discoveryId" list="{discoveryResultsId}"/>
            </td>
            <td>
                <s:a action="DiscoveryResultsXlsxDisplay" title="Discovery Results">
                    <s:param name="discoveryResultsId" value="discoveryResultsId" />
                    discovery-results.xlsx
                 </s:a>
            </td>
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
