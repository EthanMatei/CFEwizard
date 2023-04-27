<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
 
<tiles:insertTemplate template="/pages/template.jsp" flush="true">

<tiles:putAttribute name="header">
    <title>CFE Wizard - Database Upload</title>
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

<h1>Data Upload</h1>

<s:include value="/pages/error_include.jsp"/>
        
<s:actionerror />

        

<s:form id ="dataUploadForm" theme="simple" name="dataUploadForm"
        action="BatchDataUpload"
        method="post" enctype="multipart/form-data">

    <!-- <s:submit value="Upload" id="uploadButton" onclick="submitForm();" /> -->
    <p>
    <s:submit value="Upload" id="uploadButton" style="font-weight: bold; font-size: 120%;"/>
    </p>
    
    <s:token />
    
    <table class="dataTable">
        <tr> <th>Data</th> <th>File</th> </tr>
        <tr>
            <td> Testing Database </td>
            <td> <s:file name="testingDb" label="Testing Database" /> </td>
        </tr>
    </table>

    <s:hidden name="testingDbTempFileName" /> 

    <hr/>
    
    <p>
    <span style="font-weight: bold;">Ending Step:</span>
    <s:select name="endingResultsType" list="endingResultsTypeList" value="@cfe.model.CfeResultsType@TESTING_SCORES"/>
    </p>
    
    <hr/>
    
    <p style="font-weight: bold;">Starting Point (Optional)</p>

    <div style="margin-left: 4em;">
    
        <fieldset class="dataInput">
            <p style="font-weight: bold;">
            <s:radio name="startingCfeResultsId" list="#{@cfe.action.BatchAction@MANUAL_RESULTS_START: 'Manually created result:'}"/> [WORK IN PROGESS]
            </p>
            <div style="margin-left: 3em;">
                <div style="margin-bottom: 5px;">
                Type: <s:select name="manualResultsType" list="manualResultsTypeList" value="@cfe.model.CfeResultsType@DISCOVERY_COHORT"/>
                &nbsp; Spreadsheet: <s:file name="manualResultsSpreadsheet" label="Spreadsheet"/>
                </div>
                <div style="margin-bottom: 5px;">
                Discovery Phene: <s:textfield name="discoveryPheneInfo"/> format: <i>table-name.phene-name</i> (e.g., "PANSS.P1 Delusions (1-7)")
                </div>
                <div>
                Discovery Phene Low Cutoff (phene &ge;): <s:textfield name="discoveryPheneLowCutoff" style="text-align: right;" size="4"/>
                &nbsp; Discovery Phene High Cutoff (phene &le;): <s:textfield name="discoveryPheneHighCutoff" style="text-align: right;" size="4"/>
                </div>
            </div>
        </fieldset>
    
        <fieldset class="dataInput" style="margin-top: 17px;">
            <p style="font-weight: bold;">
            Past calculated or uploaded result:
            </p>
            
            <table class="dataTable" style="margin-left: 3em;">
                <tr> 
                    <th>ID</th>
                    <th>Results</th>
                    <th>Results Type</th>
                    <th>Time Generated</th>
                    <th>Phene</th>
                    <th>Phene Low Cutoff</th>
                    <th>Phene High Cutoff</th>
                </tr>

                <s:if test="startingResultsList != null">
                    <s:iterator value="startingResultsList" var="result">
                        <tr>
                            <td>
                                <s:radio name="startingCfeResultsId" list="{cfeResultsId}"/>
                            </td>
                            <td>
                                <s:a action="CfeResultsXlsxDisplay" title="Discovery Results">
                                    <s:param name="cfeResultsId" value="cfeResultsId" />
                                    results.xlsx
                                </s:a>
                            </td>
                            <td> <s:property value="resultsType"/>
                            <td> <s:date name="generatedTime" format="MM/dd/yyyy hh:mm"/> </td>
                            <td> <s:property value="phene"/> </td>
                            <td style="text-align: right;"> <s:property value="lowCutoff"/> </td>
                            <td style="text-align: right;"> <s:property value="highCutoff"/> </td>
                        </tr>
                    </s:iterator>
                </s:if>
            </table>
        </fieldset>
    </div>


</s:form>

<s:iterator value="validationMsgs" status="vstat">
    <s:property value="validationMsgs[%{#vstat.index}]" />
</s:iterator>

</tiles:putAttribute>
</tiles:insertTemplate>
