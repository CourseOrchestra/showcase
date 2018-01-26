# coding: utf-8
'''
Created on 15.02.2013

@author: bogatov
'''
from ru.curs.showcase.core.jython import JythonProc
from ru.curs.showcase.core.jython import JythonDTO
import xml.etree.ElementTree as ET
from ru.curs.showcase.app.api import UserMessage
from ru.curs.showcase.app.api import MessageType
from ru.curs.showcase.core import UserMessageFactory
from net.sf.saxon.functions import Nilled_1


# init vars
main = ""
add = ""
session = ""
filterContext = ""


class extJsTreeGetData2(JythonProc):
    def getTreeSelectorData(self, context, attr):
        global main, add, session, filterContext, elementId
        main = context.getMain()
        if context.getAdditional():
            add = context.getAdditional()
        session = context.getSession()
        if context.getFilter():
            filterContext = context.getFilter()
            
        return mainproc(attr)


def mainproc(attributes):
    parentId=''
    curValue=''
    
    if attributes!=None:
        
        pId = attributes.getParentId()
        if pId!=None:
            parentId=pId+'.'
        pCurValue = attributes.getCurValue()
        if pCurValue!=None and pCurValue!='':
            curValue=' ['+pCurValue+']'

        #attributes.getParams()
        #attributes.isStartsWith()
        
        #print "dddddddddddddd"    
        #print curValue
          

    data = None
    
    data = u'''
    <items>
    </items>'''
    
    data = u'''
    <items>
        <item/>
    </items>'''
    
    data = u'''
    <items>
        <item id="'''+parentId+'''1" name="Название1 записи '''+parentId+'''1'''+curValue+'''" column1="Значение1" column2="solutions/default/resources/imagesingrid/test.jpg"  leaf="false" checked="true"                      />
        <item id="'''+parentId+'''2" name="Название2 записи '''+parentId+'''2'''+curValue+'''" column1="Значение2" column2="solutions/default/resources/imagesingrid/test.jpg"  leaf="false"                  hasChildren="true"  />
        <item id="'''+parentId+'''3" name="Название3 записи '''+parentId+'''3'''+curValue+'''" column1="Значение3" column2="solutions/default/resources/imagesingrid/test.jpg"                                hasChildren="false" />        
        <item id="'''+parentId+'''4" name="Название4 записи '''+parentId+'''4'''+curValue+'''" column1="Значение4" column2="solutions/default/resources/imagesingrid/test.jpg"               checked="true"   hasChildren="true"  />        
    </items>'''




        
    
    #res = JythonDTO(data, UserMessageFactory().build(555, u"Триселектор успешно построен из Jython"))
    res = JythonDTO(data)    
    return res

if __name__ == "__main__":
    mainproc()
    
    
    
    
    
    
    
    
    
    
    
    
    
    