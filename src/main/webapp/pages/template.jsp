<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
      
<%-- 
CFG Template
--%>

<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>

 <html>
    <head>
        <link rel="stylesheet"  type="text/css" href="<s:url includeParams="none" value='/css/cfg.css'/>" />
        <tiles:insertAttribute name="header" />
    </head>
    <body>
        <%-- Page Header --%>

        <div id="logo">
            <h2 style="margin-bottom: 4px; margin-top: 4px;"">CFG Wizard Scoring Program</h2>
            <h4 style="margin-top:2px; margin-bottom:4px">version <s:property value="@cfg.model.VersionNumber@VERSION_NUMBER" /> </h4>
        </div>
        

        <div style="clear: both;"></div>
        
        <s:if test="#session.username != null & #session.username != ''" >
            <div style="float: left;"> <s:a action="Home">Home</s:a> </div>
            <div style="float: right;"> <s:a action="LogoutAction">Logout</s:a> </div>
            <div style="clear: both;"></div>
        </s:if>
        
        <hr />
                    
        <%-- Content --%>
    	<div class="content">
            <tiles:insertAttribute name="content" />
        </div>
    </body>
</html>