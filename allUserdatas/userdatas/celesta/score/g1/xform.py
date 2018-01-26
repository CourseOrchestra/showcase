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
                <xf:label>Selector22</xf:label>
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

    context.message(u"555");

#    return UserMessage("557", None);


    
def submit(context, main, add, filterinfo, session, data):
    print 'Submit xform data from Celesta Python procedure.'
    print 'context %s' % context    
    print 'User %s' % context.userId
    print 'main "%s".' % main
    print 'add "%s".' % add
    print 'filterinfo "%s".' % filterinfo
    print 'session "%s".' % session
    print 'ddddddddddddddddddddata "%s".' % data
    
    
#    context.error(u"555");

    context.message(u"Это инф<ор>  мац'и+о-н\"ное сообщение");
#    context.warning(u"Это предупреждение");
#    context.error(u"Это ошибка");


#    return UserMessage("557", None);
    
    
    
    
#    return data;

    return XMLJSONConverter.jsonToXml(data);

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
    
    return UserMessageFactory().build(555, u"Файл успешно загружен из Celesta");    

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