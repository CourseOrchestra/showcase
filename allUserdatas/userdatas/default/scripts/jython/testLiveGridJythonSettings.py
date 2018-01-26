# coding: utf-8
'''
Created on 19.12.2011

@author: bogatov
'''
from ru.curs.showcase.core.jython import JythonProc
from ru.curs.showcase.core.jython import JythonDTO

# init vars
main = ""
add = ""
session = ""
filterContext = ""
elementId = ""


class testLiveGridJythonSettings(JythonProc):
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
        <h3>Test Grid jython metadata</h3>
        </header>
      </labels>
      <columns>
        <col id="name" />
      </columns>
      
        <sorting>
           <sort column="name" direction="DESC"/>        
        </sorting>        
      
      
      <properties flip="false" pagesize="75" totalCount="1" profile="grid.nowidth.properties"/>
   </gridsettings>'''
    res = JythonDTO(None, settings)
    return res
    

if __name__ == "__main__": 
    mainproc();
