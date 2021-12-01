<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
 
<tiles:insertTemplate template="/pages/template.jsp" flush="true">

<tiles:putAttribute name="header">
    <title>CFE Wizard - Testing Scoring - Testing Scoring Specification</title>
    <s:head />
    <script src="<s:url includeParams='none' value='/js/jquery-3.6.0.min.js'/>"></script> 
    <script src="<s:url includeParams='none' value='/js/jquery.fancytree-all-deps.min.js'/>"></script> 
</tiles:putAttribute>
<tiles:putAttribute name="content">

<h2>Testing Scoring Specification</h2>

<s:actionerror />

<s:if test="errorMessage != null && errorMessage != ''">
    <div class="cfeError">
        <span style="font-weight: bold;">ERROR:</span> <s:property value="errorMessage" />
    </div>
</s:if>

<p>
Testing Data ID: <s:property value="testingDataId"/>
</p>


<s:form action="TestingScoringCalculation" theme="simple">

    <s:hidden name="lowCutoff" />
    <s:hidden name="highCutoff" />
    <s:hidden name="pheneSelection" />
    <s:hidden name="pheneTable" />
    <s:hidden name="microarrayTable" />

    <s:submit value="Calculate" style="margin-top: 17px; padding-left: 2em; padding-right: 2em; font-weight: bold;"/>
</s:form>

</tiles:putAttribute>
</tiles:insertTemplate>
