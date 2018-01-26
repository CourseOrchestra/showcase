# coding: utf-8
from g1._g1_orm import testCursor 
from ru.curs.showcase.core.jython import JythonDTO
from ru.curs.showcase.core.jython import JythonDownloadResult
from ru.curs.showcase.runtime import AppInfoSingleton
from java.io import FileInputStream
from java.io import File
from ru.curs.showcase.app.api import UserMessage
from ru.curs.showcase.app.api import MessageType
from ru.curs.showcase.core import UserMessageFactory

def template(context, main, add, filterinfo, session, elementId):
    print 'Get xform data from Celesta Python procedure.'
    print 'User %s' % context.userId
    print 'main "%s".' % main
    print 'add "%s".' % add
    print 'filterinfo "%s".' % filterinfo
    print 'session "%s".' % session
    print 'elementId "%s".' % elementId
    
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
    return JythonDTO(data)



def main(context, main, add, filterinfo, session, elementId):
    print 'Get xform data from Celesta Python procedure.'
    print 'User %s' % context.userId
    print 'main "%s".' % main
    print 'add "%s".' % add
    print 'filterinfo "%s".' % filterinfo
    print 'session "%s".' % session
    print 'elementId "%s".' % elementId
    
    data = u'''
    <schema xmlns="">
      <info>
        <name>Белгородская обл.</name>
        <growth />
        <eyescolour />
        <music />
        <comment />
      </info>
    </schema>
    '''
    settings = u'''
    <properties>        
    </properties>
    '''    
    return JythonDTO(data, settings, UserMessageFactory().build(555, u"xforms успешно построен из Celesta"))
