<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>


<div class="steps">

    <%-- ================================================
    Set the selected step
    ================================================ --%>
    <s:set var="step1class" value="''"/>
    <s:set var="step2class" value="''"/>
    <s:set var="step3class" value="''"/>
    <s:set var="step4class" value="''"/>
     
    <s:if test="currentStep == 1">
        <s:set var="step1class" value="'selectedStep'"/>
    </s:if>
    <s:elseif test="currentStep == 2">
        <s:set var="step2class" value="'selectedStep'"/>
    </s:elseif>
    <s:elseif test="currentStep == 3">
        <s:set var="step3class" value="'selectedStep'"/>
    </s:elseif>
    <s:elseif test="currentStep == 4">
        <s:set var="step4class" value="'selectedStep'"/>
    </s:elseif>

    <%-- STEP 1 - GENE LIST SPECIFICATION --%>
    <div class="<s:property value='step1class'/>" style="display: inline-block;">
        <span class="stepNumber">1</span>
        Gene List Specification
    </div>

    <i class="fa fa-right-long"></i>

    <%-- STEP 2 - GLOBAL SCORING WEIGHTS --%>
    <div class="<s:property value='step2class'/>"style="display: inline-block;">
        <span class="stepNumber">2</span>
        Global Scoring Weights
    </div>

    <i class="fa fa-right-long"></i>

    <%-- STEP 3 - DISEASE SELECTION --%>
    <div class="<s:property value='step3class'/>"style="display: inline-block;">
        <span class="stepNumber">3</span>
        Disease Selection
    </div>

    <i class="fa fa-right-long"></i>

    <%-- STEP 4 - PRIORITIZATION SCORES --%>
    <div class="<s:property value='step4class'/>"style="display: inline-block;">
        <span class="stepNumber">4</span>
        Prioritization Scores
    </div>
    
</div>

<hr/>
