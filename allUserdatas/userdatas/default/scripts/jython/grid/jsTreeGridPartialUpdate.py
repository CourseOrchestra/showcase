# coding: utf-8

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


class jsTreeGridPartialUpdate (JythonProc):
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
            <name>Тест1_Частичное обновление</name>
            <code>1</code>            
            <_x007e__x007e_id>1</_x007e__x007e_id>
        </rec>
        <rec>
            <name>Тест4_Частичное обновление</name>
            <code>4</code>
            <_x007e__x007e_id>4</_x007e__x007e_id>
        </rec>
    </records>'''
    
    settings = None
    

    
#   res = JythonDTO(data, settings, UserMessage("555", u"Грид успешно построен из Jython", MessageType.INFO))

#       res = JythonDTO(data, settings, UserMessageFactory().build(555, u"Грид успешно построен из Jython"))

    print u'dddddddddddddddddddddddddd: Частичное обновление'

    res = JythonDTO(data, settings)
    return res
    

if __name__ == "__main__": 
    mainproc();
