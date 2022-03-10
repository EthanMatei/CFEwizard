<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
 
<tiles:insertTemplate template="/pages/template.jsp" flush="true">

<tiles:putAttribute name="header">
    <title>CFE Wizard - Testing Scores</title>
    <s:head />
    <script src="<s:url includeParams='none' value='/js/jquery-3.6.0.min.js'/>"></script> 
    <script src="<s:url includeParams='none' value='/js/jquery.fancytree-all-deps.min.js'/>"></script> 
</tiles:putAttribute>
<tiles:putAttribute name="content">

<h2>Testing Scores</h2>

<s:actionerror />

<s:if test="errorMessage != null && errorMessage != ''">
    <div class="cfeError">
        <span style="font-weight: bold;">ERROR:</span> <s:property value="errorMessage" />
    </div>
</s:if>

<p>
Testing Data ID: <s:property value="testingDataId"/> <br/>
Special predictor list CSV file: <s:property value="specialPredictorListTempFile" /> <br/>
Pedictor list CSV file: <s:property value="predictorListFile" /> <br/>
Testing Master Sheet: <s:property value="testingMasterSheetFile"/>
</p>

<table class="dataTable">
    <tr> <th>Property</th> <th>Value</th> </tr>
    
    <tr>
        <td>State Cross-Sectional</td> <td><s:property value="stateCrossSectional"/></td>
    </tr>
    <tr>
        <td>State Longitudinal</td> <td><s:property value="stateLongitudinal"/></td>
    </tr>
    <tr>
        <td>Prediction Phene</td> <td><s:property value="predictionPhene"/></td>
    </tr>
    <tr>
        <td>Prediction Phene High Cutoff</td> <td><s:property value="predictionPheneHighCutoff"/></td>
    </tr>
    
    <tr>
        <td>First Year Cross-Sectional</td> <td><s:property value="firstYearCrossSectional"/></td>
    </tr>
    <tr>
        <td>First Year Longitudinal</td> <td><s:property value="firstYearLongitudinal"/></td>
    </tr>

    <tr>
        <td>Future Cross-Sectional</td> <td><s:property value="futureCrossSectional"/></td>
    </tr>
    <tr>
        <td>Future Longitudinal</td> <td><s:property value="futuretLongitudinal"/></td>
    </tr>

</table>

<p>
Final Testing Master Sheet:
<s:a action="CsvTextFileDisplay" title="Final Testing Mastersheet">
    <s:param name="textFilePath" value="finalMasterSheetFile" />
    <s:property value="finalMasterSheetFile" />
</s:a>
</p>

<p>
Predictor List:
<s:a action="CsvDisplay">
    <s:param name="csvFilePath" value="predictorListFile" />
    <s:property value="predictorListFile" />
</s:a>
</p>


<p>
Special Predictor List:
<s:if test="specialPredictorListTempFile != null && specialPredictorListTempFile != ''">
    <s:a action="CsvDisplay">
        <s:param name="csvFilePath" value="specialPredictorListTempFile" />
        <s:property value="predictorListFile" />
    </s:a>
</s:if>
</p>
</tiles:putAttribute>
</tiles:insertTemplate>
