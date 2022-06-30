<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
 
<tiles:insertTemplate template="/pages/template.jsp" flush="true">

<tiles:putAttribute name="header">
    <title>CFE Wizard - Testing Cohorts Specification</title>
    <s:head />
    <script src="<s:url includeParams='none' value='/js/jquery-3.6.0.min.js'/>"></script> 
    <script src="<s:url includeParams='none' value='/js/jquery.fancytree-all-deps.min.js'/>"></script> 
</tiles:putAttribute>
<tiles:putAttribute name="content">


<h2>Testing Cohorts Specification</h2>

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

<s:if test="validationConstraint1 != '' || validationConstraint2 != '' || validationConstraint3 != ''">
<p style="font-weight: bold;">&nbsp;</p>
   
<table class="dataTable" style="margin-bottom: 32px;">

    <tr> <th>Validation Cohort Constraints</th> </tr>
    
    <s:if test="validationConstraint1 != ''">
    <tr>
        <td> <s:property value="validationConstraint1"/> </td>
    </tr>
    </s:if>
    
    <s:if test="validationConstraint2 != ''">
    <tr>
        <td> <s:property value="validationConstraint2"/> </td>
    </tr>
    </s:if>
    
    <s:if test="validationConstraint3 != ''">
        <tr>
            <td> <s:property value="validationConstraint3"/> </td>
        </tr>
    </s:if>
</table>
</s:if>

<div style="border: 1px solid #222222; border-radius: 10px; maring-top: 20px; padding: 10px;">
    
    <s:form theme="simple" action="TestingCohortsProcess" method="post" enctype="multipart/form-data">
        <s:hidden name="validationId"/>
        <s:hidden name="discoveryPhene"/>
        <s:hidden name="discoveryLowCutoff"/>
        <s:hidden name="discoveryHighCutoff"/>
        
        <s:hidden name="validationConstraint1"/>
        <s:hidden name="validationConstraint2"/>
        <s:hidden name="validationConstraint3"/>
        
        <s:hidden name="percentInValidationCohort"/>
                    
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
        
        <%--
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
        --%>
        
        <br />
        
        <div style="margin-bottom: 14px;">
            Follow up database: <s:file name="followUpDb" />
        </div>
        
        <p>
        Admission phene: <s:select name="admissionPhene" list="admissionReasons"/>
        </p>
    
    
        <div>
        <s:submit value="Process" style="padding-left: 2em; padding-right: 2em; font-weight: bold;"/>
        </div>
    
        <s:token />
    </s:form>

</div>

<br/>

</tiles:putAttribute>
</tiles:insertTemplate>
