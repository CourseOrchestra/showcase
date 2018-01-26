<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	version="2.0" xmlns:xs="http://www.w3.org/2001/XMLSchema">
	<xsl:strip-space elements="*" />
	<xsl:output indent="yes" method="html" />

	<xsl:template match="/">		
			<xsl:for-each select="node()">
				<xsl:apply-templates/>			  
			</xsl:for-each>
	</xsl:template>
	
	<xsl:template match="div">
	</xsl:template>
	
	<xsl:template match="node()">
				<xsl:call-template name="begin" />
				<xsl:for-each select="node()">
					<xsl:call-template name="begin" />
						<xsl:for-each select="node()">
							<xsl:call-template name="begin" />
							<xsl:call-template name="end" />
						</xsl:for-each>
					<xsl:call-template name="end" />
				</xsl:for-each>
				<xsl:call-template name="end" />	
	</xsl:template>

	<xsl:template name="begin">
		<xsl:if test="name(current()) != ''">
			&lt;<xsl:copy-of select="name(current())" />
			<xsl:for-each select="attribute::*">
				<xsl:copy-of select="concat(name(current()),'=&quot;',current(),'&quot;')" />
			</xsl:for-each>&gt;
			<xsl:copy-of select="current()/text()" />
		</xsl:if>
	</xsl:template>

	<xsl:template name="end">
		<xsl:if test="name(current()) != ''">
			&lt;/<xsl:copy-of select="name(current())" />&gt;
			<br/>
		</xsl:if>
	</xsl:template>

</xsl:stylesheet>