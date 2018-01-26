# coding: utf-8
'''
Created on 01.07.2014

@author: d.bozhenko.
'''
#модуль загрузки/выгрузки данных таблицы roles

import json

try:
    from ru.curs.showcase.core.jython import JythonDTO
except:
    from ru.curs.celesta.showcase import JythonDTO

try:
    from ru.curs.showcase.core.jython import JythonDownloadResult
except:
    from ru.curs.celesta.showcase import JythonDownloadResult

from ru.curs.celesta.showcase.utils import XMLJSONConverter
from ru.curs.celesta.syscursors import RolesCursor
from security.functions import tableDownload, tableUpload

def cardData(context, main, add, filterinfo=None, session=None, elementId=None):
    xformsdata = {"schema":{"@xmlns":"",
                            "context":{"@add":add}
                            }
                  }
    xformssettings = {"properties":{"event":[{"@name": "single_click",
                                              "@linkId": "1",
                                              "action":{"#sorted":[{"main_context": "current"},
                                                                    {"datapanel":{"@type": "current",
                                                                                 "@tab": "current",
                                                                                 "element":{"@id": "id_roles_grid",
                                                                                            "add_context": 'current'}
                                                                                 }
                                                                    }]}
                                              }]
                                    }
                      }
    jsonData = XMLJSONConverter.jsonToXml(json.dumps(xformsdata))
    jsonSettings = XMLJSONConverter.jsonToXml(json.dumps(xformssettings))
    return JythonDTO(jsonData, jsonSettings)

def cardSave(context, main=None, add=None, filterinfo=None, session=None, elementId=None, xformsdata=None):
    return

def rolesDownload(context, main=None, add=None, filterinfo=None, session=None, elementId=None, data=None):
    roles = RolesCursor(context)
    fileName = 'roles'
    return tableDownload(roles, fileName)

def rolesUpload(context, main=None, add=None, filterinfo=None, session=None, elementId=None, data=None, fileName=None, file=None):
    roles = RolesCursor(context)
    tableUpload(roles, file)
    return context.message(u"Данные успешно загружены в таблицу")