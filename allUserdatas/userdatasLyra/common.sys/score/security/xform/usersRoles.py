# coding: utf-8
'''
Created on 03.12.2013

@author: d.bozhenko

'''
import json
from java.util import ArrayList

from ru.curs.celesta import CelestaException
from ru.curs.celesta.showcase.utils import XMLJSONConverter
from ru.curs.celesta.syscursors import UserRolesCursor, RolesCursor
from security._security_orm import loginsCursor, subjectsCursor
from security.functions import Settings

try:
    from ru.curs.showcase.security import SecurityParamsFactory
except:
    pass

try:
    from ru.curs.showcase.core.jython import JythonDTO
    from ru.curs.showcase.core.selector import ResultSelectorData
    from ru.curs.showcase.app.api.selector import DataRecord
except:
    from ru.curs.celesta.showcase import JythonDTO
    # from ru.curs.celesta.showcase import ResultSelectorData
    # from ru.curs.celesta.showcase import DataRecord

def cardData(context, main=None, add=None, filterinfo=None, session=None, elementId=None):
    u'''Функция данных для карточки редактирования содержимого таблицы ролей. '''

    settings = Settings()

    # ru.curs.showcase.security.SecurityParamsFactory.getAuthServerUrl()
    rolesUsers = UserRolesCursor(context)
    roles = RolesCursor(context)
    logins = loginsCursor(context)

    currId = json.loads(session)['sessioncontext']['related']['gridContext']['currentRecordId']
    if settings.isUseAuthServer() and settings.loginIsSubject():
        # В данном режиме в currId передаются имя пользователя и сид.
        # Сделано так, чтобы не лопатить огромный xml, приходящий из меллофона
        currId = json.loads(currId)
        if not logins.tryGet(currId[0]):
            # in "tt" regime we copy record from mellophone(AuthServer) to logins if it doesn't appear in logins.
            subjects = subjectsCursor(context)
            subjects.name = currId[0]
            subjects.sid = currId[1]
            subjects.insert()
            logins.userName = currId[0]
            logins.subjectId = currId[1]
            logins.password = ""
            logins.insert()
        rolesUsers.setRange("userid", logins.subjectId)
    elif settings.loginIsSubject():
        logins.get(currId)
        rolesUsers.setRange("userid", logins.subjectId)    # роли всегда привязываются к сидам
    else:
        rolesUsers.setRange("userid", currId)
    content = []
    if rolesUsers.tryFindSet():
        while True:
            if roles.tryGet(rolesUsers.roleid):
                content.append({"@id" : roles.id,
                                "@description" : '%s - %s' % (roles.id, roles.description if roles.description else '')
                                })
            if not rolesUsers.nextInSet():
                break

    if content == []:
        xformsdata = {"schema":{"roles":""}
                      }
    else:
        xformsdata = {"schema":{"roles": {"role": content}
                                }
                      }
        # сортировка
        xformsdata["schema"]["roles"]["role"].sort(key=lambda x: (x["@id"].lower()))


    # raise Exception(xformsdata)

    # print xformsdata
    xformssettings = {"properties":{"event":{"@name":"single_click",
                                             "@linkId": "1",
                                             "action":{"#sorted":[{"main_context": "current"},
                                                                   {"datapanel": {"@type": "current",
                                                                                 "@tab": "current",
                                                                                 "element": {"@id":"usersGrid",
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
    rolesUsers = UserRolesCursor(context)
    logins = loginsCursor(context)
    currId = json.loads(session)['sessioncontext']['related']['gridContext']['currentRecordId']
    settings = Settings()
    if settings.isUseAuthServer() and settings.loginIsSubject():
        currId = json.loads(currId)[0]
    logins.get(currId)
    rolesUsers.setRange("userid", logins.subjectId)
    rolesUsers.deleteAll()
    if json.loads(xformsdata)["schema"]["roles"] <> '':
        content = json.loads(xformsdata)["schema"]["roles"]["role"]
        content = content if isinstance(content, list) else [content]
        rolesUsersOld = UserRolesCursor(context)
        for role in content:
            rolesUsers.roleid = role["@id"]
            rolesUsers.userid = logins.subjectId
            if rolesUsers.canInsert() and rolesUsers.canModify():
                if not rolesUsers.tryInsert():
                    rolesUsersOld.get(logins.subjectId, role["@id"])
                    rolesUsers.recversion = rolesUsersOld.recversion
                    rolesUsers.update()
            elif rolesUsers.canInsert():
                rolesUsers.tryInsert()
            elif rolesUsers.canModify():
                rolesUsersOld.get(logins.subjectId, role["@id"])
                rolesUsers.recversion = rolesUsersOld.recversion
                rolesUsers.update()
            else:
                raise CelestaException(u"Недостаточно прав для данной операции!")


def rolesCount(context, main=None, add=None, filterinfo=None, session=None, params=None,
                curvalue="", startswith=False):
    roles = RolesCursor(context)
    roles.setFilter('description', "@%s'%s'%%" % ("%"*(not startswith), curvalue.replace("'", "''")))
    count = roles.count()
    # raise Exception(count)
    return ResultSelectorData(None, count)

def rolesList(context, main=None, add=None, filterinfo=None, session=None, params=None,
                curvalue="", startswith=False, firstrecord=0, recordcount=None):
    u'''Функция list селектора типа элемента. '''
    # raise Exception(curvalue)
    recordList = ArrayList()
    roles = RolesCursor(context)
    roles.setFilter('description', "@%s'%s'%%" % ("%"*(not startswith), curvalue.replace("'", "''")))
    roles.orderBy('id')
    roles.limit(firstrecord, recordcount)
    if roles.tryFindSet():
        while True:
            rec = DataRecord()
            rec.setId(roles.id)
            rec.setName('%s - %s' % (roles.id, roles.description if roles.description else ''))
            recordList.add(rec)
            if not roles.nextInSet():
                break
    return ResultSelectorData(recordList, 0)