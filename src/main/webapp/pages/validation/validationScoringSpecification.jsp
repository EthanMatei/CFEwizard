<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
 
<tiles:insertTemplate template="/pages/template.jsp" flush="true">

<tiles:putAttribute name="header">
    <title>CFE Wizard - Validation Scoring - Validation Scoring Specification</title>
    <s:head />
    <script src="<s:url includeParams='none' value='/js/jquery-3.6.0.min.js'/>"></script> 
    <script src="<s:url includeParams='none' value='/js/jquery.fancytree-all-deps.min.js'/>"></script> 
</tiles:putAttribute>
<tiles:putAttribute name="content">

<h2>Validation Scoring - Validation Scoring Specification</h2>

<s:actionerror />

<s:if test="errorMessage != null && errorMessage != ''">
    <div class="cfeError">
        <span style="font-weight: bold;">ERROR:</span> <s:property value="errorMessage" />
    </div>
</s:if>

<p>
    Validation Data ID: <s:property value="validationDataId" />
</p>

<p>
    Prioritization ID: <s:property value="prioritizationId" />
</p>

<p>
Score cutoff (&ge;): <s:property value="scoreCutoff" />
</p>

<p>
Phene: <s:property value="phene" />
</p>

<p>
    Validation Master Sheet:
    <s:a action="CsvTextFileDisplay" title="Validation Mastersheet">
        <s:param name="textFilePath" value="validationMasterSheetFile" />
        <s:property value="validationMasterSheetFile" />
    </s:a>
</p>

<p>
    Predictor List:
    <s:a action="CsvDisplay">
        <s:param name="csvFilePath" value="predictorListFile" />
        <s:property value="predictorListFile" />
    </s:a>
</p>

<s:form action="ValidationScoresCalculation" theme="simple" method="post" enctype="multipart/form-data">
    <s:hidden name="phene" />
    <s:hidden name="validationMasterSheetFile" />
    <s:hidden name="predictorListFile" />

    <s:submit value="Calculate" style="margin-top: 17px; padding-left: 2em; padding-right: 2em; font-weight: bold;"/>
    <s:token/>
</s:form>

</tiles:putAttribute>
</tiles:insertTemplate>
