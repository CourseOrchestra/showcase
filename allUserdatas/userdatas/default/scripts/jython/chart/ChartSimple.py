# coding: utf-8
'''
Created on 02.11.2011

@author: den
'''
from ru.curs.showcase.core.jython import JythonProc
from ru.curs.showcase.core.jython import JythonDTO
from ru.curs.showcase.app.api import UserMessage
from ru.curs.showcase.app.api import MessageType
from ru.curs.showcase.core import UserMessageFactory


#from ru.curs.showcase.app.api import UserMessage
#from ru.curs.showcase.util.xml import XMLUtils
#from org.xml.sax.helpers import DefaultHandler


# init vars
main = ""
add = ""
session = ""
filterContext = ""
elementId = ""
pyconn = None


class ChartSimple(JythonProc):
    def getRawData(self, *args):
        global main, add, session, filterContext, pyconn, elementId
        context = args[0]
        main = context.getMain()
        if context.getAdditional():
            add = context.getAdditional()
        session = context.getSession()
        if context.getFilter():
            filterContext = context.getFilter()
        elementId = args[1]
        pyconn = args[2]
        return mainproc()


def mainproc():
    cur = pyconn.cursor()
    try:
        cur.execute("IF (OBJECT_ID('tempdb..#tmp') IS NOT NULL) DROP TABLE #tmp")
        cur.execute('''
        SELECT TOP 10 [Journal_48_Name], [FJField_14]
        into #tmp
        FROM [Journal_48]
        ''')
    finally:
        cur.close()

    data = u'SELECT [Journal_48_Name], [FJField_14] FROM #tmp'
    settings = u'''
<chartsettings>
        <labels>
            <header><h2>Простой график</h2></header>
        </labels>
        <properties legend="left" selectorColumn="Journal_48_Name"
        width="500px" height="500px" flip="false"
        hintFormat="%x (%labelx): %value"/>
                        <action>
                            <main_context>current</main_context>
                            <datapanel type="current" tab="current">
                                <element id="16">
                                    <add_context>hide</add_context>
                                </element>
                            </datapanel>
                        </action>
            <template>
{
"plot": {
        "type": "Pie",
        "tension": "S",
        "gap": 3,
        "markers": true,
        "precision": 3
    },
    "theme": "dojox.charting.themes.PrimaryColors",
    "action": [
        {
            "type": "dojox.charting.action2d.Shake",
            "options": {
                "duration": 500
            }
        },
        {
            "type": "dojox.charting.action2d.Tooltip"
        }
    ],
    "axisX": {
        "fixLower": "major",
        "fixUpper": "major",
        "minorLabels": false,
        "microTicks": false,
        "rotation": -90,
        "minorTicks": false
    },
    "axisY": {
        "vertical": true
    }
,
    eventHandler: "eventCallbackChartHandler"
}
        </template>
    </chartsettings>
    '''
    return JythonDTO(data, settings, UserMessageFactory().build(555, u"График(запрос) успешно построен из Jython"))

if __name__ == "__main__":
    mainproc()
