# coding: utf-8
'''
Created on 02.11.2011

@author: den
'''
from ru.curs.showcase.core.jython import JythonProc
from ru.curs.showcase.core.jython import JythonDTO
from ru.curs.showcase.runtime import AppInfoSingleton
# init vars
main = ""
add = ""
session = ""
filterContext = ""
elementId = ""


class libtest(JythonProc):
    def getRawData(self, context, elId):
        global main, add, session, filterContext, elementId
        main = context.getMain()
        if context.getAdditional():
            add = context.getAdditional()
        session = context.getSession()
        if context.getFilter():
            filterContext = context.getFilter()
        elementId = elId
        return mainproc()


def mainproc():
    excel = "simple.xlsx"
    pdf = "simple.pdf"
    data = u'''<div>
    <button type="button" onclick="gwtWebTextFunc('${elementId}','1');">Сгенерировать PDF</button>
    <button type="button" onclick="gwtWebTextFunc('${elementId}','2');">Сгенерировать Excel</button>
    <hr/>
    <a href="''' + pdf + u'''" target="_blank">Открыть сгенерированный PDF</a>
    <a href="''' + excel + u'''" target="_blank">Открыть сгенерированный Excel</a>
    </div>'''
    settings = '''
<properties>
                      <event name="single_click" linkId="1">
                        <action >
                            <main_context>current</main_context>
                            <server>
                                <activity id="srv01" name="jasperReport/pdfCreate.py">
                                    <add_context>''' + AppInfoSingleton.getAppInfo().getWebAppPath() + '/' + pdf + u'''</add_context>
                                </activity>
                            </server>
                        </action>
                       </event>
                      <event name="single_click" linkId="2">
                        <action >
                            <main_context>current</main_context>
                            <server>
                                <activity id="srv02" name="poi/excelCreate.py">
                                    <add_context>''' + AppInfoSingleton.getAppInfo().getWebAppPath() + '/' + excel + '''</add_context>
                                </activity>
                            </server>
                        </action>
                       </event>
                    </properties>
    '''
    res = JythonDTO(data, settings)
    return res

if __name__ == "__main__":
    mainproc()
