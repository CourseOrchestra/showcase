# coding: utf-8
'''
Created on 02.11.2011

@author: den
'''
from ru.curs.showcase.core.jython import JythonProc
from ru.curs.showcase.util.xml import XMLUtils
from org.xml.sax.helpers import DefaultHandler
from ru.curs.showcase.util import TextUtils


class myHandler(DefaultHandler):
    def startElement(self, namespaceURI, lname, qname, attrs):
        if (qname == "xml") and (attrs.getIndex("wise") > -1):
            print "works!"


class SAXJythonProc(JythonProc):
    def execute(self, context):
        parser = XMLUtils.createSAXParser()
        stream = TextUtils.stringToStream(context.getAdditional())
        parser.parse(stream, myHandler())
