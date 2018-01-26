SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE PROCEDURE [dbo].[extJsTree_getData]
   @main_context nvarchar(512)='',  
   @add_context nvarchar(512)='',  
   @filterinfo xml='',  
   @session_context xml='',
   @params xml='',
   @data xml output
AS
BEGIN
declare
	@parentId nvarchar(20)='',
	@curValue nvarchar(20)=''
	
set @parentId = @params.value('(/params/id)[1]','nvarchar(20)');
if @parentId is null begin 
	set @parentId = 'Root'
end
set @curValue = @params.value('(/params/curValue)[1]','nvarchar(20)');
if @curValue is null 
	begin 
		set @curValue = ''
	end else 
	begin
		set @curValue = '['+@curValue+']'
	end

set @data='
	<items>
		<item id="'+@parentId+'.1" name="Lazy2 loaded item '+@parentId+'.1'+@curValue+'" leaf="false"/>
		<item id="'+@parentId+'.2" name="Lazy2 loaded item '+@parentId+'.2'+@curValue+'" leaf="false"/>
    </items>
	';


return 555;


END
GO

CREATE PROCEDURE [dbo].[pluginExtJsTree]
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
	set @data=N'
	<items>
			<item id="1" name="Lazy load item" leaf="false"/>
			<item id="2" name="Расходование денежных средств" cls="folder">
				<children>
					<item id="21" name="Оплата поставщикам за товар" leaf="true" attr1="a" checked="false"/>
					<item id="22" name="Расходы по таможенному оформлению" leaf="true" attr1="b" checked="false"/>
					<item id="23" name="Расходы  на аренду, коммунальные услуги" cls="folder">
						<children>
							<item id="231" name="Аренда недвижимости" leaf="true" attr1="c" checked="false"/>
							<item id="232" name="Коммунальные услуги" leaf="true" attr1="d" checked="false"/>
							<item id="233" name="Расходы на содержание сооружений и оборудования" leaf="true" attr1="e" checked="false"/>
						</children>
					</item>
					<item id="24" name="Расходы на персонал" cls="folder">
						<children>
							<item id="241" name="Расходы на оплату труда" leaf="true" attr1="f" checked="false"/>
							<item id="242" name="Страхование персонала, мед.услуги" leaf="true" attr1="g" checked="false"/>
							<item id="243" name="Матпомощь, подарки" leaf="true" attr1="m" checked="false"/>
						</children>
					</item>
					<item id="25" name="Услуги связи" leaf="true" attr1="k" checked="false"/>
					<item id="26" name="Маркетинг и реклама" leaf="true" attr1="l" checked="false"/>
					<item id="27" name="Обеспечение безопасности" leaf="true" attr1="n" checked="false"/>
				</children>   
			</item>
			<item id="3" name="Поступление денежных средств" cls="folder">
				<children>
					<item id="31" name="Доход от продажи товара" leaf="true" attr1="o" checked="false"/>
					<item id="32" name="Расходы по таможенному оформлению" leaf="true" attr1="p" checked="false"/>
					<item id="33" name="Возврат денежных средств от контрагентов" leaf="true" attr1="r" checked="false"/>				
					<item id="34" name="Проценты по депозитам" leaf="true" attr1="s" checked="false"/>
				</children>   
			</item>
    </items>';
	set @settings='<properties></properties>';
	
END
GO
