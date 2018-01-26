DECLARE
 settings_str TEXT;
BEGIN

settings_str := '<gridsettings>
        <labels>
            <header><h3 class="testStyle">Грид с различными типами столбцов</h3></header>
        </labels>
        <columns>
        <col id="_Id" type="INT"/>        
        <col id="UpdateRowTime" type="DATETIME"/>        
        <col id="UpdateRowDate" type="DATE"/>                        
        <col id="Сайт" type="LINK"/>
        <col id="rnum" type="INT"/>              
          
        </columns>
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
<properties pagesize="20" profile="sngl_before_dbl.properties"/>

<records>
<rec>
  <_Id>1</_Id>
  <Journal_41_Name>Зерно</Journal_41_Name>
  <UpdateRowTime>2009-11-13T12:50:11.250</UpdateRowTime>
  <UpdateRowDate>2009-11-13</UpdateRowDate>
  <Сайт><link href="http://Зерно.рф" openInNewTab="true"/></Сайт>
      <properties>
      <event name="row_single_click">
        <action>
          <main_context>current</main_context>
          <datapanel type="current" tab="current">
            <element id="d1">
              <add_context>Зерно</add_context>
            </element>
            <element id="d2">
              <add_context>Зерно</add_context>
            </element>
            <element id="d3">
              <add_context>Зерно</add_context>
            </element>
            <element id="d4">
              <add_context>Зерно</add_context>
            </element>
            <element id="d5">
              <add_context>Зерно</add_context>
            </element>
            <element id="d6">
              <add_context>Зерно</add_context>
            </element>
            <element id="d7">
              <add_context>Зерно</add_context>
            </element>
            <element id="d8">
              <add_context>Зерно</add_context>
            </element>
            <element id="d9">
              <add_context>Зерно</add_context>
            </element>
            <element id="d10">
              <add_context>Зерно</add_context>
            </element>
          </datapanel>
        </action>
      </event>
    </properties>
  <rnum>1</rnum>
</rec>
<rec>
  <_Id>2</_Id>
  <Journal_41_Name>Пшеница</Journal_41_Name>
  <UpdateRowTime>2009-11-23T14:07:09.063</UpdateRowTime>
  <UpdateRowDate>2009-11-23</UpdateRowDate>
  <Сайт><link href="http://Пшеница.рф" openInNewTab="true"/></Сайт>
    <properties>
      <event name="row_single_click">
        <action>
          <main_context>current</main_context>
          <datapanel type="current" tab="current">
            <element id="d1">
              <add_context>Пшеница</add_context>
            </element>
            <element id="d2">
              <add_context>Пшеница</add_context>
            </element>
            <element id="d3">
              <add_context>Пшеница</add_context>
            </element>
            <element id="d4">
              <add_context>Пшеница</add_context>
            </element>
            <element id="d5">
              <add_context>Пшеница</add_context>
            </element>
            <element id="d6">
              <add_context>Пшеница</add_context>
            </element>
            <element id="d7">
              <add_context>Пшеница</add_context>
            </element>
            <element id="d8">
              <add_context>Пшеница</add_context>
            </element>
            <element id="d9">
              <add_context>Пшеница</add_context>
            </element>
            <element id="d10">
              <add_context>Пшеница</add_context>
            </element>
          </datapanel>
        </action>
      </event>
    </properties>
  <rnum>2</rnum>
</rec>
<rec>
  <_Id>3</_Id>
  <Journal_41_Name>Мясо</Journal_41_Name>
  <UpdateRowTime>2010-04-26T12:42:46.850</UpdateRowTime>
  <UpdateRowDate>2010-04-26</UpdateRowDate>
  <Сайт><link href="http://Мясо.рф" openInNewTab="true"/></Сайт>
    <properties>
      <event name="row_single_click">
        <action>
          <main_context>current</main_context>
          <datapanel type="current" tab="current">
            <element id="d1">
              <add_context>Мясо</add_context>
            </element>
            <element id="d2">
              <add_context>Мясо</add_context>
            </element>
            <element id="d3">
              <add_context>Мясо</add_context>
            </element>
            <element id="d4">
              <add_context>Мясо</add_context>
            </element>
            <element id="d5">
              <add_context>Мясо</add_context>
            </element>
            <element id="d6">
              <add_context>Мясо</add_context>
            </element>
            <element id="d7">
              <add_context>Мясо</add_context>
            </element>
            <element id="d8">
              <add_context>Мясо</add_context>
            </element>
            <element id="d9">
              <add_context>Мясо</add_context>
            </element>
            <element id="d10">
              <add_context>Мясо</add_context>
            </element>
          </datapanel>
        </action>
      </event>
    </properties>
  <rnum>3</rnum>
</rec>
<rec>
  <_Id>4</_Id>
  <Journal_41_Name>Молоко</Journal_41_Name>
  <UpdateRowTime>2010-04-26T12:42:59.413</UpdateRowTime>
  <UpdateRowDate>2010-04-26</UpdateRowDate>
  <Сайт><link href="http://Молоко.рф" openInNewTab="true"/></Сайт>
    <properties>
      <event name="row_single_click">
        <action>
          <main_context>current</main_context>
          <datapanel type="current" tab="current">
            <element id="d1">
              <add_context>Молоко</add_context>
            </element>
            <element id="d2">
              <add_context>Молоко</add_context>
            </element>
            <element id="d3">
              <add_context>Молоко</add_context>
            </element>
            <element id="d4">
              <add_context>Молоко</add_context>
            </element>
            <element id="d5">
              <add_context>Молоко</add_context>
            </element>
            <element id="d6">
              <add_context>Молоко</add_context>
            </element>
            <element id="d7">
              <add_context>Молоко</add_context>
            </element>
            <element id="d8">
              <add_context>Молоко</add_context>
            </element>
            <element id="d9">
              <add_context>Молоко</add_context>
            </element>
            <element id="d10">
              <add_context>Молоко</add_context>
            </element>
          </datapanel>
        </action>
      </event>
    </properties>
  <rnum>4</rnum>
</rec>
</records>


</gridsettings>'; 
settings := settings_str::xml;

END;
