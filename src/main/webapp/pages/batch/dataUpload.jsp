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

        
<s:if test="!errorMessage.trim().isEmpty()">
    <div class="cfeError">
        <span style="font-weight: bold;">ERROR:</span> <s:property value="errorMessage" />
    </div>
    <div style="margin-top: 17px;">
        <span style="font-weight: bold;">STACK TRACE:</span>
        <pre><s:property value="exceptionStack"/></pre>
    </div>
</s:if>
        
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

    <p>Ending Step: <s:select name="endingResultsType" list="endingResultsTypeList" value="@cfe.model.CfeResultsType@TESTING_SCORES"/> </p>
    
    <p>Starting Results (Optional)</p>
    
    <table class="dataTable">
        <tr> 
            <th>ID</th>
            <th>Results</th>
            <th>Results Type</th>
            <th>Time Generated</th>
            <th>Phene</th>
            <th>Phene Low Cutoff</th>
            <th>Phene High Cutoff</th>
        </tr>

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
    </table>


</s:form>

<s:iterator value="validationMsgs" status="vstat">
    <s:property value="validationMsgs[%{#vstat.index}]" />
</s:iterator>

</tiles:putAttribute>
</tiles:insertTemplate>
