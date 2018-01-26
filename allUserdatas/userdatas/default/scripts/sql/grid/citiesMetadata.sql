SET NOCOUNT ON;

DECLARE @gridsettings_str varchar(max)
DECLARE @cities_count int
SELECT @cities_count = COUNT(*) FROM [dbo].[geo3]
set @gridsettings_str='<gridsettings>
        <labels>
            <header><h3 class="testStyle">Новый способ загрузки - отдельные процедуры для METADATA и DATA</h3></header>
        </labels>
							<action>
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
                                <element id="d4">
                                <add_context>add</add_context>  
                                </element>                                 
                                <element id="d5">
                                <add_context>add</add_context>  
                                </element>                                 
                                <element id="d6">
                                <add_context>add</add_context>  
                                </element>                                 
                                <element id="d7">
                                <add_context>add</add_context>  
                                </element>                                 
                                <element id="d8">
                                <add_context>add</add_context>  
                                </element>                                 
                                <element id="d9">
                                <add_context>add</add_context>  
                                </element>                                                                
                                <element id="d10"> 
                                <add_context>add</add_context>  
                                </element>                                                                                                   
                            </datapanel>
                        </action>
<properties flip="false" pagesize="15" autoSelectRecordId="36"  autoSelectRelativeRecord="false" totalCount="'+
CAST(@cities_count as varchar(max))+'"/></gridsettings>' 
set  @settings=CAST(@gridsettings_str as xml)