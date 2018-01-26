    SET NOCOUNT ON;
    DECLARE @sql varchar(max)
    DECLARE @orderby varchar(max)
    if (@sortcols = '')
    SET @orderby = 'ORDER BY [Name]';
    else
    SET @orderby = @sortcols;
    
    SET @sql =  'WITH result AS 	(
         SELECT 
            [_Id], 
            [Name],cast( ''<properties>
                    <event name="row_single_click">
                        <action>
                            <main_context>current</main_context>
                            <datapanel type="current" tab="current">
                                <element id="d1"> 
                                <add_context>''+[Name]+''</add_context>  
                                </element>
                                <element id="d2">
                                <add_context>''+[Name]+''</add_context>  
                                </element>                                                              
                                <element id="d3">
                                <add_context>''+[Name]+''</add_context>  
                                </element>                                 
                                <element id="d4">
                                <add_context>''+[Name]+''</add_context>  
                                </element>                                 
                                <element id="d5">
                                <add_context>''+[Name]+''</add_context>  
                                </element>                                 
                                <element id="d6">
                                <add_context>''+[Name]+''</add_context>  
                                </element>                                 
                                <element id="d7">
                                <add_context>''+[Name]+''</add_context>  
                                </element>                                 
                                <element id="d8">
                                <add_context>''+[Name]+''</add_context>  
                                </element>                                 
                                <element id="d9">
                                <add_context>''+[Name]+''</add_context>  
                                </element>                                                                
                                <element id="d10"> 
                                <add_context>''+[Name]+''</add_context>  
                                </element>                                 
                            </datapanel>
                        </action>
                    </event>                                         
            </properties>'' as xml)  as [~~properties],           
            ROW_NUMBER() 
            OVER ('+@orderby+') AS rnum 
         FROM [dbo].[geo3])
      SELECT
         [_Id], [Name], [~~properties] FROM result 
         WHERE rnum BETWEEN ('+CAST(@firstrecord AS varchar(32)) +') AND ('+CAST(@firstrecord + @pagesize AS varchar(32))+') '
         +'ORDER by rnum';
   EXEC(@sql)