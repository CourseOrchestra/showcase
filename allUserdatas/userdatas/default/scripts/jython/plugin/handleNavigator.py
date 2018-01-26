# coding: utf-8
from ru.curs.showcase.util import XMLJSONConverter
from ru.curs.showcase.core.jython import JythonProc, JythonDTO

# init vars
data = None

class handleNavigator(JythonProc):

    def postProcess(self, context, elId, adata):
        global data
        data = adata
        return mainproc()

def mainproc():
    result = XMLJSONConverter.xmlToJson(data)
    return JythonDTO([result])


if __name__ == "__main__":
    from org.python.core import codecs
    codecs.setDefaultEncoding('utf-8')
    mainproc()
