# coding: utf-8


import json
import base64
from string import lowercase

try:
    from ru.curs.showcase.core.jython import JythonDTO
except:
    from ru.curs.celesta.showcase import JythonDTO
from ru.curs.celesta import CelestaException
from ru.curs.celesta.showcase.utils import XMLJSONConverter
from security._security_orm import customPermsCursor, rolesCustomPermsCursor

def cardData(context, main, add, filterinfo=None, session=None, elementId=None):
    xformsdata = {"schema":{"@xmlns":""}}
    xformssettings = {"properties":{"event":[{"@name":"single_click",
                                             "@linkId": "1",
                                             "action":{"#sorted":[{"main_context": "current"},
                                                                   {"datapanel": {"@type": "current",
                                                                                 "@tab": "current",
                                                                                 "element": [{"@id":"rolesCustomPermissionsGrid",
                                                                                             "add_context": "hide"},
                                                                                             {"@id":"customPermissionsGrid",
                                                                                             "add_context": ""}                                                                                 
                                                                                             ]
                                                                                 }
                                                                   }]}
                                             }]
                                    }
                      }
    jsonData = XMLJSONConverter.jsonToXml(json.dumps(xformsdata))
    jsonSettings = XMLJSONConverter.jsonToXml(json.dumps(xformssettings))
    return JythonDTO(jsonData, jsonSettings)

def cardDelete(context, main=None, add=None, filterinfo=None, session=None, elementId=None, xformsdata=None):
    currentRecordId = json.loads(session)['sessioncontext']['related']['gridContext']['currentRecordId']
    rolesCustomPermissions=rolesCustomPermsCursor(context)
    rolesCustomPermissions.setRange("permissionId", currentRecordId)
    rolesCustomPermissions.deleteAll()
    permission=customPermsCursor(context)
    permission.get(currentRecordId)
    permission.delete()