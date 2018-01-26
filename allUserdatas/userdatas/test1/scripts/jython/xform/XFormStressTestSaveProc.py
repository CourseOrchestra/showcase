# coding: utf-8
'''
Created on 19.01.2012

@author: den
'''

from ru.curs.showcase.core.jython import JythonProc
from ru.curs.showcase.test.stress import JythonPoolTest
from ru.curs.showcase.core import UserMessage

# init vars
data = u'''1'''


class XFormStressTestSaveProc(JythonProc):
    def save(self, context, elId, adata):
        global  data
        data = adata
        return mainproc()


def mainproc():
    if JythonPoolTest.getStorage().contains(data):
        return UserMessage("test1", u"Повтор!")
    JythonPoolTest.getStorage().add(data)
    return None

if __name__ == '__main__':
    mainproc()
