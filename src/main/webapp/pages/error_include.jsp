<%@ taglib prefix="s" uri="/struts-tags" %>
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