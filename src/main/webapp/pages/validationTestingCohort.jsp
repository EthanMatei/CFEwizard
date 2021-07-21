<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
 
<tiles:insertTemplate template="/pages/template.jsp" flush="true">

<tiles:putAttribute name="header">
    <title>CFE Wizard - Validation and Testing Cohort</title>
    <s:head />
    <script src="<s:url includeParams='none' value='/js/jquery-3.6.0.min.js'/>"></script> 
    <script src="<s:url includeParams='none' value='/js/jquery.fancytree-all-deps.min.js'/>"></script> 
</tiles:putAttribute>
<tiles:putAttribute name="content">


<h2>Validation and Testing Cohort</h2>

<s:actionerror />

<table class="dataTable" style="margin-bottom: 20px;">
    <tr> <th>Discovery Phene</th> <th>Low Cutoff</th> <th>High Cutoff</th> </tr>
    <tr>
        <td style="text-align: right;"> <s:property value="discoveryPhene"/> </td>
        <td style="text-align: right;"> <s:property value="discoveryLowCutoff"/> </td>
        <td style="text-align: right;"> <s:property value="discoveryHighCutoff"/> </td>
</table>



<p style="font-weight: bold; margin-top: 12px;">Additional Validation and Testing Cohort Constraints</p>
   
<table class="dataTable">
    <tr> <th>Phene</th> <th>Relation</th> <th>Value</th> </tr>
    <tr>
        <td> <s:property value="phene1"/> </td>
        <td style="text-align: center"> <s:property value="operator1"/> </td>
        <td style="text-align: right;"> <s:property value="value1"/> </td>
    </tr>
    <tr>
        <td> <s:property value="phene2"/> </td>
        <td style="text-align: center"> <s:property value="operator2"/> </td>
        <td style="text-align: right;"> <s:property value="value2"/> </td>
    </tr>
    <tr>
        <td> <s:property value="phene3"/> </td>
        <td style="text-align: center"> <s:property value="operator3"/> </td>
        <td style="text-align: right;"> <s:property value="value3"/> </td>
    </tr>
</table>
    
<div style="margin-top: 14px; margin-bottom: 14px;">
    Subjects specified in Validation Cohort:
    <s:property value="percentInValidationCohort"/>%
</div>
    

<br/>

</tiles:putAttribute>
</tiles:insertTemplate>
