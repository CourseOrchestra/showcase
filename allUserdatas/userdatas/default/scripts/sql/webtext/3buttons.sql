declare @addc varchar(MAX)
if @add_context = ''
set @add_context = NULL
SET @addc = coalesce(@add_context, '')

set    @data=CAST(
'<div><button type="button" onclick="gwtWebTextFunc(''${elementId}'',''1'');">Добавить</button>
<button type="button" onclick="gwtWebTextFunc(''${elementId}'',''2'');">Редактировать</button>
<button type="button" onclick="gwtWebTextFunc(''${elementId}'',''3'');">Удалить</button>
</div>' as xml)

set @settings='<properties>    
                                
                       <event name="single_click" linkId="1">
                        <action show_in="MODAL_WINDOW">
                            <main_context>current</main_context>                        
							<modalwindow caption="Тестовая карточка - новая запись" width="500" height="150" show_close_bottom_button="true"/>
                            <datapanel type="current" tab="current">
                                <element id="84">
	                                <add_context>add</add_context>
                                </element>                                                             
                                
                            </datapanel>
                        </action>
                       </event>  
                          
                       <event name="single_click" linkId="2">							                      
                        <action show_in="MODAL_WINDOW">
                        <main_context>current</main_context>                        
                        <modalwindow caption="Тестовая карточка - редактирование" width="400" height="150"/> 
                            <datapanel type="current" tab="current">
                                <element id="84">
	                                <add_context>'+@addc+'</add_context>
                                </element>                                                             
                                
                            </datapanel>
                        </action>
                       </event>  
                        
                       <event name="single_click" linkId="3">
                        <action>
                            <main_context>current</main_context>                        
                            <datapanel type="current" tab="current">
                                <element id="83">
	                                <add_context>'+@addc+'</add_context>
                                </element>                                                             
                                
                            </datapanel>
                        </action>
                       </event>   
                                                                  
                    </properties>'
	                                
	                                
	                                
SET @return = 555
SET @error_mes = 'Грид22222222 успешно построен'



	                                