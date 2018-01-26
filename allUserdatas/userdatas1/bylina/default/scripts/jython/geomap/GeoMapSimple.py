# coding: utf-8
'''
Created on 02.11.2011

@author: den
'''
from ru.curs.showcase.core.jython import JythonProc
from ru.curs.showcase.core.jython import JythonDTO
from java.lang import Class
from com.ziclix.python.sql import zxJDBC,PyConnection
from java.sql import Connection, SQLException, DriverManager
#from ru.curs.showcase.app.api import UserMessage
#from ru.curs.showcase.util.xml import XMLUtils
#from org.xml.sax.helpers import DefaultHandler
#from ru.curs.showcase.util import TextUtils

# init vars
main = ""
add = ""
session = ""
filterContext = ""
elementId = ""
pyconn = None


class GeoMapSimple(JythonProc):
    def getRawData(self, context, elId, conn):
        global main, add, session, filterContext, pyconn, elementId
        main = context.getMain()
        if context.getAdditional():
            add = context.getAdditional()
        session = context.getSession()
        if context.getFilter():
            filterContext = context.getFilter()
        elementId = elId
        pyconn = conn
        return mainproc()


def mainproc():
    cityProps = u'''
'<properties>
       
                    </properties>'
    '''
    data = [u'''SELECT * from geo1fake
    ''',
    u'''SELECT * from geo1fake
    ''']
    
    
    
    
    jdbc_url, driver = "jdbc:postgresql://172.16.1.44:5432/postgres", "org.postgresql.Driver"
    username, password = "postgres", "F708420Dx"
    pyConn = zxJDBC.connect(jdbc_url, username, password, driver)

    c = pyConn.cursor()
#a = 'master'
#c.executemany("select sha1(?)", [a])
    c.execute("select * from _map where \"featuresName\" = 'Sectors'")
    settings = u'''
<geomapsettings>
    <labels>
    </labels>
    <properties/>
    <template>
    {
    managerModule: "solution/mil01",
    engine: "esri",
    features: [
        '''

    k = ""

    allRows = c.fetchall()
    for i in allRows:
#    print i
        k = """%s            {
                type: "%s",
                center: %s,
                angle1: %s,
                angle2: %s,
                radius: %s
            },
""" % (k, i[2], i[3], i[4], i[5], i[6])

    if k <> "":
        k = k[1:len(k)-2]
        settings = '''%s{
        name: "Sectors",
        features: [
%s
        ]
        },
        ''' % (settings, k)
    
    c.execute("select * from _map where \"featuresName\" = 'Objects'")
    k = ""
    allRows = c.fetchall()
    for i in allRows:
#    print i
        k = """%s            {
                name: "%s",
                id: "%s",
                type: "%s",
                coords: %s,
                lebel: "%s",
                infoTitle: "%s",
                info: "%s"
            },
""" % (k, i[2], i[3], i[4], i[5], i[6], i[7], i[8])

    if k <> "":
        k = k[1:len(k)-2]
        settings = '''%s{
        name: "Objects",
        features: [
%s
        ]
        },
        ''' % (settings, k)


    settings = settings[1:len(settings)-10]
    settings = '''%s
    ]
    }
    </template>
</geomapsettings>
    ''' % (settings)
    
    print settings






    pyConn.close()    
    
  #  settings = u'''
   # '''
    return JythonDTO(data, settings)

if __name__ == "__main__":
    mainproc()
