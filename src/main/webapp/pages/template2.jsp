<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>

<!DOCTYPE HTML>
      
<%-- 
CFE Template with status message
--%>

<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>

 <html lang="en">
    <head>
        <link rel="stylesheet"  type="text/css" href="<s:url includeParams="none" value='/css/cfe.css'/>" />
        <tiles:insertAttribute name="header" />
    </head>
    <body>
        <%-- Page Header --%>

        <div id="logo">
            <h2 style="margin-bottom: 4px; margin-top: 4px;">CFE Wizard Scoring Program</h2>
            <h4 style="margin-top:2px; margin-bottom:4px">version <s:property value="@cfe.model.VersionNumber@VERSION_NUMBER" /> </h4>
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
    	    <%-- Error Message --%>
    	    <s:if test="errorMessage != null && errorMessage != ''">
                <div class="cfeError">
                    <span style="font-weight: bold;">ERROR:</span> <s:property value="errorMessage" />
                    <s:if test="exceptionStack != null && exceptionStack != ''">
                        <br/>
                        <pre>
                        <s:property value="exceptionStack" />
                        </pre>
                    </s:if>
                </div>
            </s:if>
            <tiles:insertAttribute name="contentHeader" />
            <tiles:insertAttribute name="content" />
        </div>
    </body>
</html>
