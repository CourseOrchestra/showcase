# coding: utf-8
'''
Created on 02.11.2011

@author: AleXXl
'''
from ru.curs.showcase.core.jython import JythonProc

class partTemplate(JythonProc):
    def getRawData(self, context, elId):
        data = u'''

<partOfXFormTemplate xmlns="http://www.w3.org/1999/xhtml" xmlns:ev="http://www.w3.org/2001/xml-events"
    xmlns:xf="http://www.w3.org/2002/xforms">
        
        <div>

            <xf:trigger>
                <xf:label>Hello!</xf:label>
                <xf:message level="modal" ev:event="DOMActivate">Hello World!</xf:message>
            </xf:trigger>

        </div>

</partOfXFormTemplate>

    '''
        return data
