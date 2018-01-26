# coding: utf-8
from g1._g1_orm import testCursor 
from ru.curs.showcase.core.jython import JythonDTO
from ru.curs.showcase.app.api import UserMessage
from ru.curs.showcase.app.api import MessageType
from ru.curs.showcase.core import UserMessageFactory


def navigator(context, session):
    print 'Get navigator data from Celesta Python procedure.'
    print 'User %s' % context.userId
    print 'Sesion "%s".' % session
    
    
    cursor = testCursor(context)
    print 'cursor.count() "%s".' % cursor.count()
    
    #cursor.first();
    
    
    data = u'''
    <navigator width="200px">
        <group id="1" name="Примеры">
            <level1 id="11" name="Компоненты">
                <level2 id="111" name="WebText" >
                    <action>
                        <main_context></main_context>
                        <datapanel type="g1.datapanel.webTextDatapanel.celesta" tab="firstOrCurrent"></datapanel>
                    </action>
                </level2>
                <level2 id="112" name="XForms" >
                    <action>
                        <main_context></main_context>
                        <datapanel type="xforms.xml" tab="firstOrCurrent"></datapanel>
                    </action>
                </level2>
                
                <level2 id="113" name="GridNew"  >
                    <action>
                        <main_context></main_context>
                        <datapanel type="gridNew.xml" tab="firstOrCurrent"></datapanel>
                    </action>
                </level2>
                
                
                <level2 id="113" name="Grid">
                    <action>
                        <main_context></main_context>
                        <datapanel type="grid.xml" tab="firstOrCurrent"></datapanel>
                    </action>
                </level2>
                <level2 id="114" name="Новый триселектор"  selectOnLoad="true">
                    <action>
                        <main_context></main_context>
                        <datapanel type="plugin.xml" tab="firstOrCurrent"></datapanel>
                    </action>
                </level2>
                
                <level2 id="115" name="Alfresco"  >
                    <action>
                        <main_context></main_context>
                        <datapanel type="alfresco.xml" tab="firstOrCurrent"></datapanel>
                    </action>
                </level2>                     
                
                <level2 id="jsForm" name="jsForm"  >
                    <action>
                        <main_context>Data of main context</main_context>
                        <datapanel type="jsForm.xml" tab="firstOrCurrent" >          
                        </datapanel>
                    </action>
                </level2>
                
            </level1>
        </group>
    </navigator>
    '''    
    return data

def webtext(context, main, add, filterinfo, session, elementId):
    print 'Get navigator data from Celesta Python procedure.'
    print 'User %s' % context.userId
    print 'main "%s".' % main
    print 'add "%s".' % add
    print 'filterinfo "%s".' % filterinfo
    print 'session "%s".' % session
    print 'elementId "%s".' % elementId
    
    data = u'''
    <h1>
        <a href="#" onclick="gwtWebTextFunc('${elementId}','testIdClient');">Показать сообщение (client activity)</a>
        <br/>
        <a href="#" onclick="gwtWebTextFunc('${elementId}','testIdServer');">Показать сообщение (server activity)</a>
        
        <br/>
        <br/>
        <br/>
        <br/>
        <br/>        
        <button type="button" onclick="measureDownloadSpeed(10);">Измерить скорость загрузки контента</button>
        
    </h1>
    '''
    settings = u'''
    <properties>
        <event name="single_click" linkId="testIdClient">
             <action >
                <main_context>Москва</main_context>
                <client>
                    <activity id="activityClientID" name="showcaseShowAddContext">
                        <add_context>
                            add_context действия.
                        </add_context>
                    </activity>
                </client>
            </action>
        </event>
        <event name="single_click" linkId="testIdServer">
             <action >
                <main_context>Москва</main_context>
                <server>
                    <activity id="activityServerID" name="g1.activity.simple.celesta">
                         <add_context>
                             add_context для действия
                         </add_context>  
                    </activity>             
                </server>
            </action>
        </event>
    </properties>
    '''    
    
    #return JythonDTO(data, settings, UserMessageFactory().build(555, u"WebText успешно построен из Celesta"))
    return JythonDTO(data, settings)



