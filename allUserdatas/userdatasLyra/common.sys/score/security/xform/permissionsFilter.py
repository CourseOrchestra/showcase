# coding: utf-8
'''
Created on 07.02.2014

@author: d.bozhenko P.
'''

import json

try:
    from ru.curs.showcase.core.jython import JythonDTO
except:
    from ru.curs.celesta.showcase import JythonDTO

try:
    from ru.curs.showcase.core.selector import ResultSelectorData
    from ru.curs.showcase.app.api.selector import DataRecord
except:
    pass

from ru.curs.celesta.showcase.utils import XMLJSONConverter
from ru.curs.celesta.syscursors import TablesCursor

def cardData(context, main, add, filterinfo=None, session=None, elementId=None):
    xformsdata = {"schema":{"@xmlns":"",
                            "roleid":"",
                            "grainid":"",
                            "tablename":""
                            }
                  }
    xformssettings = {"properties":{
                                    "action":{"#sorted":[{"main_context": "current"},
                                                          {"datapanel":{"@type": "current",
                                                                       "@tab": "current",
                                                                       "element":{"@id": "permGrid",
                                                                                  "add_context": 'current'}
                                                                       }
                                                          }]},
                                    "event":[{"@name": "single_click",
                                              "@linkId": "11",
                                              "action":{"#sorted":[{"main_context": "current"},
                                                                    {"datapanel":{"@type": "current",
                                                                                 "@tab": "current",
                                                                                 "element":{"@id": "permGrid",
                                                                                            "add_context": 'current'}
                                                                                 }
                                                                    }]}
                                              }]
                                    }
                      }
    jsonData = XMLJSONConverter.jsonToXml(json.dumps(xformsdata))
    jsonSettings = XMLJSONConverter.jsonToXml(json.dumps(xformssettings))
    return JythonDTO(jsonData, jsonSettings)

#def tablesCount(context, main=None, add=None, filterinfo=None, session=None, params=None,
#                curvalue=None, startswith=None):
#    u'''Функция count для селектора выбора таблиц. '''
#    
#    paramsXML=minidom.parseString(params)
#    tables = TablesCursor(context)
#    raise Exception(params)
#    if paramsXML.getElementsByTagName('grainid')[0].childNodes:
#        grainid=paramsXML.getElementsByTagName('grainid')[0].childNodes[0].data                
#        tables.setRange('grainid', grainid)            
#    tables.setFilter('tablename', """@%s'%s'%%""" % ("%"*(not startswith), curvalue.replace("'","''")))
#    count = tables.count()
#        
#    return ResultSelectorData(None, count)
#
#def tablesList(context, main=None, add=None, filterinfo=None, session=None, params=None,
#                curvalue=None, startswith=None, firstrecord=None, recordcount=None):    
#    u'''Функция list для селектора выбора таблиц. '''
#    raise Exception(params)
#    recordList = ArrayList()
#    paramsXML=minidom.parseString(params)
#    tables = TablesCursor(context)
#    tables.setFilter('tablename', "@%s'%s'%%" % ("%"*(not startswith), curvalue.replace("'","''")))
#    if paramsXML.getElementsByTagName('grainid')[0].childNodes:
#        grainid=paramsXML.getElementsByTagName('grainid')[0].childNodes[0].data        
#        tables.setRange('grainid', grainid)
#        
#    tables.orderBy('tablename')
#    tables.limit(firstrecord, recordcount)
#    if tables.tryFindSet():
#        while True:
#            rec = DataRecord()
#            rec.setId(tables.tablename)
#            rec.setName(tables.tablename)
#            recordList.add(rec)
#            if not tables.nextInSet():
#                break
#    return ResultSelectorData(recordList, 0)

