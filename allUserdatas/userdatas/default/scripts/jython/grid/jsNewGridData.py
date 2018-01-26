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


class jsNewGridData(JythonProc):
    def getRawData(self, context, elId, scols, firstrecord, pagesize):
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
    
    
    if sortcols != None: 
        print u'dddddddddddddddddddddddddd: sortcols'
        print sortcols[0].getId()
        print sortcols[0].getSorting()
    
    
    
    data = u'''
    <records>
        <rec>
            <name>Тест1</name>
            <code>1</code>            
            <_x007e__x007e_id>1</_x007e__x007e_id>
        </rec>
        <rec>
            <name>Тест2</name>
            <code>2</code>
            <_x007e__x007e_id>2</_x007e__x007e_id>
        </rec>
        <rec>
            <name>Тест3</name>
            <code>3</code>
            <_x007e__x007e_id>3</_x007e__x007e_id>
        </rec>
        <rec>
            <name>Тест4</name>
            <code>4</code>
            <_x007e__x007e_id>4</_x007e__x007e_id>
        </rec>
        <rec>
            <name>Тест5</name>
            <code>5</code> 
            <_x007e__x007e_id>5</_x007e__x007e_id>           
        </rec>
        <rec>
            <name>Тест6</name>
            <code>6</code>            
            <_x007e__x007e_id>6</_x007e__x007e_id>
        </rec>
        <rec>
            <name>Тест7</name>
            <code>7</code>            
            <_x007e__x007e_id>7</_x007e__x007e_id>
        </rec>
        <rec>
            <name>Тест8</name>
            <code>8</code>            
            <_x007e__x007e_id>8</_x007e__x007e_id>
        </rec>
    </records>'''
    
   
#   res = JythonDTO(data, settings, UserMessage("555", u"Грид успешно построен из Jython", MessageType.INFO))

#       res = JythonDTO(data, settings, UserMessageFactory().build(555, u"Грид успешно построен из Jython"))

    print u'dddddddddddddddddddddddddd: Полное обновление'

    res = JythonDTO(data)
    return res
    

if __name__ == "__main__": 
    mainproc();
