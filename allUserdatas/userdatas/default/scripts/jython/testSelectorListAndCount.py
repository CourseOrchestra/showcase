# coding: utf-8
'''
Created on 27.12.2012

@author: bogatov
'''
from ru.curs.showcase.core.jython import JythonProc
from ru.curs.showcase.core.selector import ResultSelectorData
from ru.curs.showcase.core.jython import DataSelectorAttributes
from ru.beta2.extra.gwt.ui.selector.api import DataRecord
from jarray import array
from ru.curs.showcase.runtime import ConnectionFactory
from java.sql import Types
from java.util import ArrayList 
from java.lang import System 


class testSelectorListAndCount(JythonProc):
    def getSelectorData(self, context, attributes):
        return mainproc(attributes)


def mainproc(attributes):
    conn = ConnectionFactory.getInstance().acquire();
    try:
         stmt = conn.prepareCall("{call [dbo].[regions_list_and_count](?,?,?,?,?,?,?,?,?,?)}")
         try:
             stmt.setString(1, attributes.getMainContext())
             stmt.setString(2, attributes.getAddContext())
             setSQLXMLParam(stmt, 3, attributes.getFilterinfo())
             setSQLXMLParam(stmt, 4, attributes.getSessionContext())
             stmt.setString(5, attributes.getParams())
             stmt.setString(6, attributes.getCurValue())
             stmt.setBoolean(7, attributes.getStartsWith())
             stmt.setInt(8, attributes.getFirstRecord())
             stmt.setInt(9, attributes.getRecordCount())
             stmt.registerOutParameter(10, Types.INTEGER)
             
             stmt.execute()
                                      
             records = getDataList(stmt.getResultSet())
             count = stmt.getInt(10)
             return ResultSelectorData(records, count)
         finally:
             stmt.close()
    finally:
        ConnectionFactory.getInstance().release(conn)        
        
    return ResultSelectorData(None, 0)

def setSQLXMLParam(stmt, index, value):
    sqlxml = stmt.getConnection().createSQLXML()
    if value == None:
        value = '';
    sqlxml.setString(value)
    stmt.setSQLXML(index, sqlxml)

def getDataList(rs):
    recordList = ArrayList()
    if rs != None:
        metaData = rs.getMetaData()
        while rs.next():
            rec = DataRecord()
            recordList.add(rec)
            for i in range(1,metaData.getColumnCount()):
                columnName = metaData.getColumnName(i)
                if columnName.upper()=='ID':
                    rec.setId(rs.getString(i))
                elif columnName.upper()=='NAME':
                    rec.setName(rs.getString(i))
                else:    
                    rec.addParameter(columnName, rs.getString(i))
    return recordList

if __name__ == "__main__": 
    mainproc(DataSelectorAttributes());
