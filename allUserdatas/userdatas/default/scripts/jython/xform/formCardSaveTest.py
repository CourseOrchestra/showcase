# coding: utf-8
'''
Created on 02.11.2011

@author: AleXXl
'''
from ru.curs.showcase.core.jython import JythonProc
from ru.curs.showcase.util.xml import XMLUtils
from org.xml.sax.helpers import DefaultHandler
from java.io import StringWriter, StringBufferInputStream
from  javax.xml.stream import XMLOutputFactory
from ru.curs.showcase.test.stress import JythonPoolTest
from ru.curs.showcase.app.api import UserMessage

# init vars
main = '<part instance="instance.xml"/>'
add = 'guid documents'
session = u'''<sessioncontext>
   <username>master</username>
   <sid/>
   <email/>
   <fullusername>master</fullusername>
   <phone/>
   <urlparams>
      <urlparam name="uid" value="[559F4F62-E741-4BA5-B4C5-BF01BE888047]"/>
   </urlparams>
   <userdata>default</userdata>
   <related/>
</sessioncontext>
'''

elementId = None
data = u'''
<schema xmlns="">
    <info year="" date="">
        <employees>
            <chief>Иванов Иван Иванович</chief>
        </employees>
        <tu uid="" name=""/>
        <status uid="" name=""/>
        <field uid="2B9A4BF0-0334-415D-9FE3-F57E6CA34D97" name="Госморречнадзор"/>
    </info>
    <indikators>
        <pok1_1 ftid="10" value="1" value2="1111"/>
        <pok1_2 ftid="20" value="2"/>
        <pok1_3 ftid="30" value="3"/>
        <pok1_4 ftid="40" value="4"/>
        <pok1_5 ftid="50" value="5"/>
        <pok1_6 ftid="60" value="6"/>
        <pok1_7 ftid="70" value="7"/>
        <pok2_1 ftid="80" value="8"/>
        <pok2_2 ftid="90" value="9"/>
        <pok2_3 ftid="100" value="10"/>
        <pok2_4 ftid="110" value="53"/>
        <pok2_5 ftid="120" value="123"/>
        <pok2_6 ftid="130" value="32"/>
        <pok2_7 ftid="140" value="35"/>
        <pok3_1 ftid="150" value="12"/>
        <pok3_2 ftid="160" value="253"/>
        <pok3_3 ftid="170" value="523"/>
        <pok3_4 ftid="180" value="523"/>
        <pok3_5 ftid="190" value="532"/>
        <pok3_6 ftid="200" value="532"/>
        <pok3_7 ftid="210" value="5235"/>
        <pok1_8 ftid="220" value="123"/>
        <pok2_8 ftid="230" value="12"/>
        <pok3_8 ftid="240" value="21"/>
    </indikators>
</schema>
'''
xmlWriter = None
stringEmployeesWriter = None
tuUid = ""
statusUid = ""
employees = None


class formReader(DefaultHandler):
    states = ['schema', 'info', 'status', 'employees']
    currentState = 'schema'
    employeesWriter = None

    def startDocument(self):
        xmlWriter.writeStartElement('schema')

    def endDocument(self):
        xmlWriter.writeEndElement()
        xmlWriter.writeEndDocument()
        xmlWriter.flush()

    def startElement(self, namespaceURI, lname, qname, attrs):
        global stringEmployeesWriter, statusUid

        if (self.currentState != self.states[0] or qname == 'info'):
            if (self.currentState == self.states[0] and qname == 'info'):
                self.currentState = self.states[1]

            elif (self.currentState == self.states[1] and qname == 'status'):
                self.currentState = self.states[2]
                statusUid = attrs.getValue(attrs.getIndex("uid"))

            elif (self.currentState == self.states[1] and qname == 'employees'):
                self.currentState = self.states[3]

                stringEmployeesWriter = StringWriter()
                self.employeesWriter = XMLOutputFactory.newInstance().createXMLStreamWriter(stringEmployeesWriter)
                self.employeesWriter.writeStartElement(qname)

            elif (self.currentState == self.states[3]):
                self.employeesWriter.writeStartElement(qname)
                for i in range(0, attrs.getLength()):
                    self.employeesWriter.writeAttribute(attrs.getQName(i), attrs.getValue(i))

        else:
            if (attrs.getIndex("ftid") != -1):
                for i in range(0, attrs.getLength()):
                    if (attrs.getQName(i) != 'ftid'):
                        xmlWriter.writeStartElement('record')
                        xmlWriter.writeAttribute('ftid', attrs.getValue(attrs.getIndex("ftid")))
                        xmlWriter.writeAttribute('name', attrs.getQName(i))
                        xmlWriter.writeAttribute('value', attrs.getValue(i))
                        xmlWriter.writeEndElement()

    def endElement(self, uri, lname, qname):
        if (self.currentState == self.states[1] and qname == 'info'):
            self.currentState = self.states[0]
        elif (self.currentState == self.states[2] and qname == 'status'):
            self.currentState = self.states[1]
        elif (self.currentState == self.states[3] and qname == 'employees'):
            self.currentState = self.states[1]
            self.employeesWriter.writeEndElement()
            self.employeesWriter.writeEndDocument()
            self.employeesWriter.flush()
            self.employeesWriter.close()
        elif (self.currentState == self.states[3] and qname != 'employees'):
            self.employeesWriter.writeEndElement()

    def characters(self, ch, start, length):
        if (self.currentState == self.states[3]):
            self.employeesWriter.writeCharacters(ch, start, length)


class formCardSaveTest(JythonProc):
    def save(self, context, elId, adata):
        global main, add, session, filterContext, elementId, data
        main = context.getMain()
        if context.getAdditional():
            add = context.getAdditional()
        session = context.getSession()
        if context.getFilter():
            filterContext = context.getFilter()
        elementId = elId
        data = adata
        return mainproc()


def mainproc():
    global xmlWriter, statusUid

    parser = XMLUtils.createSAXParser()
    stream = StringBufferInputStream(data.encode("utf-8"))
    stringWriter = StringWriter()
    xmlWriter = XMLOutputFactory.newInstance().createXMLStreamWriter(stringWriter)
    parser.parse(stream, formReader())
    xmlWriter.close()

    if (statusUid != ''):
        if JythonPoolTest.getStorage().contains(statusUid):
            return UserMessage("test1", u"повтор!")
        JythonPoolTest.getStorage().add(statusUid)
    return None

if __name__ == "__main__":
    mainproc()
