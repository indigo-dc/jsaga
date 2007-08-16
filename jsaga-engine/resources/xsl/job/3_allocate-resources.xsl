<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
            xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
            xmlns:jsdl="http://schemas.ggf.org/jsdl/2005/11/jsdl"
            xmlns:ext="http://www.in2p3.fr/jsdl-extension">
    <!-- ###########################################################################
         # Allocate selected resources to jobs
         ###########################################################################
    -->
    <xsl:output method="xml" indent="yes"/>
    <xsl:variable name="id">
        <xsl:choose>
            <xsl:when test="/jsdl:JobDefinition/ext:JobCollection/ext:JobCollectionName/text()">
                <xsl:value-of select="/jsdl:JobDefinition/ext:JobCollection/ext:JobCollectionName/text()"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="/jsdl:JobDefinition/jsdl:JobDescription[1]/jsdl:JobIdentification/jsdl:JobName/text()"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:variable>
    <xsl:param name="selected" select="document(concat('var/',$id,'/selected-resources.xml'))/*"/>

    <!-- entry point -->
    <xsl:template match="/">
        <xsl:apply-templates/>
    </xsl:template>
    <xsl:template match="jsdl:JobDefinition">
        <jsdl:JobDefinition>
            <xsl:comment> GENERATED BY 3_allocate-resources.xsl </xsl:comment>
            <xsl:apply-templates select="@* | * | text() | comment()[position()>1]"/>
        </jsdl:JobDefinition>
    </xsl:template>

    <!-- main rules -->
    <xsl:template match="jsdl:JobDescription">
        <jsdl:JobDescription>
            <xsl:apply-templates select="@* | * | text() | comment()"/>
            <xsl:variable name="jobname" select="jsdl:JobIdentification/jsdl:JobName/text()"/>
            <xsl:for-each select="$selected/ext:ResourceRequest[@name=$jobname]">
                <xsl:comment> automatically added </xsl:comment>
                <xsl:apply-templates select="ext:SelectedResource"/>
            </xsl:for-each>
        </jsdl:JobDescription>
    </xsl:template>
    <xsl:template match="ext:SelectedResource">
        <xsl:call-template name="DUPLICATE_SelectedResource">
            <xsl:with-param name="count">
                <xsl:choose>
                    <xsl:when test="@nbslots"><xsl:value-of select="@nbslots"/></xsl:when>
                    <xsl:otherwise>1</xsl:otherwise>
                </xsl:choose>
            </xsl:with-param>
        </xsl:call-template>
    </xsl:template>
    <xsl:template name="DUPLICATE_SelectedResource">
        <xsl:param name="count"/>
        <xsl:if test="$count &gt; 0">
            <!-- append element -->
            <ext:SelectedResource>
                <xsl:apply-templates select="@*[local-name()!='nbslots'] | * | text() | comment()"/>
            </ext:SelectedResource>
            <!-- recurse -->
            <xsl:call-template name="DUPLICATE_SelectedResource">
                <xsl:with-param name="count" select=" $count - 1"/>
            </xsl:call-template>
        </xsl:if>
    </xsl:template>

    <!-- default template rules -->
    <xsl:template match="*|@*|text()|comment()">
        <xsl:copy-of select="."/>
    </xsl:template>
    <xsl:template match="processing-instruction()"/>
</xsl:stylesheet>