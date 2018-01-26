# coding: utf-8
'''
Created on 02.11.2011

@author: den
'''
from ru.curs.showcase.core.jython import JythonProc

# init vars
session = ""


class NavJythonProc(JythonProc):
    def getRawData(self, context):
        global session
        session = context.getSession().encode("utf-8")
        return mainproc()


def mainproc():
    return u'''<navigator hideOnLoad="true">
    <group id="00" name="Фичи" icon="group_icon_default1.png">
        <level1 id="04" name="secret" selectOnLoad="true">
            <action>
                <main_context>
                    Запасы на конец отчетного периода - Всего
                </main_context>
                <datapanel type="dp0903dynSession" tab="firstOrCurrent"/>
            </action>
        </level1>
    </group>
</navigator>'''

if __name__ == "__main__":
    mainproc()
