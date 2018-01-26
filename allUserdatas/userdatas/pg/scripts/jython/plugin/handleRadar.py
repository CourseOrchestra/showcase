# coding: utf-8
'''
Created on 02.11.2011

@author: den
'''
from ru.curs.showcase.core.jython import JythonProc, JythonDTO
from ru.curs.showcase.util.xml import XMLUtils
from org.xml.sax.helpers import DefaultHandler
from ru.curs.showcase.util import TextUtils

# init vars
data = u'''<series name="тест">данные</series>'''
result = None


class myHandler(DefaultHandler):
    def startElement(self, namespaceURI, lname, qname, attrs):
        global result
        if (qname == "series") and (attrs.getIndex("name") > -1):
            result += u"{name: '%s', data1: %s, data2: %s, data3: %s}," % (attrs.getValue('name'), attrs.getValue('data1'), attrs.getValue('data2'), attrs.getValue('data3'))


class handleRadar(JythonProc):

    def postProcess(self, context, elId, adata):
        global data
        data = adata
        return mainproc()


def mainproc():
    global result
    result = u'['
    parser = XMLUtils.createSAXParser()
    stream = TextUtils.stringToStream(data)
    parser.parse(stream, myHandler())
    result += u']'
    return JythonDTO([result])

if __name__ == "__main__":
    from org.python.core import codecs
    codecs.setDefaultEncoding('utf-8')
    mainproc()
