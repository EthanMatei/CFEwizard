<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
 
<tiles:insertTemplate template="/pages/template.jsp" flush="true">

<tiles:putAttribute name="header">
    <title>CFE Wizard</title>
</tiles:putAttribute>
<tiles:putAttribute name="content">

<%-- User: <s:property value="#session.username" /> --%>

<h2>Please choose:</h2>
<ul>
    
    
    <%-- Only allow admins see and make database uploads --%>
    <s:if test="#session.username==adminUser">
        <%-- <li> <s:a action="DatabaseList">Database Upload Info</s:a> </li> --%>
        <li> <s:a action="DBSelectionInitialize">Upload Databases </s:a></li>
    </s:if>
    
    <li><s:a action="ScoringWeights">Calculate Scores</s:a></li>
</ul>

<s:if test="#session.username==adminUser">
<hr />
<h3>Admin</h3>
    <ul>
        <%-- <li> <s:a action="DatabaseList">Database Upload Info</s:a> </li> --%>
        <li> <s:a action="DatabaseStatusAction">CFE Database Status</s:a></li>
        <li> <s:a action="SystemStatusAction">System Status</s:a></li>
    </ul>
</s:if>

</tiles:putAttribute>
</tiles:insertTemplate>
