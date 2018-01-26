# coding: utf-8
'''
Created on 03.01.2012

@author: bogatov
'''
from ru.curs.showcase.core.jython import JythonProc
from ru.curs.showcase.core.jython import JythonDTO
from ru.curs.showcase.runtime import ConnectionFactory
from java.sql import Types
from ru.curs.showcase.core.sp import SPUtils
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

class gridDownloadLoad(JythonProc):
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
    conn = ConnectionFactory.getInstance().acquire()
    try:
        stmt = conn.prepareCall("{call [dbo].[grid_download_load] (?, ?, ?, ?, ?, ?, ?, ?)}")
        try:
            stmt.setString(1, main)
            stmt.setString(2, add)
            setSQLXMLParam(stmt, 3, filterContext)
            setSQLXMLParam(stmt, 4, session)
            stmt.setString(5, elementId)
            stmt.setString(6, "")
            stmt.registerOutParameter(7, Types.SQLXML)
            stmt.registerOutParameter(8, Types.VARCHAR)
            
            stmt.execute()
            
            data = SPUtils.createXmlDSForGrid(stmt.getResultSet())
            setting = stmt.getSQLXML(7).getString()
            return JythonDTO(data, setting, UserMessageFactory().build(555, u"Грид(запрос) успешно построен из Jython"))
        finally:
            stmt.close()
    finally:
        ConnectionFactory.getInstance().release(conn)
    
    res = JythonDTO(None, None, UserMessageFactory().build(555, u"Грид(запрос) успешно построен из Jython"))
    return res

def setSQLXMLParam(stmt, index, value):
    sqlxml = stmt.getConnection().createSQLXML()
    if value == None:
        value = '';
    sqlxml.setString(value)
    stmt.setSQLXML(index, sqlxml)



if __name__ == "__main__": 
    mainproc();
