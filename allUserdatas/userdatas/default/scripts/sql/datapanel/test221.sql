    SET @data = '
<datapanel>	
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