<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
 
<tiles:insertTemplate template="/pages/template.jsp" flush="true">

<tiles:putAttribute name="header">
    <title>CFE Wizard - Testing Scoring - Testing Scoring Specification</title>
    <s:head />
    <script src="<s:url includeParams='none' value='/js/jquery-3.6.0.min.js'/>"></script> 
    <script src="<s:url includeParams='none' value='/js/jquery.fancytree-all-deps.min.js'/>"></script> 
</tiles:putAttribute>
<tiles:putAttribute name="content">

<h2>Testing Scoring Specification</h2>

<s:actionerror />

<s:if test="errorMessage != null && errorMessage != ''">
    <div class="cfeError">
        <span style="font-weight: bold;">ERROR:</span> <s:property value="errorMessage" />
    </div>
</s:if>

<p>
Testing Data ID: <s:property value="testingDataId"/> <br/>
Testing Data: 
<s:a action="CfeResultsXlsxDisplay" title="Testing Data Spreadsheet">
    <s:param name="cfeResultsId" value="testingDataId" />
    <%-- <div> --%>
        <%--
        <img border="0"
             style="margin-top: 2px;"
             src="<s:url includeParams='none' value='/images/gnome_48x48_mimetypes_x-office-spreadsheet.png'/>"
             alt="Testing Data" /> <br />
        --%>
        testing-data.xlsx
    <%-- </div> --%>
</s:a>
</p>

<p>
Gene expression CSV file: <s:property value="geneExpressionCsvFileName" /> <br/>
Score cutoff: <s:property value="scoreCutoff"/> <br/>
</p>
    
<p>
Predictor List:
<s:a action="CsvDisplay">
    <s:param name="csvFilePath" value="predictorListFile" />
    <s:property value="predictorListFile" />
</s:a>
</p>
    
<p>
Testing Master Sheet:
<s:a action="CsvTextFileDisplay" title="Testing Mastersheet">
    <s:param name="textFilePath" value="testingMasterSheetFile" />
    <s:property value="testingMasterSheetFile" />
</s:a>
</p>

<s:form action="TestingScoringCalculation" theme="simple" method="post" enctype="multipart/form-data"
        style="margin-bottom: 14px; margin-top: 14px; border: 1px solid #222222; border-radius: 7px; padding: 7px;">
    <s:hidden name="geneExpressionCsvFileName"/>
    <s:hidden name="scoreCutoff"/>
    <s:hidden name="predictorListFile"/>
    <s:hidden name="testingDataId"/>
    <s:hidden name="testingMasterSheetFile"/>
   
    <p>
    Updated predictor list CSV file (optional)
    <s:file name="specialPredictorListCsv" />
    </p>
    
    <table class="dataTable">
        <tr>
            <td>
                <span style="font-weight: bold;">State</span>
            </td>
            <td>
                <p>
                <s:checkbox name="stateCrossSectional"/>Cross-Sectional
                </p>
                
                <p>
                <s:checkbox name="stateLongitudinal"/>Longitudinal
                </p>
                
                <p>
                Phene: <s:select name="predictionPhene" list="phenes" />
                </p>
                
                <p>
                High Cutoff (&ge;):
                <s:textfield size="5" style="text-align: right;" name="predictionPheneHighCutoff"/>
                
                <span style="margin-left: 2em;">Comparison Threshold:</span>
                <s:textfield size="10" style="text-align: right;" name="comparisonThreshold"/>
                </p>
            </td>
        </tr>
        <tr>
            <td>
                <span style="font-weight: bold;">First Year Hospitalization</span>
            </td>
            <td>
                <p>
                <s:checkbox name="firstYearCrossSectional"/>Cross-Sectional
                </p>
                <p>
                <s:checkbox name="firstYearLongitudinal"/>Longitudinal
                </p>
            </td>
        </tr>
        <tr>
            <td>
                <span style="font-weight: bold;">Future Hospitalization</span>
            </td>
            <td>
                <p>
                <s:checkbox name="futureCrossSectional"/>Cross-Sectional
                </p>
                <p>
                <s:checkbox name="futuretLongitudinal"/>Longitudinal
                </p>
            </td>
        </tr>
    </table>

    <s:submit value="Calculate" style="margin-top: 17px; padding-left: 2em; padding-right: 2em; font-weight: bold;"/>
    <s:token/>
</s:form>

</tiles:putAttribute>
</tiles:insertTemplate>
