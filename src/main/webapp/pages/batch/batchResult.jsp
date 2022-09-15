<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
 
<tiles:insertTemplate template="/pages/template.jsp" flush="true">

<tiles:putAttribute name="header">
    <title>CFE Wizard - Results</title>
    <s:head />
</tiles:putAttribute>
<tiles:putAttribute name="content">

<script>
    function submitForm() {
    	document.getElementById("uploadButton").disabled = true;
    	document.body.style.cursor='wait';
    	document.uploadForm.submit();
    }
</script>

<h1>CFE Results</h1>

        
<s:if test="!errorMessage.trim().isEmpty()">
    <div class="cfeError">
        <span style="font-weight: bold;">ERROR:</span> <s:property value="errorMessage" />
        <div style="margin-top: 14px;">
            <span style="font-weight: bold;">STACK TRACE:</span> <s:property value="exceptionStack" />
        </div>
    </div>
</s:if>
        
<s:actionerror />

<s:iterator value="validationMsgs" status="vstat">
    <s:property value="validationMsgs[%{#vstat.index}]" />
</s:iterator>

<table class="dataTable">
    <tr>
        <th>Input</th> <th>Value</th>
    </tr>
    <tr>
        <td>Discovery Phene Table</td>
        <td>&quot;<s:property value="discoveryPheneTable"/>&quot;</td>
    </tr>
    <tr>
        <td>Discovery Phene</td>
        <td>&quot;<s:property value="discoveryPhene"/>&quot;</td>
    </tr>
    <tr>
        <td>Discovery Phene Low Cutoff</td>
        <td style="text-align: right;">
            <s:property value="discoveryPheneLowCutoff"/>
        </td>
    </tr>
    <tr>
        <td>Discovery Phene High Cutoff</td>
        <td style="text-align: right;">
            <s:property value="discoveryPheneHighCutoff"/>
        </td>
    </tr>
</table>

<table class="dataTable" style="margin-top: 27px;">
    <tr>
        <th>Name</th>
        <th>ID</th>
        <th>Results</th>
        <th>Script Command File</th>
        <th>Script Log File</th></tr>
    <tr>
        <td> Discovery Cohort </td>
        <td> <s:property value="discoveryCohortResultsId"/> </td>
        <td>
            <s:a action="CfeResultsXlsxDisplay" title="Discovery Cohort Results">
                <s:param name="cfeResultsId" value="discoveryCohortResultsId" />
                <s:param name="fileName" value="'discovery-cohort-results.xlsx'" />
                discovery-cohort-results.xlsx
            </s:a>
        </td>
        <td> N/A </td>
        <td> N/A </td>
    </tr>
    <tr>
        <td> Discovery Scores </td>
        <td> <s:property value="discoveryScoresResultsId"/> </td>
        <td>
            <s:a action="CfeResultsXlsxDisplay" title="Discovery Scores Results">
                <s:param name="cfeResultsId" value="discoveryScoresResultsId" />
                <s:param name="fileName" value="'discovery-scores-results.xlsx'" />
                discovery-scores-results.xlsx
            </s:a>
        </td>
        <td>
            <s:a action="CfeResultsFileDisplay" title="Discovery R Script Command">
                <s:param name="cfeResultsId" value="discoveryScoresResultsId" />
                <s:param name="fileType" value="@cfe.model.CfeResultsFileType@DISCOVERY_R_SCRIPT_COMMAND" />
                <s:property value="'discovery-r-script-command'" />
            </s:a>  
        </td>
        <td>
            <s:a action="CfeResultsFileDisplay" title="Discovery R Script Log">
                <s:param name="cfeResultsId" value="discoveryScoresResultsId" />
                <s:param name="fileType" value="@cfe.model.CfeResultsFileType@DISCOVERY_R_SCRIPT_LOG" />
                <s:property value="'discovery-r-script-log'" />
            </s:a> 
        </td>
    </tr>
</table>

<p>
R Script LOG: <s:property value="@cfe.model.CfeResultsFileType@DISCOVERY_R_SCRIPT_LOG" />
</p>
             
<%-- FOR DEBUGGING:
<table class="dataTable">
    <tr>
        <th>Number</th> <th>Probeset</th> <th>Gene</th>
    </tr>
    <s:iterator value="probesetToGeneMap" var="pgmap" status="pstat">
        <tr>
            <td>
                <s:property value="#pstat.index"/>
            </td>
            <td>
                <s:property value="key"/>
            </td>
            <td>
                <s:property value="value"/>
            </td>
        </tr>
    </s:iterator>
</table>
--%>

</tiles:putAttribute>
</tiles:insertTemplate>
