   SET NOCOUNT ON;

   SET @data=(SELECT TOP 1 [data] FROM [dbo].[XFormsTest]);
   
   SET @settings=
	'<properties>
                        <action>
                            <main_context>current</main_context>
                            <datapanel type="current" tab="current">
                                <element id="62">
	                                <add_context>xforms default action</add_context>
                                </element>                                                             
                                
                            </datapanel>
                        </action>
						<event name="single_click" linkId="1">
                        <action>
                            <main_context>current</main_context>
                            <datapanel type="current" tab="current">
                                <element id="62">
	                                <add_context>save click on xforms (with filtering)</add_context>
                                </element>                                                             
                                
                            </datapanel>
                        </action>
                    </event>
						<event name="single_click" linkId="2">
                        <action>
                            <main_context>current</main_context>
                            <datapanel type="current" tab="current">
                                <element id="62">
	                                <add_context>filter click on xforms</add_context>
                                </element>                                                             
                                
                            </datapanel>
                        </action>
                    </event>                    
</properties>';