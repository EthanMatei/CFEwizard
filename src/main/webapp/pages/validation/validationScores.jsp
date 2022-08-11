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
        <div style="margin-top: 17px;">
            <span style="font-weight: bold;">STACK TRACE:</span> <s:property value="exceptionStack" />
        </div>
    </div>
</s:if>

<p>
Validation Scoring Results:
<s:if test="cfeResultsId != null">
    <s:a action="CfeResultsXlsxDisplay" title="CFE Results">
        <s:param name="cfeResultsId" value="cfeResultsId" />
        <div>
            <img border="0"
                 style="margin-top: 2px;"
                 src="<s:url includeParams='none' value='/images/gnome_48x48_mimetypes_x-office-spreadsheet.png'/>"
                 alt="Validation Scores" />
            <br />
            validation-scores.xslx
        </div>
    </s:a>
    
    <!--  Next button  -->
    <p>
    <s:a action="TestingCohortsSpecification" title="Testing Cohorts Creation" class="linkButton">
        <s:param name="validationId" value="cfeResultsId" />
    Testing Cohorts Creation
    </s:a>
    </p>
</s:if>
<s:else>
    <p>No results generated</p>
</s:else>
</p>

<hr/>

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
