    SET @data = '
<datapanel>	
	<tab id="1" name="Веб-текст, sql скрипт">
		<element id="11" type="webtext" proc="webtext/3buttons.sql"/>
		<!--<element id="12" type="plugin"  proc="plugin/radarInfo.sql" plugin="radar">
			<proc id="010201" name="plugin/handleRadar.py" type="POSTPROCESS" />
		</element>-->
	</tab>
	<tab id="2" name="xform и sql скрипт">
		<element id="21" type="xforms" proc="xform/proc1.sql" template="Showcase_Template_SqlScript.xml">
			<proc id="proc1" name="xform/saveproc1.sql" type="SAVE" />
			<proc id="proc2" name="xform/submission1.sql" type="SUBMISSION" />
		</element>	
	</tab>
	<tab id="3" name="грид и sql скрипт">
		<element id="31" type="grid" proc="grid/gridbal.sql" />	
		<element id="32" type="grid"  proc="grid/citiesData.sql">
			<proc id="030201" name="grid/citiesMetadata.sql" type="METADATA" />
		</element>	
	</tab>
	<tab id="4" name="Настройки GridHeight, RowHeight">
		<element id="41" type="GRID"  subtype="EXT_LIVE_GRID" proc="extlivegrid_RowHeight1"/>
		<element id="42" type="GRID"  subtype="EXT_LIVE_GRID" proc="extlivegrid_RowHeight2"/>		
	</tab>
	<tab id="5" name="Выделение строки">
		<element id="2" type="GRID"  subtype="EXT_LIVE_GRID" proc="extlivegrid_bal"/>  
		<element id="5" type="webtext" proc="webtext_filter_and_add" hideOnLoad="true" />
		<element id="3" type="chart" proc="chart_bal_extgridlive" hideOnLoad="true" />
	</tab>
	<tab id="6" name="Выделение ячейки">
		<element id="62" type="GRID"  subtype="EXT_LIVE_GRID" proc="extlivegrid_bal2"/>  
		<element id="65" type="webtext" proc="webtext_filter_and_add" hideOnLoad="true" />
		<element id="63" type="chart" proc="chart_bal_extgridlive" hideOnLoad="true" />
	</tab>
</datapanel>    
    ';