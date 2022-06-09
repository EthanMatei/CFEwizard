<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
 
<tiles:insertTemplate template="/pages/template.jsp" flush="true">

<tiles:putAttribute name="header">
    <title>CFE Wizard - Validation and Testing Cohort Specification</title>
    <s:head />
    <script src="<s:url includeParams='none' value='/js/jquery-3.6.0.min.js'/>"></script> 
    <script src="<s:url includeParams='none' value='/js/jquery.fancytree-all-deps.min.js'/>"></script> 
</tiles:putAttribute>
<tiles:putAttribute name="content">


<h2>Validation and Testing Cohort Specification</h2>

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
</table>

<div style="border: 1px solid #222222; border-radius: 10px; maring-top: 20px; padding: 10px;">

    <p style="font-weight: bold; margin-top: 12px;">Validation Cohort Constraint</p>
    <s:form theme="simple" action="ClinicalAndTestingCohortsProcess" method="post" enctype="multipart/form-data">
        <s:hidden name="discoveryId"/>
        <s:hidden name="discoveryPhene"/>
        <s:hidden name="discoveryLowCutoff"/>
        <s:hidden name="discoveryHighCutoff"/>
    
        <%--
        <table class="dataTable">
            <tr>
                <th> Attribute </th> <th> Value </th>
            </tr>
            <tr>
                <td> Clinical Phene: </td>
                <td> <s:select name="clinicalPhene" list="phenes" /> </td>
            </tr>
            <tr>
                <td> Clinical High Cutoff: </td>
                <td style="text-align: right;">
                    <s:textfield style="text-align: right;" name="clinicalHighCutoff" value="" />
                </td>
            </tr>
        </table>
        --%>
        
        <table class="dataTable">
            <tr> <th>Phene</th> <th>Relation</th> <th>Value</th> </tr>
            <tr>
                <td> <s:select name="phene1" list="phenes" /> </td>
                <td style="text-align: center"> <s:select name="operator1" list="operators" /> </td>
                <td> <s:textfield name="value1" value="" size="4"  style="text-align: right;"/> </td>
            </tr>
            <tr>
                <td> <s:select name="phene2" list="phenes" /> </td>
                <td style="text-align: center"> <s:select name="operator2" list="operators" /> </td>
                <td> <s:textfield name="value2" value="" size="4" style="text-align: right;"/> </td>
            </tr>
            <tr>
                <td> <s:select name="phene3" list="phenes" /> </td>
                <td style="text-align: center"> <s:select name="operator3" list="operators" /> </td>
                <td> <s:textfield name="value3" value="" size="4" style="text-align: right;"/> </td>
            </tr>
        </table>
        
        <div style="margin-top: 14px; margin-bottom: 14px;">
            % Subjects in Validation Cohort:
            <s:textfield name="percentInValidationCohort" value="50" size="4" style="text-align: right;"/>
        </div>
    
        <br />
        
        <div style="margin-bottom: 14px;">
            Follow up database: <s:file name="followUpDb" />
        </div>
        
        <p>
        Admission phene: <s:select name="admissionPhene" list="admissionReasons"/>
        </p>
    
    
        <%--
        <h3>Phenes:</h3>

        <div class="scrollable" style="height: 240px;">
            <div id="tree">
                <s:iterator value="pheneMap" var="table" status="pstat">
                    <span style="font-weight: bold;"><s:property value="key"/></span> <br/>
                    <s:iterator value="value" var="pheneValue">
                        <s:radio name="pheneSelection" list="#{#pheneValue.tableAndColumnName:#pheneValue.columnName}" /> <br/>
                    </s:iterator>
                </s:iterator>
            </div>
        </div>
        --%>
    
    
        <div>
        <s:submit value="Process" style="padding-left: 2em; padding-right: 2em; font-weight: bold;"/>
        </div>
    
        <s:token />
    </s:form>

</div>

<br/>

</tiles:putAttribute>
</tiles:insertTemplate>
