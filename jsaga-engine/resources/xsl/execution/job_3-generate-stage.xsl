<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
            xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
            xmlns="http://schemas.ggf.org/jsdl/2005/11/jsdl"
            xmlns:jsdl="http://schemas.ggf.org/jsdl/2005/11/jsdl"
            xmlns:ext="http://www.in2p3.fr/jsdl-extension"
            xmlns:cfg="http://www.in2p3.fr/jsaga"
            exclude-result-prefixes="jsdl cfg">
    <!-- ###########################################################################
         # Generate staging
         ###########################################################################
    -->
    <xsl:output method="xml" indent="yes"/>
    <xsl:strip-space elements="*"/>

    <!-- resource -->
    <xsl:param name="resourceId"/><!-- required -->
    <xsl:param name="gridName"/><!-- required -->
    <xsl:param name="Intermediary"/><!-- optional -->
    <xsl:param name="StandaloneWorker" select="false"/><!-- optional -->
    <xsl:param name="Tag"/><!-- optional -->

    <!-- collection name -->
    <xsl:param name="collectionName"/><!-- required -->
    <xsl:variable name="jobName" select="/ext:Job/jsdl:JobDefinition/jsdl:JobDescription/jsdl:JobIdentification/jsdl:JobName/text()"/>

    <!-- configuration -->
    <xsl:variable name="config" select="document('var/jsaga-merged-config.xml')/cfg:effective-config"/>
    <xsl:variable name="resourceScheme" select="substring-before($resourceId, '://')"/>
    <xsl:variable name="jobService" select="$config/cfg:execution[@scheme=$resourceScheme]/cfg:jobService[@name=$gridName]"/>

    <!-- entry point -->
    <xsl:template match="/">
        <!-- check required parameters -->
        <xsl:if test="not($resourceId)"><xsl:message terminate="yes">Missing required parameter: resourceId</xsl:message></xsl:if>
        <xsl:if test="not($gridName)"><xsl:message terminate="yes">Missing required parameter: gridName</xsl:message></xsl:if>
        <xsl:if test="not($collectionName)"><xsl:message terminate="yes">Missing required parameter: collectionName</xsl:message></xsl:if>
        <!-- check required elements -->
        <xsl:if test="not($jobName)"><xsl:message terminate="yes">Missing required element: JobName</xsl:message></xsl:if>
        <xsl:if test="not($jobService)"><xsl:message terminate="yes">Missing required element: jobService[<xsl:value-of select="$gridName"/>]</xsl:message></xsl:if>
        <!-- check required attributes -->
        <xsl:if test="not($config/@localIntermediary)"><xsl:message terminate="yes">Missing required attribute: localIntermediary</xsl:message></xsl:if>
        <!-- process -->
        <xsl:apply-templates select="ext:Job"/>
    </xsl:template>
    <xsl:template match="ext:Job">
        <ext:Job>
            <xsl:comment> GENERATED BY job_3-generate-stage.xsl </xsl:comment>
            <xsl:apply-templates select="@* | * | text() | comment()"/>
        </ext:Job>
    </xsl:template>

    <!-- workaround to namespace problems... -->
    <xsl:template match="jsdl:JobDefinition">
        <JobDefinition>
            <xsl:apply-templates/>
        </JobDefinition>
    </xsl:template>

    <!-- specific templates here -->
    <xsl:template match="jsdl:DataStaging">
        <xsl:variable name="name" select="@name"/>
        <xsl:choose>
            <xsl:when test="
            $Tag
            and
            ancestor::jsdl:JobDescription/jsdl:Resources/jsdl:FileSystem[@name=$Tag]
            and
            preceding-sibling::jsdl:DataStaging[@name=$name and jsdl:FilesystemName/text()=$Tag]">
                <!-- file is already on worker: remove this alternative -->
                <xsl:comment> alternative <xsl:value-of select="@name"/> removed </xsl:comment>
            </xsl:when>
            <xsl:otherwise>
                <!-- file is not on worker: stage it -->
                <DataStaging>
                    <xsl:copy-of select="@*"/>
                    <xsl:apply-templates/>
                </DataStaging>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <xsl:template match="jsdl:Source[jsdl:URI] | jsdl:Target[jsdl:URI]">
        <xsl:variable name="scheme" select="substring-before(jsdl:URI/text(), '://')"/>
        <xsl:variable name="fragment" select="substring-after(jsdl:URI/text(), '#')"/>
        <xsl:variable name="type" select="local-name()"/>
        <xsl:choose>
            <xsl:when test="
            $scheme='tar' or $scheme='tgz' or $scheme='zip' or $scheme='gzip'
            or
            (
                (
                    $jobService/cfg:fileStaging/cfg:supportedProtocolScheme[text()=$scheme]
                    or
                    $jobService/cfg:fileStaging/cfg:workerProtocolScheme
                        [
                            text()=$scheme
                            and
                            (($type='Source' and @read='true') or ($type='Target' and @write='true'))
                        ]
                )
                and
                (
                    $gridName = $fragment
                    or
                    starts-with($gridName, concat($fragment,'-'))
                )
                and
                (
                    not($StandaloneWorker='true')
                    or
                    (
                        $Intermediary
                        and
                        starts-with(jsdl:URI/text(),$Intermediary)
                    )
                )
            )">
                <!-- file is accessible from worker: do nothing -->
                <xsl:element name="{local-name()}">
                    <xsl:apply-templates/>
                </xsl:element>
            </xsl:when>
            <xsl:otherwise>
                <!-- file is not accessible from worker: copy it to an intermediary node -->
                <xsl:variable name="selectedIntermediary">
                    <xsl:choose>
                        <xsl:when test="$StandaloneWorker='true'">
                            <xsl:if test="$Intermediary"><xsl:value-of select="$Intermediary"/></xsl:if>
                        </xsl:when>
                        <xsl:when test="../ext:Shared='true'">
                            <xsl:choose>
                                <xsl:when test="$jobService/cfg:fileStaging/@defaultIntermediary"><xsl:value-of select="$jobService/cfg:fileStaging/@defaultIntermediary"/></xsl:when>
                                <xsl:when test="$Intermediary"><xsl:value-of select="$Intermediary"/></xsl:when>
                            </xsl:choose>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:choose>
                                <xsl:when test="$Intermediary"><xsl:value-of select="$Intermediary"/></xsl:when>
                                <xsl:when test="$jobService/cfg:fileStaging/@defaultIntermediary"><xsl:value-of select="$jobService/cfg:fileStaging/@defaultIntermediary"/></xsl:when>
                            </xsl:choose>
                        </xsl:otherwise>
                    </xsl:choose>
                </xsl:variable>
                <xsl:if test="$selectedIntermediary=''">
                    <xsl:message terminate="yes">
                        <xsl:value-of select="$jobName"/>: no intermediary node to transport file <xsl:value-of select="jsdl:URI/text()"/>
                    </xsl:message>
                </xsl:if>
                <xsl:variable name="intermediaryFile">
                    <xsl:choose>
                        <xsl:when test="contains($selectedIntermediary,'?')"><xsl:value-of select="substring-before($selectedIntermediary,'?')"/></xsl:when>
                        <xsl:otherwise><xsl:value-of select="$selectedIntermediary"/></xsl:otherwise>
                    </xsl:choose>
                    <xsl:text>/</xsl:text>
                    <xsl:call-template name="INTERMEDIARY_DIR"/>
                    <xsl:text>/</xsl:text>
                    <xsl:call-template name="FILENAME"/>
                    <xsl:if test="contains($selectedIntermediary,'?')">?<xsl:value-of select="substring-after($selectedIntermediary,'?')"/></xsl:if>
                    <xsl:if test="$gridName">#<xsl:value-of select="$gridName"/></xsl:if>
                </xsl:variable>
                <xsl:choose>
                    <xsl:when test="
                    substring-before(jsdl:URI/text(),'://')='file'
                    or
                    (
                        $config/cfg:protocol[@scheme=$scheme]/@thirdparty='true'
                        and
                        substring-before($selectedIntermediary,'://')=$scheme
                        and
                        (
                            $gridName = $fragment
                            or
                            starts-with($gridName, concat($fragment,'-'))
                        )
                    )">
                        <!-- file can be transfered directly to server intermediary node -->
                        <xsl:element name="{local-name()}">
                            <URI><xsl:value-of select="$intermediaryFile"/></URI>
                            <xsl:apply-templates select="*[local-name()!='URI']"/>
                        </xsl:element>
                        <ext:Step uri="{jsdl:URI/text()}">
                            <ext:Step uri="{$intermediaryFile}"/>
                        </ext:Step>
                    </xsl:when>
                    <xsl:otherwise>
                        <!-- file must be downloaded locally before being uploaded to intermediary node -->
                        <xsl:variable name="localFile">
                            <xsl:text>file://</xsl:text>
                            <xsl:value-of select="$config/@localIntermediary"/>
                            <xsl:text>/</xsl:text>
                            <xsl:call-template name="INTERMEDIARY_DIR"/>
                            <xsl:text>/</xsl:text>
                            <xsl:call-template name="FILENAME"/>
                        </xsl:variable>
                        <xsl:element name="{local-name()}">
                            <URI><xsl:value-of select="$intermediaryFile"/></URI>
                            <xsl:apply-templates select="*[local-name()!='URI']"/>
                        </xsl:element>
                        <ext:Step uri="{jsdl:URI/text()}">
                            <ext:Step uri="{$localFile}">
                                <ext:Step uri="{$intermediaryFile}"/>
                            </ext:Step>
                        </ext:Step>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <xsl:template name="INTERMEDIARY_DIR">
        <xsl:value-of select="$collectionName"/>
        <xsl:choose>
            <xsl:when test="local-name()='Source'">.in</xsl:when>
            <xsl:when test="local-name()='Target'">.out/<xsl:value-of select="$jobName"/></xsl:when>
        </xsl:choose>
    </xsl:template>

    <xsl:template name="FILENAME">
        <xsl:param name="path" select="jsdl:URI/text()"/>
        <xsl:choose>
            <xsl:when test="contains($path,'/')">
                <xsl:call-template name="FILENAME">
                    <xsl:with-param name="path" select="substring-after($path,'/')"/>
                </xsl:call-template>
            </xsl:when>
            <xsl:otherwise>
                <xsl:choose>
                    <xsl:when test="contains($path,'?')"><xsl:value-of select="substring-before($path,'?')"/></xsl:when>
                    <xsl:when test="contains($path,'#')"><xsl:value-of select="substring-before($path,'#')"/></xsl:when>
                    <xsl:otherwise><xsl:value-of select="$path"/></xsl:otherwise>
                </xsl:choose>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <!-- default template rules -->
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