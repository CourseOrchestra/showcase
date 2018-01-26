# coding: utf-8
'''
Created on 29.03.2013

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


class selectedRowWriter(JythonProc):
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
    
    print 'ddddddddddddd22_main='+main
    print 'ddddddddddddd22_add='+add
    print 'ddddddddddddd22_filterContext='+filterContext
    print 'ddddddddddddd22_elementId='+elementId
    print 'ddddddddddddd22_session='+session
    print 'ddddddddddddd22_end'
      
    
    data = u'<div><h1>Add context=('+add+')</h1></div>'
    settings = u'''<properties>
    </properties>
    '''
    res = JythonDTO(data, settings)
    return res

if __name__ == "__main__":
    mainproc()
