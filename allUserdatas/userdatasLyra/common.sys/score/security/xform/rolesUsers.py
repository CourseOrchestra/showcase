# coding: utf-8
'''
Created on 03.12.2013

@author: d.bozhenko

'''
import json

from java.util import ArrayList
import xml.dom.minidom
import string

from ru.curs.celesta import CelestaException
from ru.curs.celesta.showcase.utils import XMLJSONConverter
from ru.curs.celesta.syscursors import UserRolesCursor
#from ru.curs.celesta.syscursors import RolesCursor
from security._security_orm import loginsCursor
from security._security_orm import subjectsCursor
from security.functions import Settings
from security.functions import getUsersFromAuthServer
try:
    from ru.curs.showcase.security import SecurityParamsFactory
except:
    print 1

try:
    from ru.curs.showcase.core.jython import JythonDTO
    from ru.curs.showcase.core.selector import ResultSelectorData
    from ru.curs.showcase.app.api.selector import DataRecord
except:
    from ru.curs.celesta.showcase import JythonDTO
    #from ru.curs.celesta.showcase import ResultSelectorData
    #from ru.curs.celesta.showcase import DataRecord

def cardData(context, main=None, add=None, filterinfo=None, session=None, elementId=None):
    u'''Функция данных для карточки редактирования содержимого таблицы ролей. '''
    
    settings=Settings()

    #ru.curs.showcase.security.SecurityParamsFactory.getAuthServerUrl()
    rolesUsers = UserRolesCursor(context)
    currId = json.loads(session)['sessioncontext']['related']['gridContext']['currentRecordId']
    rolesUsers.setRange("roleid", currId)    
    content=[]
    if settings.isUseAuthServer() and settings.loginIsSubject():            
        sessionId=json.loads(session)["sessioncontext"]["sessionid"]
        server=SecurityParamsFactory.getAuthServerUrl()                
        users_xml=getUsersFromAuthServer(server, sessionId)        
        if rolesUsers.tryFindSet():
            while True:
                for user in users_xml.getElementsByTagName("user"):
                    if user.getAttribute("SID")==rolesUsers.userid:
                        content.append({"@sid" : rolesUsers.userid,
                                        "@userName" : user.getAttribute("name")
                                        })
                        break                                                
                if not rolesUsers.nextInSet():
                    break
    else:
        subjects = subjectsCursor(context)
        if rolesUsers.tryFindSet():
            while True:
                if subjects.tryGet(rolesUsers.userid):
                    content.append({"@sid" : subjects.sid,
                                    "@userName" : subjects.name
                                    })
                if not rolesUsers.nextInSet():
                    break
                
    if content==[]:
        xformsdata = {"schema":{"users": ""
                                }
                      }
    else:
        xformsdata = {"schema":{"users": {"user": content}
                                }
                      }

    xformssettings = {"properties":{"event":{"@name":"single_click",
                                             "@linkId": "1",
                                             "action":{"#sorted":[{"main_context": "current"},
                                                                   {"datapanel": {"@type": "current",
                                                                                 "@tab": "current",
                                                                                 "element": {"@id":"id_roles_grid",
                                                                                             "add_context": ""}
                                                                                 }
                                                                   }]}
                                             }
                                    }
                      }

    return JythonDTO(XMLJSONConverter.jsonToXml(json.dumps(xformsdata)), XMLJSONConverter.jsonToXml(json.dumps(xformssettings)))


def cardDataSave(context, main=None, add=None, filterinfo=None, session=None, elementId=None, xformsdata=None):
    u'''Функция сохранения карточки редактирования содержимого справочника ролей. '''    
    rolesUsers = UserRolesCursor(context)
    currId = json.loads(session)['sessioncontext']['related']['gridContext']['currentRecordId']    
    rolesUsersOld = UserRolesCursor(context)
    rolesUsers.setRange("roleid", currId)
    rolesUsers.deleteAll()
    
    content = json.loads(xformsdata)["schema"]["users"]
    
    if content != "":    
        content=content["user"] if isinstance(content["user"], list) else [content["user"]]
    
        for user in content:
            rolesUsers.userid=user["@sid"]
            rolesUsers.roleid=currId
            if rolesUsers.canInsert() and rolesUsers.canModify():        
                if not rolesUsers.tryInsert():                
                    rolesUsersOld.get(user["@sid"], currId)
                    rolesUsers.recversion = rolesUsersOld.recversion  
                    rolesUsers.update()
            elif rolesUsers.canInsert():
                rolesUsers.tryInsert()
            elif rolesUsers.canModify():            
                rolesUsersOld.get(user["@sid"], currId)
                rolesUsers.recversion = rolesUsersOld.recversion  
                rolesUsers.update()
            else:
                raise CelestaException(u"Недостаточно прав для данной операции!")


def usersCount(context, main=None, add=None, filterinfo=None, session=None, params=None,
                curvalue=None, startswith=None):
    #raise Exception(params)   
    settings=Settings()
    if settings.isUseAuthServer() and settings.loginIsSubject():
        server=SecurityParamsFactory.getAuthServerUrl()            
        sessionId=json.loads(session)["sessioncontext"]["sessionid"]
        users_xml=getUsersFromAuthServer(server, sessionId)        
        count=0        
        for user in users_xml.getElementsByTagName("user"):
            if startswith and string.find(user.getAttribute("name"), curvalue)==0 or \
                    not startswith and string.find(user.getAttribute("name"), curvalue)>0:
                count+=1                
        #count=len(users_xml.getElementsByTagName("user"))
    else:        
        subject = subjectsCursor(context)
        #raise Exception(useAuthServer)
        subject.setFilter('name', "@%s'%s'%%" % ("%"*(not startswith), curvalue.replace("'","''")))        
        count = subject.count()    
    return ResultSelectorData(None, count)

def usersList(context, main=None, add=None, filterinfo=None, session=None, params=None,
                curvalue=None, startswith=None, firstrecord=None, recordcount=None):
    u'''Функция list селектора типа элемента. '''
    settings=Settings()      
    recordList = ArrayList()
    #raise Exception(startswith, curvalue)
    if settings.isUseAuthServer() and settings.loginIsSubject():        
        sessionId=json.loads(session)["sessioncontext"]["sessionid"]
        server=SecurityParamsFactory.getAuthServerUrl()
        users_xml=getUsersFromAuthServer(server, sessionId)
        for user in users_xml.getElementsByTagName("user"):
            if startswith and string.find(user.getAttribute("name"), curvalue)==0 or \
                    not startswith and string.find(user.getAttribute("name"), curvalue)>0:
                rec = DataRecord()
                rec.setId(user.getAttribute("SID"))
                rec.setName(user.getAttribute("login"))
                recordList.add(rec)        
    else:
        subject = subjectsCursor(context)        
        subject.setFilter('name', "@%s'%s'%%" % ("%"*(not startswith), curvalue.replace("'","''")))        
        subject.orderBy('name')        
        subject.limit(firstrecord, recordcount)
        for subject in subject.iterate():
            rec = DataRecord()
            rec.setId(subject.sid)
            if subject.name is not None or subject.name!='':
                rec.setName(subject.name)
            else:
                rec.setName(u'[Имя не назначено!]')
            recordList.add(rec)        
    return ResultSelectorData(recordList, 0)