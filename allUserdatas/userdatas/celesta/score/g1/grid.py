# coding: utf-8
from g1._g1_orm import testCursor
from ru.curs.showcase.app.api import UserMessage
from ru.curs.showcase.app.api import MessageType
from ru.curs.showcase.core import UserMessageFactory
from ru.curs.showcase.core.jython import JythonDTO
from ru.curs.showcase.core.jython import JythonDownloadResult
from java.io import ByteArrayInputStream
from java.lang import String

def getDataAndSetting(context, main, add, filterinfo, session, elementId, sortColumnList):
    print 'Get grid data and setting from Celesta Python procedure.'
    print 'User %s' % context.userId
    print 'main "%s".' % main
    print 'add "%s".' % add
    print 'filterinfo "%s".' % filterinfo
    print 'session "%s".' % session
    print 'elementId "%s".' % elementId
    
    if sortColumnList != None:
        for column in sortColumnList:
            print 'sort columnID "%s".' % column.getId()
    
    data = u'''
    <records>
        <rec>
            <name>Тест</name>
        </rec>
    </records>'''
    settings = u'''
    <gridsettings>
       <labels>
        <header>
        <h3>Test Grid jython data</h3>
        </header>
      </labels>
      <columns>
        <col id="name" />
      </columns>
      <properties flip="false" pagesize="15" totalCount="0" profile="grid.nowidth.properties"/>
    </gridsettings>'''
    
    res = JythonDTO(data, settings, UserMessageFactory().build(555, u"Грид (обычный) успешно построен из Celesta"))
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
        <h3>Test Grid jython data</h3>
        </header>
      </labels>
      <columns>
        <col id="name" />
        <col id="file"  width="130px" type="DOWNLOAD" linkId="download"/> 
      </columns>
      
      
        <sorting>
           <sort column="file" direction="DESC"/>        
        </sorting>      
      
      
      <properties  pagesize="15" totalCount="1" profile="grid.nowidth.properties"/>
    </gridsettings>'''
    
    res = JythonDTO(None, settings, UserMessageFactory().build(555, u"Грид (Live, metadata) успешно построен из Celesta"))
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
            
            
    
    data = u'''
    <records>
        <rec>
            <name>Тест</name>
            <file>Файл</file>
        </rec>
    </records>'''


    
#    res = JythonDTO(data, None)
    
    
    res = JythonDTO(data, None, UserMessageFactory().build(555, u"Грид (Live, data) успешно построен из Celesta"))
    return res

    
    
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