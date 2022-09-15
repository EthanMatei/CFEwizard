<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
 
<tiles:insertTemplate template="/pages/template.jsp" flush="true">

<tiles:putAttribute name="header">
    <title>CFE Wizard - Database Upload</title>
    <link rel="stylesheet" href="/js/jquery-ui.min.css">
    <link rel="stylesheet" href="/js/jquery-ui.structure.min.css">
    <link rel="stylesheet" href="/js/jquery-ui.theme.min.css">
    <script src="<s:url includeParams='none' value='/js/jquery-3.6.0.min.js'/>"></script> 
    <script src="<s:url includeParams='none' value='/js/jquery-ui.min.js'/>"></script> 
    <script>
        $( function() {
            var discoveryPhenes = [
                <s:set var="isFirst" value="true"/>   
                <s:iterator value="discoveryPheneList" var="phene">
                    <s:if test="isFirst">
                        "<s:property value="phene"/>"
                        <s:set var="isFirst" value="false"/>
                    </s:if>
                    <s:else>
                        , "<s:property value="phene"/>"
                    </s:else>
                </s:iterator>
            ];
            /*
            $( "#discoveryPhenes" ).autocomplete({
                source: discoveryPhenes
            });
            */
            

        } ); 
        

    </script>
    <s:head />
</tiles:putAttribute>
<tiles:putAttribute name="content">


<h1>Data Input</h1>

        
<s:if test="!errorMessage.trim().isEmpty()">
    <div class="cfeError">
        <span style="font-weight: bold;">ERROR:</span> <s:property value="errorMessage" />
    </div>
</s:if>
        
<s:actionerror />

<s:form id ="dataInputForm" theme="simple" name="dataInputForm"
        action="BatchCalculate"
        method="post" enctype="multipart/form-data">
        
    <s:hidden name="testingDbTempFileName"/>

    <fieldset class=dataInput>
        <legend>Data Files</legend>
        
        <table class="dataTable">
            <tr> <th>Data</th> <th>File</th> </tr>
            <tr>
                <td> Probeset to Gene Mapping Database </td>
                <td> <s:file name="probesetToGeneMappingDb" label="Probeset to Gene Mapping Database" /> </td>
            </tr>
            <tr>
                <td> Discovery Gene Expression CSV File </td>
                <td> <s:file name="discoveryGeneExpressionCsv" label="Discovery Gene Expression CSV" /> </td>
            </tr>            
        </table>
    </fieldset>
    
    <div>&nbsp;</div>
    
    <fieldset class="dataInput">
        <legend>Discovery</legend>

        <p>
            Phene: <s:select name="discoveryPheneInfo" list="discoveryPheneList"/>
        </p>
        
        <p>
        Low Cutoff (&le;): <s:textfield name="discoveryPheneLowCutoff"/>
        </p>
        <p>
        High Cutoff (&ge;): <s:textfield name="discoveryPheneHighCutoff"/>
        </p>
        <p>
        Genomics table <s:select name="genomicsTable" list="genomicsTables"/>
        </p>

        <%-- DIAGNOSIS --%>        
        
        <p>
        Diagnosis: <s:select name="diagnosisCode" list="diagnosisCodesList"/>
        </p>
        
        <%--
        <h3>Diagnosis</h3>
        <table class="dataTable" style="margin-top: 1em;">
            <tr>
                <th> Diagnosis&nbsp;Code </th>
                <th> Examples </th>
            </tr>
            <tr>
                <td> <s:radio name="diagnosisCode" list="{'All'}" checked="true" style="fontweight:bold;"/> </td>
                <td> (All diagnosis codes will be processed) </td>
            </tr>
            <s:iterator value="diagnosisCodes">
                <tr>
                    <td> <s:radio name="diagnosisCode" list="{key}" /> </td>
                    <td> <s:property value="value"/> </td>
                </tr>
            </s:iterator>
        </table>
        --%>
        
        <table class="dataTable">
            <thead>
                <tr>
                    <th>Percentile Range</th><th>Score</th>
                </tr>
            </thead>
            <tbody>
                <s:iterator value="discoveryPercentileScores.lowerBounds" var="lowerBound" status="status">
                    <tr>
                        <td>
                            <span style="text-align: right;margin-left: 1em; width: 10em; display: inline-block;">
                                <s:property value="lowerBound"/>
                            </span>
                            &le; x &lt;
                            <span style="text-align: right;margin-left: 1em; width: 10em; display: inline-block;">
                                <s:property value="discoveryPercentileScores.upperBounds[#status.index]"/>
                            </span>
                        </td>
                        <td>
                            <s:textfield size="7" cssStyle="text-align: right;margin-left: 1em"
                                name="discoveryPercentileScores.scores[%{#status.index}]"
                            />
                        </td>
                    </tr>
                </s:iterator>            
            </tbody>
        </table>
    </fieldset>
    
    <div>&nbsp;</div>
    
    <fieldset class="dataInput">
        <legend>Prioritization</legend>
        
        <s:a action="PrioritizationReport">
            <s:param name="reportName" value="'diseases'" />
            <s:param name="reportFormat" value="'xlsx'" />
            diseases.xlsx
        </s:a>
    </fieldset>
 
    <div>&nbsp;</div>
        
    <fieldset class="dataInput">
        <legend>Validation</legend>
    </fieldset> 
 
    <div>&nbsp;</div>
        
    <fieldset class="dataInput">
        <legend>Testing</legend>
    </fieldset>
        
    <!-- <s:submit value="Calculate" id="calculateButton" onclick="submitForm();" /> -->
    <p>
    <s:submit value="Calculate" id="calculateButton" />
    </p>
    
    <s:token />

</s:form>

<s:iterator value="validationMsgs" status="vstat">
    <s:property value="validationMsgs[%{#vstat.index}]" />
</s:iterator>

</tiles:putAttribute>
</tiles:insertTemplate>
