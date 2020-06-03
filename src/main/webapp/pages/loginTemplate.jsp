<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
      
<%-- 
CFE Login Template
--%>

<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>

 <html>
    <head>
     <!-- 
        <link rel="stylesheet"  type="text/css" href="<s:url includeParams="none" value='/css/cfe.css'/>" />
                -->
        <tiles:insertAttribute name="header" />

    </head>
    <body>
        <%-- Page Header --%>


        <h2>CFE LOGIN</h2>
        
        <%-- Content --%>
    	<div class="content">
            <tiles:insertAttribute name="content" />
        </div>
    </body>
</html>
