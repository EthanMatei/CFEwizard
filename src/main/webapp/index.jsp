<%@ page language="java" contentType="text/html; charset=UTF-8"  pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<html>
<head>
	<title>CFG Home</title>
	<s:head />
</head>

<body>

<h1>Welcome to the CFG Wizard Scoring Program</h1>
<h4>version rc 0.8.1</h4>

<hr>
<h2>Please choose:</h2>
<ul>
    <li><a href="<s:url value="pages/dbSelection.jsp"/>">Upload Databases</a></li>
    

    <!--  <li><a href="<s:url value="pages/geneListUpload.jsp"/>">Calculate Scores</a></li> -->
   
    <li><s:a action="GeneListUpload">Calculate Scores</s:a></li>

    
</ul>

</body>
</html>