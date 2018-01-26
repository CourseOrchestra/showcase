# coding: utf-8

import array
import base64

from java.io import StringWriter
from java.lang import String
from java.text import SimpleDateFormat
from javax.xml.stream import XMLOutputFactory
from org.xml.sax.helpers import XMLReaderFactory
from org.xml.sax.ext import DefaultHandler2
from org.xml.sax import InputSource
import os
from java.io import File, FileInputStream, FileOutputStream
try:
    from ru.curs.showcase.core.jython import JythonDownloadResult
except:
    from ru.curs.celesta.showcase import JythonDownloadResult

from ru.curs.celesta import CelestaException

class UploadXMLHandler(DefaultHandler2):
    """Класс SAX-парсера, производящий разбор xml и вставку данных в таблицу"""

    parentTag = None
    currentCell = None
    flag = 0
    currentString = u''
    currentEncoding = u"utf8"
    def __init__(self, tableInstance, action):
        self.tableInstance = tableInstance
        # возможность настраивать, нужно ли обновлять записи и вставлять новые
        def actionUI(ins):
            if not ins.tryInsert():
                ins.update()
        def actionU(ins):
            ins.tryUpdate()
        def actionI(ins):
            ins.tryInsert()
        # определяем, какую функцию нам нужно использовать
        self.funcAction = locals()['action%s' % action.upper()]

    def startElement(self, namespaceURI, lname, qname, attrs):

        if self.parentTag == 'field':
            if self.flag == 0:
                self.stringWriter = StringWriter()
                self.xmlWriter = XMLOutputFactory.newInstance().createXMLStreamWriter(self.stringWriter, "UTF-8")
            self.flag += 1
            self.xmlWriter.writeStartElement(qname)
            for i in range(0, attrs.getLength()):
                self.xmlWriter.writeAttribute(attrs.getQName(i), attrs.getValue(i))
        elif qname == 'table' and self.flag == 0:
            if self.parentTag is not None:
                raise CelestaException(u"Неверный формат файла")

            self.parentTag = qname
            if not attrs.getValue('name'):
                raise CelestaException(u"Атрибут 'name' отсутствует в теге 'table'")
            elif attrs.getValue('name') != self.tableInstance.meta().getName():
                raise CelestaException(u"Имя таблицы %s не соответствует значению атрибута 'name'" % self.tableInstance.meta().getName())
        elif qname == 'row' and self.flag == 0:
            if self.parentTag != 'table':
                raise CelestaException(u"Неверный формат файла")
            self.parentTag = qname
        elif qname == 'field' and self.flag == 0:
            if self.parentTag != 'row':
                raise CelestaException(u"Неверный формат файла")
            self.currentEncoding = attrs.getValue('encoding') or u"utf8"
            self.currentCell = attrs.getValue('name')
            self.parentTag = qname
            self.currentString = u''
        else:
            raise CelestaException(u"Неверный формат файла")

    def endElement(self, uri, lname, qname):
        if qname == 'table' and self.flag == 0:
            self.parentTag = None
        elif qname == 'row' and self.flag == 0:
            # обновляем или вставляем записи
            self.funcAction(self.tableInstance)
            self.tableInstance.clear()
            self.parentTag = 'table'
        elif qname == 'field' and self.flag == 0:
            # Вставка данных в поле таблицы, отдельно рассмотрен случай если данные в формате XML
            if hasattr(self, 'stringWriter') and self.stringWriter:
                self.xmlWriter.close()
                # Вставка данных в поле типа blob
                if self.tableInstance.meta().columns[self.currentCell].getCelestaType() == 'BLOB':
                    getattr(self.tableInstance, "calc%s" % self.currentCell)()
                    blobField = self.tableInstance.__dict__[self.currentCell].getOutStream()
                    blobField.write(self.stringWriter.strip())
                else:
                    self.tableInstance.__setattr__(self.currentCell, self.stringWriter.toString())
                self.stringWriter.flush()
                self.stringWriter = None
                self.xmlWriter = None
            else:
                # проверка на None
                if self.currentString.strip() != 'None':
                    self.currentString = self.currentString.strip()
                    # Вставка данных в поле типа blob
                    if self.tableInstance.meta().columns[self.currentCell].getCelestaType() == 'BLOB':
                        getattr(self.tableInstance, "calc%s" % self.currentCell)()
                        blobField = self.tableInstance.__dict__[self.currentCell].getOutStream()
                        if self.currentEncoding == u"utf8":
                            blobField.write(self.currentString)
                        elif self.currentEncoding == u"base64":
                            blobField.write(base64.b64decode(self.currentString))
                        else:
                            raise CelestaException(u"Неверная кодировка")
                    elif self.tableInstance.meta().columns[self.currentCell].getCelestaType() == 'DATETIME':
                        sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")
                        self.tableInstance.__setattr__(self.currentCell, sdf.parse(self.currentString))
                    elif self.tableInstance.meta().columns[self.currentCell].getCelestaType() == 'BIT':
                        self.tableInstance.__setattr__(self.currentCell, self.currentString.lower() == "true")
                    else:
                        if self.currentCell == 'prefix':
                            pass
                        self.tableInstance.__setattr__(self.currentCell, self.currentString)
            self.parentTag = 'row'
            self.currentCell = None
            self.currentString = None
        elif self.parentTag == 'field':
            self.xmlWriter.writeEndElement()
            self.flag -= 1

    def characters(self, ch, start, length):
        if self.parentTag == 'field' and self.flag > 0:
            self.xmlWriter.writeCharacters(ch, start, length)
        elif self.currentCell:
            self.currentString += unicode(String(ch[start:start + length]))

    def comment(self, ch, start, length):
        if self.parentTag == 'field' and self.flag > 0:
            self.xmlWriter.writeComment(ch.tostring()[start:start + length])

    def startPrefixMapping(self, prefix, uri):
        if self.parentTag == 'field' and self.flag > 0:
            if prefix == "":
                self.xmlWriter.setDefaultNamespace(uri)
            else:
                self.xmlWriter.setPrefix(prefix, uri)

    def processingInstruction(self, target, data):
        if self.parentTag == 'field' and self.flag > 0:
            self.xmlWriter.writeProcessingInstruction(target, data);

    def skippedEntity(self, name):
        if self.parentTag == 'field' and self.flag > 0:
            self.xmlWriter.writeEntityRef(name);

'''
 Класс реализует обмен данными между таблицами баз данных и xml файлами.
 Блоб данные хранятся в xml файле в формате base64
 (для передачи данных из xml в БД возможен вариант хранения в виде просто текста в utf8)

 dataStream - входящий или исходящий поток с xml данными
 tableInstance - Экземпляр курсора таблицы
'''
class DataBaseXMLExchange():

    def __init__(self, dataStream, tableInstance):
        self.dataStream = dataStream
        self.tableInstance = tableInstance

    def uploadXML(self, action="ui"):
        '''
    функция реализует загрузку данных из xml в базу данных
'''
        parser = XMLReaderFactory.createXMLReader();
        handler = UploadXMLHandler(self.tableInstance, action)
        parser.setContentHandler(handler)
        parser.setErrorHandler(handler)
        parser.setFeature("http://xml.org/sax/features/namespace-prefixes", True)
        parser.setProperty("http://xml.org/sax/properties/lexical-handler", handler)
        parser.parse(InputSource(self.dataStream))

    def blobHandler(self, field, xmlWriter):
        '''
    функция реализует запись данных закодированных в base64 вo writer
'''
        getattr(self.tableInstance, u"calc%s" % field)()
        blobField = self.tableInstance.__dict__[unicode(field)].getInStream()
        if blobField is not None:
            byteArray = [-1, -1, -1]
            counter = 0
            while True:
                byteArray[0] = blobField.read()
                byteArray[1] = blobField.read()
                byteArray[2] = blobField.read()
                if byteArray[0] == -1:
                    break
                elif byteArray[1] == -1:
                    xmlWriter.writeCharacters(base64.b64encode(array.array('B', byteArray[0:1]).tostring()))
                    break
                elif byteArray[2] == -1:
                    xmlWriter.writeCharacters(base64.b64encode(array.array('B', byteArray[0:2]).tostring()))
                    break
                else:
                    counter += 1
                    xmlWriter.writeCharacters(base64.b64encode(array.array('B', byteArray).tostring()))
                    if counter % 20 == 0:
                        xmlWriter.writeCharacters("\n\t\t\t")

    def downloadXML(self):
        '''
    функция реализует выгрузку данных из базы данных в файл xml
'''
        xmlWriter = XMLOutputFactory.newInstance().createXMLStreamWriter(self.dataStream, "UTF-8")

        xmlWriter.writeStartDocument("UTF-8", "1.0")
        xmlWriter.writeCharacters("\n")
        xmlWriter.writeStartElement("table")
        xmlWriter.writeAttribute('name', self.tableInstance.meta().getName())
        xmlWriter.writeCharacters("\n")
        while self.tableInstance.nextInSet():
            xmlWriter.writeCharacters("\t")
            xmlWriter.writeStartElement("row")
            xmlWriter.writeCharacters("\n")
            for field in self.tableInstance.meta().getColumns():
                xmlWriter.writeCharacters("\t\t")
                xmlWriter.writeStartElement("field")
                xmlWriter.writeAttribute('name', field)
                if self.tableInstance.meta().columns[field].getCelestaType() == 'BLOB':
                    xmlWriter.writeAttribute('encoding', 'base64')
                    self.blobHandler(field, xmlWriter)
                else:
                    xmlWriter.writeCharacters("\n\t\t\t")
                    xmlWriter.writeCharacters(unicode(self.tableInstance.__getattribute__(field)))
                xmlWriter.writeCharacters("\n")
                xmlWriter.writeCharacters("\t\t")
                xmlWriter.writeEndElement()
                xmlWriter.writeCharacters("\n")
            xmlWriter.writeCharacters("\t")
            xmlWriter.writeEndElement()
            xmlWriter.writeCharacters("\n")
        xmlWriter.writeEndElement()
        xmlWriter.writeEndDocument()
        xmlWriter.flush()

def tableDownload(cursorInstance, fileName):
    filePath = os.path.join(os.path.dirname(os.path.abspath(__file__)), 'resources', fileName + '.xml')
    dataStream = FileOutputStream(filePath)
    exchange = DataBaseXMLExchange(dataStream, cursorInstance)
    exchange.downloadXML()
    dataStream.close()
    report = File(filePath)
    return JythonDownloadResult(FileInputStream(report), fileName + '.xml')
