# coding: utf-8
from g1._g1_orm import testCursor 
from ru.curs.showcase.app.api import UserMessage
from ru.curs.showcase.app.api import MessageType

def simple(context, main, add, filterinfo, session):
    print 'Get navigator data from Celesta Python procedure.'
    print 'User %s' % context.userId
    print 'main "%s".' % main
    print 'add "%s".' % add
    print 'filterinfo "%s".' % filterinfo
    print 'session "%s".' % session
    
    msg = u'''Activity was performed'''
    return UserMessage(msg, MessageType.INFO)
#    return UserMessage(msg, MessageType.ERROR)
