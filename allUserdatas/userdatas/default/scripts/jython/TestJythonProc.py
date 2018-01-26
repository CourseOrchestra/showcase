# coding: utf-8
'''
Created on 02.11.2011

@author: den
'''
from ru.curs.showcase.core.jython import JythonProc
from ru.curs.showcase.util.xml import XMLUtils
from datetime import date
from ru.curs.showcase.runtime import ConnectionFactory
import sys


class TestJythonProc(JythonProc):
    def __init__(self):
        print "init func!"

    def execute(self, context):
        self.context = context
        if (not context.getMain()):
            raise Exception("не нравится мне этот контекст!")

        s = context.getMain()
        # jython print не работает с unicode строками
        print sys.getdefaultencoding()
        print u"main контекст (type %s) is %s" % (type(s), s)
        print "add context is %s" % (context.getAdditional())
        if (context.getFilter()):
            print "filter context is %s" % (context.getFilter())
        print "session context is %s" % (context.getSession())
        # пример работы с функциями Showcase
        session = XMLUtils.stringToDocument(context.getSession())
        print "userdata is %s" % (session.getDocumentElement().getElementsByTagName("userdata").item(0).getChildNodes().item(0).getNodeValue())
        doc = XMLUtils.createEmptyDoc("activity")
        el = doc.createElement("context")
        doc.getDocumentElement().appendChild(el)
        el.setAttribute("main", context.getMain())
        # пример работы с модулями Jython
        print "Дата: %s" % date.today()
        # пример работы с PyConnection
        pyConn = ConnectionFactory.getPyConnection()
        try:
            cur = pyConn.cursor()
            cur.execute("select top 1 name from geo3")
            print cur.fetchone()[0]
        finally:
            pyConn.close()
