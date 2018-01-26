# coding: utf-8
'''
Created on 02.11.2011

@author: den
'''
from ru.curs.showcase.core.jython import JythonProc
from ru.curs.showcase.core.jython import JythonDTO

# init vars
main = ""
add = ""
session = ""
filterContext = ""
elementId = ""


class webtext_ci(JythonProc):
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
    data = u'''<div>
    <button type="button" onclick="gwtWebTextFunc('${elementId}','testID');">Показать сообщение</button>
    </div>'''
    settings = u'''
    <properties>
        <event name="single_click" linkId="testId">
             <action >
                 <main_context>Москва</main_context>
                     <client>
                         <activity id="activityID" name="showcaseShowAddContext">
                             <add_context>
                                 add_context для действия.
                             </add_context>
                        </activity>
                    </client>
            </action>
        </event>
    </properties>
    '''
    res = JythonDTO(data, settings)
    return res

if __name__ == "__main__":
    mainproc()
