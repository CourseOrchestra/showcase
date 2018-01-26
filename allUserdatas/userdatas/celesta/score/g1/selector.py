# coding: utf-8
from g1._g1_orm import testCursor 
from ru.curs.showcase.core.selector import ResultSelectorData
from ru.curs.showcase.app.api.selector import DataRecord
from java.util import ArrayList

def procListAndCount(context, main, add, filterinfo, session, params, curValue, startsWith, firstRecord, recordCount):
    print 'Get data and count of selector from Celesta Python procedure.'
    print 'User %s' % context.userId
    print 'main "%s".' % main
    print 'add "%s".' % add
    print 'filterinfo "%s".' % filterinfo
    print 'session "%s".' % session
    print 'params "%s".' % params
    print 'curValue "%s".' % curValue
    print 'startsWith "%s".' % startsWith
    print 'firstRecord "%s".' % firstRecord
    print 'recordCount "%s".' % recordCount
    
    data = procList(context, main, add, filterinfo, session, params, curValue, startsWith, firstRecord, recordCount)
    count = procCount(context, main, add, filterinfo, session, params, curValue, startsWith)
    return ResultSelectorData(data.getDataRecordList(), count.getCount())

def procCount(context, main, add, filterinfo, session, params, curValue, startsWith):
    print 'Get count of selector from Celesta Python procedure.'
    print 'User %s' % context.userId
    print 'main "%s".' % main
    print 'add "%s".' % add
    print 'filterinfo "%s".' % filterinfo
    print 'session "%s".' % session
    print 'params "%s".' % params
    print 'curValue "%s".' % curValue
    print 'startsWith "%s".' % startsWith
    
    
    context.warning('dd1');    
#    context.error('dd1');
    
    return ResultSelectorData(None, 3)
#    return ResultSelectorData(None, 0)

def procList(context, main, add, filterinfo, session, params, curValue, startsWith, firstRecord, recordCount):
    print 'Get data of selector from Celesta Python procedure.'
    print 'User %s' % context.userId
    print 'main "%s".' % main
    print 'add "%s".' % add
    print 'filterinfo "%s".' % filterinfo
    print 'session "%s".' % session
    print 'params "%s".' % params
    print 'curValue "%s".' % curValue
    print 'startsWith "%s".' % startsWith
    print 'firstRecord "%s".' % firstRecord
    print 'recordCount "%s".' % recordCount
    
    recordList = ArrayList()
    for i in range(3):
        rec = DataRecord()
        rec.setId("item"+str(i))
        rec.setName("Item "+str(i))
        recordList.add(rec)
        
        
#    context.warning('dd2');
#    context.message('dd1', u'Заголовок4', u"solutions/default/resources/group_icon_default.png");
    
#    context.error('dd4', u'Заголовок4', u"solutions/default/resources/group_icon_default.png");
    
  
#    recordList = ArrayList()
    
    return ResultSelectorData(recordList, 0)









