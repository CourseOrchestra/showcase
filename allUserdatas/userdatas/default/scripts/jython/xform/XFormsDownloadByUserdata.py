# coding: utf-8
'''
Created on 06.01.2012

@author: bogatov
'''
from ru.curs.showcase.core.jython import JythonProc
from ru.curs.showcase.core.jython import JythonDTO
from ru.curs.showcase.runtime import ConnectionFactory
from java.sql import Types
from java.io import ByteArrayInputStream
from ru.curs.showcase.core.jython import JythonDownloadResult
from ru.curs.showcase.core.jython import XFormDownloadAttributes
from ru.curs.showcase.core.jython import JythonErrorResult


class XFormsDownloadByUserdata(JythonProc):
    def getInputStream(self, context, attributes):
        return mainproc(attributes)


def mainproc(attributes):    
    conn = ConnectionFactory.getInstance().acquire()
    try:
        stmt = conn.prepareCall("{? = call [dbo].[xforms_download_by_userdata] (?, ?, ?, ?, ?, ?, ?, ?, ?)}")
        try:
            stmt.registerOutParameter(1, Types.INTEGER)
            stmt.setString(2, attributes.getMainContext())
            stmt.setString(3, attributes.getAddContext())
            setSQLXMLParam(stmt, 4, attributes.getFilterinfo())
            setSQLXMLParam(stmt, 5, attributes.getSessionContext())
            stmt.setString(6, attributes.getElementId())
            setSQLXMLParam(stmt, 7, attributes.getFormData())
            stmt.registerOutParameter(8, Types.VARCHAR)
            stmt.registerOutParameter(9, Types.BLOB)
            stmt.registerOutParameter(10, Types.VARCHAR)
            
            stmt.execute()
            
            errorCode = stmt.getInt(1)
            if errorCode!=0:
                error = stmt.getString(10)
                return JythonDownloadResult(JythonErrorResult(error, errorCode))
            
            fileName = stmt.getString(8)
            bt = stmt.getBytes(9)
            if bt!=None:
                return JythonDownloadResult(ByteArrayInputStream(bt),fileName)
            else:
                return JythonDownloadResult(None,fileName)
        finally:
            stmt.close()
    finally:
        ConnectionFactory.getInstance().release(conn)

def setSQLXMLParam(stmt, index, value):
    sqlxml = stmt.getConnection().createSQLXML()
    if value == None:
        value = '';
    sqlxml.setString(value)
    stmt.setSQLXML(index, sqlxml)



if __name__ == "__main__": 
    mainproc(XFormDownloadAttributes());
