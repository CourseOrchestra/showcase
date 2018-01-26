# coding: utf-8
'''
Created on 03.01.2012

@author: bogatov
'''
from ru.curs.showcase.core.jython import JythonProc
from ru.curs.showcase.core.jython import JythonDTO
from ru.curs.showcase.runtime import ConnectionFactory
from java.sql import Types
from java.io import ByteArrayInputStream
from ru.curs.showcase.core.jython import JythonDownloadResult
from ru.curs.showcase.core.jython import GridDownloadAttributes


class gridDownload2(JythonProc):
    def getInputStream(self, context, attributes):
        return mainproc(attributes)


def mainproc(attributes):    
    conn = ConnectionFactory.getInstance().acquire()
    try:
        stmt = conn.prepareCall("{call [dbo].[grid_download2] (?, ?, ?, ?, ?, ?, ?, ?, ?)}")
        try:
            stmt.setString(1, attributes.getMainContext())
            stmt.setString(2, attributes.getAddContext())
            setSQLXMLParam(stmt, 3, attributes.getFilterinfo())
            setSQLXMLParam(stmt, 4, attributes.getSessionContext())
            stmt.setString(5, attributes.getElementId())
            stmt.setString(6, attributes.getRecordId())
            stmt.registerOutParameter(7, Types.VARCHAR)
            stmt.registerOutParameter(8, Types.BLOB)
            stmt.registerOutParameter(9, Types.VARCHAR)
            
            stmt.execute()
            
            fileName = stmt.getString(7)
            bt = stmt.getBytes(8)
            if bt!=None:
                return JythonDownloadResult(ByteArrayInputStream(bt),fileName)
            else:
                return JythonDownloadResult(None,fileName)
        finally:
            stmt.close()
    finally:
        ConnectionFactory.getInstance().release(conn)
    
    res = JythonDTO(None, None)
    return res

def setSQLXMLParam(stmt, index, value):
    sqlxml = stmt.getConnection().createSQLXML()
    if value == None:
        value = '';
    sqlxml.setString(value)
    stmt.setSQLXML(index, sqlxml)



if __name__ == "__main__": 
    mainproc(GridDownloadAttributes());
