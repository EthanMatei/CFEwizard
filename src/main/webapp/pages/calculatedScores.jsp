<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
 
<tiles:insertTemplate template="/pages/template.jsp" flush="true">

<tiles:putAttribute name="header">
    <title>CFE Wizard - Calculated Scores</title>
    <s:head />
</tiles:putAttribute>
<tiles:putAttribute name="content">


<s:url var="download" namespace="/pages" action="downloadScores" ></s:url>

<div style="float:right">
<table style="border:0">
<tr>
<td style="padding-left:1em">
<s:a action="Report" title="Download Excel spreadsheet">
    <s:param name="reportName" value="'scores'" />
    <s:param name="reportFormat" value="'xlsx'" />
    <div style="text-align:center">
    <img border="0"
     style="margin-top: 2px;"
     src="<s:url includeParams='none' value='/images/application-vnd.ms-excel.png'/>"
     alt="SCORES" /> <br />
    cfe-scores.xlsx
    </div>
</s:a>
</td>
</tr>
</table>
</div>

<div style="clear:both;"></div>



<%--
<s:form action="Report">
    <s:hidden name="reportName" value="scores"/>
    <s:hidden name="reportFormat" value="xlsx"/>
    <s:submit value="Excel"/>
</s:form>
--%>

            
<h1 style="padding-top:0px; margin-top:4px;">CFE Scores </h1>
<s:actionerror />


<p>
Discovery Weight: <s:number name="cfeScores.discoveryWeight" minimumFractionDigits="1"/> <br/>
Prioritization Weight: <s:number name="cfeScores.prioritizationWeight" minimumFractionDigits="1"/> <br/>
Validation Weight: <s:number name="cfeScores.validationWeight" minimumFractionDigits="1"/> <br/>
Testing Weight: <s:number name="cfeScores.testingWeight" minimumFractionDigits="1"/> <br/>
</p>

<table class="dataTable">
<tr>
    <th> Probeset </th>
    <th> GeneCards Symbol </th>
    <th> Gene Title </th>
    <th> Change in expression<br/>in tracked phene </th>
    <th> Discovery<br/>Score </th>
    <th> Prioritization<br/>Score </th>
    <th> Validation<br/>Score </th>
    <th> Testing<br/>Score </th>
    <th> TOTAL<br/>Score </th>
</tr>


<s:iterator value="scores" var="var" status="rstatus">
    <tr>
        <td> <s:property value="key"/> </td>
        <td> <s:property value="value.geneCardsSymbol"/> </td>
        <td> <s:property value="value.geneTitle"/> </td>
        <td> <s:property value="value.changeInExpressionInTrackedPhene"/> </td>
        <td style="text-align: right;"> <s:property value="value.discoveryScore"/> </td>
        <td style="text-align: right;"> <s:number name="value.prioritizationScore" minimumFractionDigits="1"/> </td>
        <td style="text-align: right;"> <s:property value="value.validationScore"/> </td>
        <td style="text-align: right;"> <s:property value="value.testingScore"/> </td>
        <td style="text-align: right;"> <s:number name="value.totalScore" minimumFractionDigits="1"/> </td>                           
    </tr>
</s:iterator>
</table>

<br />
<br />


</tiles:putAttribute>
</tiles:insertTemplate>
