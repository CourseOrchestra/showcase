# coding: utf-8
'''
Created on 03.12.2013

@author: d.bozhenko

'''
import json

try:
    from ru.curs.showcase.core.jython import JythonDTO
    from ru.curs.showcase.core.selector import ResultSelectorData
    from ru.curs.showcase.app.api.selector import DataRecord
except:
    from ru.curs.celesta.showcase import JythonDTO, DataRecord, ResultSelectorData


from ru.curs.celesta import CelestaException
from ru.curs.celesta.showcase.utils import XMLJSONConverter
# from dirusing.commonfunctions import relatedTableCursorImport
from security._security_orm import customPermsTypesCursor



def cardData(context, main=None, add=None, filterinfo=None, session=None, elementId=None):
    u'''Функция данных для карточки редактирования содержимого таблицы типоа разрешений. '''
    
    #raise Exception(session)

    permissionsTypes = customPermsTypesCursor(context)

    if add == 'add':
        xformsdata = {"schema":{"type":{"@name":"",
                                        "@description":""}
                                }
                      }
    elif add == 'edit':
        currId = json.loads(session)['sessioncontext']['related']['gridContext']['currentRecordId']
        permissionsTypes.get(currId)
        xformsdata = {"schema":{"type":{"@name": permissionsTypes.name,
                                        "@description": permissionsTypes.description,
                                        "@add":add}
                                }
                      }
    # print xformsdata
    xformssettings = {"properties":{"event":{"@name":"single_click",
                                             "@linkId": "1",
                                             "action":{"#sorted":[{"main_context": "current"},
                                                                   {"datapanel": {"@type": "current",
                                                                                 "@tab": "current",
                                                                                 "element": {"@id":"customPermissionsTypesGrid",
                                                                                             "add_context": ""}
                                                                                 }
                                                                   }]}
                                             }
                                    }
                      }

    return JythonDTO(XMLJSONConverter.jsonToXml(json.dumps(xformsdata)), XMLJSONConverter.jsonToXml(json.dumps(xformssettings)))


def cardDataSave(context, main=None, add=None, filterinfo=None, session=None, elementId=None, xformsdata=None):
    u'''Функция сохранения карточки редактирования содержимого справочника типов разрешений. '''
    permissionsTypes = customPermsTypesCursor(context)
    content = json.loads(xformsdata)["schema"]["type"]
    permissionsTypes.name=content["@name"]
    permissionsTypes.description=content["@description"]

    if add == 'add' and permissionsTypes.canInsert() and permissionsTypes.canModify():
        if not permissionsTypes.tryInsert():
            permissionsTypesOld = customPermsTypesCursor(context)
            permissionsTypesOld.get(content["@name"])
            permissionsTypes.recversion = permissionsTypesOld.recversion
            permissionsTypes.update()
    elif add == 'add' and permissionsTypes.canInsert():
        permissionsTypes.insert()
    elif add == 'edit' and permissionsTypes.canModify():
        permissionsTypesOld = customPermsTypesCursor(context)
        currId = json.loads(session)['sessioncontext']['related']['gridContext']['currentRecordId']        
        permissionsTypesOld.get(currId)
        if permissionsTypesOld.name==permissionsTypes.name:
            permissionsTypes.recversion = permissionsTypesOld.recversion
            permissionsTypes.update()
        elif permissionsTypes.canInsert():
            permissionsTypes.insert()
            permissionsTypesOld.delete()
        else:
            raise CelestaException(u"Недостаточно прав для данной операции!")        
    else:
        raise CelestaException(u"Недостаточно прав для данной операции!")