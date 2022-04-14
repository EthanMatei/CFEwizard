<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
 
<tiles:insertTemplate template="/pages/template.jsp" flush="true">

<tiles:putAttribute name="header">
    <title>CFG Wizard - Dynamic Disease Scoring</title>
    <s:head />


<script>

//--------------------------------------------------------------------------------------------------
// This code is for the disease check boxes. This code tries to implement the following behavior:
// 1) If a domain box is checked, all of its subdomain and disorder boxes should be checked
// 2) if a domain box is uncheced, all of its subdomain and disorder boxes should be unchecked
// 3) If a subdomain box is checked, all of its disorder boxes should be checked, and if all of
//    its domain's subdomains boxes are now checked, its domain box should be checked
// ...
//
//--------------------------------------------------------------------------------------------------
function getInfo(id) {
	var info = new Array(null, null, null, null);
	var domain = null;
	var subDomain = null;
	var disorder = null;
	
	var splitId = id.split(':');
	
	if (splitId.length == 2) {
	    var type = splitId[0];

	    var values = splitId[1].split('|');
	    if (values.length > 0) {
	    	domain = values[0];
	    }
    	if (values.length > 1) {
    		subDomain = values[1];
    	}
    	if (values.length > 2) {
    		disorder = values[2];
    	}
    	
	    info[0] = type;
    	info[1] = domain;
    	info[2] = subDomain;
    	info[3] = disorder;
    }
	
	return info;
}

function checkDomain(domain) {
    var inputs = document.getElementsByTagName("input");
    var domainNode = null;
    
    var subDomainsChecked = true;
    for (var i = 0; i < inputs.length; i++) {  
       
    	if (inputs[i].type == "checkbox") {
    	    var eInfo = getInfo(inputs[i].id);
    	    if (eInfo[0] == 'SUBDOMAIN' && eInfo[1] == domain && !inputs[i].checked) {
    	    	subDomainsChecked = false;
    	    }
    	    else if (eInfo[0] == 'DOMAIN' && eInfo[1] == domain) {
    	    	domainNode = inputs[i];
    	    }
        }
        
    }
    
    if (subDomainsChecked && domainNode != null) {
    	domainNode.checked = true;
    }
    
}

function checkSubDomain(domain, subdomain) {
    var inputs = document.getElementsByTagName("input");
    var subDomainNode = null;
    
    var disordersChecked = true;
    for (var i = 0; i < inputs.length; i++) {  
       
    	if (inputs[i].type == "checkbox") {
    	    var eInfo = getInfo(inputs[i].id);
    	    if (eInfo[0] == 'DISORDER' && eInfo[1] == domain && eInfo[2] == subdomain && !inputs[i].checked) {
    	    	disordersChecked = false;
    	    }
    	    else if (eInfo[0] == 'SUBDOMAIN' && eInfo[1] == domain && eInfo[2] == subdomain) {
    	    	subDomainNode = inputs[i];
    	    }
        }
        
    }
    
    if (disordersChecked && subDomainNode != null) {
    	subDomainNode.checked = true;
    }
    
}

function selectBox(element) {
    //alert("TEST: " + element.id);
    var id = element.id;
    var checked = element.checked;

    var info = getInfo(id);
    var type      = info[0];
    var domain    = info[1];
    var subDomain = info[2];
    var disorder  = info[3];
    
   
    if (type == 'DOMAIN') {
    	// Set all checkboxes with this domain to the value selected (checked or unchecked)
        var inputs = document.getElementsByTagName("input");
        for (var i = 0; i < inputs.length; i++) {  
            if (inputs[i].type == "checkbox") {
        	    var eInfo = getInfo(inputs[i].id);
        	    if (eInfo[1] == domain) {
        		    if (checked) {
        		        inputs[i].checked = true;
        		    }
        		    else {
        		        inputs[i].checked = false;
        		    }
        	    }
        	  
            }  
        }
    }
    else if (type == 'SUBDOMAIN') {
    	var values = id.replace(/^SUBDOMAIN:/,'');
    	var varr = values.split('|');
    	domain = varr[0];
    	subDomain = varr[1];
    	
        var inputs = document.getElementsByTagName("input");
        for (var i = 0; i < inputs.length; i++) {  
          if (inputs[i].type == "checkbox") {
        	  var eInfo = getInfo(inputs[i].id);
        	  if (eInfo[0] == 'DISORDER' && eInfo[1] == domain && eInfo[2] == subDomain) {
        		  if (checked) {
        		      inputs[i].checked = true;
        		  }
        		  else {
        		      inputs[i].checked = false;
        		  }
        	  }
        	  if (eInfo[0] == 'DOMAIN' && eInfo[1] == domain) {
        		  if (checked) {
        			  checkDomain(domain);
        		  }
        		  else {  // A subdomain of the domain has been unchecked, so uncheck the domain
        			  inputs[i].checked = false;
        		  }
        	  }
        	  
          }  
        }  
    	//alert('Sub Domain: ' + domain + " " + subDomain);
    }
    else if (type == 'DISORDER') {
        var inputs = document.getElementsByTagName("input");
        for (var i = 0; i < inputs.length; i++) {  
            if (inputs[i].type == "checkbox") {
        	    var eInfo = getInfo(inputs[i].id);
        	    if (eInfo[0] == 'DOMAIN' && eInfo[1] == domain) {
        		    if (checked) {
        		    	checkSubDomain(domain, subDomain);
        		    	checkDomain(domain);
        		    }
        		    else {
          			    inputs[i].checked = false;
        		    }
        	    }
        	    if (eInfo[0] == 'SUBDOMAIN' && eInfo[1] == domain && eInfo[2] == subDomain) {
        		    if (checked) {
        		    	checkSubDomain(domain, subDomain);
        		    	checkDomain(domain);
        		    }
        		    else {
          			    inputs[i].checked = false;
        		    }
        	    }
            }
        }
    	//alert('A Disorder!');
    }
}
</script>

</tiles:putAttribute>
<tiles:putAttribute name="content">

<div style="width:100%">
    <div style="float:left">
        <h1>Disease Selection</h1>
    </div>
    
    <div style="float:right">
        <s:a action="PrioritizationReport">
            <s:param name="reportName" value="'diseases'" />
            <s:param name="reportFormat" value="'xlsx'" />
            <div style="text-align:center">
                <img border="0"
                     style="margin-top: 2px;"
                     src="<s:url includeParams='none' value='/images/application-vnd.ms-excel.png'/>"
                     alt="SCORES" />
            </div>
        </s:a>
    </div>
</div>

<div style="clear:both">
</div>

<s:actionerror />

<p>
Import a diseases CSV file with columns "Domain", "SubDomain", "Relavant Disorder", "Coefficient". <b>OR</b>,
manually select diseases and enter coefficients.
</p>

<%-- Diseases import form --%>
<div style="border: 1px solid #222222; padding: 4px; margin-bottom: 32px;">
<h4>Diseases CSV File Import</h4>
<s:form id ="importDiseases" action="PrioritizationDiseasesImport" method="post" enctype="multipart/form-data">
    <s:file name="diseasesImport" label="Disease Selection CSV File" />
    <s:submit value="Import"/>
    <s:token />
</s:form>
</div>

<s:form action="PrioritizationDiseaseSelectionProcess" theme="simple" cssStyle="margin-bottom: 10px;">
    <s:hidden name="score" />
    <s:hidden name="otherCompleted" />


<s:set var="previousDomain" value="" />
<s:set var="previousSubDomain" value="" />

<div class="scrollable">

<div>
<s:iterator value="diseaseSelectors" var="dSelectors" status="status">
	

    <%-- If this is a new psychiatric domain (or the first one), start a new table --%>
    <s:if test="#previousDomain!=psychiatricDomain">
        <table border="0">
            <thead>
                <tr><th>Psychiatric Domain</th><th>SubDomain</th><th>Relevant Disorder</th><th style="margin-left: 1em">Coefficient</th></tr>
            </thead>
            <tbody>    
    </s:if>                      

    <tr>
    
        <%-- Pyschiatric Domain --%>
        <s:hidden name="diseaseSelectors[%{#status.index}].psychiatricDomain" />
        <s:if test="#previousDomain==psychiatricDomain">
            <td>&nbsp;</td>
        </s:if>
        <s:else>
            <td>
                <s:checkbox name="diseaseSelectors[%{#status.index}].psychiatricDomainSelected"
                            id="%{'DOMAIN:' + psychiatricDomain}"
                            onclick="selectBox(this);"/>
                <s:property value="psychiatricDomain"/>
            </td>
        </s:else>

        <%-- Psychiatric Sub Domain --%>
        <s:hidden name="diseaseSelectors[%{#status.index}].psychiatricSubDomain" />
        <s:if test="#previousSubDomain==psychiatricSubDomain">
            <td>&nbsp;</td>
        </s:if>
        <s:else>
            <td>
                <s:checkbox name="diseaseSelectors[%{#status.index}].psychiatricDomainSelected"
                            id="%{'SUBDOMAIN:' + psychiatricDomain + '|' + psychiatricSubDomain}"
                            onclick="selectBox(this);"/>
                <s:property value="psychiatricSubDomain"/>
            </td>
        </s:else>
        
        <%-- Relevant Disorder --%>
        <td>
            <s:checkbox name="diseaseSelectors[%{#status.index}].relevantDisorderSelected"
                        id="%{'DISORDER:' + psychiatricDomain + '|' + psychiatricSubDomain + '|' + relevantDisorder}"
                        onclick="selectBox(this);"/>
            <s:property value="relevantDisorder"/>
            <s:hidden name="diseaseSelectors[%{#status.index}].relevantDisorder" />
        </td>
        
        <%-- Coefficient --%>
        <td>
            <s:set var="coefficient" value="diseaseSelectors[%{#status.index}].coefficient"/>
            <s:textfield size="7" cssStyle="text-align: right;margin-left: 1em"
                name="diseaseSelectors[%{#status.index}].coefficient"
                value="%{getText('{0,number,##0.0}',{coefficient})}"
                theme="simple"
            />
        </td>
    </tr>


    <%-- If this is the last of all rows, or the next row has a different domain name,
         end the current table.
     --%>
    <s:if test="#status.index==diseaseSelectors.size-1||!psychiatricDomain.equals(diseaseSelectors[#status.index+1].psychiatricDomain)">
       </tbody>
       </table>
       <hr />
    </s:if>
    
    <s:set var="previousDomain" value="psychiatricDomain" />
    <s:set var="previousSubDomain" value="psychiatricSubDomain" />
</s:iterator>
</div>
</div>

<s:submit value="Next" cssStyle="margin-top: 4px;"/>
  
<s:token />

</s:form>


<s:iterator value="disorders">
<s:property value="domain" />
<s:property value="subdomain" />
<s:property value="relevantDisorder" />
<br />
</s:iterator>


</tiles:putAttribute>
</tiles:insertTemplate>