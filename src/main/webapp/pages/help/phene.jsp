<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>

<h1>Phene Help</h1>

<p>
For the CFE Wizard to get the discovery phene information from an uploaded spreadsheet,
it needs to contain a sheet named "<s:property value="@cfe.model.CfeResultsSheets@DISCOVERY_COHORT_INFO"/>""
that contains the following information (example phene name and cutoffs shown):
</p>

<table border="1">
    <tr>
        <th>attribute</th>	<th>value</th>
    </tr>
    
    <tr>
        <td>Phene</td> <td>Sheet1.SAS4</td>
    </tr> 
    <tr>
        <td>Low Cutoff</td>	<td style="text-align: right;">40</td>
    </tr>
    <tr>
        <td>High Cutoff</td> <td style="text-align: right;">60</td>
    </tr>
</table>

<p>
Note: the phene name needs to contain the phene's database table name ("Sheet1" in the example above).
</p>
