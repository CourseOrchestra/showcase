# coding: utf-8
from g1._g1_orm import testCursor
from ru.curs.showcase.app.api import UserMessage
from ru.curs.showcase.app.api import MessageType
from ru.curs.showcase.core import UserMessageFactory
from ru.curs.showcase.core.jython import JythonDTO
from ru.curs.showcase.core.jython import JythonDownloadResult
from java.io import ByteArrayInputStream
from java.lang import String
from ru.curs.showcase.app.api.grid import GridSaveResult
from ru.curs.showcase.app.api.grid import GridAddRecordResult

import simplejson as json
from ru.curs.celesta.showcase.utils import XMLJSONConverter



def getDataAndSetting(context, main, add, filterinfo, session, elementId, sortColumnList):
    print 'Get grid data and setting from Celesta Python procedure.'
    print 'User %s' % context.userId
    print 'main "%s".' % main
    print 'add "%s".' % add
    print 'filterinfo "%s".' % filterinfo
    print 'session "%s".' % session
    print 'elementId "%s".' % elementId
    print 'sortColumnList '  
    print sortColumnList
    
    
    data = u'''
    <records>
        <rec>
            <Название>Тест1</Название>
            <code>1</code>            
            <_x007e__x007e_id>1</_x007e__x007e_id>
            <HasChildren>1</HasChildren>
            
        </rec>
        <rec>
            <Название>Тест2</Название>
            <code>2</code>
            <_x007e__x007e_id>2</_x007e__x007e_id>
            <HasChildren>1</HasChildren>
        </rec>
        <rec>
            <Название>Тест3</Название>
            <code>3</code>
            <_x007e__x007e_id>3</_x007e__x007e_id>
        </rec>
        <rec>
            <Название>Тест4</Название>
            <code>4</code>
            <_x007e__x007e_id>4</_x007e__x007e_id>
        </rec>
        <rec>
            <Название>Тест5</Название>
            <code>5</code> 
            <_x007e__x007e_id>5</_x007e__x007e_id>           
        </rec>
        <rec>
            <Название>Тест6</Название>
            <code>6</code>            
            <_x007e__x007e_id>6</_x007e__x007e_id>
        </rec>
        <rec>
            <Название>Тест7</Название>
            <code>7</code>            
            <_x007e__x007e_id>7</_x007e__x007e_id>
        </rec>
        <rec>
            <Название>Тест8</Название>
            <code>8</code>            
            <_x007e__x007e_id>8</_x007e__x007e_id>
        </rec>
        


        
        
    </records>'''
    settings = u'''
    <gridsettings>
       <labels>
        <header>
        <h3>Edit Grid jython2</h3>
        </header>
      </labels>
      
      <columns>
        <col id="Название" />
      </columns>
      
      <properties flip="false" gridWidth="1200px" gridHeight="500" pagesize="15" totalCount="0" />
   </gridsettings>'''
    

    
#   res = JythonDTO(data, settings, UserMessage("555", u"Грид успешно построен из Jython", MessageType.INFO))

    #res = JythonDTO(data, settings, UserMessageFactory().build(554, u"Грид успешно построен из Jython"))
    

    #context.warning(u"Грид успешно построен из Jython22222222");
    
    #context.message(u"555");
    
    #context.warning(u"555");
    
    #context.error(u"555");
    
    print u'dddddddddddddddddddddddddd: Полное обновление'
    
    res = JythonDTO(data, settings)
    

    return res



def getSetting(context, main, add, filterinfo, session, elementId):
    print 'Get grid setting from Celesta Python procedure.'
    print 'User %s' % context.userId
    print 'main "%s".' % main
    print 'add "%s".' % add
    print 'filterinfo "%s".' % filterinfo
    print 'session "%s".' % session
    print 'elementId "%s".' % elementId
    
    
   
    
    settings = u'''
    <gridsettings>
       <labels>
        <header>
        <h3>Edit Grid jython2</h3>
        </header>
      </labels>
      
      
        <sorting>
                    <sort column="Название" direction="ASC"/>        
        </sorting>      
      
      <columns>
        <col id="Название" />
        <col id="code" />        
      </columns>
      <properties flip="false" gridWidth="1200px" gridHeight="500" pagesize="15" totalCount="8" />
   </gridsettings>'''
    
    
    #context.message(u"555");
    
    #context.message(u"Сообщение", u"Заголовок");
    #context.message(u"Сообщение", u"Заголовок", u"solutions/default/resources/group_icon_default.png" );
    
    #context.warning(u"555");
    
    #context.warning(u"Сообщение", u"Заголовок");
    #context.warning(u"Сообщение", u"Заголовок", u"solutions/default/resources/group_icon_default.png" );
    
    #context.error(u"555");
    
    #context.error(u"Сообщение", u"Заголовок");
    #context.error(u"Сообщение", u"Заголовок22", u"solutions/default/resources/group_icon_default.png" );
    
    

    
#    res = JythonDTO(None, settings, UserMessageFactory().build(555, u"Грид (Live, metadata) успешно построен из Celesta"))
    res = JythonDTO(None, settings)    
    return res

def getData(context, main, add, filterinfo, session, elementId, sortColumnList, firstrecord, pagesize):
    print 'Get grid data from Celesta Python procedure.'
    print 'User %s' % context.userId
    print 'main "%s".' % main
    print 'add "%s".' % add
    print 'filterinfo "%s".' % filterinfo
    print 'session "%s".' % session
    print 'elementId "%s".' % elementId
    print 'firstrecord "%s".' % firstrecord
    print 'pagesize "%s".' % pagesize
    
    print 'sortColumnList'    
    if sortColumnList != None:
        for column in sortColumnList:
            print 'sort columnID "%s".' % column.getId()
            print 'sort direction "%s".' % column.getSorting()
            
            
            
    raise Exception(
            u"СНИЛС должен состоять из 9 значащих и 2 контрольных цифр.")
            
            
            
    
    data = u'''
    <records>
        <rec>
<!--        
            <Название>Тест1</Название>
-->            
            <code>1</code>            
            <_x007e__x007e_id>1</_x007e__x007e_id>
            <HasChildren>1</HasChildren>
            
        </rec>
        <rec>
            <Название>Тест2</Название>
            <code>2</code>
            <_x007e__x007e_id>2</_x007e__x007e_id>
            <HasChildren>1</HasChildren>
        </rec>
        <rec>
<!--        
            <Название>Тест3</Название>
-->            
            <code>3</code>
            <_x007e__x007e_id>3</_x007e__x007e_id>
        </rec>
        <rec>
            <Название>Тест4</Название>
            <code>4</code>
            <_x007e__x007e_id>4</_x007e__x007e_id>
        </rec>
        <rec>
            <Название>Тест5</Название>
            <code>5</code> 
            <_x007e__x007e_id>5</_x007e__x007e_id>           
        </rec>
        <rec>
            <Название>Тест6</Название>
            <code>6</code>            
            <_x007e__x007e_id>6</_x007e__x007e_id>
        </rec>
        <rec>
            <Название>Тест7</Название>
            <code>7</code>            
            <_x007e__x007e_id>7</_x007e__x007e_id>
        </rec>
        <rec>
            <Название>Тест8</Название>
            <code>8</code>            
            <_x007e__x007e_id>8</_x007e__x007e_id>
        </rec>
    </records>'''        


    
#    res = JythonDTO(data, None)
    
    
#    res = JythonDTO(data, None, UserMessageFactory().build(555, u"Грид (Live, data) успешно построен из Celesta"))
    
    
    
    
    
    
    
    res = JythonDTO(data, None)    
    return res

    
    
    return res


def gridSaveData(context, main, add, filterinfo, session, elementId, editorData):
    print 'Сохранение отредактированных данных.'
    print 'User %s' % context.userId
    print 'main "%s".' % main
    print 'add "%s".' % add
    print 'filterinfo "%s".' % filterinfo
    print 'session "%s".' % session
    print 'elementId "%s".' % elementId
    print 'editorData: %s' % editorData
    
    
    #context.message(u"555");
    context.message(u"ffffffffffff");
    
    #context.warning(u"555");
    #context.warning(u"gggggggggg");
    
    #context.error(u"555");    
    #context.error(u"ddddddddddddd");
    
    
#    res = GridSaveResult(UserMessageFactory().build(555, u"Данные успешно сохранены из Челесты"))
    res = GridSaveResult()    
    res.setRefreshAfterSave(0);
    return res



def gridAddRecord(context, main, add, filterinfo, session, elementId, addRecordData):
    print 'Добавление новой записи.'
    print 'User %s' % context.userId
    print 'main "%s".' % main
    print 'add "%s".' % add
    print 'filterinfo "%s".' % filterinfo
    print 'session "%s".' % session
    print 'elementId "%s".' % elementId
    print 'addRecordData: %s' % addRecordData
    
    
    #context.message(u"555");
    context.message(u"ffffffffffff");
    
    #context.warning(u"555");
    #context.warning(u"gggggggggg");
    
    #context.error(u"555");    
    #context.error(u"ddddddddddddd");
    


    
    #res = GridAddRecordResult(UserMessageFactory().build(555, u"Запись успешно добавлена из Челесты"))
    res = GridAddRecordResult()    
    return res
    






def downloadFile(context, main, add, filterinfo, session, elementId, recordId):
    print 'Save xform data from Celesta Python procedure.'
    print 'User %s' % context.userId
    print 'main "%s".' % main
    print 'add "%s".' % add
    print 'filterinfo "%s".' % filterinfo
    print 'session "%s".' % session
    print 'elementId "%s".' % elementId
    print 'recordId "%s".' % recordId
    
    fileName = 'test.txt'
    data = String('grid data')
    return JythonDownloadResult(ByteArrayInputStream(data.getBytes()),fileName)

def gridToolBar(context, main, add, filterinfo, session, elementId):
    print 'Get grid data from Celesta Python procedure.'
    print 'User %s' % context.userId
    print 'main "%s".' % main
    print 'add "%s".' % add
    print 'filterinfo "%s".' % filterinfo
    print 'session "%s".' % session
    print 'elementId "%s".' % elementId
    
    data = u'''
    <gridtoolbar>
    
    
        <item text="Частичное обновление" img="imagesingrid/test.jpg" hint="Частичное обновление">
            <action>
                <main_context>current</main_context>
                <datapanel type="current" tab="current">
                    <element id="102" keep_user_settings="true" partial_update="true">
                        <add_context>ElementId='+@elementId+' Name='+@columnName+', Id='+@id+'</add_context>
                    </element>
                </datapanel>
            </action>
        </item>


        <item text="Полное обновление" img="imagesingrid/test.jpg" hint="Полное обновление">
            <action>
                <main_context>current</main_context>
                <datapanel type="current" tab="current">
                    <element id="102" keep_user_settings="true">
                        <add_context>ElementId='+@elementId+' Name='+@columnName+', Id='+@id+'</add_context>
                    </element>
                </datapanel>
            </action>
        </item>
    
    
    
        <separator/>
        <item text="Item1" img="imagesingrid/test.jpg" hint="Item one" disable="false">
            <action show_in="MODAL_WINDOW">
                <main_context>current</main_context>
                <modalwindow caption="Item1 click" height="200" width="600"/>
                <datapanel type="current" tab="current">
                    <element id="0201">
                        <add_context>ElementId='''+elementId+''' Add context value</add_context>
                    </element>
                </datapanel>
            </action>
        </item>
        <group text="Item2Group" >
            <item text="Item21" hint="Item two" disable="true" />
            <separator/>
            <item text="Item22" hint="Item three" disable="false">
                <action show_in="MODAL_WINDOW">
                    <main_context>current</main_context>
                    <modalwindow caption="Item22 click." height="200" width="600"/>
                    <datapanel type="current" tab="current">
                        <element id="0201">
                            <add_context>ElementId='''+elementId+''' Add context value</add_context>
                        </element>
                    </datapanel>
                </action>
            </item>
            <group text="Item23Group" >
                <item text="Item231" hint="Item two" disable="true" />
                <separator/>
                <item text="Item232" hint="Item three" disable="false">
                    <action show_in="MODAL_WINDOW">
                        <main_context>current</main_context>
                        <modalwindow caption="Item232 click." height="200" width="600"/>
                        <datapanel type="current" tab="current">
                            <element id="0201">
                                <add_context>ElementId='''+elementId+''' Add context value</add_context>
                            </element>
                        </datapanel>
                    </action>
                </item>
            </group>
        </group>
    </gridtoolbar>
    '''    
    return data


def gridPartialUpdate(context, main, add, filterinfo, session, elementId, sortColumnList, parentId ):
    print 'Get grid data and setting from Celesta Python procedure.'
    print 'User %s' % context.userId
    print 'main "%s".' % main
    print 'add "%s".' % add
    print 'filterinfo "%s".' % filterinfo
    print 'session "%s".' % session
    print 'elementId "%s".' % elementId
    
    data = u'''
    <records>
        <rec>
            <name>Тест1_Частичное обновление</name>
            <code>1</code>            
            <_x007e__x007e_id>1</_x007e__x007e_id>
            <HasChildren>1</HasChildren>
        </rec>
        
        <rec>        
            <name>Тест4_Частичное обновление</name>
            <code>4</code>
            <_x007e__x007e_id>4</_x007e__x007e_id>
        </rec>
        
    </records>'''
    
    settings = None

    
#   res = JythonDTO(data, settings, UserMessage("555", u"Грид успешно построен из Jython", MessageType.INFO))

    #res = JythonDTO(data, settings, UserMessageFactory().build(554, u"Грид успешно построен из Jython"))
    

    #context.warning(u"Грид успешно построен из Jython22222222");
    
    #context.message(u"555");
    
    #context.warning(u"555");
    
    #context.error(u"555");
    
    print u'dddddddddddddddddddddddddd: Частичное обновление'
    
    res = JythonDTO(data, settings)
    

    return res
