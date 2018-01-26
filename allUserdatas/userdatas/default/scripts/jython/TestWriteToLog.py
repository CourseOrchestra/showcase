# coding: utf-8
'''
Created on 02.11.2011

@author: den
'''
from ru.curs.showcase.core.jython import JythonProc
from ru.curs.showcase.app.api import UserMessage
from ru.curs.showcase.app.api import MessageType
from ru.curs.showcase.core import UserMessageFactory



class TestWriteToLog(JythonProc):
    def execute(self, context):
        print context.getMain() + u" из jython"
        print u"из jython 2"
        
#       return None
        return UserMessageFactory().build(555, u"Серверное действие успешно выполнено из Jython")
        
