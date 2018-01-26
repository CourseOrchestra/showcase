# coding: utf-8
from g1._g1_orm import testCursor 
from ru.curs.showcase.core.jython import JythonDTO

def webTextDatapanel(context, main, session):
    print 'Get datapanel data from Celesta Python procedure.'
    print 'User %s' % context.userId
    print 'Main "%s".' % main
    print 'Sesion "%s".' % session
    
    data = u'''
    <datapanel>
        <tab id="01" name="Samples">
            <element id="0101" type="webtext" proc="g1.main.webtext.celesta"/>        
        </tab>
    </datapanel>
    '''    
    return data

