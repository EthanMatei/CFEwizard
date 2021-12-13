<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
 
<tiles:insertTemplate template="/pages/template.jsp" flush="true">

<tiles:putAttribute name="header">
    <title>CFE Wizard - Validation Scoring - Validation Scores</title>
    <s:head />
    <script src="<s:url includeParams='none' value='/js/jquery-3.6.0.min.js'/>"></script> 
    <script src="<s:url includeParams='none' value='/js/jquery.fancytree-all-deps.min.js'/>"></script> 
</tiles:putAttribute>
<tiles:putAttribute name="content">

<h2>Validation Scoring - Validation Scores</h2>

<s:actionerror />

<s:if test="errorMessage != null && errorMessage != ''">
    <div class="cfeError">
        <span style="font-weight: bold;">ERROR:</span> <s:property value="errorMessage" />
    </div>
</s:if>

<p>
Validation Scoring Command:
</p>
<pre>
<s:property value="validationScoringCommand" />
</pre>

<p>
Validation R Script Output:
<s:a action="TextFileDisplay" title="Validation R Script Output">
        <s:param name="textFilePath" value="scriptOutputFile" />
        <s:property value="scriptOutputFile" />
    </s:a>
</p>

</tiles:putAttribute>
</tiles:insertTemplate>
