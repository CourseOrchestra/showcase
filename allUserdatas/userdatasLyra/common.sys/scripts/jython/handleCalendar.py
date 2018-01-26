# coding: utf-8
from ru.curs.showcase.core.jython import JythonProc, JythonDTO
from ru.curs.showcase.util.xml import XMLUtils
from org.xml.sax.helpers import DefaultHandler
from ru.curs.showcase.util import TextUtils

# init vars
data = None
resultMetadata = None
resultData = None


class myHandler(DefaultHandler):
    def startElement(self, namespaceURI, lname, qname, attrs):
        global resultMetadata
        global resultData
        if (qname == "metadata"):
            resultMetadata = u"{date: %s, minHours: %s, maxHours: %s, dateInterval: '%s', style: '%s', editable: %s, toolbarVisible: %s}" % (attrs.getValue('date'), attrs.getValue('minHours'), attrs.getValue('maxHours'), attrs.getValue('dateInterval'), attrs.getValue('style'), attrs.getValue('editable'), attrs.getValue('toolbarVisible'))
        if (qname == "event"):
            resultData += u"{id: %s, summary: %s, startTime: %s, endTime: %s, allDay: '%s'}," % (attrs.getValue('id'), attrs.getValue('summary'), attrs.getValue('startTime'), attrs.getValue('endTime'), attrs.getValue('allDay'))

class handleCalendar(JythonProc):

    def postProcess(self, context, elId, adata):
        global data
        data = adata
        return mainproc()

def mainproc():
    global resultMetadata
    global resultData
    resultData = u'['
    parser = XMLUtils.createSAXParser()
    stream = TextUtils.stringToStream(data)
    parser.parse(stream, myHandler())
    resultData += u']'
    result = u"{metadata: "+resultMetadata+u", data: "+resultData+u"}"  
    return JythonDTO([result])

if __name__ == "__main__":
    from org.python.core import codecs
    codecs.setDefaultEncoding('utf-8')
    mainproc()
