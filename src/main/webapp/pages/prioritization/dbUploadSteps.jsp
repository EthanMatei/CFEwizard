<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>


<div class="steps">

    <%-- ================================================
    Set the selected step
    ================================================ --%>
    <s:set var="step1class" value="''"/>
    <s:set var="step2class" value="''"/>
    <s:set var="step3class" value="''"/>
     
    <s:if test="currentStep == 1">
        <s:set var="step1class" value="'selectedStep'"/>
    </s:if>
    <s:elseif test="currentStep == 2">
        <s:set var="step2class" value="'selectedStep'"/>
    </s:elseif>
    <s:elseif test="currentStep == 3">
        <s:set var="step3class" value="'selectedStep'"/>
    </s:elseif>


    <%-- STEP 1 - DATABASE SELECTION --%>
    <div class="<s:property value='step1class'/>" style="display: inline-block;">
        <span class="stepNumber">1</span>
        Database Selection
    </div>

    <i class="fa fa-right-long"></i>

    <%-- STEP 2 - DATABSE UPLOAD --%>
    <div class="<s:property value='step2class'/>"style="display: inline-block;">
        <span class="stepNumber">2</span>
        Database Upload
    </div>

    <i class="fa fa-right-long"></i>

    <%-- STEP 3 - DATABASE UPLOAD RESULTS --%>
    <div class="<s:property value='step3class'/>"style="display: inline-block;">
        <span class="stepNumber">3</span>
        Database Upload Results
    </div>
    
</div>

<hr style="margin-top: 0;"/>
