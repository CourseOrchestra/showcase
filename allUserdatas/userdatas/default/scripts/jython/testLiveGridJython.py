# coding: utf-8
'''
Created on 19.12.2011

@author: bogatov
'''
from ru.curs.showcase.core.jython import JythonProc
from ru.curs.showcase.core.jython import JythonDTO
from ru.curs.showcase.app.api import UserMessage
from ru.curs.showcase.app.api import MessageType
from ru.curs.showcase.core import UserMessageFactory


# init vars
main = ""
add = ""
session = ""
filterContext = ""
elementId = ""
sortcols = None #объект типа java.util.List<ru.curs.gwt.datagrid.model.Column>


class testLiveGridJython(JythonProc):
    def getRawData(self, context, elId, scols, frecord, psize):
        global main, add, session, filterContext, elementId, sortcols2, firstrecord, pagesize
        
        main = context.getMain()
        if context.getAdditional():
            add = context.getAdditional()
        session = context.getSession()
        if context.getFilter():
            filterContext = context.getFilter()
        elementId = elId
        sortcols = scols
        firstrecord = frecord
        pagesize = psize
        
        
        print 'sortColumnList'    
        if sortcols != None:
            for column in sortcols:
                print 'sort columnID "%s".' % column.getId()
                print 'sort direction "%s".' % column.getSorting()        

        
        return mainproc()


def mainproc():    
    data = u'''
    <records>
        <rec>
            <name>Тест</name>
        </rec>
    </records>'''
    res = JythonDTO(data, UserMessageFactory().build(555, u"Грид(xmlds) успешно построен из Jython"))
    
    
#    print 'qqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqq'
    
    
    return res
    

if __name__ == "__main__": 
    mainproc();
