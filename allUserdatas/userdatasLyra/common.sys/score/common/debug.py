# coding: utf-8

from ru.curs.celesta import Celesta
from ru.curs.celesta.score import Score
from ru.curs.celesta import ConnectionPool
from ru.curs.celesta import CallContext
from common import navigator, sysfunctions, hierarchy
from common import settings
from ru.curs.celesta import SessionContext
from datetime import datetime
#try:
#    from java.lang import String111
#except:
#    from java.lang import Integer as III
from mytest._mytest_orm import testTableCursor
import sys, os
import common
#from security.grid import permissions

a = Celesta.getInstance()
conn = ConnectionPool.get()
sesContext = SessionContext('admin', 'testsession')
context = CallContext(conn, sesContext)
#print context.getCelesta()




def proc1(context):
    print datetime.now()
    aa = settings.settingsManager()

    print aa.getGrainSettings('common', '''navigator/userdata/@name''')
    print datetime.now()
proc1(context)

#    from dirusing._dirusing_orm import filtersConditionsCursor
#    filtersConditions = filtersConditionsCursor(context)
#    filtersConditions.id = 1
#    filtersConditions.prefix = None
#
#    if not filtersConditions.tryInsert():
#        filtersConditions.update()

#ConnectionPool.putBack(conn)


