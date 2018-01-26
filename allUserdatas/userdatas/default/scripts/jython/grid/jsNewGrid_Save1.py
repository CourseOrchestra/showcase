# coding: utf-8
from ru.curs.showcase.core.jython import JythonProc
from ru.curs.showcase.app.api import UserMessage
from ru.curs.showcase.app.api import MessageType
from ru.curs.showcase.core import UserMessageFactory
from ru.curs.showcase.core.jython import JythonDTO
from ru.curs.showcase.app.api.grid import GridSaveResult 


# init vars
main = ""
add = ""
session = ""
filterContext = ""
elementId = ""
editorData = ""



class jsNewGrid_Save1(JythonProc):
    def gridSaveData(self, context, elId, saveData):
        global main, add, session, filterContext, elementId, editorData 
        main = context.getMain()
        if context.getAdditional():
            add = context.getAdditional()
        session = context.getSession()
        if context.getFilter():
            filterContext = context.getFilter()
        elementId = elId
        
        editorData = saveData
        
        return mainproc()


def mainproc():
    
    print u"dddddddddddddddddddddddddd1: " + session    
    print u"dddddddddddddddddddddddddd2: " + editorData

    res = GridSaveResult(UserMessageFactory().build(555, u"Данные успешно сохранены из Jython"))
    res.setRefreshAfterSave(1);
    return res
    

if __name__ == "__main__": 
    mainproc();
