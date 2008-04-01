<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:jsdl="http://schemas.ggf.org/jsdl/2005/11/jsdl"
                xmlns:posix="http://schemas.ggf.org/jsdl/2005/11/jsdl-posix"
                xmlns:spmd="http://schemas.ogf.org/jsdl/2007/02/jsdl-spmd"
                xmlns:ext="http://www.in2p3.fr/jsdl-extension">
    <xsl:output method="xml"/>
    <xsl:param name="ShellPath"/>

    <!-- entry point (MUST BE RELATIVE) -->
    <xsl:template match="jsdl:JobDefinition">
        <xsl:apply-templates select="jsdl:JobDescription"/>
    </xsl:template>

    <xsl:template match="jsdl:JobDescription">
		<job><xsl:text/>
        <!-- executable and arguments -->
        <xsl:choose>
            <xsl:when test="$ShellPath">
<executable><xsl:value-of select="$ShellPath"/></executable>
<argument>-c </argument>
<argument><xsl:value-of select="jsdl:Application/posix:POSIXApplication/posix:Executable/text()"/></argument>
                <xsl:if test="jsdl:Application/posix:POSIXApplication/posix:Argument/text()">
                    <xsl:for-each select="jsdl:Application/posix:POSIXApplication/posix:Argument/text()">
                        <argument><xsl:value-of select="."/></argument>
                    </xsl:for-each>
                </xsl:if>
            </xsl:when>
            <xsl:otherwise>
<executable><xsl:value-of select="jsdl:Application/posix:POSIXApplication/posix:Executable/text()"/></executable><xsl:text/>
                <xsl:if test="jsdl:Application/posix:POSIXApplication/posix:Argument/text()">
				<xsl:for-each select="jsdl:Application/posix:POSIXApplication/posix:Argument/text()">
                    <argument><xsl:value-of select="."/></argument>
                </xsl:for-each>
                </xsl:if>
            </xsl:otherwise>
        </xsl:choose>

        <!-- other -->
        <xsl:for-each select="jsdl:Application/posix:POSIXApplication/posix:Output/text()">
<stdout><xsl:text/><xsl:value-of select="."/></stdout><xsl:text/>
        </xsl:for-each>
        <xsl:for-each select="jsdl:Application/posix:POSIXApplication/posix:Error/text()">
<stderr><xsl:value-of select="."/></stderr><xsl:text/>
        </xsl:for-each>
        <xsl:for-each select="jsdl:Application/posix:POSIXApplication/posix:Input/text()">
<stdin><xsl:value-of select="."/></stdin><xsl:text/>
        </xsl:for-each>
      	<xsl:if test="jsdl:Application/posix:POSIXApplication/posix:Environment">
       		<xsl:for-each
                select="jsdl:Application/posix:POSIXApplication/posix:Environment">
                <environment>
                	<name><xsl:value-of select="@name"/></name>
                	<value><xsl:value-of select="text()"/></value>
                </environment>
            </xsl:for-each><xsl:text/>
        </xsl:if>
        <!-- TODO : depends on configuration -->
        <xsl:for-each select="jsdl:Resources/jsdl:FileSystem[@name='WorkingDirectory']/jsdl:MountPoint/text()">
<directory><xsl:value-of select="."/></directory><xsl:text/>
        </xsl:for-each>
        <!--  Memory in Mb (must be an integer) -->
       <xsl:for-each select="jsdl:Resources/jsdl:TotalPhysicalMemory/jsdl:UpperBoundedRange/text()">
<maxMemory><xsl:value-of select="."/></maxMemory><xsl:text/>
        </xsl:for-each>
         <!--  CPU time request in minutes (must be an integer) -->
        <xsl:for-each select="jsdl:Resources/jsdl:TotalCPUTime/jsdl:UpperBoundedRange/text()">
<maxCpuTime><xsl:value-of select="ceiling(. div 60)"/></maxCpuTime><xsl:text/>
        </xsl:for-each>
         <xsl:for-each select="jsdl:Application/spmd:SPMDApplication/spmd:SPMDVariation/text()[not(. = 'None')]">
        	<xsl:choose>
           		<xsl:when test=". = 'MPI'">
<jobType>mpi</jobType><xsl:text/>
           		</xsl:when>
            	<xsl:otherwise>
	            	<xsl:message terminate="yes">Unsupported SPMDVariation : <xsl:value-of select="."/></xsl:message>
            	</xsl:otherwise>
        	</xsl:choose>
        </xsl:for-each>
        <xsl:for-each select="jsdl:Application/spmd:SPMDApplication/spmd:NumberOfProcesses/text()">
<count><xsl:value-of select="."/></count><xsl:text/>
        </xsl:for-each>
        <xsl:for-each select="jsdl:Application/spmd:SPMDApplication">
	        <xsl:if test="spmd:ProcessesPerHost/text()">
<hostCount><xsl:value-of select="ceiling(spmd:NumberOfProcesses/text() div spmd:ProcessesPerHost/text())"/></hostCount><xsl:text/>
        	</xsl:if>
        </xsl:for-each>
        <xsl:for-each select="jsdl:JobIdentification/jsdl:JobAnnotation/text()">
<queue><xsl:value-of select="."/></queue><xsl:text/>
        </xsl:for-each>
    </job></xsl:template>
</xsl:stylesheet>