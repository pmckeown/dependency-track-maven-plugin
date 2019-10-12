<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

    <xsl:output method="html" />

    <xsl:template match="/">
        <html>
            <head>
                <title>Dependency Track Findings Report</title>
            </head>
            <body>
                <h1>Dependency Track Findings Report</h1>
                <div>
                    Bad stuff found
                </div>
                <h2>Policy Applied</h2>
                <table>
                    <tr>
                        <td>Maximum Number of Critical Issues</td>
                        <td><xsl:value-of select="findingsReport/policyApplied/maximumCriticalIssueCount" /></td>
                    </tr>
                    <tr>
                        <td>Maximum Number of High Issues</td>
                        <td><xsl:value-of select="findingsReport/policyApplied/maximumHighIssueCount" /></td>
                    </tr>
                    <tr>
                        <td>Maximum Number of Medium Issues</td>
                        <td><xsl:value-of select="findingsReport/policyApplied/maximumMediumIssueCount" /></td>
                    </tr>
                    <tr>
                        <td>Maximum Number of Low Issues</td>
                        <td><xsl:value-of select="findingsReport/policyApplied/maximumLowIssueCount" /></td>
                    </tr>
                    <tr>
                        <td>Maximum Number of Unassigned Issues</td>
                        <td><xsl:value-of select="findingsReport/policyApplied/maximumUnassignedIssueCount" /></td>
                    </tr>
                </table>
            </body>
        </html>
    </xsl:template>

</xsl:stylesheet>