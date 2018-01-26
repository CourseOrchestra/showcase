# coding: utf-8
from g1._g1_orm import testCursor
from ru.curs.showcase.app.api import UserMessage
from ru.curs.showcase.app.api import MessageType
from ru.curs.showcase.core import UserMessageFactory
from ru.curs.showcase.core.jython import JythonDTO
from ru.curs.showcase.core.jython import JythonDownloadResult
from ru.curs.gwt.datagrid.model import Column
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
            <name>Тест1</name>
            <code>1</code>            
            <_x007e__x007e_id>1</_x007e__x007e_id>
            <HasChildren>1</HasChildren>
            
        </rec>
        <rec>
            <name>Тест2</name>
            <code>2</code>
            <_x007e__x007e_id>2</_x007e__x007e_id>
            <HasChildren>1</HasChildren>
        </rec>
        <rec>
            <name>Тест3</name>
            <code>3</code>
            <_x007e__x007e_id>3</_x007e__x007e_id>
        </rec>
        <rec>
            <name>Тест4</name>
            <code>4</code>
            <_x007e__x007e_id>4</_x007e__x007e_id>
        </rec>
        <rec>
            <name>Тест5</name>
            <code>5</code> 
            <_x007e__x007e_id>5</_x007e__x007e_id>           
        </rec>
        <rec>
            <name>Тест6</name>
            <code>6</code>            
            <_x007e__x007e_id>6</_x007e__x007e_id>
        </rec>
        <rec>
            <name>Тест7</name>
            <code>7</code>            
            <_x007e__x007e_id>7</_x007e__x007e_id>
        </rec>
        <rec>
            <name>Тест8</name>
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
<!--      
      <columns>
        <col id="name" />
      </columns>
-->      
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






def gridSaveData(context, main, add, filterinfo, session, elementId, editorData):
    print 'Сохранение отредактированных данных.'
    print 'User %s' % context.userId
    print 'main "%s".' % main
    print 'add "%s".' % add
    print 'filterinfo "%s".' % filterinfo
    print 'session "%s".' % session
    print 'elementId "%s".' % elementId
    print 'editorData: %s' % editorData
    
    
    res = GridSaveResult(UserMessageFactory().build(555, u"Данные успешно сохранены из Челесты"))
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
    
    
    res = GridAddRecordResult(UserMessageFactory().build(555, u"Запись успешно добавлена из Челесты"))
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
