# coding: utf-8
'''
Created on 03.12.2013

@author: d.bozhenko

'''
import json

try:
    from ru.curs.showcase.core.jython import JythonDTO
except:
    from ru.curs.celesta.showcase import JythonDTO
from ru.curs.celesta import CelestaException
from ru.curs.celesta.showcase.utils import XMLJSONConverter
from ru.curs.celesta.syscursors import RolesCursor, PermissionsCursor





def cardData(context, main=None, add=None, filterinfo=None, session=None, elementId=None):
    u'''Функция данных для карточки редактирования содержимого таблицы ролей. '''

    roles = RolesCursor(context)

    if add == 'add':
        xformsdata = {"schema":{"role":{"id": "",
                                        "description": ""
                                        }
                                }
                      }
    elif add == 'edit':
        currId = json.loads(session)['sessioncontext']['related']['gridContext']['currentRecordId']
        #currId = json.loads(base64.b64decode(currIdEncoded))
        roles.get(currId)
        xformsdata = {"schema":{"role":{"id": roles.id,
                                       "description": roles.description,
                                       "add":add
                                       }
                                }
                      }


    #print xformsdata
    xformssettings = {"properties":{"event":{"@name":"single_click",
                                             "@linkId": "1",
                                             "action":{"#sorted":[{"main_context": "current"},
                                                                   {"datapanel": {"@type": "current",
                                                                                 "@tab": "current",
                                                                                 "element": {"@id":"id_roles_grid",
                                                                                             "add_context": ""
                                                                                             }
                                                                                 }
                                                                   }]}
                                             }
                                    }
                      }

    return JythonDTO(XMLJSONConverter.jsonToXml(json.dumps(xformsdata)), XMLJSONConverter.jsonToXml(json.dumps(xformssettings)))


def cardDataSave(context, main=None, add=None, filterinfo=None, session=None, elementId=None, xformsdata=None):
    u'''Функция сохранения карточки редактирования содержимого справочника ролей. '''
    roles = RolesCursor(context)
    
    #raise Exception(xformsdata)

    content = json.loads(xformsdata)["schema"]["role"]
    roles.id=content["id"]
    if content["description"]=='':
        roles.description=content["id"]
    else:
        roles.description=content["description"]

    if add == 'add' and roles.canInsert() and roles.canModify():
        if not roles.tryInsert():
            rolesOld = RolesCursor(context)
            rolesOld.get(content["id"])
            roles.recversion = rolesOld.recversion
            roles.update()
        rolesReadPermission(roles)
    elif add == 'add' and roles.canInsert():
        roles.insert()
        rolesReadPermission(roles)
    elif add == 'edit' and roles.canModify():
        rolesOld = RolesCursor(context)
        currId = json.loads(session)['sessioncontext']['related']['gridContext']['currentRecordId']
        rolesOld.get(currId)
        if rolesOld.id == roles.id:
            roles.recversion = rolesOld.recversion
            roles.update()
            rolesReadPermission(roles)
        else:
            raise CelestaException(u"Недостаточно прав для данной операции!")
    else:
        raise CelestaException(u"Недостаточно прав для данной операции!")

def rolesReadPermission(rec):
    context=rec.callContext()
    permissions=PermissionsCursor(context)    
    tables={'celesta':['userroles',
                       'permissions',
                       'roles'],
            'security':['customPerms',
                        'rolesCustomPerms',
                        'customPermsTypes']
            }
    for grain in tables.keys():
        for table in tables[grain]:
            if permissions.tryGet(rec.id, grain, table):
                permissions.r=True
                permissions.update()
            else:
                permissions.roleid=rec.id
                permissions.grainid=grain
                permissions.tablename=table
                permissions.r=True
                permissions.i=False
                permissions.m=False
                permissions.d=False
                permissions.insert()        
    
    

