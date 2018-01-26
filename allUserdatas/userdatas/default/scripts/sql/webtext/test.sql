set	@data=CAST('<div>
<a onclick="gwtWebTextFunc(''7'', ''1'')" class="linkStyle" >'+@add_context+'</a></div>' as xml)
set @settings='<properties>
                       <action>
                            <main_context>current</main_context>                                               
                            <datapanel type="current" tab="current">
                                <element id="8">
	                                <add_context>hide</add_context>
                                </element>   
                            </datapanel>
                        </action>
									
						<event name="single_click" linkId="1">
                        <action>
                            <main_context>current</main_context>                                                
                            <datapanel type="current" tab="current">
                                <element id="8">
	                                <add_context>'+ @add_context+ '</add_context>
                                </element>   
                            </datapanel>
                        </action>
                       </event>               
					</properties>'