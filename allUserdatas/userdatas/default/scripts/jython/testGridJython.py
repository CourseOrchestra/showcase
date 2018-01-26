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
sortcols = None #объект типа java.util.List<ru.curs.gwt.datagrid.model.Column>


class testGridJython(JythonProc):
    def getRawData(self, context, elId, scols):
        global main, add, session, filterContext, elementId, sortcols
        main = context.getMain()
        if context.getAdditional():
            add = context.getAdditional()
        session = context.getSession()
        if context.getFilter():
            filterContext = context.getFilter()
        elementId = elId
        sortcols = scols
        return mainproc()


def mainproc():
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
    

    
#   res = JythonDTO(data, settings, UserMessage("555", u"Грид успешно построен из Jython", MessageType.INFO))

    res = JythonDTO(data, settings, UserMessageFactory().build(555, u"Грид успешно построен из Jython"))
    return res
    

if __name__ == "__main__": 
    mainproc();
