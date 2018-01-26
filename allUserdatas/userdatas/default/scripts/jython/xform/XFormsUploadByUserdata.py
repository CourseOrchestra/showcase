# coding: utf-8
'''
Created on 06.01.2012

@author: bogatov
'''
from ru.curs.showcase.core.jython import JythonProc
from ru.curs.showcase.core.jython import JythonDTO
from ru.curs.showcase.runtime import ConnectionFactory
from java.sql import Types
from ru.curs.showcase.core.jython import XFormUploadAttributes
from ru.curs.showcase.core.jython import JythonErrorResult


class XFormsUploadByUserdata(JythonProc):
    def doUpload(self, context, attributes):
        return mainproc(attributes)


def mainproc(attributes):    
    conn = ConnectionFactory.getInstance().acquire()
    try:
        stmt = conn.prepareCall("{? = call [dbo].[xforms_upload_by_userdata] (?, ?, ?, ?, ?, ?, ?, ?, ?)}")
        try:
            stmt.registerOutParameter(1, Types.INTEGER)
            stmt.setString(2, attributes.getMainContext())
            stmt.setString(3, attributes.getAddContext())
            setSQLXMLParam(stmt, 4, attributes.getFilterinfo())
            setSQLXMLParam(stmt, 5, attributes.getSessionContext())
            stmt.setString(6, attributes.getElementId())
            setSQLXMLParam(stmt, 7, attributes.getFormData())
            stmt.setString(8, attributes.getFilename())
            stmt.setBinaryStream(9, attributes.getFile());
            stmt.registerOutParameter(10, Types.VARCHAR)
            
            stmt.execute()
            
            errorCode = stmt.getInt(1)
            if errorCode!=0:
                error = stmt.getString(10)
                return JythonErrorResult(error, errorCode)            
            
            return JythonErrorResult()
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
    mainproc(XFormUploadAttributes());
