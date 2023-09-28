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