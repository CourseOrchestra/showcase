# coding: utf-8
'''
Created on 26.07.2014

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
xmlParams = ""


class htmlEditorTinymce(JythonProc):
    def getPluginRawData(self, context, elId, params):
        global main, add, session, filterContext, elementId, xmlParams
        main = context.getMain()
        if context.getAdditional():
            add = context.getAdditional()
        session = context.getSession()
        if context.getFilter():
            filterContext = context.getFilter()
        elementId = elId
        xmlParams = params
        return mainproc()


def mainproc():
    data = u'''<div><h1>Test HTML</h1><b>bold text</b></div>'''
    settings = u'''
    <properties>
    </properties>
    '''
    res = JythonDTO(data, settings)
    return res

if __name__ == "__main__":
    mainproc()