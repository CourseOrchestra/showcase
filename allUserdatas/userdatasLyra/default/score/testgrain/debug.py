# coding: utf-8

import ru.curs.celesta.Celesta as Celesta
import ru.curs.celesta.ConnectionPool as ConnectionPool
import ru.curs.celesta.CallContext as CallContext
import ru.curs.celesta.SessionContext as SessionContext
import sys
import lyra.lyraplayer as lyraplayer
from java.io import FileOutputStream

#1. При запуске из Eclipse надо обеспечить, чтобы первый вызов, 
# инициализирующий Celesta, был бы именно getDebugInstane(), а не getInstance()

Celesta.getDebugInstance()
conn = ConnectionPool.get()
sesContext = SessionContext('admin', 'master')
context = CallContext(conn, sesContext)

#2. Этот блок должен идти до выражений import, импортирующих отлаживаемые гранулы!!
sys.modules['initcontext'] = lambda: context

from ru.curs.celesta.showcase.utils import XMLJSONConverter
import testgrain
print unicode('Aaa').lower()


def proc1(context):
    #result = lyraplayer.getInstance(context, main='''{"sessioncontext":{"sid":"admin","userdata":"default","phone":"","username":"admin","fullusername":"admin","urlparams":{"urlparam":{"@name":"patientId","@value":["56"]}},"email":"","login":"admin","sessionid":"06635C092D9884D757C65CC9D21DCD10","related":{"xformsContext":{"formData":{"schema":{"data":{"@visitDate":"2015-05-22T17:49:04"},"hypotheses":{"@block":"0","hypothesis":{"@name":"\u0422\u0435\u0441\u0442\u043e\u0432\u0430\u044f \u0440\u0430\u0437\u0440\u0430\u0431\u043e\u0442\u043a\u0430 \u0441 \u0442\u0430\u0431\u043b\u0438\u0446\u0430\u043c\u0438","@id":"11"}},"@xmlns":""}},"partialUpdate":"false","@id":"inspectionCard"}},"ip":"127.0.0.1"}}''')
    #result = lyraplayer.getInstance(context, main='testgrain.testform.TestForm')
    result = lyraplayer.getFormInstance(context, 'testgrain.testform.TestForm')
    fos = FileOutputStream('c:/temp/debug.log')
    try:
        print result.findRec()
        
        print result.move('=', '''<schema recversion="1" formId="testgrain.testform.TestForm">
    <ff1 type="INT" null="false" local="true">5</ff1>
    <ff2 type="INT" null="false" local="true">0</ff2>
    <id type="INT" null="false" local="false">2</id>
    <attrVarchar type="VARCHAR" null="false" local="false">e</attrVarchar>
    <attrInt type="INT" null="false" local="false">11</attrInt>
    <f1 type="BIT" null="false" local="false">true</f1>
    <f2 type="BIT" null="false" local="false">true</f2>
    <f4 type="REAL" null="false" local="false">4.0</f4>
    <f5 type="REAL" null="false" local="false">4.0</f5>
    <f6 type="VARCHAR" null="false" local="false">1</f6>
    <f7 type="VARCHAR" null="true" local="false"/>
    <f8 type="DATETIME" null="false" local="false">2015-11-04T19:25:54</f8>
    <f9 type="DATETIME" null="false" local="false">2015-11-20T19:25:56</f9>
</schema>
''')
        #result.serialize(fos)
    finally:
        fos.close()
    print 'done'
#    result = getTemplate(context, session='''{"sessioncontext":{"sid":"admin","userdata":"default","phone":"","username":"admin","fullusername":"admin","urlparams":{"urlparam":{"@name":"patientId","@value":["56"]}},"email":"","login":"admin","sessionid":"06635C092D9884D757C65CC9D21DCD10","related":{"xformsContext":{"formData":{"schema":{"data":{"@visitDate":"2015-05-22T17:49:04"},"hypotheses":{"@block":"0","hypothesis":{"@name":"\u0422\u0435\u0441\u0442\u043e\u0432\u0430\u044f \u0440\u0430\u0437\u0440\u0430\u0431\u043e\u0442\u043a\u0430 \u0441 \u0442\u0430\u0431\u043b\u0438\u0446\u0430\u043c\u0438","@id":"11"}},"@xmlns":""}},"partialUpdate":"false","@id":"inspectionCard"}},"ip":"127.0.0.1"}}''')
#    print result.data
#    result = submissionNext(context, data=XMLJSONConverter.xmlToJson(u'''<data recversion="11"><id type="INT" null="false">7</id><attrVarchar type="VARCHAR" null="false">7</attrVarchar><attrInt type="INT" null="false">7</attrInt><f1 type="BIT" null="false">true</f1><f2 type="BIT" null="false">true</f2><f4 type="REAL" null="false">7.0</f4><f5 type="REAL" null="false">7.0</f5><f6 type="TEXT" null="false">ьиь</f6><f7 type="VARCHAR" null="false">7</f7><f8 type="DATETIME" null="false">2015-06-04T13:01:06</f8><f9 type="DATETIME" null="false">2015-06-12T13:01:09</f9></data>
#'''))
#    print result
    
try:
    proc1(context)
except:
    conn.rollback()
    raise
finally:
    #ВСЕГДА ЗАКРЫВАЕМ КУРСОРЫ!!
    context.closeCursors()
    #ВСЕГДА ВОЗВРАЩАЕМ СОЕДИНЕНИЕ В ПУЛ!!
    ConnectionPool.putBack(conn)

