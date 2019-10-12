<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

    <xsl:output method="html" />

    <xsl:template match="/">
        <html>
            <head>
                <title>Dependency Track Findings Report</title>
                <style>
                    table {border-collapse: collapse;}
                    td {padding: 4px;border: 1px black solid;}
                    th {text-align: left;padding: 4px;border: 1px black solid;}
                    div {margin-bottom: 10px}

                    body{
                        font-family: Verdana, Geneva, sans-serif;
                        font-size: 12px;
                        color: #000000;
                        font-weight: normal;
                        text-decoration: none;
                        font-style: normal;
                        font-variant: normal;
                        text-transform: none;
                    }
                </style>
            </head>
            <body>
                <h1>Dependency Track Findings Report</h1>
                <h2>Policy</h2>
                <div>
                    The following Policy was applied when the report was generated.
                </div>
                <table>
                    <tr>
                        <th>Severity</th>
                        <th>Maximum Allowed with Severity</th>
                        <th>Issues Found with Severity</th>
                    </tr>
                    <tr>
                        <td>Maximum Number of Critical Issues</td>
                        <td><xsl:value-of select="findingsReport/policyApplied/maximumCriticalIssueCount" /></td>
                        <td><xsl:value-of select="findingsReport/critical/count" /></td>
                    </tr>
                    <tr>
                        <td>Maximum Number of High Issues</td>
                        <td><xsl:value-of select="findingsReport/policyApplied/maximumHighIssueCount" /></td>
                        <td><xsl:value-of select="findingsReport/high/count" /></td>
                    </tr>
                    <tr>
                        <td>Maximum Number of Medium Issues</td>
                        <td><xsl:value-of select="findingsReport/policyApplied/maximumMediumIssueCount" /></td>
                        <td><xsl:value-of select="findingsReport/medium/count" /></td>
                    </tr>
                    <tr>
                        <td>Maximum Number of Low Issues</td>
                        <td><xsl:value-of select="findingsReport/policyApplied/maximumLowIssueCount" /></td>
                        <td><xsl:value-of select="findingsReport/low/count" /></td>
                    </tr>
                    <tr>
                        <td>Maximum Number of Unassigned Issues</td>
                        <td><xsl:value-of select="findingsReport/policyApplied/maximumUnassignedIssueCount" /></td>
                        <td><xsl:value-of select="findingsReport/unassigned/count" /></td>
                    </tr>
                </table>

                <xsl:if test="findingsReport/critical/count > 0">
                    <h2>Critical Issues</h2>
                    <table id="critical-issues">
                        <tr>
                            <th>Group</th>
                            <th>Name</th>
                            <th>Version</th>
                            <th>Suppression</th>
                            <th>Description</th>
                        </tr>
                        <xsl:for-each select="findingsReport/critical/findings/finding">
                            <tr>
                                <td><xsl:value-of select="component/group" /></td>
                                <td><xsl:value-of select="component/name" /></td>
                                <td><xsl:value-of select="component/version" /></td>
                                <td><xsl:value-of select="analysis/state" /></td>
                                <td><xsl:value-of select="vulnerability/description" /></td>
                            </tr>
                        </xsl:for-each>
                    </table>
                </xsl:if>

                <xsl:if test="findingsReport/high/count > 0">
                    <h2>High Issues</h2>
                    <table id="high-issues">
                        <tr>
                            <th>Group</th>
                            <th>Name</th>
                            <th>Version</th>
                            <th>Suppression</th>
                            <th>Description</th>
                        </tr>
                        <xsl:for-each select="findingsReport/high/findings/finding">
                            <tr>
                                <td><xsl:value-of select="component/group" /></td>
                                <td><xsl:value-of select="component/name" /></td>
                                <td><xsl:value-of select="component/version" /></td>
                                <td><xsl:value-of select="analysis/state" /></td>
                                <td><xsl:value-of select="vulnerability/description" /></td>
                            </tr>
                        </xsl:for-each>
                    </table>
                </xsl:if>

                <xsl:if test="findingsReport/medium/count > 0">
                    <h2>Medium Issues</h2>
                    <table id="medium-issues">
                        <tr>
                            <th>Group</th>
                            <th>Name</th>
                            <th>Version</th>
                            <th>Suppression</th>
                            <th>Description</th>
                        </tr>
                        <xsl:for-each select="findingsReport/medium/findings/finding">
                            <tr>
                                <td><xsl:value-of select="component/group" /></td>
                                <td><xsl:value-of select="component/name" /></td>
                                <td><xsl:value-of select="component/version" /></td>
                                <td><xsl:value-of select="analysis/state" /></td>
                                <td><xsl:value-of select="vulnerability/description" /></td>
                            </tr>
                        </xsl:for-each>
                    </table>
                </xsl:if>

                <xsl:if test="findingsReport/low/count > 0">
                    <h2>Low Issues</h2>
                    <table id="low-issues">
                        <tr>
                            <th>Group</th>
                            <th>Name</th>
                            <th>Version</th>
                            <th>Suppression</th>
                            <th>Description</th>
                        </tr>
                        <xsl:for-each select="findingsReport/low/findings/finding">
                            <tr>
                                <td><xsl:value-of select="component/group" /></td>
                                <td><xsl:value-of select="component/name" /></td>
                                <td><xsl:value-of select="component/version" /></td>
                                <td><xsl:value-of select="analysis/state" /></td>
                                <td><xsl:value-of select="vulnerability/description" /></td>
                            </tr>
                        </xsl:for-each>
                    </table>
                </xsl:if>

                <xsl:if test="findingsReport/unassigned/count > 0">
                    <h2>Unassigned Issues</h2>
                    <table id="unassigned-issues">
                        <tr>
                            <th>Group</th>
                            <th>Name</th>
                            <th>Version</th>
                            <th>Suppression</th>
                            <th>Description</th>
                        </tr>
                        <xsl:for-each select="findingsReport/unassigned/findings/finding">
                            <tr>
                                <td><xsl:value-of select="component/group" /></td>
                                <td><xsl:value-of select="component/name" /></td>
                                <td><xsl:value-of select="component/version" /></td>
                                <td><xsl:value-of select="analysis/state" /></td>
                                <td><xsl:value-of select="vulnerability/description" /></td>
                            </tr>
                        </xsl:for-each>
                    </table>
                </xsl:if>
            </body>
        </html>
    </xsl:template>

</xsl:stylesheet>