# coding: utf-8
'''
Created on 02.11.2011

@author: den
'''
from ru.curs.showcase.core.jython import JythonProc
from ru.curs.showcase.core.jython import JythonDTO
from ru.curs.showcase.core import UserMessage

# init vars
main = ""
add = ""
session = ""
filterContext = ""
elementId = ""


class UpdateGrid1(JythonProc):
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
    if main == "плохой":
        return UserMessage(u"1", u"проверка на ошибку сработала")
    data = u'''<div>
    <button type="button" onclick="gwtWebTextFunc('${elementId}','testId');">Обновить нижележащие элементы</button>
    </div>'''
    settings = u'''
    <properties>
        <event name="single_click" linkId="testId">
             <action>
                 <main_context>current</main_context>
                     <datapanel type="current" tab="current">
                         <element id="grid1">
                             <add_context>
                                 add_context для действия.
                             </add_context>
                        </element>
                         <element id="xform1">
                             <add_context>
                                 add_context для действия.
                             </add_context>
                        </element>
                         <element id="livegrid1">
                             <add_context>
                                 add_context для действия.
                             </add_context>
                        </element>
                    </datapanel>
            </action>
        </event>
    </properties>
    '''
    res = JythonDTO(data, settings)
    return res

if __name__ == "__main__":
    mainproc()
