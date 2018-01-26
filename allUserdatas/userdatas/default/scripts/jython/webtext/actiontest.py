# coding: utf-8
'''
Created on 02.11.2011

@author: den
'''
from ru.curs.showcase.core.jython import JythonProc
from ru.curs.showcase.core.jython import JythonDTO

# init vars
main = ""
add = ""
session = ""
filterContext = ""
elementId = ""


class actiontest(JythonProc):
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
    data = u'''<div>
    <button type="button" onclick="gwtWebTextFunc('${elementId}','1');">Перейти на панель 12.1 c контекстом Москва</button>
    </div>'''
    settings = u'''
<properties>
                      <event name="single_click" linkId="1">
                        <action >
                            <main_context>Москва</main_context>
                            <navigator element="1201"/>
                        </action>
                       </event>
                    </properties>
    '''
    res = JythonDTO(data, settings)
    return res

if __name__ == "__main__":
    mainproc()
