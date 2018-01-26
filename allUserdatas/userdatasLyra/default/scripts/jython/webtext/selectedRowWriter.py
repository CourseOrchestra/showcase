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
    
    data = u'<div><h2>Add context=('+add+')</h2></div>'
    settings = u'''<properties>
    </properties>
    '''
    res = JythonDTO(data, settings)
    return res

if __name__ == "__main__":
    mainproc()
