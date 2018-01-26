# coding: utf-8
'''
@author: d.bozhenko

'''
import json
from java.util import ArrayList

try:
    from ru.curs.showcase.core.jython import JythonDTO
    from ru.curs.showcase.core.selector import ResultSelectorData
    from ru.curs.showcase.app.api.selector import DataRecord
    from ru.curs.showcase.security import SecurityParamsFactory
except:
    from ru.curs.celesta.showcase import JythonDTO
from ru.curs.celesta import CelestaException
from ru.curs.celesta.showcase.utils import XMLJSONConverter
from security.functions import Settings, getUsersFromAuthServer, id_generator
import hashlib
from common.numbersseries.getNextNo import getNextNoOfSeries
from common.sysfunctions import tableCursorImport
from security._security_orm import loginsCursor
from security._security_orm import subjectsCursor


# Процедуры карточки и сохранения для всех четырёх режимов работы гранулы


def cardData(context, main=None, add=None, filterinfo=None, session=None, elementId=None):

    settings = Settings()
    session = json.loads(session)['sessioncontext']
    logins = loginsCursor(context)
    subjects = subjectsCursor(context)
    isEmployees = settings.isEmployees()    # описана ли таблица сотрудников в настройках?
    if isEmployees:
        # Если да, получаем наименование таблицы, импортируем курсор, создаём экземпляр курсора сотрудников
        employeesGrain = settings.getEmployeesParam("employeesGrain")
        employeesTable = settings.getEmployeesParam("employeesTable")
        employeesId = settings.getEmployeesParam("employeesId")    # название поля с первичным ключом
        employeesName = settings.getEmployeesParam("employeesName")    # название поля с именем
        employeesCursor = tableCursorImport(employeesGrain, employeesTable)
        employees = employeesCursor(context)
    subjectId = ""
    subjectName = ""
    empId = ""
    empName = ""
    if 'currentRecordId' in session['related']['gridContext']:
        currId = session['related']['gridContext']['currentRecordId']
        currIdSid = ""
        if settings.loginIsSubject() and settings.isUseAuthServer():
            # в данном режиме Id записи грида - sid+username. делано, чтобы достать сид в карточке,
            # не перелопачивая весь xml пользователй.
            currIdSid = json.loads(currId)[1]
            currId = json.loads(currId)[0]
        if logins.tryGet(currId) and add <> 'add':
            if subjects.tryGet(logins.subjectId):
                subjectId = subjects.sid
                subjectName = subjects.name
                if isEmployees:
                    if employees.tryGet(subjects.employeeId):
                        empId = getattr(employees, employeesId)
                        empName = getattr(employees, employeesName)

    if add == 'add':
        xformsdata = {"schema":{"@xmlns":"",
                                "user":{"@sid": "",
                                        "@password": id_generator(),
                                        "@userName": "",
                                        "@subjectId":"",
                                        "@subjectName":"",
                                        "@employeeId":"",
                                        "@employeeName":"",
                                        "@isAuthServer":unicode(settings.isUseAuthServer()).lower(),
                                        "@loginIsSubject":unicode(settings.loginIsSubject()).lower(),
                                        "@add":add,
                                        "@isEmployees":unicode(isEmployees).lower(),
                                        "@key":unichr(9911)    # символ ключа, например. Уже не нужен, в иконку вставляется картинка.
                                        }
                                }
                      }
        if settings.loginIsSubject():
            # если логины тождественны субъектам, при открытии карточки генерится sid
            sid = getNextNoOfSeries(context, 'subjects') + id_generator()    # последнее слагаемое сделано, чтобы sid
                                                                        # нельзя было просто подобрать.
            xformsdata["schema"]["user"]["@subjectId"] = sid
            xformsdata["schema"]["user"]["@sid"] = sid
    elif add == 'edit' and settings.isUseAuthServer():
        # Редактирование в случае получения пользователей из mellophone
        xformsdata = {"schema":{"user":{"@sid": currIdSid,
                                        "@password": "",
                                        "@userName": currId,
                                        "@subjectId":subjectId,
                                        "@subjectName":subjectName,
                                        "@employeeId":empId,
                                        "@employeeName":empName,
                                        "@isAuthServer":unicode(settings.isUseAuthServer()).lower(),
                                        "@loginIsSubject":unicode(settings.loginIsSubject()).lower(),
                                        "@add":"",
                                        "@isEmployees":unicode(isEmployees).lower(),
                                        "@key":unichr(9911)}
                                }
                      }
    elif add == 'edit':
        # Редактирование в случае получения пользователей из таблицы logins
        logins.get(currId)
        xformsdata = {"schema":{"user":{"@sid": logins.subjectId,
                                        "@password": "",
                                        "@userName": logins.userName,
                                        "@subjectId":subjectId,
                                        "@subjectName":subjectName,
                                        "@employeeId":empId,
                                        "@employeeName":empName,
                                        "@isAuthServer":unicode(settings.isUseAuthServer()).lower(),
                                        "@loginIsSubject":unicode(settings.loginIsSubject()).lower(),
                                        "@add":add,
                                        "@isEmployees":unicode(isEmployees).lower()
                                        }
                                }
                      }

    xformssettings = {"properties":{"event":{"@name":"single_click",
                                             "@linkId": "1",
                                             "action":{"#sorted":[{"main_context": "current"},
                                                                   {"datapanel": {"@type": "current",
                                                                                 "@tab": "current",
                                                                                 "element":{"@id":"usersGrid",
                                                                                            "add_context": ""}
                                                                                 }
                                                                   }]}
                                             }
                                    }
                      }

    return JythonDTO(XMLJSONConverter.jsonToXml(json.dumps(xformsdata)), XMLJSONConverter.jsonToXml(json.dumps(xformssettings)))


def cardDataSave(context, main=None, add=None, filterinfo=None, session=None, elementId=None, xformsdata=None):
    u'''Функция сохранения карточки редактирования содержимого справочника ролей. '''
    settings = Settings()

    logins = loginsCursor(context)
    subject = subjectsCursor(context)
    content = json.loads(xformsdata)["schema"]["user"]
    if not settings.isUseAuthServer():
        # пользователи берутся из logins
        if add == 'edit' and content["@password"] == '':
            # если пароль при редактировании не заполнен, сохраняем в курсоре старый пароль из базы
            loginsOld = loginsCursor(context)
            currId = json.loads(session)['sessioncontext']['related']['gridContext']['currentRecordId']
            loginsOld.get(currId)
            logins.password = loginsOld.password
        else:
            # иначе зашифровываем пароль из карточки
            pass_hash = hashlib.sha1()    # Объект типа hash
            pass_hash.update(content["@password"])    # Записываем в него текущий пароль из карточки
            logins.password = pass_hash.hexdigest()    # Выполняем hash функцию, записываем результат в курсор.
        logins.userName = content["@userName"]

        if content["@subjectId"] == "":    # если subjectId из карточки пуст, записываем в базу null
            logins.subjectId = None
        else:
            logins.subjectId = content["@subjectId"]

        if settings.loginIsSubject():
            # если loginIsSubject, cохраняем сначала subject, потом login
            subject.sid = content["@subjectId"]
            subject.name = content["@userName"]

            if content["@employeeId"] == '':    # если employeeId из карточки пуст (сотрудник не выбран), записываем в базу null
                subject.employeeId = None
            else:
                subject.employeeId = content["@employeeId"]


            # сохранение subject
            if add == 'add' and subject.canInsert() and subject.canModify():
                if not subject.tryInsert():
                    subjectOld = subjectsCursor(context)
                    subjectOld.get(content["@subjectId"])
                    subject.recversion = subjectOld.recversion
                    subject.update()
            elif add == 'add' and logins.canInsert():
                subject.insert()
            elif add == 'edit' and subject.canModify():
                subjectOld = subjectsCursor(context)
                subjectOld.get(content["@subjectId"])
                subject.recversion = subjectOld.recversion
                subject.update()
            else:
                raise CelestaException(u"Недостаточно прав для данной операции!")

        # сохранение login
        if add == 'add' and logins.canInsert() and logins.canModify():
            if not logins.tryInsert():
                logins.update()
        elif add == 'add' and logins.canInsert():
            logins.insert()
        elif add == 'edit' and logins.canModify():
            loginsOld = loginsCursor(context)
            loginsOld.get(content["@userName"])
            logins.recversion = loginsOld.recversion
            logins.update()
        else:
            raise CelestaException(u"Недостаточно прав для данной операции!")
    else:
        # пользователи берутся из mellophone
        if logins.tryGet(content["@userName"]):
            # если запись с данным userName уже есть в таблице logins (Подробнее см. ниже)
            if settings.loginIsSubject():
                # если логины и субъекты тождественны
                if not subject.tryGet(logins.subjectId):
                    subject.sid = content["@sid"]
                if content["@employeeId"] == '':
                    subject.employeeId = None
                else:
                    subject.employeeId = content["@employeeId"]
                subject.name = content["@userName"]
                logins.subjectId = subject.sid

                # сохранение subject. Запись в logins уже есть
                if subject.canInsert() and subject.canModify():
                    if not subject.tryInsert():
                        subjectOld = subjectsCursor(context)
                        subjectOld.get(content["@subjectId"])
                        subject.recversion = subjectOld.recversion
                        subject.update()
                elif subject.canModify():
                    subjectOld = subjectsCursor(context)
                    subjectOld.get(content["@subjectId"])
                    subject.recversion = subjectOld.recversion
                    subject.update()
                else:
                    raise CelestaException(u"Недостаточно прав для данной операции!")
            else:
                # Если субъекты тождественны сотрудникам, записываем subjectId в logins.subjectId
                # то есть, просто привязываем logins к subjects
                if content["@subjectId"] == "":
                    logins.subjectId = None
                else:
                    logins.subjectId = content["@subjectId"]

            # обновление logins
            if logins.canModify():
                logins.update()
            else:
                raise CelestaException(u"Недостаточно прав для данной операции!")
        else:
            # пользователя нет в logins
            if settings.loginIsSubject():
                # если loginIsSubject, cохраняем сначала subject, потом login
                if content["@employeeId"] == '':
                    logins.subjectId = None
                else:
                    subject.employeeId = content["@employeeId"]
                    subject.sid = content["@sid"]
                    subject.name = content["@userName"]
                    logins.subjectId = subject.sid
                    if subject.canInsert() and subject.canModify():
                        if not subject.tryInsert():
                            subjectOld = subjectsCursor(context)
                            subjectOld.get(content["@subjectId"])
                            subject.recversion = subjectOld.recversion
                            subject.update()
                    elif subject.canInsert():
                        subject.insert()
                    else:
                        raise CelestaException(u"Недостаточно прав для данной операции!")
            else:
                if content["@subjectId"] == "":
                    logins.subjectId = None
                else:
                    logins.subjectId = content["@subjectId"]
            # В случае, когда пользователи берутся из mellophone, и к пользователю делается привязка,
            # (<employees> к logins-subjects или <employees>-subjects к logins)
            # делается запись в logins
            logins.userName = content["@userName"]
            logins.password = ""
            if logins.canInsert():
                logins.insert()
            else:
                raise CelestaException(u"Недостаточно прав для данной операции!")


def subjectsCount(context, main=None, add=None, filterinfo=None, session=None, params=None,
                curvalue=None, startswith=None):
    u'''Функция count для селектора выбора ролей. '''

    subjects = subjectsCursor(context)
    subjects.setFilter('name', """@%s'%s'%%""" % ("%"*(not startswith), curvalue.replace("'", "''")))
    count = subjects.count()

    return ResultSelectorData(None, count)

def subjectsList(context, main=None, add=None, filterinfo=None, session=None, params=None,
                curvalue=None, startswith=None, firstrecord=None, recordcount=None):
    u'''Функция list для селектора выбора ролей. '''

    subjects = subjectsCursor(context)
    subjects.setFilter('name', """@%s'%s'%%""" % ("%"*(not startswith), curvalue.replace("'", "''")))
    subjects.orderBy('name')
    subjects.limit(firstrecord, recordcount)

    recordList = ArrayList()
    for subjects in subjects.iterate():
        rec = DataRecord()
        rec.setId(unicode(subjects.sid))
        rec.setName(subjects.name)
        recordList.add(rec)

    return ResultSelectorData(recordList, 0)

def employeesCount(context, main=None, add=None, filterinfo=None, session=None, params=None,
                curvalue="", startswith=False):
    settings = Settings()

    employeesGrain = settings.getEmployeesParam("employeesGrain")
    employeesTable = settings.getEmployeesParam("employeesTable")
#    employeesId=getEmployeesParam("employeesId")
    employeesName = settings.getEmployeesParam("employeesName")

    employeesCursor = tableCursorImport(employeesGrain, employeesTable)
    employees = employeesCursor(context)
    employees.setFilter(employeesName, "@%s'%s'%%" % ("%"*(not startswith), curvalue.replace("'", "''")))
    count = employees.count()
    # raise Exception(count)
    return ResultSelectorData(None, count)

def employeesList(context, main=None, add=None, filterinfo=None, session=None, params=None,
                curvalue="", startswith=False, firstrecord=0, recordcount=None):
    u'''Функция list селектора типа элемента. '''
    settings = Settings()

    # raise Exception(curvalue)
    recordList = ArrayList()
    employeesGrain = settings.getEmployeesParam("employeesGrain")
    employeesTable = settings.getEmployeesParam("employeesTable")
    employeesId = settings.getEmployeesParam("employeesId")
    employeesName = settings.getEmployeesParam("employeesName")

    employeesCursor = tableCursorImport(employeesGrain, employeesTable)
    employees = employeesCursor(context)
    employees.setFilter(employeesName, "@%s'%s'%%" % ("%"*(not startswith), curvalue.replace("'", "''")))
    employees.orderBy(employeesName)
    employees.limit(firstrecord, recordcount)
    for employees in employees.iterate():
        rec = DataRecord()
        rec.setId(getattr(employees, employeesId))
        rec.setName(getattr(employees, employeesName))
        recordList.add(rec)
    return ResultSelectorData(recordList, 0)




# Триггеры для автоматического добавления, редактирования, удаления записей в subjects при редактировании таблицы сотрудников.
# Добавляются в случае, если субъекты тождественны сотрудникам.

def employeesSubjectsPostInsert(rec):
    # Триггер добавляется только в случае loginEqualSubject = "false"
    settings = Settings()

    context = rec.callContext()
    employeesId = settings.getEmployeesParam("employeesId")
    employeesName = settings.getEmployeesParam("employeesName")
    sid = getNextNoOfSeries(context, 'subjects') + id_generator()
    subjects = subjectsCursor(context)
    subjects.sid = sid
    subjects.name = getattr(rec, employeesName)
    subjects.employeeId = getattr(rec, employeesId)
    if subjects.canInsert() and subjects.canModify():
        if not subjects.tryInsert():
            subjects.update()
    elif subjects.canInsert():
        subjects.insert()

def employeesSubjectsPostUpdate(rec):
    # Триггер добавляется только в случае loginEqualSubject = "false"
    settings = Settings()

    context = rec.callContext()
    employeesId = settings.getEmployeesParam("employeesId")
    employeesName = settings.getEmployeesParam("employeesName")
    subjects = subjectsCursor(context)
    subjects.setRange("employeeId", getattr(rec, employeesId))
    if subjects.tryFirst():    # and subjects.count()==1:
        subjects.name = getattr(rec, employeesName)
        if subjects.canModify():
            subjects.update()

def employeesSubjectsPreDelete(rec):
    # Триггер добавляется только в случае loginEqualSubject = "false"
    settings = Settings()

    context = rec.callContext()
    employeesId = settings.getEmployeesParam("employeesId")
    subjects = subjectsCursor(context)
    subjects.setRange("employeeId", getattr(rec, employeesId))
    if subjects.canDelete():
        subjects.deleteAll()

