<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
 
<tiles:insertTemplate template="/pages/template.jsp" flush="true">

<tiles:putAttribute name="header">
    <title>CFE Wizard - Scoring Weights</title>
    <s:head />
</tiles:putAttribute>
<tiles:putAttribute name="content">

<h1>Global Scoring Weights</h1>

<s:actionerror />

<p>
Enter the scoring weights:
</p>

<s:form action="ScoringWeightsProcess" >

<s:iterator value="@cfe.enums.ScoringWeights@values()" var="scoringWeight">
    <s:set var="scoreWeight" value="%{#scoringWeight.weight}"/>
	<s:textfield label= "%{#scoringWeight.label}"
	    name="%{#scoringWeight.name}"
	    value="%{getText('{0,number,##0.0}',{#scoringWeight.weight})}"
	    cssStyle="text-align:right;"/>
</s:iterator>
<s:submit value="Next" />
<s:token />
</s:form>

<!-- value="%{#scoringWeight.score}" -->
	    
</tiles:putAttribute>
</tiles:insertTemplate>
