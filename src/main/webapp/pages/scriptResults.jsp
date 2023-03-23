<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
 
<tiles:insertTemplate template="/pages/template.jsp" flush="true">

<tiles:putAttribute name="header">
    <title>CFE Wizard - Script Results</title>
</tiles:putAttribute>
<tiles:putAttribute name="content">

<%-- User: <s:property value="#session.username" /> --%>

<h2>Script Results</h2>

<s:if test="errorMessage != null && errorMessage != ''">
    <div class="cfeError">
        <span style="font-weight: bold;">ERROR:</span> <s:property value="errorMessage" />
        <s:if test="exceptionStack != null && exceptionStack != ''">
            <p>
                <s:property value="exceptionStack" />
            </p>
        </s:if>
    </div>
</s:if>

<h4> Script Command </h4>
<pre>
<s:property value="scriptCommand"/>
</pre>

<h4> Script Output </h4>
<pre>
<s:property value="scriptOutput"/>
</pre>


</tiles:putAttribute>
</tiles:insertTemplate>
