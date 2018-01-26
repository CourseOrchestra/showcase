# coding: utf-8
'''
Created on 02.11.2011

@author: den
'''
from ru.curs.showcase.core.jython import JythonProc
from ru.curs.showcase.core.jython import JythonDTO
from ru.curs.showcase.app.api import UserMessage
from ru.curs.showcase.app.api import MessageType
from ru.curs.showcase.core import UserMessageFactory


#from ru.curs.showcase.util.xml import XMLUtils
#from org.xml.sax.helpers import DefaultHandler
#from ru.curs.showcase.util import TextUtils

# init vars
main = ""
add = ""
session = ""
filterContext = ""
elementId = ""


class GetJythonProc(JythonProc):
    def getRawData(self, context, elId):
        global main, add, session, filterContext, elementId
        main = context.getMain()
        if context.getAdditional():
            add = context.getAdditional()
        session = context.getSession()
        if context.getFilter():
            filterContext = context.getFilter()
        elementId = elId
        return mainproc()


def mainproc():
    data = u'''<schema xmlns="">
                    <info>
                        <name>Денис</name>
                        <growth>188</growth>
                        <eyescolour>Зеленый</eyescolour>
                        <music>Инструментальная</music>
                        <comment>no comments</comment>
                    </info>
                </schema>'''
    settings = u'''<properties>
                        <action >
                            <main_context>current</main_context>
                                 <datapanel type="current" tab="current">
                                <element id="d2">
                                    <add_context>я оригинальный</add_context>
                                </element>
                            </datapanel>
                            <server>
                                <activity id="srv01" name="sc_init_debug_console_adapter"/>
                            </server>
                        </action>
                        <event name="single_click" linkId="1">
                        <action >
                            <main_context>current</main_context>
                                 <datapanel type="current" tab="current">
                                <element id="d2">
                                    <add_context>я оригинальный</add_context>
                                </element>
                            </datapanel>
                            <server>
                                <activity id="srv01" name="sc_init_debug_console_adapter"/>
                            </server>
                        </action>
                       </event>
                    </properties>'''
    res = JythonDTO(data, settings, UserMessageFactory().build(555, u"xforms успешно построен из Jython"))
    return res

if __name__ == "__main__":
    mainproc()
