# coding: utf-8
'''
Created on 19.01.2012

@author: den
'''

from ru.curs.showcase.core.jython import JythonProc
from ru.curs.showcase.runtime import AppInfoSingleton
from ru.curs.showcase.app.api.element import  VoidElement
from ru.curs.showcase.app.api import UserMessage
from ru.curs.showcase.app.api import MessageType
from ru.curs.showcase.core import UserMessageFactory
import codecs


# init vars
source = 'formdata.xml'
data = u'''<?xml version="1.0" encoding="UTF-8"?>
<test a="test">тест</test>'''
root = 'x:/jprojects/Showcase/userdatas/default/data/xforms'


class XFormSaveProc(JythonProc):
    def save(self, context, elId, adata):
        global source, data, root
        if context.getAdditional():
            source = context.getAdditional()
        else:
            source = context.getMain()
        data = adata
        root = AppInfoSingleton.getAppInfo().getCurUserData().getPath() + "\\data\\xforms\\"
        return mainproc()


def mainproc():
    filename = root + source
    f = codecs.open(filename, 'w', 'utf-8')
    f.write(data)
    f.close()

    
#   return None
    return UserMessageFactory().build(555, u"Сохранение xforms успешно выполнено из Jython")


if __name__ == '__main__':
    mainproc()
