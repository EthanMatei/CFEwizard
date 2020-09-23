<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
 
<tiles:insertTemplate template="/pages/template.jsp" flush="true">

<tiles:putAttribute name="header">
    <title>CFE Wizard - Validation Weights</title>
    <s:head />
</tiles:putAttribute>
<tiles:putAttribute name="content">

<h1>Validation Scoring Weights</h1>

<s:actionerror />

<p>
Enter validation weights:
</p>

<s:form action="ValidationWeightsProcess" >

<s:iterator value="@cfe.enums.ValidationWeights@values()" var="validationWeight">
	<s:textfield label= "%{#validationWeight.label}"
	    name="%{#validationWeight.name}"
	    value="%{getText('{0,number,##0.0}',{#validationWeight.weight})}"
	    cssStyle="text-align:right;"/>
</s:iterator>
<s:submit value="Next" />
<s:token />
</s:form>

</tiles:putAttribute>
</tiles:insertTemplate>
