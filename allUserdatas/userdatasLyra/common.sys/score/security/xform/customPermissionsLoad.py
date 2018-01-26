# coding: utf-8
'''
Created on 01.07.2014

@author: d.bozhenko.
'''
#модуль загрузки/выгрузки данных таблицы customPerms

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
from security._security_orm import rolesCustomPermsCursor, customPermsCursor, customPermsTypesCursor
from ru.curs.celesta.syscursors import PermissionsCursor, RolesCursor
import os
from common.dbutils import DataBaseXMLExchange
from security.functions import tableDownload, tableUpload

from java.io import FileInputStream, FileOutputStream


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
                                                                                 "element":[{"@id": "customPermissionsGrid",
                                                                                            "add_context": 'current'},
                                                                                            {"@id": "rolesCustomPermissionsGrid",
                                                                                            "add_context": 'hide'}]
            
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

def permissionsload(context, main=None, add=None, filterinfo=None, session=None, elementId=None, data=None):
    permissions = PermissionsCursor(context)
    roles = RolesCursor(context)
    rolesCustomPerms = rolesCustomPermsCursor(context)
    customPerms = customPermsCursor(context)
    customPermsTypes = customPermsTypesCursor(context)
    cursors = [roles, permissions, customPermsTypes, customPerms, rolesCustomPerms]
    files = ['roles', 'permissions', 'customPermsTypes', 'customPerms', 'rolesCustomPerms']

    for i in range(len(cursors)):
        filePath = os.path.join(os.path.dirname(os.path.dirname(os.path.abspath(__file__))), files[i] + '.xml')
        # raise Exception(filePath)
        if add == 'upload':
            dataStream = FileOutputStream(filePath)
        elif add == 'download':
            dataStream = FileInputStream(filePath)
        exchange = DataBaseXMLExchange(dataStream, cursors[i])
        if add == 'upload':
            exchange.downloadXML()
        elif add == 'download':
            exchange.uploadXML()
        dataStream.close()

def permissionsDownload(context, main=None, add=None, filterinfo=None, session=None, elementId=None, data=None):
    customPerms = customPermsCursor(context)
    fileName = 'customPermissions'
    return tableDownload(customPerms, fileName)

def permissionsUpload(context, main=None, add=None, filterinfo=None, session=None, elementId=None, data=None, fileName=None, file=None):
    customPerms = customPermsCursor(context)
    tableUpload(customPerms, file)
    return context.message(u"Данные успешно загружены в таблицу")