<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
 
<tiles:insertTemplate template="/pages/template.jsp" flush="true">

<tiles:putAttribute name="header">
    <title>CFE Wizard - Clinical and Testing Cohorts</title>
    <s:head />
    <script src="<s:url includeParams='none' value='/js/jquery-3.6.0.min.js'/>"></script> 
    <script src="<s:url includeParams='none' value='/js/jquery.fancytree-all-deps.min.js'/>"></script> 
</tiles:putAttribute>
<tiles:putAttribute name="content">


<h2>Validation and Testing Cohorts</h2>

<s:actionerror />

<s:if test="errorMessage != null && errorMessage != ''">
    <div class="cfeError">
        <span style="font-weight: bold;">ERROR:</span> <s:property value="errorMessage" />
        <div style="margin-top: 17px;">
            <span style="font-weight: bold;">STACK TRACE:</span> <s:property value="exceptionStack" />
        </div>
    </div>
</s:if>

<table class="dataTable" style="margin-bottom: 20px;">
    <tr> <th>Discovery Phene</th> <th>Low Cutoff</th> <th>High Cutoff</th> </tr>
    <tr>
        <td style="text-align: right;"> <s:property value="discoveryPhene"/> </td>
        <td style="text-align: right;"> <s:property value="discoveryLowCutoff"/> </td>
        <td style="text-align: right;"> <s:property value="discoveryHighCutoff"/> </td>
    </tr>
</table>

<%--
<table class="dataTable" style="margin-bottom: 20px;">
    <tr> <th>Clinical Phene</th> <th>High Cutoff</th> </tr>
    <tr>
        <td style="text-align: right;"> <s:property value="clinicalPhene"/> </td>
        <td style="text-align: right;"> <s:property value="clinicalHighCutoff"/> </td>
    </tr>
</table>
--%>

<p>
Follow-up Database File: <s:property value="followUpDbFileName" />
</p>

<s:if test="(phene1 != '' && value1 != '') || (phene2 != '' && value2 != '') || (phene3 != '' && value3 != '')">
<p style="font-weight: bold; margin-top: 24px;">Additional Validation and Testing Cohort Constraints</p>
   
<table class="dataTable" style="margin-bottom: 32px;">


    <tr> <th>Phene</th> <th>Relation</th> <th>Value</th> </tr>
    
    <s:if test="phene1 != '' && value1 != ''">
    <tr>
        <td> <s:property value="phene1"/> </td>
        <td style="text-align: center"> <s:property value="operator1"/> </td>
        <td style="text-align: right;"> <s:property value="value1"/> </td>
    </tr>
    </s:if>
    
    <s:if test="phene2 != '' && value2 != ''">
    <tr>
        <td> <s:property value="phene2"/> </td>
        <td style="text-align: center"> <s:property value="operator2"/> </td>
        <td style="text-align: right;"> <s:property value="value2"/> </td>
    </tr>
    </s:if>
    
    <s:if test="phene3 != '' && value3 != ''">
        <tr>
            <td> <s:property value="phene3"/> </td>
            <td style="text-align: center"> <s:property value="operator3"/> </td>
            <td style="text-align: right;"> <s:property value="value3"/> </td>
        </tr>
    </s:if>
</table>
</s:if>


<table class="dataTable">
    <tr>
        <th> Cohort </th> <th> Number of Subjects </th>
    </tr>
    <tr>
        <td> Validation </td>
        <td style="text-align: right;"> <s:property value="numberOfValidationSubjects" />
    </tr>
    <tr>
        <td> Testing  </td>
        <td style="text-align: right;"> <s:property value="numberOfTestingSubjects" />
    </tr>
</table>
    
<div style="margin-top: 14px; margin-bottom: 14px;">
    Subjects specified in Validation Cohort:
    <s:property value="percentInValidationCohort"/>%
</div>
    

<p>
    Cohort Check:
    <s:a action="CsvTextFileDisplay" title="Cohort Check">
        <s:param name="textFilePath" value="cohortCheckCsvFileName" />
        <s:property value="cohortCheckCsvFileName" />
    </s:a>
</p>

<p>
    Scoring Data:
    <s:a action="CsvTextFileDisplay" title="Scoring Data">
        <s:param name="textFilePath" value="scoringDataFileName" />
        <s:property value="scoringDataFileName" />
    </s:a>
</p>

<p>
    Phene Visits Data:
    <s:a action="CsvTextFileDisplay" title="Phene Visits">
        <s:param name="textFilePath" value="pheneVisitsFileName" />
        <s:property value="pheneVisitsFileName" />
    </s:a>
</p>

<br/>
<hr/>

<p>
Prediction cohort creation command: <s:property value="predictionCohortCreationCommand"/>
</p>

<p>
Prediction Cohort Creation Python Script Output:
<s:a action="TextFileDisplay" title="Prediction Cohort Python Script Output">
    <s:param name="textFilePath" value="scriptOutputFile" />
    <s:property value="scriptOutputFile" />
</s:a>
</p>

<p>
Prediction Cohort:
<s:a action="CsvTextFileDisplay" title="Prediction Cohort">
    <s:param name="textFilePath" value="outputFile" />
    <s:property value="outputFile" />
</s:a>
</p>

<div style="margin-bottom: 17px;">
    <s:a action="CfeResultsXlsxDisplay" title="Cohort Spreadsheet">
        <s:param name="cfeResultsId" value="cfeResultsId" />
        <div>
            <img border="0"
                 style="margin-top: 2px;"
                 src="<s:url includeParams='none' value='/images/gnome_48x48_mimetypes_x-office-spreadsheet.png'/>"
                 alt="Report" /> <br />
            results.xlsx
        </div>
    </s:a>
</div>


<%--       
<h4>Validation Cohort</h4>
<s:iterator value="validationSubjects" var="subject">
    <s:property value="subject"/> <br/>
</s:iterator>

<h4>Testing Cohort</h4>
<s:iterator value="testingSubjects" var="subject">
    <s:property value="subject"/> <br/>
</s:iterator>
--%>

</tiles:putAttribute>
</tiles:insertTemplate>
