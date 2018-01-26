DECLARE

BEGIN

IF add_context IS NULL OR add_context ='' THEN
  add_context := 'Здесь мог бы быть...';
END IF; 

data := XMLPARSE (CONTENT '<div><h1>'||add_context||'</h1>
<a onclick="gwtWebTextFunc(''77'', ''1'')" class="linkStyle" >Обнови меня скорей!</a></div>');

settings := '<properties>									
						<event name="single_click" linkId="1">
                        <action>
                            <main_context>current</main_context>
                            <datapanel type="current" tab="current">
                                <element id="77">
	                                <add_context>SelfRefresh Context</add_context>
                                </element>                                                             
                                
                            </datapanel>
                        </action>
                       </event>     
					</properties>';

END;