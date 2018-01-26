
DECLARE
 settings_str TEXT;
BEGIN

settings_str := 
'<gridsettings>
<labels>
<header>
<h3>Порталы</h3>
</header>
</labels>
        <columns>
        <col id="Название" width="100px" /> 
        <col id="Картинка" width="20px" type="IMAGE"/>        
        <col id="Файл1"  width="130px" type="DOWNLOAD" linkId="11"/>                 
        <col id="Файл2"  width="100px" type="DOWNLOAD" linkId="12"/>                         
        <col id="Логотип" width="250px" type="LINK"/>
        <col id="URL" width="100px" type="LINK"/>
        </columns>
<properties flip="false" pagesize="2" profile="grid.nowidth.properties" autoSelectRecordId="3" 
 autoSelectRelativeRecord="false" autoSelectColumnId="URL"/>
 
 
<records> 
<rec>
  <Название>Советский<br>Яндекс</br>ведущий<br>поисковик</br>Рунета</Название>
  <Картинка>imagesingrid/test.jpg</Картинка>
  <Файл1><div style="text-align:center">Файл c навигатором в имени которого содержится GUID записи</div></Файл1>
  <Файл2></Файл2>
  <Логотип><link href="http://yandex.ru" image="${images.in.grid.dir}/imagesingrid/yandex.png" text="Яндекс" openInNewTab="true"/></Логотип>
  <URL> <link href="http://yandex.ru/yandsearch?text=КУРС-ИТ" text="Яндекс" openInNewTab="true"/></URL>
  <id>7451DF70-ACC3-48CC-8CC0-3092F8A237BE</id>
    <properties>
      <styleClass name="grid-record-bold" />
      <styleClass name="grid-record-italic" />
      <event name="row_single_click">
        <action>
          <main_context>current</main_context>
          <datapanel type="current" tab="current">
            <element id="d1">
              <add_context><br>Яндекс</br> ведущий <br/>поисковик <br/>Рунета</add_context>
            </element>
            <element id="d2">
              <add_context><br>Яндекс</br> ведущий <br/>поисковик <br/>Рунета</add_context>
            </element>
          </datapanel>
        </action>
      </event>
    </properties>
</rec>
<rec>
  <Название>РБК</Название>
  <Картинка>imagesingrid/test.jpg</Картинка>
  <Файл1>Файл12</Файл1>
  <Файл2>Файл22</Файл2>
  <Логотип><link href="http://rbc.ru" image="${images.in.grid.dir}/imagesingrid/rbc.gif" text="rbc.ru" openInNewTab="true"/></Логотип>
  <URL><link href="http://rbc.ru"  openInNewTab="true"/></URL>
  <id>8BC3D54A-AE03-4728-AFCD-54DC092B0823</id>
    <properties>
      <styleClass name="grid-record-bold" />
      <styleClass name="grid-record-italic" />
      <event name="row_single_click">
        <action>
          <main_context>current</main_context>
          <datapanel type="current" tab="current">
            <element id="d1">
              <add_context>РБК</add_context>
            </element>
            <element id="d2">
              <add_context>РБК</add_context>
            </element>
          </datapanel>
        </action>
      </event>
    </properties>
</rec>
<rec>
  <Название>Рамблер</Название>
  <Картинка>imagesingrid/test.jpg</Картинка>
  <Файл1>Файл13</Файл1>
  <Файл2>Файл23</Файл2>
  <Логотип><link href="http://rambler.ru" image="${images.in.grid.dir}/imagesingrid/rambler.gif" text="rambler.ru" openInNewTab="true"/></Логотип>
  <URL><link href="http://rambler.ru" text="rambler.ru" openInNewTab="true"/></URL>
  <id>77F60A7C-42EB-4E32-B23D-F179E58FB138</id>
    <properties>
      <styleClass name="grid-record-bold" />
      <styleClass name="grid-record-italic" />
      <event name="row_single_click">
        <action>
          <main_context>current</main_context>
          <datapanel type="current" tab="current">
            <element id="d1">
              <add_context>Рамблер</add_context>
            </element>
            <element id="d2">
              <add_context>Рамблер</add_context>
            </element>
          </datapanel>
        </action>
      </event>
    </properties>
</rec>
<rec>
  <Название>Mail.ru</Название>
  <Картинка>imagesingrid/test.jpg</Картинка>
  <Файл1>Файл14</Файл1>
  <Файл2>Файл24</Файл2>
  <Логотип><link href="http://mail.ru" image="${images.in.grid.dir}/imagesingrid/mailru.gif" text="mail.ru" openInNewTab="true"/></Логотип>
  <URL><link href="http://mail.ru" text="mail.ru" openInNewTab="true"/></URL>
  <id>856ACCF2-53AB-4AF0-A956-F6E85601D0B4</id>
    <properties>
      <styleClass name="grid-record-bold" />
      <styleClass name="grid-record-italic" />
      <event name="row_single_click">
        <action>
          <main_context>current</main_context>
          <datapanel type="current" tab="current">
            <element id="d1">
              <add_context>Mail.ru</add_context>
            </element>
            <element id="d2">
              <add_context>Mail.ru</add_context>
            </element>
          </datapanel>
        </action>
      </event>
    </properties>
</rec>
</records>		 
 
</gridsettings>'; 
settings := settings_str::xml;

END;
