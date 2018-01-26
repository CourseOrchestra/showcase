# coding: utf-8
'''
Created on 02.11.2011

@author: AleXXl
'''
try:
    from ru.curs.showcase.runtime import AppInfoSingleton
except:
    pass
from ru.curs.showcase.core.jython import JythonProc
from org.xml.sax.helpers import XMLReaderFactory
from org.xml.sax.ext import DefaultHandler2
from org.xml.sax import InputSource
from  javax.xml.stream import XMLOutputFactory
from java.io import FileInputStream, StringWriter

import simplejson as json

[main, add, session, filter, elementId] = [None, None, None, None, None]

class rulesTemplate(JythonProc):
    def getRawData(self, context, elId):
        global main, add, session, filter, elementId
        main = context.getMain()
        if context.getAdditional():
            add = json.loads(context.getAdditional())[1]
        session = context.getSession()
        if context.getFilter():
            filter = context.getFilter()
        elementId = elId
        return mainproc()

def mainproc():
    u'''Функция для склейки карточки с блоками правил'''
    try:
        rootPath = AppInfoSingleton.getAppInfo().getCurUserData().getPath() + '/xforms/'
    except:
        rootPath = 'E:/Projects/celesta/ssmmd/userscore/ssmmd/xforms/file/'

    templatePath = rootPath + add #путь к карточке, в которую необходимо вставить праила
    rulePath = rootPath + 'ruleTemplate.xml' #путь к блоку с правилами
    bindPath = rootPath + 'bindTemplate.xml' #путь к блоку с биндами для правил

    stringWriter = StringWriter()
    xmlWriter = XMLOutputFactory.newInstance().createXMLStreamWriter(stringWriter)

    parser = XMLReaderFactory.createXMLReader()
    handler = XformsAddRules(rulePath, bindPath, xmlWriter)
    parser.setContentHandler(handler)
    parser.setErrorHandler(handler)
    parser.setFeature("http://xml.org/sax/features/namespace-prefixes", True)
    parser.setProperty("http://xml.org/sax/properties/lexical-handler", handler)

    stream = FileInputStream(templatePath)
    parser.parse(InputSource(stream))
    xmlWriter.close()
    stringWriter.close()
    return unicode(stringWriter)

class XformsAddRules(DefaultHandler2):
    u'''SAX-parser для xforms, в которую необходимо вставить правила'''
    def __init__(self, rulePath, bindPath, xmlWriter):
        self.rulePath = rulePath
        self.bindPath = bindPath
        self.xmlWriter = xmlWriter
        self.parser = XMLReaderFactory.createXMLReader()
        handler = RulesWriter(self.xmlWriter)
        self.parser.setContentHandler(handler)
        self.parser.setErrorHandler(handler)
        self.parser.setFeature("http://xml.org/sax/features/namespace-prefixes", True)
        self.parser.setProperty("http://xml.org/sax/properties/lexical-handler", handler)
        self.state = 0
    def startDocument(self):
        self.xmlWriter.writeStartDocument("UTF-8", "1.0")

    def endDocument(self):
        self.xmlWriter.writeEndDocument()
        self.xmlWriter.flush()

    def startElement(self, namespaceURI, lname, qname, attrs):
        if (namespaceURI == "http://www.w3.org/2002/xforms" and lname == "submission" and self.state == 1):
            #вставка биндов для правил в mainModel
            stream = FileInputStream(self.bindPath)
            self.parser.parse(InputSource(stream))
            self.state = 2
        self.xmlWriter.writeStartElement(qname)
        for i in range(0, attrs.getLength()):
            self.xmlWriter.writeAttribute(attrs.getQName(i), attrs.getValue(i))
        if (attrs.getValue(attrs.getIndex("class")) == "rule"):
            #вставка блока с правилами в div с классом "rule"
            stream = FileInputStream(self.rulePath)
            self.parser.parse(InputSource(stream))
        if (namespaceURI == "http://www.w3.org/2002/xforms" and lname == "model" \
            and attrs.getValue(attrs.getIndex("id")) == "xformId_mainModel"):
            #вставка биндов для правил в mainModel
            self.state = 1
        
    def endElement(self, uri, lname, qname):
        if self.state==1 and uri == "http://www.w3.org/2002/xforms" and lname == "model":
            #вставка биндов для правил в mainModel
            stream = FileInputStream(self.bindPath)
            self.parser.parse(InputSource(stream))
            self.state = 0
        self.xmlWriter.writeEndElement()

    def comment(self, ch, start, length):
        self.xmlWriter.writeComment(''.join(ch[start:start + length]))

    def startPrefixMapping(self, prefix, uri):
        if prefix == "":
            self.xmlWriter.setDefaultNamespace(uri)
        else:
            self.xmlWriter.setPrefix(prefix, uri)


    def characters(self, ch, start, length):
        self.xmlWriter.writeCharacters(ch, start, length)

    def processingInstruction(self, target, data):
        self.xmlWriter.writeProcessingInstruction(target, data)

    def skippedEntity(self, name):
        self.xmlWriter.writeEntityRef(name)


class RulesWriter(DefaultHandler2):
    u'''SAX-parser для блока xforms с правилами'''
    def __init__(self, xmlWriter):
        self.xmlWriter = xmlWriter

    def startElement(self, namespaceURI, lname, qname, attrs):
        if qname != "specialTag":
            self.xmlWriter.writeStartElement(qname)

            for i in range(0, attrs.getLength()):
                self.xmlWriter.writeAttribute(attrs.getQName(i), attrs.getValue(i))

    def endElement(self, uri, lname, qname):
        if qname != "specialTag":
            self.xmlWriter.writeEndElement()

    def characters(self, ch, start, length):
        self.xmlWriter.writeCharacters(ch, start, length)

    def comment(self, ch, start, length):
        self.xmlWriter.writeComment(''.join(ch[start:start + length]))

    def startPrefixMapping(self, prefix, uri):
        if prefix == "":
            self.xmlWriter.setDefaultNamespace(uri)
        else:
            self.xmlWriter.setPrefix(prefix, uri)

    def processingInstruction(self, target, data):
        self.xmlWriter.writeProcessingInstruction(target, data)

    def skippedEntity(self, name):
        self.xmlWriter.writeEntityRef(name)

