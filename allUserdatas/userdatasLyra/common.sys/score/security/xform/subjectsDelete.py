# coding: utf-8


import json

try:
    from ru.curs.showcase.core.jython import JythonDTO
except:
    from ru.curs.celesta.showcase import JythonDTO
from ru.curs.celesta.showcase.utils import XMLJSONConverter
from security._security_orm import subjectsCursor, loginsCursor

def cardData(context, main, add, filterinfo=None, session=None, elementId=None):
    xformsdata = {"schema":{"@xmlns":""}}
    xformssettings = {"properties":{"event":{"@name":"single_click",
                                             "@linkId": "1",
                                             "action":{"@name":"single_click",
                                                       "#sorted":[{"main_context": "current"},                                                                  
                                                                  {"datapanel": {"@type": "current",
                                                                                "@tab": "current",
                                                                                "element": {"@id":"subjectsGrid",
                                                                                            "add_context": ""
                                                                                            }
                                                                                }
                                                                  }]
                                                       }
                                             }
                                    }
                      }
    jsonData = XMLJSONConverter.jsonToXml(json.dumps(xformsdata))
    jsonSettings = XMLJSONConverter.jsonToXml(json.dumps(xformssettings))
    return JythonDTO(jsonData, jsonSettings)

def cardDelete(context, main=None, add=None, filterinfo=None, session=None, elementId=None, xformsdata=None):    
    currId=json.loads(session)['sessioncontext']['related']['gridContext']["currentRecordId"]        
    subject = subjectsCursor(context)
    login = loginsCursor(context)
    subject.get(currId)
    login.setRange('subjectId', subject.sid)
    for login in login.iterate():
        login.subjectId=None
        login.update()
    subject.delete()