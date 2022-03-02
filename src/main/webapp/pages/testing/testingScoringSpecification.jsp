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
Testing Data ID: <s:property value="testingDataId"/>
</p>

<p>
<s:a action="CfeResultsXlsxDisplay" title="Testing Data Spreadsheet">
    <s:param name="cfeResultsId" value="testingDataId" />
    <div>
        <img border="0"
             style="margin-top: 2px;"
             src="<s:url includeParams='none' value='/images/gnome_48x48_mimetypes_x-office-spreadsheet.png'/>"
             alt="Testing Data" /> <br />
        testing-data-results.xlsx
    </div>
</s:a>
</p>

<p>
Gene expression CSV file: <s:property value="geneExpressionCsvFileName" /> <br/>
Score cutoff: <s:property value="scoreCutoff"/> <br/>
Special predictor list CSV file: <s:property value="specialPredictorListCsvFileName" />
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

<s:form action="TestingScoringCalculation" theme="simple">
   
   <s:hidden name="predictorListFile"/>
   <s:hidden name="specialPredictorListTempFile"/>
   <s:hidden name="testingDataId"/>
   <s:hidden name="testingMasterSheetFile"/>
   
    <table class="dataTable">
        <tr>
            <td>
                <s:checkbox name="state"/>State
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
                High Cutoff: <s:textfield style="text-align: right;" name="predictionPheneHighCutoff"/>
                </p>
            </td>
        </tr>
        <tr>
            <td>
                <s:checkbox name="firstYear"/>First Year Hospitalization
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
                <s:checkbox name="future"/>Future Hospitalization
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
</s:form>

</tiles:putAttribute>
</tiles:insertTemplate>
