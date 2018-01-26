# coding: utf-8
'''
Created on 19.01.2012

@author: den
'''

from ru.curs.showcase.core.jython import JythonProc
#from ru.curs.showcase.core import UserMessage
import xml.dom.minidom as minidom

# init vars
data = u'''<data a="test">тест</data>'''


class submission(JythonProc):
    def transform(self, context, adata):
        global source, data, root
        data = adata
        
        print 'dddddddddddddddddd'
        print context
        
        return mainproc()


def mainproc():
#    if not data:
#        return UserMessage(u"Передана пустая строку")
    doc = minidom.parseString(data)
    doc.getElementsByTagName("data")[0].childNodes[0].data = doc.getElementsByTagName("data")[0].childNodes[0].data + "_handled"
    output = doc.toxml().split('\n', 1)[1]
    return output

if __name__ == '__main__':
    mainproc()
