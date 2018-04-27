# coding: utf-8

from ru.curs.showcase.core.jython import JythonDTO

def doRun1(context):
    dData = u'hello1'
    return JythonDTO(dData, None)