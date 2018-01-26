    SET NOCOUNT ON;
    
declare @filters as varchar(1024)
declare @ordering as varchar(1024)


IF OBJECT_ID('tempdb.dbo.#Per') is not null 
drop table #Per
IF OBJECT_ID('tempdb.dbo.#Reg_year') is not null 
drop table #Reg_year
IF OBJECT_ID('tempdb.dbo.#Columns_Name') is not null 
drop table #Columns_Name
IF OBJECT_ID('tempdb.dbo.#Tab1') is not null 
drop table #Tab1

declare @year as varchar(50)
declare @quater as varchar(50)
declare @params as varchar(8000)
Set @year='2010'
Set @quater='3'
Set @params=''
Select
    [Год],
    [Квартал]
Into #Per
From
    (Select 
        Journal_45_Name as [Год],
        '1' as [Квартал]
    From Journal_45
    Union ALL
    Select 
        Journal_45_Name as [Год],
        '2' as [Квартал]
    From Journal_45
    Union ALL
    Select 
        Journal_45_Name as [Год],
        '3' as [Квартал]
    From Journal_45
    Union ALL
    Select 
        Journal_45_Name as [Год],
        '4' as [Квартал]
    From Journal_45
    ) a

Select
    newid() as id__,
    geo5_id,
    geo5.NAME as [Регион],
    Journal_45_Name,
    Journal_45_Id,
    geo6.Name as [Федеральный округ],
    #Per.[Квартал] ,
      Case
            When geo6.Name='Южный ФО' then 3
            When geo6.Name='Уральский ФО' then 5
            When geo6.Name='Сибирский ФО' then 6
            When geo6.Name='Приволжский ФО' then 4
            When geo6.Name='Дальневосточный ФО' then 7
            When geo6.Name='Центральный ФО' then 1
            When geo6.Name='Северо-Западный ФО' then 2
            Else 0
         End as sort     
Into #Reg_year
From Journal_45 Inner Join #Per
    On #Per.[Год]= Journal_45_Name,
    geo5 left Join geo6
    On geo5.FJField_9=geo6_Id
Where geo5.FJField_16=1   and
 cast( Journal_45_Name as float)
   + cast(#Per.[Квартал] as float)/10 >= cast(cast(@year as int)-5  as float)+cast(@quater as float)/10
 and
 cast( Journal_45_Name as float)
     + cast(#Per.[Квартал] as float)/10 < cast(cast(@year as int)+1  as float)+cast(@quater as float)/10
Select distinct    
    [Квартал]  +'кв. ' +Journal_45_Name+ 'г.' as Col_Id,
    Journal_45_Name,
     [Квартал]
Into #Columns_Name
From #Reg_year
Order by Journal_45_Name, [Квартал]

select Journal_44.FJField_17 as [~Всего, тыс тонн],
       Journal_44.FJField_20,
       Journal_44.FJField_18,
        geo5.Name as [регион],
        Journal_41.Journal_41_Name,
        Journal_40_Name,
        Journal_45.Journal_45_Name as [год],
       Journal_44.FJField_16 as [квартал],
       Case
            When geo6.Name in ('Южный ФО','Уральский ФО','Сибирский ФО',
                'Приволжский ФО','Дальневосточный ФО','Центральный ФО','Северо-Западный ФО','Российская Федерация') then 0
            Else 1
         End as sort2,
         geo6.Name
Into #Tab1
From Journal_44
    Inner Join geo5
    On geo5.geo5_Id=Journal_44.FJField_18
    Inner Join Journal_40
    On Journal_40_Id=Journal_44.FJField_14
    Inner Join Journal_41
    On Journal_41_Id=Journal_44.FJField_12
    left Join geo6
    On geo5.FJField_9=geo6_Id
    Inner Join Journal_45
    On Journal_45_Id=Journal_44.FJField_21
    Where geo5.FJField_16=1    and
                  Journal_40_Name= @main_context

and
(((cast( Journal_45.Journal_45_Name as float)
               + cast(Journal_44.FJField_16 as float)/10)>=(cast(cast(@year as int)  as float)+ cast(@quater as float)/10 )

               and (cast( Journal_45.Journal_45_Name as float)
               + cast(Journal_44.FJField_16 as float)/10)<(cast((cast(@year as int)+1 )  as float)+cast(@quater as float)/10)
                and Journal_44.FJField_20='Прогноз')
        or
                (( cast( Journal_45.Journal_45_Name as float)
                   + cast(Journal_44.FJField_16 as float)/10 )< (cast(cast(@year as int)  as float)+cast(@quater as float)/10 )
                   and Journal_44.FJField_20='Факт'))
                   
                   
select @params = @params + ', [' + RTRIM(#Columns_Name.Col_Id)+']' FROM #Columns_Name
set @params = substring(@params, 3, len(@params) - 1);



Insert into #Tab1 (#Tab1.[~Всего, тыс тонн],#Tab1.FJField_20,#Tab1.[регион],
        #Tab1.Journal_41_Name,#Tab1.Journal_40_Name,#Tab1.год,#Tab1.квартал,#Tab1.sort2)
select sum([~Всего, тыс тонн]),
       #Tab1.FJField_20,
       'Итого по россии',
        #Tab1.Journal_41_Name,
        #Tab1.Journal_40_Name,
        #Tab1.год,#Tab1.квартал,
        -1
        From #Tab1
   Group by
   #Tab1.FJField_20,
        #Tab1.Journal_41_Name,
        #Tab1.Journal_40_Name,
        #Tab1.год,#Tab1.квартал

Set @ordering=(Select
				Case
					When @sortcols='' then 'Order by sort2'
					Else @sortcols 
				End)     
declare @Sql varchar(8000);
set @Sql = 
'Select [Регион], ''imagesingrid/test.jpg'' AS [Картинка],' + @params+',cast( ''<properties>
                    <event name="row_single_click">
                        <action>
                            <main_context>current</main_context>
                            <datapanel type="current" tab="current">
                                <element id="3">
									<add_context>''+[Регион]+''</add_context>                                                                                             
                                </element> 
                                <element id="5">
									<add_context>''+[Регион]+''</add_context>                                                                                             
                                </element> 
                                
                            </datapanel>
                        </action>
                    </event>    
                    <event name="row_selection">
                        <action>
                            <main_context>current</main_context>
                            <datapanel type="current" tab="current">
                                <element id="5">
									<add_context>''+[Регион]+''</add_context>                                                                                             
                                </element> 
                                
                            </datapanel>
                        </action>
                    </event>                                       
            </properties>'' as xml)  as [~~properties] FROM('+
'SELECT [Регион], sort2,' + @params+
' FROM' +
' (Select
     cast (#Tab1.[~Всего, тыс тонн] as numeric(10,2))as t1,
     #Reg_year.[Регион] ,
     #Tab1.квартал+''кв. ''+#Reg_year.[Journal_45_Name]+''г.'' as [Квартал],     
     #Reg_year.[Федеральный округ],
      #Tab1.sort2,
     ''Зерно''  as [~Продовольственный ресурс]
      
FROM #Reg_year
    Left join #Tab1
        On #Reg_year.[Регион]=#Tab1.[регион] and
         #Reg_year.Journal_45_Name=#Tab1.год and 
         #Reg_year.Квартал =#Tab1.квартал
         ) p '+
         
' PIVOT ('+
'    max(t1)'+
'    FOR [Квартал] in('+@params+')'+
' ) AS pvt Where [3кв. 2005г.] is Not NULL )p '+@ordering
EXEC(@Sql)
Declare @gridsettings_str as varchar(max)
set @gridsettings_str='<gridsettings>
        <labels>
            <header><h3 class="testStyle">'+@main_context+' зерна, тыс. тонн </h3></header>
        </labels>
        <columns>
        <col id="Регион" width="100px"/> <col id="Картинка" width="20px" type="IMAGE"/>'
        
select     @gridsettings_str=@gridsettings_str+'<col id="'+#Columns_Name.Col_Id+'" width="60px" precision="2"/>' From #Columns_Name
set @gridsettings_str=@gridsettings_str+'</columns>
							<action>
							<main_context>current</main_context>
                            <datapanel type="current" tab="current">
                                <element id="3">
	                                <add_context>current</add_context>
                                </element>   
                                <element id="5">
	                                <add_context>current</add_context>
                                </element>                                                                   
                                
                            </datapanel>
                        </action>
<properties flip="false" pagesize="15" autoSelectRecordId="16"  autoSelectRelativeRecord="false" totalCount="0"/></gridsettings>' 
set    @settings=CAST(@gridsettings_str as xml)


SET @return = 555
SET @error_mes = 'Грид успешно построен'



