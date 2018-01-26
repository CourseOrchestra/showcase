set    @data=CAST(
'<root>
<data>
<series name="Russia" data1="63.82" data2="17.18" data3="7.77"/>
<series name="Moscow" data1="47.22" data2="19.12" data3="20.21"/>
<series name="Piter" data1="58.77" data2="13.06" data3="15.22"/>
</data>
</root>' as xml)

set @settings='<properties width="800px" height="600px">                                    
                       <event name="single_click" linkId="1">
                        <action >
                            <main_context>current</main_context>                        
							<datapanel type="current" tab="current">
                                <element id="d1">
	                                <add_context>add</add_context>
                                </element>                                                             
                                <element id="d2">
	                                <add_context>add</add_context>
                                </element> 
                                <element id="d3">
	                                <add_context>add</add_context>
                                </element>                                                                 
                            </datapanel>
                        </action>
                       </event>                                                                     
                    </properties>'