<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
 
<tiles:insertTemplate template="/pages/template.jsp" flush="true">

<tiles:putAttribute name="header">
    <title>Prioritization - Scoring Weights</title>
    <s:head />
</tiles:putAttribute>
<tiles:putAttribute name="content">

<h1>Prioritization - Global Scoring Weights</h1>
<s:actionerror />
Enter the scoring weights:
<s:form action="PrioritizationScoringWeightsProcess" >
    <s:hidden name="discoveryId"/>
    <s:hidden name="discoveryScoreCutoff"/>
    <s:hidden name="geneListFileName"/>
    
    <s:iterator value="@cfe.enums.prioritization.ScoringWeights@values()" var="scoringWeight">
	    <s:textfield label= "%{#scoringWeight.label}" name="%{#scoringWeight.name}" value="%{#scoringWeight.score}"
	                 cssStyle="text-align:right;"/>
    </s:iterator>
    <s:submit value="Next" />
    <s:token />
</s:form>

</tiles:putAttribute>
</tiles:insertTemplate>