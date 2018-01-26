# coding: utf-8
'''
Created on 02.11.2011

@author: den
'''
from ru.curs.showcase.core.jython import JythonProc, JythonDTO

# init vars
data = u'''[]'''
result = None


class handleFlashD(JythonProc):

    def postProcess(self, context, elId, adata):
        global data
        data = adata
        return mainproc()


def mainproc():
    global result
    result = u'''[
        { "DirectionName": "Направление 1", "ID": "14456", "Name": "Имя 1", "Number": "Номер поручения 1", "ShortName": "краткое содержание 1", "Term": "2010-02-01 00:00:00", "StatusNum": "0", "ExecutorName": "Исполнитель 1", "DirID": "3", "TermUnix": "1264968000" },
        { "DirectionName": "Направление 1", "ID": "14456", "Name": "Имя 2", "Number": "Номер поручения 2", "ShortName": "краткое содержание 2", "Term": "2010-02-02 00:00:00", "StatusNum": "0", "ExecutorName": "Исполнитель 2", "DirID": "3", "TermUnix": "1265068000" },
        { "DirectionName": "Направление 5", "ID": "14456", "Name": "Имя 3", "Number": "Номер поручения 3", "ShortName": "краткое содержание 3", "Term": "2010-04-03 00:00:00", "StatusNum": "0", "ExecutorName": "Исполнитель 3", "DirID": "3", "TermUnix": "1265168000" }
    ]'''
    return JythonDTO([result])

if __name__ == "__main__":
    from org.python.core import codecs
    codecs.setDefaultEncoding('utf-8')
    mainproc()
