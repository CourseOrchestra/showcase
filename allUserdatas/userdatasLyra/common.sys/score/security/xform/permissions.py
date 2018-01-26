# coding: utf-8
'''
Created on 03.12.2013

@author: d.bozhenko

'''
import json
from java.util import ArrayList
import base64
from xml.dom import minidom
try:
    from ru.curs.showcase.core.jython import JythonDTO
    from ru.curs.showcase.core.selector import ResultSelectorData
    from ru.curs.showcase.app.api.selector import DataRecord
except:
    from ru.curs.celesta.showcase import JythonDTO, DataRecord, ResultSelectorData


from ru.curs.celesta import CelestaException
from ru.curs.celesta.showcase.utils import XMLJSONConverter
from ru.curs.celesta.syscursors import PermissionsCursor, RolesCursor, GrainsCursor,TablesCursor



def cardData(context, main=None, add=None, filterinfo=None, session=None, elementId=None):
    u'''Функция данных для карточки редактирования содержимого таблицы разрешения. '''

    permissions = PermissionsCursor(context)    

    if add == 'add':
        formData=json.loads(session)['sessioncontext']['related']["xformsContext"]["formData"]["schema"]
        xformsdata = {"schema":{"roleid": formData["roleid"],
                                "grainid": formData["grainid"],
                                "tablename": formData["tablename"],
                                "r": "",
                                "i": "",
                                "m": "",
                                "d": ""
                                }
                      }
    elif add == 'edit':
        currIdEncoded = json.loads(session)['sessioncontext']['related']['gridContext']['currentRecordId']
        currId = json.loads(base64.b64decode(currIdEncoded))
        permissions.get(*currId)
        xformsdata = {"schema":{"roleid": permissions.roleid,
                                "grainid": permissions.grainid,
                                "tablename": permissions.tablename,
                                "r": unicode(permissions.r).lower(),
                                "i": unicode(permissions.i).lower(),
                                "m": unicode(permissions.m).lower(),
                                "d": unicode(permissions.d).lower()
                                }
                      }


    # print xformsdata
    xformssettings = {"properties":{"event":{"@name":"single_click",
                                             "@linkId": "1",
                                             "action":{"#sorted":[{"main_context": "current"},
                                                                   {"datapanel": {"@type": "current",
                                                                                 "@tab": "current",
                                                                                 "element": {"@id":"permGrid",
                                                                                             "add_context": ""
                                                                                             }
                                                                                 }
                                                                   }]}
                                             }
                                    }
                      }

    return JythonDTO(XMLJSONConverter.jsonToXml(json.dumps(xformsdata)), XMLJSONConverter.jsonToXml(json.dumps(xformssettings)))


def cardDataSave(context, main=None, add=None, filterinfo=None, session=None, elementId=None, xformsdata=None):
    u'''Функция сохранения карточки редактирования содержимого справочника разрешений. '''
    permissions = PermissionsCursor(context)
    content = json.loads(xformsdata)["schema"]    
#    for field in permissions.meta().getColumns():
#        permissions.__setattr__(field, content[field])
    permissions.roleid=content["roleid"]
    permissions.grainid=content["grainid"]
    permissions.tablename=content["tablename"]
    permissions.r=content["r"]=="true"
    permissions.i=content["i"]=="true"
    permissions.m=content["m"]=="true"
    permissions.d=content["d"]=="true"

    if add == 'add' and permissions.canInsert() and permissions.canModify():
        if not permissions.tryInsert():
            permissionsOld = PermissionsCursor(context)
            permissionsOld.get(content["roleid"], content["grainid"], content["tablename"])
            permissionsOld.r=content["r"]=="true"
            permissionsOld.i=content["i"]=="true"
            permissionsOld.m=content["m"]=="true"
            permissionsOld.d=content["d"]=="true"            
            permissionsOld.update()
    elif add == 'add' and permissions.canInsert():
        permissions.insert()
    elif add == 'edit' and permissions.canModify():
        permissionsOld = PermissionsCursor(context)
        currIdEncoded = json.loads(session)['sessioncontext']['related']['gridContext']['currentRecordId']        
        currId = json.loads(base64.b64decode(currIdEncoded))
        permissionsOld.get(*currId)
        if permissionsOld.roleid==permissions.roleid and \
                permissionsOld.grainid==permissions.grainid and \
                permissionsOld.tablename==permissions.tablename:
            permissions.recversion = permissionsOld.recversion
            permissions.update()
        elif permissions.canInsert():
            permissions.insert()
            permissionsOld.delete()
        else:
            raise CelestaException(u"Недостаточно прав для данной операции!")
        
    else:
        raise CelestaException(u"Недостаточно прав для данной операции!")
    
# В системные курсоры нет возможности добавить триггер. Не надо больше писать триггер одновременного ведения разрешений для
# таблиц logins/employees с subjects. Всё равно его к permissionsCursor не прицепить. 


def rolesCount(context, main=None, add=None, filterinfo=None, session=None, params=None,
                curvalue=None, startswith=None):
    u'''Функция count для селектора выбора ролей. '''
    
    #specialRoles = ["!'editor'", "!'reader'"]
    roles = RolesCursor(context)
    roles.setFilter('id', """!'editor'&!'reader'&@%s'%s'%%""" % ("%"*(not startswith), curvalue.replace("'","''")))
    
    count = roles.count()

    return ResultSelectorData(None, count)

def rolesList(context, main=None, add=None, filterinfo=None, session=None, params=None,
                curvalue=None, startswith=None, firstrecord=None, recordcount=None):
    u'''Функция list для селектора выбора ролей. '''
    
    roles = RolesCursor(context)
    roles.setFilter('id', """!'editor'&!'reader'&@%s'%s'%%""" % ("%"*(not startswith), curvalue.replace("'","''")))
    roles.orderBy('id')
    roles.limit(firstrecord, recordcount)

    recordList = ArrayList()
    if roles.tryFindSet():
        while True:
            rec = DataRecord()
            rec.setId(unicode(roles.id))
            rec.setName(roles.id)
            recordList.add(rec)
            if not roles.nextInSet():
                break

    return ResultSelectorData(recordList, 0)

def grainsCount(context, main=None, add=None, filterinfo=None, session=None, params=None,
                curvalue=None, startswith=None):
    u'''Функция count для селектора выбора гранул. '''

    grains = GrainsCursor(context)
    grains.setFilter('id', """@%s'%s'%%""" % ("%"*(not startswith), curvalue.replace("'","''")))
    count = grains.count()

    return ResultSelectorData(None, count)

def grainsList(context, main=None, add=None, filterinfo=None, session=None, params=None,
                curvalue=None, startswith=None, firstrecord=None, recordcount=None):
    u'''Функция list для селектора выбора гранул. '''
    
    grains = GrainsCursor(context)
    grains.setFilter('id', "@%s'%s'%%" % ("%"*(not startswith), curvalue.replace("'","''")))
    grains.orderBy('id')
    grains.limit(firstrecord, recordcount)

    recordList = ArrayList()
    if grains.tryFindSet():
        while True:
            rec = DataRecord()
            rec.setId(unicode(grains.id))
            rec.setName(grains.id)
            recordList.add(rec)
            if not grains.nextInSet():
                break

    return ResultSelectorData(recordList, 0)

def tablesCount(context, main=None, add=None, filterinfo=None, session=None, params=None,
                curvalue=None, startswith=None):
    u'''Функция count для селектора выбора таблиц. '''
    
    paramsXML=minidom.parseString(params)
    tables = TablesCursor(context)
    
    if paramsXML.getElementsByTagName('grainid')[0].childNodes:
        grainid=paramsXML.getElementsByTagName('grainid')[0].childNodes[0].data
        tables.setFilter('tablename', """@%s'%s'%%""" % ("%"*(not startswith), curvalue.replace("'","''")))
        tables.setRange('grainid', grainid)
        count = tables.count()
    else:
        tables.setFilter('tablename', """@%s'%s'%%""" % ("%"*(not startswith), curvalue.replace("'","''")))
        count=tables.count()
        
    return ResultSelectorData(None, count)

def tablesList(context, main=None, add=None, filterinfo=None, session=None, params=None,
                curvalue=None, startswith=None, firstrecord=None, recordcount=None):    
    u'''Функция list для селектора выбора таблиц. '''
    tables = TablesCursor(context)
    recordList = ArrayList()
    paramsXML=minidom.parseString(params)
    if paramsXML.getElementsByTagName('grainid')[0].childNodes:
        grainid=paramsXML.getElementsByTagName('grainid')[0].childNodes[0].data        
        tables.setRange('grainid', grainid)
    tables.setFilter('tablename', "@%s'%s'%%" % ("%"*(not startswith), curvalue.replace("'","''")))
    tables.orderBy('tablename')
    tables.limit(firstrecord, recordcount)
    
        
    if tables.tryFindSet():
        while True:
            rec = DataRecord()
            rec.setId(tables.tablename)
            rec.setName(tables.tablename)
            recordList.add(rec)
            if not tables.nextInSet():
                break

    return ResultSelectorData(recordList, 0)
