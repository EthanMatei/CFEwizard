<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
 
<tiles:insertTemplate template="/pages/template.jsp" flush="true">

<tiles:putAttribute name="header">
    <title>CFG Wizard - Database Upload Processing...</title>
    <meta http-equiv="refresh" content="5;url=<s:url includeParams="all" />"/>
    <s:head />
</tiles:putAttribute>
<tiles:putAttribute name="content">

<h3>Processing...</h3>
<p>Please wait.</p>
<p>Click <s:a action="Home">here</s:a> to return to the Home page</p>
</body>
</html>

</tiles:putAttribute>
</tiles:insertTemplate>