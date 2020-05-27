<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Error</title>
</head>
<body>
<p><a href="<s:url value="/"/>">Home</a></p>
<H1>Error!</H1>
<s:actionerror />

<h4>The application has malfunctioned.</h4>
 
<p>Please contact technical support with the following information:</p>
 
<h4>Exception Name: <s:property value="exception" /> </h4>
 
<h4>Exception Details: <s:property value="exceptionStack" /></h4> 

<s:if test="errorMessage != ''">
    ERROR: <s:property value="errorMessage" />
</s:if>
</body>

</html>