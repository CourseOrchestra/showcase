# coding: UTF-8
'''
Created on 19.01.2012

@author: den
'''



from ru.curs.showcase.core.jython import JythonProc
from ru.curs.showcase.app.api import UserMessage
from ru.curs.showcase.app.api import MessageType
from ru.curs.showcase.core import UserMessageFactory
from ru.curs.showcase.core.jython import JythonDTO


# init vars
data = u'''<data a="test">тест</data>'''


class simple_submission(JythonProc):
    def transform(self, context, adata):
        global source, data, root
        data = adata
        
        
        print 'ffffffffffffff1'
        print context
        print 'ffffffffffffff2'        
        
        
        
        return mainproc()


def mainproc():
    data = u'''
<!--
<?xml version="1.0" encoding="UTF-8"?>
-->    
<schema>
   <info>
      <name>Николай23</name>
      <growth/>
      <eyescolour/>
      <music>Классическая Эстрадная</music>
      <comment>xform из Jython</comment>
   </info>
</schema>
'''
    res = JythonDTO(data, UserMessageFactory().build(555, u"Сабмишен успешно выполнен из Jython"))
#    res = data
    return res

if __name__ == '__main__':
    mainproc()









