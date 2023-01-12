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

<s:if test="errorMessage != ''">
    <div class="cfeError">
        <span style="font-weight: bold;">ERROR:</span> <s:property value="errorMessage" />
    </div>
</s:if>

<s:if test="exceptionStack != ''">
    <h4>Exception Details</h4>
    <pre>
    <s:property value="exceptionStack" />
    </pre> 
</s:if>

</body>

</html>