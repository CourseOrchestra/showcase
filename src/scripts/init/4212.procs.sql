GO
SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
GO

CREATE PROCEDURE [dbo].[pluginRadarInfo]
    @main_context varchar(512) ='',
    @add_context varchar(512) ='',
    @filterinfo xml='',
    @session_context xml ='',
    @element_id varchar(512) ='',    
	@params xml='',
    @data xml output,
    @settings xml output
AS
BEGIN
    -- SET NOCOUNT ON added to prevent extra result sets from
    -- interfering with SELECT statements.
    SET NOCOUNT ON;


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

END
GO


GO
SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
GO


CREATE PROCEDURE [dbo].[jstreegrid_addrecord1]
	@main_context varchar(512),
	@add_context varchar(512),
	@filterinfo xml,
	@session_context xml,
	@element_Id varchar(512),

	@addrecorddata xml,

  @gridaddrecordresult xml output,

	@error_mes varchar(2048) output	
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

--INSERT INTO Debug (context) VALUES (@addrecorddata)


DECLARE @currentRecordId varchar(2048)
SET @currentRecordId=(select @addrecorddata.value('(/addrecorddata/currentRecordId)[1]','varchar(MAX)'))


DECLARE @name varchar(2048)
DECLARE @Id INTEGER
DECLARE @ParentId uniqueidentifier

SET @name = (SELECT Name FROM geo6 where geo6_Id =  @currentRecordId)
IF @name IS NOT NULL
BEGIN
	SET @Id = (SELECT Id FROM geo6 where geo6_Id =  @currentRecordId)
	SET @name = @name+'_New'
	INSERT INTO geo6 (Name, Id) VALUES (@name, @Id)
END

SET @name = (SELECT Name FROM geo5 where geo5_Id =  @currentRecordId)
IF @name IS NOT NULL
BEGIN
	SET @Id = (SELECT Id FROM geo5 where geo5_Id =  @currentRecordId)+10000
	SET @name = @name+'_New'
	SET @ParentId = (SELECT FJField_9 FROM geo5 where geo5_Id =  @currentRecordId)
	INSERT INTO geo5 (Name, Id, FJField_9) VALUES (@name, @Id, @ParentId)
END

SET @name = (SELECT Name FROM geo3 where geo3_Id =  @currentRecordId)
IF @name IS NOT NULL
BEGIN
	SET @Id = (SELECT Id FROM geo3 where geo3_Id =  @currentRecordId)+10000
	SET @name = @name+'_New'
	SET @ParentId = (SELECT FJField_9 FROM geo3 where geo3_Id =  @currentRecordId)
	INSERT INTO geo3 (Name, Id, FJField_9) VALUES (@name, @Id, @ParentId)
END

--

--geo6_Id
--geo5_Id
--geo3_Id


Declare @gridaddrecordresult_str as varchar(max)
set @gridaddrecordresult_str='
<gridaddrecordresult>
</gridaddrecordresult>'

set    @gridaddrecordresult=CAST(@gridaddrecordresult_str as xml)

--set    @gridaddrecordresult=NULL


	

--	SET @error_mes=N'Ошибка при добавлении записи'
--	RETURN 30


  set @error_mes = N'Record successfully added'
  RETURN 555;


	SET @error_mes=''
	RETURN 0


	
	
END
GO

GO
SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
GO



CREATE PROCEDURE [dbo].[jstreegrid_save1]
	@main_context varchar(512),
	@add_context varchar(512),
	@filterinfo xml,
	@session_context xml,
	@element_Id varchar(512),

	@savedata xml,

  @gridsaveresult xml output,

	@error_mes varchar(2048) output	
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

--INSERT INTO Debug (context) VALUES (@savedata)
--INSERT INTO Debug (string) VALUES (@savedata)



Declare @gridsaveresult_str as varchar(max)
set @gridsaveresult_str='
<gridsaveresult>
	<properties refreshAfterSave="true" />
</gridsaveresult>'

set    @gridsaveresult=CAST(@gridsaveresult_str as xml)


--set    @gridsaveresult=NULL


--	SET @error_mes=N'Ошибка при сохранении данных'
--	RETURN 10


  set @error_mes = N'Data saved successfully'
  RETURN 555;


	SET @error_mes=''
	RETURN 0

	
END
GO


GO
SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
GO

CREATE PROCEDURE [dbo].[pluginToolBarInfo]
    @main_context varchar(512) ='',
    @add_context varchar(512) ='',
    @filterinfo xml='',
    @session_context xml ='',
    @element_id varchar(512) ='',    
	@params xml='',
    @data xml output,
    @settings xml output
AS
BEGIN
    -- SET NOCOUNT ON added to prevent extra result sets from
    -- interfering with SELECT statements.
    SET NOCOUNT ON;


set    @data=CAST(
'
<data>

	<selected withoutQuotes="true">0</selected>

	<tabs>
		<groups>
			<title>Clipboard</title>
			<tools>
				<size>large</size>
				<text>Paste</text>
				<iconAlign>top</iconAlign>
				<name>paste</name>
				<type>splitbutton</type>
				<menuItems>
					<iconCls>icon-paste</iconCls>
					<text>Paste</text>
					<name>paste</name>
				</menuItems>
				<menuItems>
					<iconCls>icon-paste</iconCls>
					<text>Paste Special...</text>
					<name>paste-special</name>
				</menuItems>
				<iconCls>icon-paste-large</iconCls>
			</tools>
			<tools>
				<type>toolbar</type>
				<tools>
					<iconCls>icon-cut</iconCls>
					<text>Cut</text>
					<name>cut</name>
				</tools>
				<tools>
					<iconCls>icon-copy</iconCls>
					<text>Copy</text>
					<name>copy</name>
				</tools>
				<tools>
					<iconCls>icon-format</iconCls>
					<text>Format</text>
					<name>format</name>
				</tools>
				<dir>v</dir>
			</tools>
		</groups>
		<groups>
			<title>Font</title>
			<tools>
				<type>toolbar</type>
				<tools>



					<editable withoutQuotes="true">false</editable>
					<panelHeight>auto</panelHeight>
					<width>116</width>
					<data>
						<text>Arial</text>
						<selected>True</selected>
					</data>
					<data>
						<text>Century</text>
					</data>
					<data>
						<text>Tahoma</text>
					</data>
					<valueField>text</valueField>
					<textField>text</textField>
					<type>combobox</type>


          <onSelect withoutQuotes="true">function(record){gwtPluginFunc(''parentId'',record.text);}</onSelect>


<!--
          <onSelect withoutQuotes="true">function(record){gwtPluginFunc(''dpe_Calendar__E40F6599F809__0301__plugin'',record.text);}</onSelect>
-->


				</tools>
				<tools>
					<editable withoutQuotes="true">false</editable>
					<panelHeight>auto</panelHeight>
					<width>50</width>
					<data>
						<text>8</text>
					</data>
					<data>
						<text>12</text>
						<selected>True</selected>
					</data>
					<data>
						<text>14</text>
					</data>
					<valueField>text</valueField>
					<textField>text</textField>
					<type>combobox</type>




				</tools>
			</tools>
			<tools>
				<type>toolbar</type>
				<tools>
					<iconCls>icon-increase-font</iconCls>
					<name>increase-font</name>
				</tools>
				<tools>
					<iconCls>icon-decrease-font</iconCls>
					<name>decrease-font</name>
				</tools>
				<style>
					<marginLeft>5px</marginLeft>
				</style>
			</tools>
			<tools>
				<type>toolbar</type>
				<tools>
					<iconCls>icon-bold</iconCls>
					<toggle>True</toggle>
					<name>bold</name>
				</tools>
				<tools>
					<iconCls>icon-italic</iconCls>
					<toggle>True</toggle>
					<name>italic</name>
				</tools>
				<tools>
					<iconCls>icon-underline</iconCls>
					<toggle>True</toggle>
					<name>underline</name>
				</tools>
				<tools>
					<iconCls>icon-strikethrough</iconCls>
					<toggle>True</toggle>
					<name>strikethrough</name>
				</tools>
				<tools>
					<iconCls>icon-superscript</iconCls>
					<toggle>True</toggle>
					<name>superscript</name>
				</tools>
				<tools>
					<iconCls>icon-subscript</iconCls>
					<toggle>True</toggle>
					<name>subscript</name>
				</tools>
				<style>
					<marginTop>2px</marginTop>
					<clear>both</clear>
				</style>
			</tools>
			<tools>
				<type>toolbar</type>
				<tools>
					<iconCls>icon-case-font</iconCls>
					<name>case-font</name>
				</tools>
				<tools>
					<iconCls>icon-grow-font</iconCls>
					<name>grow-font</name>
				</tools>
				<tools>
					<iconCls>icon-shrink-font</iconCls>
					<name>shrink-font</name>
				</tools>
				<style>
					<clear>both</clear>
				</style>
			</tools>
		</groups>
		<groups>
			<dir>v</dir>
			<tools>
				<type>toolbar</type>
				<tools>
					<iconCls>icon-align-left</iconCls>
					<toggle>True</toggle>
					<name>slign-left</name>
					<group>p1</group>
				</tools>
				<tools>
					<iconCls>icon-align-center</iconCls>
					<toggle>True</toggle>
					<name>align-center</name>
					<group>p1</group>
				</tools>
				<tools>
					<iconCls>icon-align-right</iconCls>
					<toggle>True</toggle>
					<name>align-right</name>
					<group>p1</group>
				</tools>
				<tools>
					<iconCls>icon-align-justify</iconCls>
					<toggle>True</toggle>
					<name>align-justify</name>
					<group>p1</group>
				</tools>
			</tools>
			<tools>
				<type>toolbar</type>
				<tools>
					<iconCls>icon-bullets</iconCls>
					<name>bullets</name>
				</tools>
				<tools>
					<iconCls>icon-numbers</iconCls>
					<name>numbers</name>
				</tools>
				<style>
					<marginTop>2px</marginTop>
				</style>
			</tools>
			<title>Paragraph</title>
		</groups>

		<groups>
			<dir>v</dir>
			<tools>
				<iconCls>icon-find</iconCls>
				<text>Find</text>
				<name>find</name>
				<type>splitbutton</type>
				<menuItems>
					<iconCls>icon-find</iconCls>
					<text>Find</text>
					<name>find</name>
				</menuItems>
				<menuItems>
					<iconCls>icon-go</iconCls>
					<text>Go to...</text>
					<name>go</name>
				</menuItems>
			</tools>
			<tools>
				<iconCls>icon-replace</iconCls>
				<text>Replace</text>
				<name>replace</name>
			</tools>
			<tools>
				<iconCls>icon-select</iconCls>
				<text>Select</text>
				<name>select</name>
				<type>menubutton</type>
				<menuItems>
					<iconCls>icon-selectall</iconCls>
					<text>Select All</text>
					<name>selectall</name>
				</menuItems>
				<menuItems>
					<iconCls>icon-select</iconCls>
					<text>Select Objects</text>
					<name>select-object</name>
				</menuItems>
			</tools>
			<title>Editing</title>
		</groups>
		<title>Home</title>
	</tabs>
	<tabs>
		<groups>
			<title>Table</title>
			<tools toJsonArray="true">
				<size>large</size>
				<text>Table</text>
				<iconAlign>top</iconAlign>
				<name>table</name>
				<type>menubutton</type>
				<iconCls>icon-table-large</iconCls>
			</tools>




		</groups>

		<groups>
			<title>Illustrations</title>
			<tools>
				<size>large</size>
				<text>Picture</text>
				<iconAlign>top</iconAlign>
				<name>picture</name>
				<iconCls>icon-picture-large</iconCls>
			</tools>
			<tools>
				<size>large</size>
				<text>Clip Art</text>
				<iconAlign>top</iconAlign>
				<name>clipart</name>
				<iconCls>icon-clipart-large</iconCls>
			</tools>
			<tools>
				<size>large</size>
				<text>Shapes</text>
				<iconAlign>top</iconAlign>
				<name>shapes</name>
				<type>menubutton</type>
				<iconCls>icon-shapes-large</iconCls>
			</tools>
			<tools>
				<size>large</size>
				<text>SmartArt</text>
				<iconAlign>top</iconAlign>
				<name>smartart</name>
				<iconCls>icon-smartart-large</iconCls>
			</tools>
			<tools>
				<size>large</size>
				<text>Chart</text>
				<iconAlign>top</iconAlign>
				<name>chart</name>
				<iconCls>icon-chart-large</iconCls>
			</tools>
		</groups>


		<title>Insert</title>
	</tabs>

</data>

' as xml)

set @settings='<properties width="610px" height="170px">                                    


                       <event name="single_click" linkId="cut">
                        <action >
                            <main_context>current</main_context>                        
							              <datapanel type="current" tab="current">
                                <element id="053_">
	                                <add_context>cut</add_context>
                                </element>                                                             
                            </datapanel>
                        </action>
                       </event>                                                                     



                       <event name="single_click" linkId="picture">
                        <action >
                            <main_context>current</main_context>                        
							              <datapanel type="current" tab="current">
                                <element id="053_">
	                                <add_context>picture</add_context>
                                </element>                                                             
                            </datapanel>
                        </action>
                       </event>                                                                     


                       <event name="single_click" linkId="align-center">
                        <action >
                            <main_context>current</main_context>                        
							              <datapanel type="current" tab="current">
                                <element id="053_">
	                                <add_context>align-center</add_context>
                                </element>                                                             
                            </datapanel>
                        </action>
                       </event>                                                                     


                       <event name="single_click" linkId="Tahoma">
                        <action >
                            <main_context>current</main_context>                        
							              <datapanel type="current" tab="current">
                                <element id="053_">
	                                <add_context>Tahoma</add_context>
                                </element>                                                             
                            </datapanel>
                        </action>
                       </event>                                                                     






                    </properties>'



--RETURN 555;


END
GO




GO
SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
GO

CREATE PROCEDURE [dbo].[webtext_calendar]
@main_context varchar(512) ='',
    @add_context varchar(512) ='',
    @filterinfo xml='',
    @session_context xml ='',
    @element_id varchar(512) ='',    
    @webtextdata xml output,
    @webtextsettings xml output
AS
BEGIN
    -- SET NOCOUNT ON added to prevent extra result sets from
    -- interfering with SELECT statements.
    SET NOCOUNT ON;

declare @addc varchar(MAX)
if @add_context = ''
set @add_context = NULL
SET @addc = coalesce(@add_context, '')

set    @webtextdata=CAST(
'<div>
<button type="button" onclick="gwtWebTextFunc(''${elementId}'',''21'');">keep_user_settings=true</button>
<button type="button" onclick="gwtWebTextFunc(''${elementId}'',''22'');">keep_user_settings=false</button>
</div>' as xml)

set @webtextsettings='<properties>    


                       <event name="single_click" linkId="21">
                        <action>
                            <main_context>current</main_context>                        
                            <datapanel type="current" tab="current">

                                <element id="0101"  keep_user_settings="true">
	                                <add_context>add</add_context>
                                </element>                                                             
                                
                            </datapanel>
                        </action>
                       </event>  


                       <event name="single_click" linkId="22">
                        <action>
                            <main_context>current</main_context>                        
                            <datapanel type="current" tab="current">

                                <element id="0101"  keep_user_settings="false">
	                                <add_context>add</add_context>
                                </element>                                                             
                                
                            </datapanel>
                        </action>
                       </event>  



                                                                  
                    </properties>'


END
GO




GO
SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
GO

CREATE PROCEDURE [dbo].[grid_new_meta_geo_expandall]
    @main_context varchar(512) ='',
    @add_context varchar(512) ='',
    @filterinfo xml='',
    @session_context xml ='',
	@element_id varchar(512) ='',    
	@settings xml output,
    @error_mes varchar(512) output    
AS
BEGIN
SET NOCOUNT ON;



--insert into Debug (string) VALUES (NULL)
--insert into Debug (string) VALUES (NULL)
--insert into Debug (context) VALUES (@session_context)



Declare @gridsettings_str as varchar(max)
set @gridsettings_str='
<gridsettings>
        <labels>
            <header><h3 class="testStyle">Хедер tree-грида</h3></header>
            <footer><h3 class="testStyle">Футер tree-грида</h3></footer>            
        </labels>

<!--
-->
        <sorting>
					<sort column="Название" direction="ASC"/>        
        </sorting>



<!--
					<col id="Код" width="50px" editor="{editOn: has(''touch'') ? ''click'' : ''dblclick'', editor: NumberSpinner, editorArgs: {smallDelta: 0.1} }"/>
-->

        <columns>
					<col id="Название" width="200px"  editor="{editOn: has(''touch'') ? ''click'' : ''dblclick'', editor: ''text''}" readonly = "true" />        
					<col id="Идентификатор" width="100px" editor="{editOn: has(''touch'') ? ''click'' : ''dblclick'', editor: ''text''}" readonly = "false" />        
					<col id="Код" width="50px" readonly = "false" editor="{editor: CheckBox}" />
					<col id="Картинка" width="50px" type="IMAGE" readonly = "false" editor="{editor: CheckBox}" />
        </columns>        

				<action>
														<main_context>current</main_context>
                            <datapanel type="current" tab="current">
                                <element id="105">
	                                <add_context>Действие по умолчанию</add_context>
                                </element>                                                                   
                            </datapanel>
        </action>

<!--
<expandAllRecords>true</expandAllRecords>


-->


         <properties  gridWidth="1200px" gridHeight="500" 

autoSelectRecordId="AEACB658-4D23-446B-8CDC-EA8858BF60DA"  

expandAllRecords="true"  

/></gridsettings>'         


set @settings=CAST(@gridsettings_str as xml)


--SET @error_mes = 'Грид3 успешно построен';
--return 555;


END
GO


GO
SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
GO

CREATE PROCEDURE [dbo].[grid_new_data_geo_expandall]
    @main_context varchar(512) ='',
    @add_context varchar(512) ='',
    @filterinfo xml='',
    @session_context xml ='',
	@element_id varchar(512) ='',    
    @sortcols varchar(1024) ='',		

    @firstrecord int = 1,
    @pagesize int = 20,   

    @parent_id varchar(512) =''        
	
AS
BEGIN
SET NOCOUNT ON;



--insert into Debug (string) VALUES (NULL)
--insert into Debug (string) VALUES (NULL)
--insert into Debug (context) VALUES (@session_context)

--insert into Debug (string) VALUES (@sortcols)


--    if (@sortcols = '')
--     insert into Debug (string) VALUES ('NULLLLLLLLLLLLLLLLLLL')
--    else
--     insert into Debug (string) VALUES ('NOT NULLLLLLLLLLLLLLLLLLL')


declare @Sql varchar(8000);

IF (@parent_id IS NULL) OR (LTrim(@parent_id) = '')
BEGIN



-- set @Sql = 'select Name as "Название", cast(geo6_Id as varchar(50)) as "Идентификатор", Id as "Код", ''imagesingrid/test.jpg'' AS [Картинка], 1 as HasChildren, geo6_Id as "~~id", cast( ''<properties>

 set @Sql = 'select Name as "Название", cast(geo6_Id as varchar(50)) as "Идентификатор", Id as "Код", 
''NULL'' AS [Картинка], 1 as HasChildren, geo6_Id as "~~id", cast( ''<properties>

			<readonly value="false"/>

<!--

			<styleClass name="jslivegrid-record-bold"/>
			<styleClass name="jslivegrid-record-italic"/>

-->


<!-- 

                    <event name="row_save_data">
                        <action>
                            <main_context>current</main_context>
                            <datapanel type="current" tab="current">
                                <element id="105">
									<add_context>''+[Name]+''_row_save_data</add_context>                                                                                             
                                </element> 
                            </datapanel>
                        </action>
                    </event>    



                    <event name="row_add_record">
                        <action>
                            <main_context>current</main_context>
                            <datapanel type="current" tab="current">
                                <element id="105">
									<add_context>''+[Name]+''_row_add_record</add_context>                                                                                             
                                </element> 
                            </datapanel>
                        </action>
                    </event>    

--> 



<!-- 
--> 


                    <event name="row_single_click">
                        <action>
                            <main_context>current</main_context>
                            <datapanel type="current" tab="current">
                                <element id="105">
									<add_context>''+[Name]+''</add_context>                                                                                             
                                </element> 
                            </datapanel>
                        </action>
                    </event>    


                             

                    <event name="row_selection">
                        <action>
                            <main_context>current</main_context>
                            <datapanel type="current" tab="current">
                                <element id="105">
									<add_context>''+[Name]+''</add_context>                                                                                             
                                </element> 
                            </datapanel>
                        </action>
                    </event>        





            </properties>'' as xml)  as [~~properties] 

from geo6 where 


--Id IS NOT NULL

_Id = 5


'



END

IF (select COUNT(*) from geo6 where geo6_Id = @parent_id) > 0
BEGIN
 set @Sql = 'select Name as "Название", cast(geo5_Id as varchar(50)) as "Идентификатор", Id as "Код", ''imagesingrid/test.jpg'' AS [Картинка], 1 as HasChildren, geo5_Id as "~~id", cast( ''<properties>


		<readonly value="false"/>

<!--

			<styleClass name="jslivegrid-record-bold"/>
			<styleClass name="jslivegrid-record-italic"/>

-->


                    <event name="row_single_click">
                        <action>
                            <main_context>current</main_context>
                            <datapanel type="current" tab="current">
                                <element id="105">
									<add_context>''+[Name]+''</add_context>                                                                                             
                                </element> 
                            </datapanel>
                        </action>
                    </event>    
                    <event name="row_selection">
                        <action>
                            <main_context>current</main_context>
                            <datapanel type="current" tab="current">
                                <element id="105">
									<add_context>''+[Name]+''</add_context>                                                                                             
                                </element> 
                            </datapanel>
                        </action>
                    </event>                                       
            </properties>'' as xml)  as [~~properties] from geo5 where (FJField_9 = '''+@parent_id+''') AND (Id IS NOT NULL)' 
END





IF (select COUNT(*) from geo5 where geo5_Id = @parent_id) > 0
BEGIN
 set @Sql = 'select Name as "Название", Id as "Код", cast(geo3_Id as varchar(50)) as "Идентификатор", ''imagesingrid/test.jpg'' AS [Картинка], 0 as HasChildren, geo3_Id as "~~id", cast( ''<properties>

<!--
			<styleClass name="jslivegrid-record-bold"/>
			<styleClass name="jslivegrid-record-italic"/>
-->


                    <event name="row_single_click">
                        <action>
                            <main_context>current</main_context>
                            <datapanel type="current" tab="current">
                                <element id="105">
									<add_context>''+[Name]+''</add_context>                                                                                             
                                </element> 
                            </datapanel>
                        </action>
                    </event>    
                    <event name="row_selection">
                        <action>
                            <main_context>current</main_context>
                            <datapanel type="current" tab="current">
                                <element id="105">
									<add_context>''+[Name]+''</add_context>                                                                                             
                                </element> 
                            </datapanel>
                        </action>
                    </event>                                       
            </properties>'' as xml)  as [~~properties] from geo3 where (FJField_9 = '''+@parent_id+''') AND (Id IS NOT NULL)'  
END

IF (select COUNT(*) from geo3 where geo3_Id = @parent_id) > 0
BEGIN
-- SET @error_mes = 'Вызов процедуры для города' 
-- RETURN 1

 set @Sql = 'select top(0) Name as "Название", Id as "Код", ''imagesingrid/test.jpg'' AS [Картинка], 0 as HasChildren, geo3_Id as "~~id", cast( ''<properties>
			<styleClass name="jslivegrid-record-bold"/>
			<styleClass name="jslivegrid-record-italic"/>
                    <event name="row_single_click">
                        <action>
                            <main_context>current</main_context>
                            <datapanel type="current" tab="current">
                                <element id="105">
									<add_context>''+[Name]+''</add_context>                                                                                             
                                </element> 
                            </datapanel>
                        </action>
                    </event>    
                    <event name="row_selection">
                        <action>
                            <main_context>current</main_context>
                            <datapanel type="current" tab="current">
                                <element id="105">
									<add_context>''+[Name]+''</add_context>                                                                                             
                                </element> 
                            </datapanel>
                        </action>
                    </event>                                       
            </properties>'' as xml)  as [~~properties] from geo3 where (FJField_9 = '''+@parent_id+''') AND (Id IS NOT NULL)'  
	
END

IF LTRIM(@sortcols)!=''
BEGIN
 set @Sql = @Sql+' '+@sortcols
END
ELSE
BEGIN
 set @Sql = @Sql+' order by Name'
END


--RAISERROR (@Sql, 12, 2)

--set @Sql = 'select top (0) 5 as dd'

EXEC(@Sql)



--SET @error_mes = 'Грид3 успешно построен';
--return 555;


END
GO




GO
SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
GO

CREATE PROCEDURE [dbo].[parents_grid_new_geo]
    @main_context varchar(512) ='',
    @add_context varchar(512) ='',
    @filterinfo xml='',
    @session_context xml ='',
	@element_id varchar(512) ='',    
    @sortcols varchar(1024) ='',		

    @firstrecord int = 1,
    @pagesize int = 20,   

    @parent_id varchar(512) =''        
AS
BEGIN
SET NOCOUNT ON;



select 
 'Приволжский ФО_Обновленный_Parent' as "Название"
, 16 as "Код"
, 'AFAF2D58-7016-4A0B-B228-8DC765444A37' as "~~id"
--, '85CD7E79-F23E-454B-B4F5-E81EC3755D99' as "Идентификатор"
--, NULL as "Картинка"
, NULL as "parentId"

--, 1 as HasChildren

--, 'false' as readonly
, cast('<properties></properties>' as xml) as [~~properties]




--SET @error_mes = 'Грид3 успешно построен';
--return 555;


END
GO


GO
SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
GO

CREATE PROCEDURE [dbo].[toolbar_grid_new_download_file]
   @main_context nvarchar(512)='',  
   @add_context nvarchar(512)='',  
   @filterinfo xml='',  
   @session_context xml='',
   @elementId nvarchar(512)='',
   @data xml output
AS
BEGIN

declare
	@id nvarchar(50)='',
	@columnName nvarchar(150)=''

set @id = @session_context.value('(/sessioncontext/related/gridContext/currentRecordId)[1]','nvarchar(50)');
if @id is null begin 
	set @id = ''
end

set @columnName = @session_context.value('(/sessioncontext/related/gridContext/currentColumnId)[1]','nvarchar(150)');
if @columnName is null begin 
	set @columnName = ''
end



DECLARE @data_str NVARCHAR(MAX);

set @data_str='


	<gridtoolbar>

		<item text="Серверное действие"  hint="Серверное действие" disable="false"
            iconClassName="testJSToolbarButton"  >
   

                        <action >
                            <main_context>current</main_context>                        

                            <server>
															<activity id="srv02" name="sc_add_to_debug_console_adapter">
																<add_context>
																	add
																</add_context>
															</activity>
                            </server>
                        </action>


		</item>



		<item text="Скачать файл 1"  hint="Скачать файл"  downloadLinkId="11" iconClassName="testJSToolbarButton" >
		</item>


		<group text="Item2Group"   iconClassName="testJSToolbarButton" >
			<item text="Скачать файл 2" hint="Скачать файл 2" downloadLinkId="12"  disable="false" iconClassName="testJSToolbarButton">





			</item>
			<separator/>
			<item text="Item22" hint="Item three"  iconClassName="testJSToolbarButton">
				<action show_in="MODAL_WINDOW">
					<main_context>current</main_context>
					<modalwindow caption="Item22 click." height="700" width="600"/>
					<datapanel type="current" tab="current">
						<element id="443">
						  <add_context>ElementId='+@elementId+', Столбец='+@columnName+', RecordId='+@id+'</add_context>
						</element>
					</datapanel>
				</action>
			</item>
		</group>


 	  <item text="Item1"  hint="Item one" disable="false" iconClassName="testJSToolbarButton"></item>






    </gridtoolbar>';



SET @data = @data_str;



RETURN 0;

END
GO




