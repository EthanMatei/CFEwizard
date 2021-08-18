<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
 
<tiles:insertTemplate template="/pages/template.jsp" flush="true">

<tiles:putAttribute name="header">
    <title>CFE Wizard</title>
</tiles:putAttribute>
<tiles:putAttribute name="content">

<%-- User: <s:property value="#session.username" /> --%>

<h2>Discovery Results</h2>

<s:if test="errorMessage != null && errorMessage != ''">
    <div style="color: #CC0000; border: 1px solid #CC0000; padding: 7px;">
        ERROR: <s:property value="errorMessage" />
        <p>
        <s:if test="exceptionStack != ''">
            <s:property value="exceptionStack" />
        </s:if>
        </p>
    </div>
</s:if>


<br/>
Discovery Scoring Results:
<s:if test="cfeResultsId != null">
    <s:a action="CfeResultsXlsxDisplay" title="CFE Results">
        <s:param name="cfeResultsId" value="cfeResultsId" />
        results.xlsx
    </s:a>
</s:if>
<s:else>
    <p>No results generated</p>
</s:else>
    
<br/>
Discovery R Script Output:


<s:a action="TextFileDisplay" title="Discovery R script output file">
    <s:param name="textFilePath" value="scriptOutputTextFile" />
    <div>
    <img border="0"
     style="margin-top: 2px;"
     src="<s:url includeParams='none' value='/images/gnome_48x48_mimetypes_text-x-generic.png'/>"
     alt="Discovery R Script Output" /> <br />
    <s:property value="scriptOutputTextFileName"/>
    </div>
</s:a>


<br/>
Timing:

<s:if test="timingFile != null">
    <s:a action="CsvDisplay" title="Discovery Timing">
        <s:param name="csvFilePath" value="timingFile" />
        <div>
        <img border="0"
         style="margin-top: 2px;"
         src="<s:url includeParams='none' value='/images/gnome_48x48_mimetypes_x-office-spreadsheet.png'/>"
         alt="Discovery Timing" /> <br />
        <s:property value="timingFileName"/>
        </div>
    </s:a>
</s:if>

<hr style="margin-top: 12px;"/>

<h3>Inputs</h3>
<table class="dataTable">
    <tr>
        <th> Phene </th>
        <td> <s:property value="pheneSelection"/> </td>
    </tr>
    <tr>
        <th> Phene Table </th>
        <td> <s:property value="pheneTable"/> </td>
    </tr>
    <tr>
        <th>Phene Low Cutoff</th>
        <td> <s:property value="lowCutoff" /> </td>
    </tr>
    <tr>
        <th>Phene High Cutoff</th>
        <td> <s:property value="highCutoff" /> </td>
    </tr>
    <tr>
        <th> Diagnosis Code </th>
        <td> <s:property value="diagnosisCode"/> </td>
    </tr>
    <tr>
        <th>Phene Visit Database File</th>
        <td><s:property value="discoveryDbFileName" /></td>
    </tr>
    <tr>
        <th>Gene Expression CSV File</th>
        <td><s:property value="discoveryCsvFileName" /></td>
    </tr>
    <tr>
        <th>Microarray Table</th>
        <td><s:property value="microarrayTable" /></td>
    </tr>    
</table>


<hr style="margin-top: 12px;"/>

<h3>System Information</h3>

<table class="dataTable">
    <tr>
        <th>Temp Directory</th>
        <td> <s:property value="tempDir" /> </td>
    </tr>
    <tr>
        <th>Phene Visit DB temp file</th>
        <td> <s:property value="discoveryDbTempFileName" /> </td>
    </tr>
    <tr>
        <th>Cohort CSV temp file</th>
        <td> <s:property value="cohortCsvFile" /> </td>
    </tr>
    <tr>
        <th>Gene Expression CSV temp file</th>
        <td> <s:property value="discoveryCsvTempFileName" /> </td>
    </tr>
    <tr>
        <th>CFE Wizard Base directory</th>
        <td> <s:property value="baseDir"/> </td>
    </tr>
    <tr>
        <th>R Script Directory</th>
        <td> <s:property value="scriptDir" /> </td>
    <tr>
        <th>R Script File</th>
        <td> <s:property value="scriptFile" /> </td>
    </tr>
</table>



<br/>

</tiles:putAttribute>
</tiles:insertTemplate>
