# coding: utf-8
'''
Created on 02.11.2011

@author: den
'''
from ru.curs.showcase.core.jython import JythonProc
#from ru.curs.showcase.runtime import AppInfoSingleton
#from ru.curs.showcase.core.jython import JythonDTO
#from ru.curs.showcase.app.api import UserMessage;
#from ru.curs.showcase.util.xml import XMLUtils
#from org.xml.sax.helpers import DefaultHandler
#from ru.curs.showcase.util import TextUtils

import os.path
from course import JasperReportProducer

# init vars
main = None
outputFile = None
session = None
filterContext = None
elementId = None
request = None
pyconn = None


class pdfCreate(JythonProc):
    def execute(self, context):
        global main, outputFile, session, filterContext
        main = context.getMain()
        if context.getAdditional():
            outputFile = context.getAdditional()
        session = context.getSession()
        if context.getFilter():
            filterContext = context.getFilter()
        return mainproc()


def mainproc():
    JasperReportProducer.produce(os.path.dirname(__file__) + "/simple.jasper", outputFile)

if __name__ == "__main__":
    mainproc()
