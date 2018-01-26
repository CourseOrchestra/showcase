# coding: utf-8
'''
Created on 03.12.2013

@author: d.bozhenko

'''
import json
from java.util import ArrayList
try:
    from ru.curs.showcase.core.jython import JythonDTO
    from ru.curs.showcase.core.selector import ResultSelectorData
    from ru.curs.showcase.app.api.selector import DataRecord
except:
    from ru.curs.celesta.showcase import JythonDTO, DataRecord, ResultSelectorData



from ru.curs.celesta import CelestaException
from ru.curs.celesta.showcase.utils import XMLJSONConverter
from security._security_orm import customPermsCursor, customPermsTypesCursor

def cardData(context, main=None, add=None, filterinfo=None, session=None, elementId=None):
    u'''Функция данных для карточки редактирования содержимого таблицы типоа разрешений. '''

    permissions = customPermsCursor(context)

    if add == 'add':
        typeId = json.loads(session)['sessioncontext']['related']['xformsContext']['formData']['schema']['permission']['@type']
        xformsdata = {"schema":{"permission":{"@name":"",
                                              "@description":"",
                                              "@type":typeId}
                                }
                      }
    elif add == 'edit':
        currId = json.loads(session)['sessioncontext']['related']['gridContext']['currentRecordId']
        permissions.get(currId)
        xformsdata = {"schema":{"permission":{"@name": permissions.name,
                                              "@description": permissions.description,
                                              "@type": permissions.type}
                                }
                      }
    # print xformsdata
    xformssettings = {"properties":{"event":{"@name":"single_click",
                                             "@linkId": "1",
                                             "action":{"#sorted":[{"main_context": "current"},
                                                                   {"datapanel": {"@type": "current",
                                                                                 "@tab": "current",
                                                                                 "element": {"@id":"customPermissionsGrid",
                                                                                             "add_context": ""}
                                                                                 }
                                                                   }]}
                                             }
                                    }
                      }

    return JythonDTO(XMLJSONConverter.jsonToXml(json.dumps(xformsdata)), XMLJSONConverter.jsonToXml(json.dumps(xformssettings)))


def cardDataSave(context, main=None, add=None, filterinfo=None, session=None, elementId=None, xformsdata=None):
    u'''Функция сохранения карточки редактирования содержимого справочника типов разрешений. '''
    permissions = customPermsCursor(context)
    content = json.loads(xformsdata)["schema"]["permission"]
    permissions.name=content["@name"]
    permissions.description=content["@description"]
    permissions.type=content["@type"]

    if add == 'add' and permissions.canInsert() and permissions.canModify():
        if not permissions.tryInsert():
            permissionsOld = customPermsCursor(context)
            permissionsOld.get(content["@name"])
            permissions.recversion = permissionsOld.recversion             
            permissions.update()
    elif add == 'add' and permissions.canInsert():
        permissions.insert()
    elif add == 'edit' and permissions.canModify():
        permissionsOld = customPermsCursor(context)
        currId = json.loads(session)['sessioncontext']['related']['gridContext']['currentRecordId']        
        permissionsOld.get(currId)
        if permissionsOld.name==permissions.name:
            permissions.recversion = permissionsOld.recversion
            permissions.update()
        elif permissions.canInsert():
            permissions.insert()
            permissionsOld.delete()
        else:
            raise CelestaException(u"Недостаточно прав для данной операции!")        
    else:
        raise CelestaException(u"Недостаточно прав для данной операции!")
    
    
def customPermissionsTypesCount(context, main=None, add=None, filterinfo=None, session=None, params=None,
                curvalue=None, startswith=None):
    u'''Функция count для селектора выбора ролей. '''

    customPermissionsTypes = customPermsTypesCursor(context)
    customPermissionsTypes.setFilter('name', """@%s'%s'%%""" % ("%"*(not startswith), curvalue.replace("'","''")))
    count = customPermissionsTypes.count()

    return ResultSelectorData(None, count)

def customPermissionsTypesList(context, main=None, add=None, filterinfo=None, session=None, params=None,
                curvalue=None, startswith=None, firstrecord=None, recordcount=None):
    u'''Функция list для селектора выбора ролей. '''
    
    customPermissionsTypes = customPermsTypesCursor(context)
    customPermissionsTypes.setFilter('name', """@%s'%s'%%""" % ("%"*(not startswith), curvalue.replace("'","''")))
    customPermissionsTypes.orderBy('name')
    customPermissionsTypes.limit(firstrecord, recordcount)

    recordList = ArrayList()
    for customPermissionsTypes in customPermissionsTypes.iterate():
        rec = DataRecord()
        rec.setId(unicode(customPermissionsTypes.name))
        rec.setName(customPermissionsTypes.name)
        recordList.add(rec)

    return ResultSelectorData(recordList, 0)