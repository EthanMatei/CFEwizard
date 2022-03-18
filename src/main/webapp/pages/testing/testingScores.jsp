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
    <tr> <th>Property</th> <th>Value</th> <th>R Script Command</th> <th>R Script Output</th> </tr>
    
    <tr>
        <td>State Cross-Sectional</td>
        <td><s:property value="stateCrossSectional"/></td>
        <td><s:property value="rCommandStateCrossSectional"/></td>
        <td>
            <s:a action="TextFileDisplay" title="State Cross-Sectional R Script Output">
                <s:param name="textFilePath" value="rScriptOutputFileStateCrossSectional" />
                <s:property value="rScriptOutputFileStateCrossSectional" />
            </s:a>
        </td>
    </tr>
    <tr>
        <td>State Longitudinal</td>
        <td><s:property value="stateLongitudinal"/></td>
        <td><s:property value="rCommandStateLongitudinal"/></td>
        <td>
            <s:a action="TextFileDisplay" title="State Longitudinal R Script Output">
                <s:param name="textFilePath" value="rScriptOutputFileStateLongitudinal" />
                <s:property value="rScriptOutputFileStateLongitudinal" />
            </s:a>
        </td>
    </tr>
    <tr>
        <td>Prediction Phene</td>
        <td><s:property value="predictionPhene"/></td>
        <td> &nbsp; </td>
        <td> &nbsp; </td>
    </tr>
    <tr>
        <td>Prediction Phene High Cutoff</td>
        <td><s:property value="predictionPheneHighCutoff"/></td>
        <td> &nbsp; </td>
        <td> &nbsp; </td>
    </tr>
    
    <tr>
        <td>First Year Cross-Sectional</td>
        <td><s:property value="firstYearCrossSectional"/></td>
        <td><s:property value="rCommandFirstYearCrossSectional"/></td>
        <td>
            <s:a action="TextFileDisplay" title="First Year Cross-Sectional R Script Output">
                <s:param name="textFilePath" value="rScriptOutputFileFirstYearCrossSectional" />
                <s:property value="rScriptOutputFileFirstYearCrossSectional" />
            </s:a>
        </td>
    </tr>
    <tr>
        <td>First Year Longitudinal</td>
        <td><s:property value="firstYearLongitudinal"/></td>
        <td><s:property value="rCommandFirstYearLongitudinal"/></td>        
        <td>
            <s:a action="TextFileDisplay" title="First Year Longitudinal R Script Output">
                <s:param name="textFilePath" value="rScriptOutputFileFirstYearLongitudinal" />
                <s:property value="rScriptOutputFileFirstYearLongitudinal" />
            </s:a>
        </td>
    </tr>

    <tr>
        <td>Future Cross-Sectional</td>
        <td><s:property value="futureCrossSectional"/></td>
        <td><s:property value="rCommandFutureCrossSectional"/></td>        
        <td>
            <s:a action="TextFileDisplay" title="Future Cross-Sectional R Script Output">
                <s:param name="textFilePath" value="rScriptOutputFutureCrossSectional" />
                <s:property value="rScriptOutputFutureCrossSectional" />
            </s:a>
        </td>        
    </tr>
    <tr>
        <td>Future Longitudinal</td>
        <td><s:property value="futuretLongitudinal"/></td>
        <td><s:property value="rCommandFutureLongitudinal"/></td>     
        <td>
            <s:a action="TextFileDisplay" title="Future Longitudinal R Script Output">
                <s:param name="textFilePath" value="rScriptOutputFileFutureLongitudinal" />
                <s:property value="rScriptOutputFileFutureLongitudinal" />
            </s:a>
        </td>
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

<hr/>

<p>
Testing Scoring Results:
<s:if test="cfeResultsId != null">
    <s:a action="CfeResultsXlsxDisplay" title="CFE Results">
        <s:param name="cfeResultsId" value="cfeResultsId" />
        <div>
            <img border="0"
                 style="margin-top: 2px;"
                 src="<s:url includeParams='none' value='/images/gnome_48x48_mimetypes_x-office-spreadsheet.png'/>"
                 alt="Testing Scores" />
            <br />
            testing-scores.xslx
        </div>
    </s:a>
</s:if>
<s:else>
    <p>No results generated</p>
</s:else>
</p>

</tiles:putAttribute>
</tiles:insertTemplate>