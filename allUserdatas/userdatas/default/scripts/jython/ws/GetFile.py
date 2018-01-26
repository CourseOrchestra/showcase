# coding: utf-8
'''
Created on 02.11.2011

@author: den
'''
from ru.curs.showcase.core.jython import JythonProc
from ru.curs.showcase.util.xml import XMLUtils
from ru.curs.showcase.runtime import AppInfoSingleton
from ru.curs.showcase.runtime import ConnectionFactory
#from ru.curs.showcase.core import UserMessage
import re

# init vars
request = ""


class GetFile(JythonProc):
    def handle(self, requestStr):
        global request
        request = requestStr
        return mainproc()


def mainproc():
    pyConn = ConnectionFactory.getPyConnection()
    try:
        cur = pyConn.cursor()
        cur.execute("select top 10000 name from geo3")
        data = cur.fetchone()[0]
        print type(data), data
    finally:
        cur.close()
        pyConn.close()

    requestDoc = XMLUtils.stringToDocument(request)
    if requestDoc.getDocumentElement().getNodeName() == "command":
        commandNode = requestDoc.getDocumentElement()
    else:
        commandNode = requestDoc.getElementsByTagName("command").item(0)

    commandName = commandNode.getAttributes().getNamedItem("type").getNodeValue()
    if commandName == "getDP":
        filename = commandNode.getAttributes().getNamedItem("param").getNodeValue()
        path = AppInfoSingleton.getAppInfo().getCurUserData().getPath() + "\\datapanelstorage\\"
        data = ""
        for i, line in enumerate(open(path + filename)):
            if i != 0:
                line = line.replace("</", "</sc:")
                line = re.sub(r"<(\w)", r"<sc:\1", line)
                data += line
        return unicode("<responseAnyXML xmlns:sc=\"http://showcase.curs.ru\">"
                       + data + "</responseAnyXML>", "utf-8")
    # при работе с Document строки приходят в формате ISO-8859-1!
    errorMes = commandName + u" не реализовано !"
    #return UserMessage(errorMes)
    raise Exception(errorMes)

if __name__ == "__main__":
    mainproc()
