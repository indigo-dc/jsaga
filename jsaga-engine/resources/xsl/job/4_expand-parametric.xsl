<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
            xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
            xmlns="http://schemas.ggf.org/jsdl/2005/11/jsdl"
            xmlns:jsdl="http://schemas.ggf.org/jsdl/2005/11/jsdl"
            xmlns:ext="http://www.in2p3.fr/jsdl-extension"
            exclude-result-prefixes="jsdl">
    <!-- ###########################################################################
         # Expand parametric jobs for allocated resources
         ###########################################################################
    -->
    <xsl:output method="xml" indent="yes"/>

    <!-- entry point -->
    <xsl:template match="/jsdl:JobDefinition">
        <JobDefinition>
            <xsl:comment> GENERATED BY 4_expand-parametric.xsl </xsl:comment>
            <xsl:apply-templates select="@* | * | text() | comment()[position()>1]"/>
        </JobDefinition>
    </xsl:template>

    <!-- main rules -->
    <xsl:template match="jsdl:JobDescription">
        <xsl:choose>
            <!-- expand parametric allocated jobs -->
            <xsl:when test="ext:SelectedResource and contains(jsdl:JobIdentification/jsdl:JobName/text(),'@{INDICE}')">
                <xsl:variable name="param" select="/jsdl:JobDefinition/ext:JobCollection/ext:Parametric"/>
                <xsl:for-each select="ext:SelectedResource">
                    <JobDescription>
                        <xsl:apply-templates select="../@* | ../*[local-name()!='SelectedResource']">
                            <xsl:with-param name="value" select="$param/@start + (position()-1)*$param/@step"/>
                        </xsl:apply-templates>
                        <xsl:copy-of select="."/>
                    </JobDescription>
                </xsl:for-each>
            </xsl:when>
            <!-- keep other allocated jobs -->
            <xsl:when test="ext:SelectedResource">
                <xsl:copy-of select="."/>
            </xsl:when>
            <!-- remove unallocated jobs -->
            <xsl:otherwise/>
        </xsl:choose>
    </xsl:template>
    <xsl:template match="ext:JobCollection">
        <ext:JobCollection>
            <xsl:apply-templates select="@* | * | text() | comment()"/>
        </ext:JobCollection>
    </xsl:template>
    <xsl:template match="ext:Parametric">
        <!-- remove parametric info -->
    </xsl:template>

    <!-- default template rules -->
    <xsl:template match="*">
        <xsl:param name="value"/>
        <xsl:element name="{name()}">
            <xsl:apply-templates select="@* | * | text() | comment()">
                <xsl:with-param name="value" select="$value"/>
            </xsl:apply-templates>
        </xsl:element>
    </xsl:template>
    <xsl:template match="@*">
        <xsl:param name="value"/>
        <xsl:choose>
            <xsl:when test="$value">
                <xsl:attribute name="{name()}">
                    <xsl:call-template name="REPLACE_ALL">
                        <xsl:with-param name="string" select="."/>
                        <xsl:with-param name="value" select="$value"/>
                    </xsl:call-template>
                </xsl:attribute>
            </xsl:when>
            <xsl:otherwise>
                <xsl:copy-of select="."/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    <xsl:template match="text()">
        <xsl:param name="value"/>
        <xsl:choose>
            <xsl:when test="$value">
                <xsl:call-template name="REPLACE_ALL">
                    <xsl:with-param name="string" select="."/>
                    <xsl:with-param name="value" select="$value"/>
                </xsl:call-template>
            </xsl:when>
            <xsl:otherwise>
                <xsl:copy-of select="."/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    <xsl:template match="comment()">
        <xsl:param name="value"/>
        <xsl:choose>
            <xsl:when test="$value">
                <xsl:comment>
                    <xsl:call-template name="REPLACE_ALL">
                        <xsl:with-param name="string" select="."/>
                        <xsl:with-param name="value" select="$value"/>
                    </xsl:call-template>
                </xsl:comment>
            </xsl:when>
            <xsl:otherwise>
                <xsl:copy-of select="."/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    <xsl:template match="processing-instruction()"/>

    <!-- named templates -->
    <xsl:template name="REPLACE_ALL">
        <xsl:param name="string"/>
        <xsl:param name="token" select="'@{INDICE}'"/>
        <xsl:param name="value"/>
        <xsl:choose>
            <!-- next occurence: keep text before & replace occurence -->
            <xsl:when test="contains($string,$token) and $value">
                <xsl:value-of select="substring-before($string,$token)"/>
                <xsl:value-of select="$value"/>
                <xsl:call-template name="REPLACE_ALL">
                    <xsl:with-param name="string" select="substring-after($string,$token)"/>
                    <xsl:with-param name="token" select="$token"/>
                    <xsl:with-param name="value" select="$value"/>
                </xsl:call-template>
            </xsl:when>
            <!-- rest of string -->
            <xsl:otherwise>
                <xsl:value-of select="$string"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
</xsl:stylesheet>