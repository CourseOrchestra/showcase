# coding: utf-8
'''
Created on 10.10.2013

@author: bogatov
'''
from ru.curs.showcase.core.jython import JythonProc


class gridToolBar(JythonProc):
    def getGridToolBarData(self, context, elementId):
        return mainproc(context, elementId)


def mainproc(context, elementId):
    data = u'''
    <gridtoolbar>
        <separator/>
        <item text="Item1" img="imagesingrid/test.jpg" hint="Item one" disable="false">
            <action show_in="MODAL_WINDOW">
                <main_context>current</main_context>
                <modalwindow caption="Item1 click" height="200" width="600"/>
                <datapanel type="current" tab="current">
                    <element id="0402">
                        <add_context>ElementId='''+elementId+''' Add context value</add_context>
                    </element>
                </datapanel>
            </action>
        </item>
        <group text="Item2Group" >
            <item text="Item21" hint="Item two" disable="true" />
            <separator/>
            <item text="Item22" hint="Item three" disable="false">
                <action show_in="MODAL_WINDOW">
                    <main_context>current</main_context>
                    <modalwindow caption="Item22 click." height="200" width="600"/>
                    <datapanel type="current" tab="current">
                        <element id="0402">
                            <add_context>ElementId='''+elementId+''' Add context value</add_context>
                        </element>
                    </datapanel>
                </action>
            </item>
            <group text="Item23Group" >
                <item text="Item231" hint="Item two" disable="true" />
                <separator/>
                <item text="Item232" hint="Item three" disable="false">
                    <action show_in="MODAL_WINDOW">
                        <main_context>current</main_context>
                        <modalwindow caption="Item232 click." height="200" width="600"/>
                        <datapanel type="current" tab="current">
                            <element id="0402">
                                <add_context>ElementId='''+elementId+''' Add context value</add_context>
                            </element>
                        </datapanel>
                    </action>
                </item>
            </group>
        </group>
    </gridtoolbar>
    '''
    return data

if __name__ == "__main__":
    mainproc()