# coding: utf-8
'''
Created on 02.11.2011

@author: den
'''
from ru.curs.showcase.core.jython import JythonProc

# init vars
request = ""


class GetNotXMLFile(JythonProc):
    def handle(self, requestStr):
        global request
        request = requestStr
        return mainproc()


def mainproc():
    return u"responseAnyXML"

if __name__ == "__main__":
    mainproc()
