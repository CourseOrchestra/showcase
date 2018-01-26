# coding: utf-8
'''
Created on 19.12.2011

@author: bogatov
'''
from ru.curs.showcase.core.jython import JythonProc
from ru.curs.showcase.app.api import UserMessage
from ru.curs.showcase.app.api import MessageType
from ru.curs.showcase.core import UserMessageFactory
from ru.curs.showcase.core.jython import JythonDTO


# init vars
main = ""
add = ""
session = ""
filterContext = ""
elementId = ""


class jsNewGridMeta(JythonProc):
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


    settings = u'''
    <gridsettings>
       <labels>
        <header>
        <h3>Edit Grid jython</h3>
        </header>
      </labels>
      <columns>
        <col id="name" />
        <col id="code" />
      </columns>
      <properties  gridWidth="1200px" gridHeight="500" pagesize="50" totalCount="8" />
   </gridsettings>'''
    

    
#   res = JythonDTO(data, settings, UserMessage("555", u"Грид успешно построен из Jython", MessageType.INFO))

#       res = JythonDTO(data, settings, UserMessageFactory().build(555, u"Грид успешно построен из Jython"))

    print u'dddddddddddddddddddddddddd: Полное обновление'

    res = JythonDTO("", settings)
    return res
    

if __name__ == "__main__": 
    mainproc();






