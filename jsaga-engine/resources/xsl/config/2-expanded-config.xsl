<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns="http://www.in2p3.fr/jsaga"
                xmlns:cfg="http://www.in2p3.fr/jsaga"
                exclude-result-prefixes="cfg">
    <xsl:output method="xml" indent="yes"/>
    <xsl:strip-space elements="*"/>

    <xsl:template match="/">
        <xsl:apply-templates/>
    </xsl:template>

    <xsl:template match="cfg:UNIVERSE">
        <UNIVERSE>
            <xsl:copy-of select="@*[local-name()!='wrapperMonitoring' and local-name()!='defaultIntermediary']"/>
            <xsl:apply-templates select="cfg:UNIVERSE | cfg:GRID | text() | comment()"/>
        </UNIVERSE>
    </xsl:template>

    <xsl:template match="cfg:GRID">
        <xsl:variable name="this" select="."/>
        <GRID>
            <xsl:copy-of select="@*[local-name()!='wrapperMonitoring' and local-name()!='defaultIntermediary']"/>
            <xsl:apply-templates select="cfg:attribute | cfg:SITE | text() | comment()"/>
            <SITE name="{@name}">
                <!-- do not copy GRID attributes -->
                <xsl:apply-templates select="cfg:domain"/>
                <xsl:apply-templates select="ancestor-or-self::cfg:*/cfg:fileSystem"/>
                <xsl:apply-templates select="cfg:job[not(@deactivated='true')]"/>
                <xsl:apply-templates select="ancestor-or-self::cfg:*/cfg:data[not(@deactivated='true')]"/>
            </SITE>
        </GRID>
    </xsl:template>

    <xsl:template match="cfg:SITE">
        <xsl:variable name="this" select="."/>
        <SITE name="{concat(parent::cfg:GRID/@name,'-',@name)}">
            <xsl:copy-of select="@*[local-name()!='wrapperMonitoring' and local-name()!='defaultIntermediary' and local-name()!='name']"/>
            <xsl:apply-templates select="cfg:attributes"/>
            <xsl:apply-templates select="cfg:domain"/>
            <xsl:apply-templates select="ancestor-or-self::cfg:*/cfg:fileSystem"/>
            <xsl:apply-templates select="parent::cfg:*/cfg:job[not(@deactivated='true')] | cfg:job[not(@deactivated='true')]"/>
            <xsl:apply-templates select="ancestor-or-self::cfg:*/cfg:data[not(@deactivated='true')]"/>
        </SITE>
    </xsl:template>

    <xsl:template match="cfg:data">
        <data>
            <xsl:copy-of select="@*"/>
            <xsl:apply-templates/>
        </data>
    </xsl:template>

    <xsl:template match="cfg:job">
        <xsl:variable name="this" select="."/>
        <job>
            <xsl:copy-of select="@*[local-name()!='wrapperMonitoring' and local-name()!='defaultIntermediary']"/>
            <xsl:copy-of select="ancestor-or-self::cfg:*/@wrapperMonitoring"/>
            <xsl:copy-of select="ancestor-or-self::cfg:*/@defaultIntermediary"/>
            <xsl:apply-templates/>
        </job>
    </xsl:template>

    <xsl:template match="*">
        <xsl:element name="{name()}">
            <xsl:copy-of select="@*"/>
            <xsl:apply-templates/>
        </xsl:element>
    </xsl:template>
    <xsl:template match="comment()">
        <xsl:comment><xsl:value-of select="."/></xsl:comment>
    </xsl:template>
</xsl:stylesheet>