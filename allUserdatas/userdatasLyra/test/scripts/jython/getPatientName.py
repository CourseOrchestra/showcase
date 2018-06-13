# coding: utf-8
'''
Created on 02.11.2011

@author: den
'''
from __future__ import with_statement
from ru.curs.showcase.core.jython import JythonProc
from ru.curs.showcase.runtime import ConnectionFactory,AppInfoSingleton
from ru.curs.showcase.util.xml import XMLUtils
import xml.dom.minidom as minidom
from java.io import FileOutputStream,FileInputStream,StringWriter,StringBufferInputStream,ByteArrayInputStream
from java.lang import Class
from com.ziclix.python.sql import zxJDBC, PyConnection
from java.sql import Connection, SQLException, DriverManager
from javax.xml.stream import XMLOutputFactory,XMLStreamWriter
import datetime 

#from org.xml.sax.helpers import DefaultHandler;
#from ru.curs.showcase.util import TextUtils;

# init vars
request = '''s4o1'''

class getPatientName(JythonProc):        
    def handle(self, input):
        global request
        request = input
        return mainproc()
        
def mainproc():
    '''
    if __name__ == "__main__": 
        Class.forName("org.postgresql.Driver");
        conn = DriverManager.getConnection("jdbc:postgresql://172.16.1.154:5432/postgres","postgres","F708420Dx")
        pyConn = PyConnection(conn)
        # print 'testCon'
    else: 
        pyConn = ConnectionFactory.getPyConnection();
	'''
    pyConn = ConnectionFactory.getPyConnection()
    #pyConn = PyConnection(conn)    
    pyConn.autocommit = True 
    result= None
    try:
        info = pyConn.cursor()
        try:
            #queryStr= u'''select name||' '||patronymic||' '||surname from "ssmmd"."patients" where id = 13'''
            queryStr= u'''select name||' '||patronymic||' '||surname from "ssmmd"."patients" where id=%d'''%int(request)
            info.executemany(queryStr)
            result=info.fetchall()          
           
        finally:
            info.close()
    finally:  
        pass 
    
    if (len(result)==0):
        return unicode(None)
    return unicode(result[0][0])


     
  
if __name__ == "__main__":       
    mainproc()