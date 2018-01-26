# coding: utf-8
from g1._g1_orm import testCursor 
from ru.curs.showcase.core.jython import JythonDTO
from ru.curs.showcase.core.jython import JythonDownloadResult
from ru.curs.showcase.runtime import AppInfoSingleton
from java.io import FileInputStream
from java.io import File
from ru.curs.showcase.app.api import UserMessage
from ru.curs.showcase.app.api import MessageType
from ru.curs.showcase.core import UserMessageFactory
from ru.curs.celesta.showcase.utils import XMLJSONConverter



from ru.curs.showcase.util.alfresco import AlfrescoManager
#from ru.curs.showcase.util.alfresco import AlfrescoLoginResult
#from ru.curs.showcase.util.alfresco import AlfrescoUploadFileResult

def template(context, main, add, filterinfo, session, elementId):
    print 'Get xform data from Celesta Python procedure.'
    print 'User %s' % context.userId
    print 'main "%s".' % main
    print 'add "%s".' % add
    print 'filterinfo "%s".' % filterinfo
    print 'session "%s".' % session
    print 'elementId "%s".' % elementId
    
    data = u'''

<html xmlns="http://www.w3.org/1999/xhtml" xmlns:ev="http://www.w3.org/2001/xml-events"
    xmlns:xsd="http://www.w3.org/2001/XMLschema" xmlns:fs="http://www.curs.ru/ns/FormServer"
    xmlns:xf="http://www.w3.org/2002/xforms">
    <head>
    

    
    
    
        <!-- Простейшие контролы ввода и вывода -->
        <xf:model id="xformId_mainModel">
            <xf:instance id="xformId_mainInstance">
                <schema xmlns="">
                    <info>
                        <name />
                        <growth />
                        <eyescolour />
                        <music />
                        <comment />
                    </info>
                </schema>
            </xf:instance>
            
            
            <xf:action ev:event="xforms-subform-ready">
<xf:action if="instance('xformId_srvdata')/element/needReload = 'true'">
<!--            
            <xf:message>Действие на загрузку22</xf:message>
-->            
</xf:action>            
              
<!--                 <xf:action if="instance('xformId_srvdata')/element/needReload = 'true'">             -->
<!--                     <xf:load resource="javascript:gwtXFormUpdate('xformId','1',  Writer.toString(getSubformInstanceDocument('xformId_mainModel', 'xformId_mainInstance')))"></xf:load> -->
<!--                 </xf:action> -->
            </xf:action>            
            
            <xf:submission id="xformId_wrong_save" method="post" instance="xformId_mainInstance"
                replace="instance" ref="instance('xformId_mainInstance')" 
                action="secured/submit?proc=xforms_submission11">
                <xf:action ev:event="xforms-submit-done">
                    <xf:message>Submission успешно выполнен</xf:message>
                </xf:action>
                <xf:action if="event('response-body')!='null'" ev:event="xforms-submit-error">
                    <xf:message>
                        Ошибка при выполнении submission:
                        <xf:output value="event('response-body')" />
                    </xf:message>
                </xf:action>
            </xf:submission>

            <xf:submission id="xformId_good_save" method="post" instance="xformId_mainInstance"
                replace="instance" ref="instance('xformId_mainInstance')" action="secured/submit?proc=xforms_submission1">
                <xf:action ev:event="xforms-submit-done">
                    <xf:message>Submission успешно выполнен</xf:message>
                </xf:action>
                <xf:action if="event('response-body')!='null'" ev:event="xforms-submit-error">
                    <xf:message>
                        Ошибка при выполнении submission:
                        <xf:output value="event('response-body')" />
                    </xf:message>
                </xf:action>
            </xf:submission>


            <xf:submission id="xformId_xslttransformation" method="post"
ref="instance('xformId_mainInstance')"                instance="xformId_mainInstance" replace="instance" 
                action="secured/xslttransformer?xsltfile=xformsxslttransformation_test.xsl">
                <xf:action if="event('response-body')!='null'" ev:event="xforms-submit-error">
                    <xf:message>
                        Ошибка при выполнении submission:
                        <xf:output value="event('response-body')" />
                    </xf:message>
                </xf:action>
            </xf:submission>


            <xf:bind>
            </xf:bind>
        </xf:model>
    </head>
    <body>

        <div style="font-size: 15px;"> Имя: </div>
        <div>
            <xf:input  ref="instance('xformId_mainInstance')/info/name">
                <xf:help>Справка</xf:help>
                <xf:hint>Дополнительная информация</xf:hint>
            </xf:input>


            <xf:trigger>
                <xf:label>Selector</xf:label>
                <xf:action ev:event="DOMActivate">
                    <xf:load
                        resource="javascript:showSelector({
                       id : 'xformId',
                       procCount : '[dbo].[regionscount]',
                       procList  : '[dbo].[regionslist]',                       
                       generalFilters      : '',
                       currentValue        : '',
                       windowCaption       : 'Выберите название',
                       onSelectionComplete : function(ok, selected){
                    if (ok) {
                    var a = getSubformInstanceDocument('xformId_mainModel', 'xformId_mainInstance').getElementsByTagName('info')[0].getElementsByTagName('name')[0];
                    setValue(a, selected.name);
         
                    xforms.ready = false;
                    xforms.refresh();
                    xforms.ready = true;
                            }
                                   }});" />
                </xf:action>
            </xf:trigger>            
            

        </div>
        

        
        <div style="font-size: 15px;"> Цвет глаз (1): </div>
        <div>
            <xf:select1 appearance="full" ref="instance('xformId_mainInstance')/info/eyescolour">
                <xf:item>
                    <xf:label>Голубой</xf:label>
                    <xf:value>Голубой</xf:value>
                </xf:item>
                <xf:item>
                    <xf:label>Карий</xf:label>
                    <xf:value>Карий</xf:value>
                </xf:item>
                <xf:item>
                    <xf:label>Зеленый</xf:label>
                    <xf:value>Зеленый</xf:value>
                </xf:item>
                <xf:item>
                    <xf:label>Серый</xf:label>
                    <xf:value>Серый</xf:value>
                </xf:item>
            </xf:select1>
        </div>
        <div style="font-size: 15px;"> Цвет глаз (2): </div>
        <div>
            <xf:select1 ref="instance('xformId_mainInstance')/info/eyescolour">
                <xf:item>
                    <xf:label>Голубой</xf:label>
                    <xf:value>Голубой</xf:value>
                </xf:item>
                <xf:item>
                    <xf:label>Карий</xf:label>
                    <xf:value>Карий</xf:value>
                </xf:item>
                <xf:item>
                    <xf:label>Зеленый</xf:label>
                    <xf:value>Зеленый</xf:value>
                </xf:item>
                <xf:item>
                    <xf:label>Серый</xf:label>
                    <xf:value>Серый</xf:value>
                </xf:item>
            </xf:select1>
        </div>
        <div style="font-size: 15px;"> Любимая музыка (1): </div>
        <div>
            <xf:select appearance="full" ref="instance('xformId_mainInstance')/info/music">
                <xf:item>
                    <xf:label>Классическая</xf:label>
                    <xf:value>Классическая</xf:value>
                </xf:item>
                <xf:item>
                    <xf:label>Инструментальная</xf:label>
                    <xf:value>Инструментальная</xf:value>
                </xf:item>
                <xf:item>
                    <xf:label>Эстрадная</xf:label>
                    <xf:value>Эстрадная</xf:value>
                </xf:item>
            </xf:select>
        </div>
        <div style="font-size: 15px;"> Любимая музыка (2): </div>
        <div>
            <xf:select ref="instance('xformId_mainInstance')/info/music">
                <xf:item>
                    <xf:label>Классическая</xf:label>
                    <xf:value>Классическая</xf:value>
                </xf:item>
                <xf:item>
                    <xf:label>Инструментальная</xf:label>
                    <xf:value>Инструментальная</xf:value>
                </xf:item>
                <xf:item>
                    <xf:label>Эстрадная</xf:label>
                    <xf:value>Эстрадная</xf:value>
                </xf:item>
            </xf:select>
        </div>
        <div style="font-size: 15px;"> Комментарии: </div>
        <div>
            <xf:textarea ref="instance('xformId_mainInstance')/info/comment" />
        </div>
        <div style="clear: both;">
            <xf:output value="'Уважаемый '"/> 
            <xf:output value="instance('xformId_mainInstance')/info/name"/>  
            <xf:output value="'! Ваш рост: '"/>
            <xf:output value="instance('xformId_mainInstance')/info/growth"/>
            <xf:output value="', ваш цвет глаз: '"/>
            <xf:output value="instance('xformId_mainInstance')/info/eyescolour"/>
            <xf:output value="', ваш музыкальные предпочения: '"/>
            <xf:output value="instance('xformId_mainInstance')/info/music"/>

        </div>
        <div>

            <xf:trigger>
                <xf:label>Вызов XFormsSubmissionServlet</xf:label>
                <xf:action ev:event="DOMActivate">
                    <xf:send submission="xformId_good_save" />
                </xf:action>
            </xf:trigger>

            <xf:trigger>
                <xf:label>Вызов XFormsSubmissionServlet с ошибкой</xf:label>
                <xf:action ev:event="DOMActivate">
                    <xf:send submission="xformId_wrong_save" />
                </xf:action>
            </xf:trigger>

            <xf:trigger>
                <xf:label>Вызов XFormsTransformationServlet</xf:label>
                <xf:action ev:event="DOMActivate">
                    <xf:send submission="xformId_xslttransformation" />
                </xf:action>
            </xf:trigger>

        </div>


        <div>
        
            <xf:trigger>
                <xf:label id = "xformId_Save">Сохранить</xf:label>
                <xf:action ev:event="DOMActivate">
                    <xf:load resource="javascript:gwtXFormSave('xformId', '1',  Writer.toString(getSubformInstanceDocument('xformId_mainModel', 'xformId_mainInstance')), 'xformId_Save')"/>
                </xf:action>
            </xf:trigger>
            
            <xf:trigger>
                <xf:label>Отфильтровать</xf:label>
                <xf:action ev:event="DOMActivate">
                    <xf:load
                        resource="javascript:gwtXFormFilter('xformId', '2',  Writer.toString(getSubformInstanceDocument('xformId_mainModel', 'xformId_mainInstance')))" />
                </xf:action>
            </xf:trigger>
            
            <xf:trigger>
                <xf:label>Обновить</xf:label>
                <xf:action ev:event="DOMActivate">
                    <xf:load
                        resource="javascript:gwtXFormUpdate('xformId', '3',  Writer.toString(getSubformInstanceDocument('xformId_mainModel', 'xformId_mainInstance')))" />
                </xf:action>
            </xf:trigger>
        </div>

    </body>
</html>






    
    '''
    return JythonDTO(data)



def main(context, main, add, filterinfo, session, elementId):
    print 'Get xform data from Celesta Python procedure.'
    print 'User %s' % context.userId
    print 'main "%s".' % main
    print 'add "%s".' % add
    print 'filterinfo "%s".' % filterinfo
    print 'session "%s".' % session
    print 'elementId "%s".' % elementId
    
    data = u'''
    <schema xmlns="">
      <info>
        <name>Белгородская обл.</name>
        <growth />
        <eyescolour />
        <music />
        <comment />
      </info>
    </schema>
    '''
    settings = u'''
    <properties>        
    </properties>
    '''    
#    return JythonDTO(data, settings, UserMessageFactory().build(555, u"xforms успешно построен из Celesta"))
    return JythonDTO(data, settings)

def save(context, main, add, filterinfo, session, elementId, data):
    print 'Save xform data from Celesta Python procedure.'
    print 'User %s' % context.userId
    print 'main "%s".' % main
    print 'add "%s".' % add
    print 'filterinfo "%s".' % filterinfo
    print 'session "%s".' % session
    print 'elementId "%s".' % elementId
    print 'data "%s".' % data
    
#    return UserMessageFactory().build(555, u"xforms успешно сохранен из Celesta");
#    return UserMessage("557");
#    return UserMessage("557", MessageType.INFO);
#    return UserMessage("557", "");

    context.error(u"555");

    return UserMessage("557", None);

 

def downloadFile(context, main, add, filterinfo, session, elementId, data):
    print 'Download file xform from Celesta Python procedure.'
    print 'User %s' % context.userId
    print 'main "%s".' % main
    print 'add "%s".' % add
    print 'filterinfo "%s".' % filterinfo
    print 'session "%s".' % session
    print 'elementId "%s".' % elementId
    print 'data "%s".' % data
    
    fileName = 'app.properties'
    path = AppInfoSingleton.getAppInfo().getCurUserData().getPath() + "\\" + fileName
    file = File(path)
    return JythonDownloadResult(FileInputStream(file),fileName)


def uploadFile(context, main, add, filterinfo, session, elementId, data, fileName, file):
    print 'Upload file xform from Celesta Python procedure.'
    print 'User %s' % context.userId
    print 'main "%s".' % main
    print 'add "%s".' % add
    print 'filterinfo "%s".' % filterinfo
    print 'session "%s".' % session
    print 'elementId "%s".' % elementId
    print 'data "%s".' % data
    print 'fileName "%s".' % fileName

#    return UserMessageFactory().build(555, u"Файл успешно загружен из Celesta");    

    
#    alfUser = "user222" 
#    alfPass = "пароль"
#    alfUploadParams = {}
#    alfUploadParams["siteid"] = "demo"
#    alfUploadParams["containerid"] = "documentlibrary"
#    alfUploadParams["uploaddirectory"] = "/abc"
    

#    alfUser = "admin" 
#    alfPass = "F708420Dx"
#    alfUploadParams = {}
#    alfUploadParams["destination"] = "workspace://SpacesStore/8096d4bd-4e97-4c4d-9849-73275b722f0f"
#    alfUploadParams["uploaddirectory"] = "/folder1"
    
    
    
    alfURL = "http://127.0.0.1:8080/alfresco"
    alfUser = "user222" 
    alfPass = "пароль"
    
    alfUploadParams = {}
#    alfUploadParams["destination"] = "workspace://SpacesStore/59baaac6-0f3b-44e3-aac9-56aaa2767769"
#    alfUploadParams["uploaddirectory"] = "/test_folder1"
    
    alfUploadParams["updatenoderef"] = "workspace://SpacesStore/97cf8b94-0407-40ed-9582-711f4d42dfa0"    
    
    alfUploadParams["description"] = "Это комментарий44"
    alfUploadParams["majorversion"] = "1.0"
#    alfUploadParams["overwrite"] = "yes"
#    alfUploadParams["thumbnails"] = "no"
    

# contenttype
# majorversion
    
     
    
    resultLogin = AlfrescoManager.login(alfURL, alfUser, alfPass)
    if resultLogin.getResult() == 0:
        resultUploadFile = AlfrescoManager.uploadFile(fileName, file, alfURL, resultLogin.getTicket(), alfUploadParams);
        if resultUploadFile.getResult() == 0:
            context.message(u"Файл успешно загружен в Alfresco. Координаты файла в Alfresco: "+resultUploadFile.getNodeRef());            
        else:
            context.error(resultUploadFile.getErrorMessage());
    else:
        context.error(resultLogin.getErrorMessage());        
                



def webtext(context, main, add, filterinfo, session, elementId):
    print 'Get webtext data from Celesta Python procedure.'
    print 'User %s' % context.userId
    print 'main "%s".' % main
    print 'add "%s".' % add
    print 'filterinfo "%s".' % filterinfo
    print 'session "%s".' % session
    print 'elementId "%s".' % elementId
    
    
    alfURL = "http://127.0.0.1:8080/alfresco"
    alfUser = "user222" 
    alfPass = "пароль"    
    
    alfTicket = ""
    
    resultLogin = AlfrescoManager.login(alfURL, alfUser, alfPass)
    if resultLogin.getResult() == 0:
        alfTicket = resultLogin.getTicket() 
    else:
        context.error(resultLogin.getErrorMessage());           
    
    data = u'''
    <h1>
        <a href="#" onclick="gwtWebTextFunc('${elementId}','testIdClient');">Показать сообщение (client activity)</a>
        <br/>
        <a href="#" onclick="gwtWebTextFunc('${elementId}','testIdServer');">Показать сообщение (server activity)</a>
        <br/>        
        
<!--  Guest        
        
  <a href="#">
   <img src="http://127.0.0.1:8080/alfresco/service/api/node/content/workspace/SpacesStore/2c31a645-549c-4479-b939-9cd11cbf8f10?guest=true" 
   width="1100px" height="700px" alt="lorem"></img>
  </a>
-->


  <a href="#">
<img src="http://127.0.0.1:8080/alfresco/service/api/node/content/workspace/SpacesStore/97cf8b94-0407-40ed-9582-711f4d42dfa0/versions?alf_ticket='''+alfTicket+'''" 
   width="1100px" height="700px" alt="lorem"></img>
  </a>

  
        
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
    
#    return JythonDTO(data, settings, UserMessageFactory().build(555, u"WebText успешно построен из Celesta"))
    return JythonDTO(data, settings)


def webtextParticularVersion(context, main, add, filterinfo, session, elementId):
    print 'Get webtext data from Celesta Python procedure.'
    print 'User %s' % context.userId
    print 'main "%s".' % main
    print 'add "%s".' % add
    print 'filterinfo "%s".' % filterinfo
    print 'session "%s".' % session
    print 'elementId "%s".' % elementId
    
    
    alfURL = "http://127.0.0.1:8080/alfresco"
    alfUser = "user222" 
    alfPass = "пароль"    
    
    alfTicket = ""
   
    alfFileId = "97cf8b94-0407-40ed-9582-711f4d42dfa0"
    
    alfVersionId = "" 

    
    resultLogin = AlfrescoManager.login(alfURL, alfUser, alfPass)
    if resultLogin.getResult() == 0:
        alfTicket = resultLogin.getTicket() 
        
        resultGetFileVersions = AlfrescoManager.getFileVersions(alfFileId, alfURL, resultLogin.getTicket());
        if resultGetFileVersions.getResult() == 0:
            versions = resultGetFileVersions.getVersions()
            
# Далее на основе полученных версий, находим идентификатор нужной нам версии. Предположим, что это
            alfVersionId = "b3984526-43e8-44d7-9ba5-b50318959750"
        else:
            context.error(resultGetFileVersions.getErrorMessage());
        
        
    else:
        context.error(resultLogin.getErrorMessage());           
    
    data = u'''
    <h1>
        <a href="#" onclick="gwtWebTextFunc('${elementId}','testIdClient');">Показать сообщение (client activity)</a>
        <br/>
        <a href="#" onclick="gwtWebTextFunc('${elementId}','testIdServer');">Показать сообщение (server activity)</a>
        <br/>        
        

  <a href="#">
<img src="http://127.0.0.1:8080/alfresco/service/api/node/content/workspace/version2Store/'''+alfVersionId+'''?alf_ticket='''+alfTicket+'''" 
   width="1100px" height="700px" alt="lorem"></img>
  </a>
  
        
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
    
#    return JythonDTO(data, settings, UserMessageFactory().build(555, u"WebText успешно построен из Celesta"))
    return JythonDTO(data, settings)


def submitDeleteFile(context, main, add, filterinfo, session, data):
    print 'Submit xform data from Celesta Python procedure.'
    print 'User %s' % context.userId
    print 'main "%s".' % main
    print 'add "%s".' % add
    print 'filterinfo "%s".' % filterinfo
    print 'session "%s".' % session
    print 'data "%s".' % data

    
    alfURL = "http://127.0.0.1:8080/alfresco"
    alfUser = "user222" 
    alfPass = "пароль"
    
    alfFileId = "50694ed0-63b7-47d8-8e54-67ad19e3c359"
    
    resultLogin = AlfrescoManager.login(alfURL, alfUser, alfPass)
    if resultLogin.getResult() == 0:
        resultDeleteFile = AlfrescoManager.deleteFile(alfFileId, alfURL, resultLogin.getTicket());
        if resultDeleteFile.getResult() == 0:
            context.message(u"Файл успешно удален из Alfresco.");            
        else:
            context.error(resultDeleteFile.getErrorMessage());
    else:
        context.error(resultLogin.getErrorMessage());        

    
    
    return XMLJSONConverter.jsonToXml(data);





def submitGetFileMetaData(context, main, add, filterinfo, session, data):
    print 'Submit xform data from Celesta Python procedure.'
    print 'User %s' % context.userId
    print 'main "%s".' % main
    print 'add "%s".' % add
    print 'filterinfo "%s".' % filterinfo
    print 'session "%s".' % session
    print 'data "%s".' % data

    
    alfURL = "http://127.0.0.1:8080/alfresco"
    alfUser = "user222" 
    alfPass = "пароль"
    
    alfFileId = "97cf8b94-0407-40ed-9582-711f4d42dfa0"
    
#    acceptLanguage = ""
    acceptLanguage = "en-US,en;"
#    acceptLanguage = "ru-RU,ru;"
    
    resultLogin = AlfrescoManager.login(alfURL, alfUser, alfPass)
    if resultLogin.getResult() == 0:
        resultGetFileMetaData = AlfrescoManager.getFileMetaData(alfFileId, alfURL, resultLogin.getTicket(), acceptLanguage);
        if resultGetFileMetaData.getResult() == 0:
            metaData = resultGetFileMetaData.getMetaData()
            
# Далее использование полученных метаданных
# ............
                       
            context.error(metaData);            
        else:
            context.error(resultGetFileMetaData.getErrorMessage());
    else:
        context.error(resultLogin.getErrorMessage());        

    
    
    return XMLJSONConverter.jsonToXml(data);



def submitSetFileMetaData(context, main, add, filterinfo, session, data):
    print 'Submit xform data from Celesta Python procedure.'
    print 'User %s' % context.userId
    print 'main "%s".' % main
    print 'add "%s".' % add
    print 'filterinfo "%s".' % filterinfo
    print 'session "%s".' % session
    print 'data "%s".' % data

    
    alfURL = "http://127.0.0.1:8080/alfresco"
    alfUser = "user222" 
    alfPass = "пароль"
    
    alfFileId = "97cf8b94-0407-40ed-9582-711f4d42dfa0"
    
    
#    acceptLanguage = ""
    acceptLanguage = "en-US,en;"
#    acceptLanguage = "ru-RU,ru;"
    
    metaData = unicode("{\"properties\":{\"title\":\"1Название файла\",\"description\":\"1Описание файла\"}}")
    
    resultLogin = AlfrescoManager.login(alfURL, alfUser, alfPass)
    if resultLogin.getResult() == 0:
        resultSetFileMetaData = AlfrescoManager.setFileMetaData(alfFileId, metaData, alfURL, resultLogin.getTicket(), acceptLanguage);
        if resultSetFileMetaData.getResult() == 0:
            context.message(u"Метаданные файла из Alfresco успешно заданы.");            
        else:
            context.error(resultSetFileMetaData.getErrorMessage());
    else:
        context.error(resultLogin.getErrorMessage());        

    
    
    return XMLJSONConverter.jsonToXml(data);



def submitGetFileVersions(context, main, add, filterinfo, session, data):
    print 'Submit xform data from Celesta Python procedure.'
    print 'User %s' % context.userId
    print 'main "%s".' % main
    print 'add "%s".' % add
    print 'filterinfo "%s".' % filterinfo
    print 'session "%s".' % session
    print 'data "%s".' % data

    
    alfURL = "http://127.0.0.1:8080/alfresco"
    alfUser = "user222" 
    alfPass = "пароль"
    
    alfFileId = "97cf8b94-0407-40ed-9582-711f4d42dfa0"

    
    resultLogin = AlfrescoManager.login(alfURL, alfUser, alfPass)
    if resultLogin.getResult() == 0:
        resultGetFileVersions = AlfrescoManager.getFileVersions(alfFileId, alfURL, resultLogin.getTicket());
        if resultGetFileVersions.getResult() == 0:
            versions = resultGetFileVersions.getVersions()
# Далее использование полученных версий
# ............
                       
            context.error(versions);            
        else:
            context.error(resultGetFileVersions.getErrorMessage());
    else:
        context.error(resultLogin.getErrorMessage());        

    
    
    return XMLJSONConverter.jsonToXml(data);



def submitCreateFolder(context, main, add, filterinfo, session, data):
    print 'Submit xform data from Celesta Python procedure.'
    print 'User %s' % context.userId
    print 'main "%s".' % main
    print 'add "%s".' % add
    print 'filterinfo "%s".' % filterinfo
    print 'session "%s".' % session
    print 'data "%s".' % data
    
    alfURL = "http://127.0.0.1:8080/alfresco"
    alfUser = "user222" 
    alfPass = "пароль"

    alfParentFolderId = "a9488807-c96c-41e9-a9ee-8a176ae1ccf6"    
    
    alfCreateFolderParams = unicode("{\"name\": \"Новая директория1\", \"title\": \"Заголовок новой директории1\", \"description\": \"Описание новой директории1\", \"type\": \"cm:folder\"}")    
    
    
    resultLogin = AlfrescoManager.login(alfURL, alfUser, alfPass)
    if resultLogin.getResult() == 0:
        resultCreateFolder = AlfrescoManager.createFolder(alfParentFolderId, alfCreateFolderParams, alfURL, resultLogin.getTicket());
        if resultCreateFolder.getResult() == 0:
            nodeRef = resultCreateFolder.getNodeRef()
# Далее использование nodeRef созданной директории
# ............

            context.error(u"Директория успешно создана в Alfresco. Координаты директории в Alfresco: "+nodeRef);
        else:
            context.error(resultCreateFolder.getErrorMessage());
    else:
        context.error(resultLogin.getErrorMessage());        

    return XMLJSONConverter.jsonToXml(data);










