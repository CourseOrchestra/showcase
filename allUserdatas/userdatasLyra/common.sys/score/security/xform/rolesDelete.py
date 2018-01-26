# coding: utf-8


import json

try:
    from ru.curs.showcase.core.jython import JythonDTO
except:
    from ru.curs.celesta.showcase import JythonDTO

from ru.curs.celesta.showcase.utils import XMLJSONConverter
from ru.curs.celesta.syscursors import RolesCursor
from ru.curs.celesta.syscursors import PermissionsCursor
from ru.curs.celesta.syscursors import UserRolesCursor
from security._security_orm import rolesCustomPermsCursor

def cardData(context, main, add, filterinfo=None, session=None, elementId=None):
    xformsdata = {"schema":{"@xmlns":""}}
    xformssettings = {"properties":{"event":[{"@name":"single_click",
                                             "@linkId": "1",
                                             "action":{"#sorted":[{"main_context": "current"},
                                                                   {"datapanel": {"@type": "current",
                                                                                 "@tab": "current",
                                                                                 "element": [{"@id":"id_roles_grid",
                                                                                             "add_context": ""},
                                                                                             {"@id":"id_perm_roles_grid",
                                                                                             "add_context": "hide"}
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
    userRoles=UserRolesCursor(context)
    userRoles.setRange("roleid", currentRecordId)
    userRoles.deleteAll()
    rolesCustomPermissions=rolesCustomPermsCursor(context)
    rolesCustomPermissions.setRange("roleid", currentRecordId)
    rolesCustomPermissions.deleteAll()
    permissions=PermissionsCursor(context)
    permissions.setRange("roleid", currentRecordId)
    permissions.deleteAll()
    role = RolesCursor(context)
    role.get(currentRecordId)
    role.delete()