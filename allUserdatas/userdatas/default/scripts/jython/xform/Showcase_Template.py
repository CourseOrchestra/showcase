# coding: utf-8
'''
Created on 02.11.2011

@author: AleXXl
'''
from ru.curs.showcase.core.jython import JythonProc
from ru.curs.showcase.util.xml import XMLUtils
from org.xml.sax.helpers import DefaultHandler
from java.io import StringWriter, StringBufferInputStream
from  javax.xml.stream import XMLOutputFactory
from ru.curs.showcase.test.stress import JythonPoolTest
from ru.curs.showcase.app.api import UserMessage

# init vars
main = '<part instance="instance.xml"/>'
add = 'guid documents'
session = u'''<sessioncontext>
   <username>master</username>
   <sid/>
   <email/>
   <fullusername>master</fullusername>
   <phone/>
   <urlparams>
      <urlparam name="uid" value="[559F4F62-E741-4BA5-B4C5-BF01BE888047]"/>
   </urlparams>
   <userdata>default</userdata>
   <related/>
</sessioncontext>
'''

elementId = None
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
<!--            
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
xmlWriter = None
stringEmployeesWriter = None
tuUid = ""
statusUid = ""
employees = None


class formReader(DefaultHandler):
    states = ['schema', 'info', 'status', 'employees']
    currentState = 'schema'
    employeesWriter = None

    def startDocument(self):
        xmlWriter.writeStartElement('schema')

    def endDocument(self):
        xmlWriter.writeEndElement()
        xmlWriter.writeEndDocument()
        xmlWriter.flush()

    def startElement(self, namespaceURI, lname, qname, attrs):
        global stringEmployeesWriter, statusUid

        if (self.currentState != self.states[0] or qname == 'info'):
            if (self.currentState == self.states[0] and qname == 'info'):
                self.currentState = self.states[1]

            elif (self.currentState == self.states[1] and qname == 'status'):
                self.currentState = self.states[2]
                statusUid = attrs.getValue(attrs.getIndex("uid"))

            elif (self.currentState == self.states[1] and qname == 'employees'):
                self.currentState = self.states[3]

                stringEmployeesWriter = StringWriter()
                self.employeesWriter = XMLOutputFactory.newInstance().createXMLStreamWriter(stringEmployeesWriter)
                self.employeesWriter.writeStartElement(qname)

            elif (self.currentState == self.states[3]):
                self.employeesWriter.writeStartElement(qname)
                for i in range(0, attrs.getLength()):
                    self.employeesWriter.writeAttribute(attrs.getQName(i), attrs.getValue(i))

        else:
            if (attrs.getIndex("ftid") != -1):
                for i in range(0, attrs.getLength()):
                    if (attrs.getQName(i) != 'ftid'):
                        xmlWriter.writeStartElement('record')
                        xmlWriter.writeAttribute('ftid', attrs.getValue(attrs.getIndex("ftid")))
                        xmlWriter.writeAttribute('name', attrs.getQName(i))
                        xmlWriter.writeAttribute('value', attrs.getValue(i))
                        xmlWriter.writeEndElement()

    def endElement(self, uri, lname, qname):
        if (self.currentState == self.states[1] and qname == 'info'):
            self.currentState = self.states[0]
        elif (self.currentState == self.states[2] and qname == 'status'):
            self.currentState = self.states[1]
        elif (self.currentState == self.states[3] and qname == 'employees'):
            self.currentState = self.states[1]
            self.employeesWriter.writeEndElement()
            self.employeesWriter.writeEndDocument()
            self.employeesWriter.flush()
            self.employeesWriter.close()
        elif (self.currentState == self.states[3] and qname != 'employees'):
            self.employeesWriter.writeEndElement()

    def characters(self, ch, start, length):
        if (self.currentState == self.states[3]):
            self.employeesWriter.writeCharacters(ch, start, length)


class Showcase_Template(JythonProc):
    def getRawData(self, context, elId):
        global main, add, session, filterContext, elementId, data
        main = context.getMain()
        if context.getAdditional():
            add = context.getAdditional()
        session = context.getSession()
        if context.getFilter():
            filterContext = context.getFilter()
        elementId = elId
        
        #data = adata
        #return mainproc()
        
        print u'dddddddddddddddddddddddddddd'
        print data
        return data


def mainproc():
    global xmlWriter, statusUid

    parser = XMLUtils.createSAXParser()
    stream = StringBufferInputStream(data.encode("utf-8"))
    stringWriter = StringWriter()
    xmlWriter = XMLOutputFactory.newInstance().createXMLStreamWriter(stringWriter)
    parser.parse(stream, formReader())
    xmlWriter.close()

    if (statusUid != ''):
        if JythonPoolTest.getStorage().contains(statusUid):
            return UserMessage("test1", u"повтор!")
        JythonPoolTest.getStorage().add(statusUid)
    return None

if __name__ == "__main__":
    mainproc()
