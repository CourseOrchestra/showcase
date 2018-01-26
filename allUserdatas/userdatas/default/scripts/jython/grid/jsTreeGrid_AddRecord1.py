# coding: utf-8
from ru.curs.showcase.core.jython import JythonProc
from ru.curs.showcase.app.api import UserMessage
from ru.curs.showcase.app.api import MessageType
from ru.curs.showcase.core import UserMessageFactory
from ru.curs.showcase.core.jython import JythonDTO
from ru.curs.showcase.app.api.grid import GridAddRecordResult 


# init vars
main = ""
add = ""
session = ""
filterContext = ""
elementId = ""
addRecordData = ""



class jsTreeGrid_AddRecord1(JythonProc):
    def gridAddRecord(self, context, elId, addRecordData1):
        global main, add, session, filterContext, elementId, addRecordData 
        main = context.getMain()
        if context.getAdditional():
            add = context.getAdditional()
        session = context.getSession()
        if context.getFilter():
            filterContext = context.getFilter()
        elementId = elId
        
        addRecordData = addRecordData1
        
        return mainproc()


def mainproc():
    
#    print u"dddddddddddddddddddddddddd: " + addRecordData
    
    
#    print u"ffffffffffffff: " + session
    
    

    res = GridAddRecordResult(UserMessageFactory().build(555, u"Запись успешно добавлена из Jython"))
    return res
    

if __name__ == "__main__": 
    mainproc();
