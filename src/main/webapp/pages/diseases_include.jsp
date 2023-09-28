<%@ taglib prefix="s" uri="/struts-tags" %>

<s:set var="previousDomain" value="" />
<s:set var="previousSubDomain" value="" />

<div class="scrollable">

<div>
<s:iterator value="diseaseSelectors" var="dSelectors" status="status">
	

    <%-- If this is a new psychiatric domain (or the first one), start a new table --%>
    <s:if test="#previousDomain!=psychiatricDomain">
        <table border="0">
            <thead>
                <tr><th>Psychiatric Domain</th><th>SubDomain</th><th>Relevant Disorder</th><th style="margin-left: 1em">Coefficient</th></tr>
            </thead>
            <tbody>    
    </s:if>                      

    <tr>
    
        <%-- Pyschiatric Domain --%>
        <s:hidden name="diseaseSelectors[%{#status.index}].psychiatricDomain" />
        <s:if test="#previousDomain==psychiatricDomain">
            <td>&nbsp;</td>
        </s:if>
        <s:else>
            <td>
                <s:checkbox name="diseaseSelectors[%{#status.index}].psychiatricDomainSelected"
                            id="%{'DOMAIN:' + psychiatricDomain}"
                            onclick="selectBox(this);"/>
                <s:property value="psychiatricDomain"/>
            </td>
        </s:else>

        <%-- Psychiatric Sub Domain --%>
        <s:hidden name="diseaseSelectors[%{#status.index}].psychiatricSubDomain" />
        <s:if test="#previousSubDomain==psychiatricSubDomain">
            <td>&nbsp;</td>
        </s:if>
        <s:else>
            <td>
                <s:checkbox name="diseaseSelectors[%{#status.index}].psychiatricDomainSelected"
                            id="%{'SUBDOMAIN:' + psychiatricDomain + '|' + psychiatricSubDomain}"
                            onclick="selectBox(this);"/>
                <s:property value="psychiatricSubDomain"/>
            </td>
        </s:else>
        
        <%-- Relevant Disorder --%>
        <td>
            <s:checkbox name="diseaseSelectors[%{#status.index}].relevantDisorderSelected"
                        id="%{'DISORDER:' + psychiatricDomain + '|' + psychiatricSubDomain + '|' + relevantDisorder}"
                        onclick="selectBox(this);"/>
            <s:property value="relevantDisorder"/>
            <s:hidden name="diseaseSelectors[%{#status.index}].relevantDisorder" />
        </td>
        
        <%-- Coefficient --%>
        <td>
            <s:set var="coefficient" value="diseaseSelectors[%{#status.index}].coefficient"/>
            <s:textfield size="7" cssStyle="text-align: right;margin-left: 1em"
                name="diseaseSelectors[%{#status.index}].coefficient"
                value="%{getText('{0,number,##0.0}',{coefficient})}"
                theme="simple"
            />
        </td>
    </tr>


    <%-- If this is the last of all rows, or the next row has a different domain name,
         end the current table.
     --%>
    <s:if test="#status.index==diseaseSelectors.size-1||!psychiatricDomain.equals(diseaseSelectors[#status.index+1].psychiatricDomain)">
       </tbody>
       </table>
       <hr />
    </s:if>
    
    <s:set var="previousDomain" value="psychiatricDomain" />
    <s:set var="previousSubDomain" value="psychiatricSubDomain" />
</s:iterator>
</div>
</div>