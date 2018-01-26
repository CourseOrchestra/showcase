# coding: utf-8
'''
Created on 02.11.2011

@author: den
'''
from ru.curs.showcase.core.jython import JythonProc
from ru.curs.showcase.core.jython import JythonDTO
from ru.curs.showcase.runtime import AppInfoSingleton
import codecs
from os.path import exists
import re

# init vars
source = 'jython_save_data.xml'
root = 'x:/jprojects/Showcase/userdatas/default/data/xforms'


class GetXFormsData(JythonProc):
    def getRawData(self, context, elId):
        global source, root
        source = context.getMain()
        root = AppInfoSingleton.getAppInfo().getCurUserData().getPath() + "\\data\\xforms\\"
        return mainproc()


def mainproc():
    data = u'''<test a="test">тест</test>'''
    filename = root + source
    if exists(filename):
        f = codecs.open(filename, 'r', 'utf-8')
        data = f.read()
    settings = None
    encoded = data.encode('ascii', 'xmlcharrefreplace')
    print encoded
    encoded = re.sub(r"<", r"&lt;", encoded)
    encoded = re.sub(r">", r"&gt;", encoded)
    encoded = '<div>' + encoded + '</div>'
    res = JythonDTO(encoded, settings)
    return res

if __name__ == "__main__":
    mainproc()
