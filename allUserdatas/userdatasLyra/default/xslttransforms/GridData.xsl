<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	version="2.0">
	<xsl:output method="xml" encoding="UTF-8" />
	<xsl:template match="/">
		<xsl:processing-instruction name="mso-application">
			progid="Excel.Sheet"
		</xsl:processing-instruction>
		<Workbook xmlns="urn:schemas-microsoft-com:office:spreadsheet"
			xmlns:o="urn:schemas-microsoft-com:office:office" xmlns:x="urn:schemas-microsoft-com:office:excel"
			xmlns:ss="urn:schemas-microsoft-com:office:spreadsheet" xmlns:html="http://www.w3.org/TR/REC-html40">
			<Styles>
				<Style ss:ID="Default" ss:Name="Normal">
					<Alignment ss:Vertical="Bottom" />
					<Borders />
					<Font ss:FontName="Calibri" x:CharSet="204" x:Family="Swiss"
						ss:Size="9" ss:Color="#000000" />
					<Interior />
					<NumberFormat />
					<Protection />
				</Style>
				<Style ss:ID="s1">
					<Borders>
						<Border ss:Position="Bottom" ss:LineStyle="Continuous"
							ss:Weight="1" />
						<Border ss:Position="Left" ss:LineStyle="Continuous"
							ss:Weight="1" />
						<Border ss:Position="Right" ss:LineStyle="Continuous"
							ss:Weight="1" />
						<Border ss:Position="Top" ss:LineStyle="Continuous"
							ss:Weight="1" />
					</Borders>
					<Interior ss:Color="#BFBFBF" ss:Pattern="Solid" />
				</Style>
				<Style ss:ID="s2">
					<Borders>
						<Border ss:Position="Bottom" ss:LineStyle="Continuous"
							ss:Weight="1" />
						<Border ss:Position="Left" ss:LineStyle="Continuous"
							ss:Weight="1" />
						<Border ss:Position="Right" ss:LineStyle="Continuous"
							ss:Weight="1" />
						<Border ss:Position="Top" ss:LineStyle="Continuous"
							ss:Weight="1" />
					</Borders>
				</Style>
			</Styles>

			<Worksheet ss:Name="Данные">
				<Names>
					<NamedRange ss:Name="_FilterDatabase" ss:Hidden="1"
						ss:RefersTo="{concat('=Данные!R1C1:R1C',count(Table/Row[1]/Cell))}">
					</NamedRange>
				</Names>
				<Table ss:ExpandedColumnCount="{count(Table/Row[1]/Cell)}"
					ss:ExpandedRowCount="{count(Table/Row)}" x:FullColumns="1"
					x:FullRows="1" ss:DefaultRowHeight="15" ss:DefaultColumnWidth="100">
					<xsl:for-each select="Table/Column">
						<Column ss:AutoFitWidth="0" ss:Width="{@width}" />
					</xsl:for-each>
					<xsl:for-each select="Table/Row">
						<xsl:variable name="row" select="position()" />
						<Row ss:AutoFitHeight="0">
							<xsl:for-each select="Cell">
								<xsl:choose>
									<xsl:when test="$row=1">
										<Cell ss:StyleID="s1">
											<xsl:if test="count(@href)>0">
												<xsl:attribute name="ss:HRef" select="@href" />
											</xsl:if>
											<Data ss:Type="{@type}">
												<xsl:value-of select="text()" />
											</Data>
											<NamedCell ss:Name="_FilterDatabase" />
										</Cell>
									</xsl:when>
									<xsl:otherwise>
										<Cell ss:StyleID="s2">
											<xsl:if test="not(text()='')">
												<Data ss:Type="{@type}">
													<xsl:value-of select="text()" />
												</Data>
											</xsl:if>
										</Cell>
									</xsl:otherwise>
								</xsl:choose>

							</xsl:for-each>
						</Row>
					</xsl:for-each>

				</Table>
				<WorksheetOptions xmlns="urn:schemas-microsoft-com:office:excel">
					<PageSetup>
						<Header x:Margin="0.3" />
						<Footer x:Margin="0.3" />
						<PageMargins x:Bottom="0.75" x:Left="0.7" x:Right="0.7"
							x:Top="0.75" />
					</PageSetup>
					<Unsynced />
					<Selected />
					<ProtectObjects>False</ProtectObjects>
					<ProtectScenarios>False</ProtectScenarios>
				</WorksheetOptions>
				<AutoFilter x:Range="{concat('R1C1:R1C',count(Table/Row[1]/Cell))}"
					xmlns="urn:schemas-microsoft-com:office:excel">
				</AutoFilter>
			</Worksheet>
		</Workbook>
	</xsl:template>
</xsl:stylesheet>
