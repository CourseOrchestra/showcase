# coding: utf-8

import json

from common.sysfunctions import tableCursorImport, getGridHeight, toHexForXml
from ru.curs.celesta.showcase.utils import XMLJSONConverter
from security._security_orm import loginsCursor, subjectsCursor
from security.functions import Settings
import security.functions as func


try:
    from ru.curs.showcase.core.jython import JythonDTO
except:
    from ru.curs.celesta.showcase import JythonDTO

try:
    from ru.curs.showcase.security import SecurityParamsFactory
except:
    pass


# функции грида пользователей одни на все 4 режима работы гранулы.


def gridData(context, main=None, add=None, filterinfo=None,
             session=None, elementId=None, sortColumnList=None, firstrecord=None, pagesize=None):
    u'''Функция получения данных для грида списка пользователей. '''
    session = json.loads(session)["sessioncontext"]
    settings = Settings()
    logins = loginsCursor(context)
    if sortColumnList:
        sortName = toHexForXml(sortColumnList[0].id)
        sortType = unicode(sortColumnList[0].sorting).lower()
    else:
        sortName = None
    # Определяем переменную для JSON данных
    data = {"records":{"rec":[]}}
    _header = {"id": ["~~id"],
               "sid": [u"SID"],
               "userName": [u"Имя пользователя"],
               "subject": [u"Субъект"],
               "employee": [u"Сотрудник"],
               "properties": [u"properties"]
               }
    event = {"event": {"@name": "row_single_click",
                       "action": {"#sorted": [{"main_context": 'current'},
                                              {"datapanel":{"@type":"current",
                                                            "@tab":"current"}
                                               }]
                                  }
                       }
             }
    for column in _header:
        _header[column].append(toHexForXml(_header[column][0]))

    isEmployees = settings.isEmployees()

    if isEmployees:
        employeesGrain = settings.getEmployeesParam("employeesGrain")
        employeesTable = settings.getEmployeesParam("employeesTable")
        employeesName = settings.getEmployeesParam("employeesName")  # название поля с именем
        employeesCursor = tableCursorImport(employeesGrain, employeesTable)
        employees = employeesCursor(context)

    if settings.isUseAuthServer():
        sessionId = session["sessionid"]  # получаем из контекста сессии Id сессии
        server = SecurityParamsFactory.getAuthServerUrl()  # получаем url mellophone
        logins_xml = func.getUsersFromAuthServer(server, sessionId)  # получаем xml с пользователями

    if settings.isUseAuthServer() and settings.loginIsSubject():
        # грид состоит из колонок sid, имя пользователя и сотрудник
        subjects = subjectsCursor(context)
        for i, user in enumerate(logins_xml.getElementsByTagName("user")):
            if i < firstrecord - 1:
                continue  # пропускаем элементы с 1 по firstrecord
            loginsDict = {}
            loginsDict[_header["id"][1]] = json.dumps([user.getAttribute("login"),
                                                       user.getAttribute("SID")])
            loginsDict[_header["sid"][1]] = user.getAttribute("SID")
            loginsDict[_header["userName"][1]] = user.getAttribute("login")
            if isEmployees:
                # если таблица сотрудников существует (прописана в настройках)
                # добавляем в грид сотрудника колонку Сотрудник.
                if logins.tryGet(user.getAttribute("login")) and \
                        subjects.tryGet(logins.subjectId) and\
                        employees.tryGet(subjects.employeeId):
                    loginsDict[_header["employee"][1]] = getattr(employees, employeesName)
                else:
                    loginsDict[_header["employee"][1]] = ''
            loginsDict['properties'] = event
            data["records"]["rec"].append(loginsDict)
            if i >= firstrecord + pagesize:
                break  # прерываем цикл после достижения записи № firstrecord + pagesize
    elif settings.isUseAuthServer():
        # грид состоит из колонок sid, имя пользователя и субъект
        subjects = subjectsCursor(context)
        for i, user in enumerate(logins_xml.getElementsByTagName("user")):
            if i < firstrecord - 1:
                continue  # пропускаем элементы с 1 по firstrecord

            loginsDict = {}
            loginsDict[_header["id"][1]] = user.getAttribute("login")
            loginsDict[_header["sid"][1]] = user.getAttribute("SID")
            loginsDict[_header["userName"][1]] = user.getAttribute("login")
            if logins.tryGet(user.getAttribute("login")) and subjects.tryGet(logins.subjectId):
                loginsDict[_header["subject"][1]] = subjects.name
            else:
                loginsDict[_header["subject"][1]] = ''
            loginsDict['properties'] = event

            data["records"]["rec"].append(loginsDict)
            if i >= firstrecord + pagesize:
                break  # прерываем цикл после достижения записи № firstrecord + pagesize

    elif not settings.isUseAuthServer() and not settings.loginIsSubject():
        # грид состоит из колонок имя пользователя и субъект
        subjects = subjectsCursor(context)

        logins.limit(firstrecord - 1, pagesize)

        for logins in logins.iterate():
            loginsDict = {}
            loginsDict[_header["id"][1]] = logins.userName
            loginsDict[_header["userName"][1]] = logins.userName

            if subjects.tryGet(logins.subjectId):
                loginsDict[_header["subject"][1]] = subjects.name
            else:
                loginsDict[_header["subject"][1]] = ""

            loginsDict['properties'] = event

            data["records"]["rec"].append(loginsDict)
    else:
        # грид состоит из колонки имя пользователя
        subjects = subjectsCursor(context)
        logins.limit(firstrecord - 1, pagesize)
        for logins in logins.iterate():
            loginsDict = {}
            loginsDict[_header["id"][1]] = logins.userName
            loginsDict[_header["userName"][1]] = logins.userName
            if isEmployees and subjects.tryGet(logins.subjectId) and employees.tryGet(subjects.employeeId):
                loginsDict[_header["employee"][1]] = getattr(employees, employeesName)
            else:
                loginsDict[_header["employee"][1]] = ""


            loginsDict['properties'] = event
            data["records"]["rec"].append(loginsDict)

    # сортировка
    if sortColumnList:
        data["records"]["rec"].sort(key=lambda x: (x[unicode(sortName)].lower()), reverse=(sortType == 'desc'))
    res = XMLJSONConverter.jsonToXml(json.dumps(data))
    return JythonDTO(res, None)

def gridMeta(context, main=None, add=None, filterinfo=None, session=None, elementId=None):
    u'''Функция получения настроек грида. '''
    settings = Settings()

    if settings.isUseAuthServer():
        server = SecurityParamsFactory.getAuthServerUrl()
        sessionId = json.loads(session)["sessioncontext"]["sessionid"]
        logins_xml = func.getUsersFromAuthServer(server, sessionId)
        totalcount = len(logins_xml.getElementsByTagName("user"))
    else:
        logins = loginsCursor(context)
        # Вычисляем количества записей в таблице
        totalcount = logins.count()
    # Заголовок таблицы
    _header = {
               "sid":[u"SID"],
               "userName":[u"Имя пользователя"],
               "subject": [u"Субъект"],
               "employee": [u"Сотрудник"]
               }


    gridSettings = {}
    if settings.loginIsSubject():
        number = 1
    else:
        number = 2
    gridSettings["gridsettings"] = {"columns": {"col":[]},
                                    "properties": {"@pagesize":"50",
                                                   "@gridWidth": "100%",
                                                   "@gridHeight": getGridHeight(session, number, delta=40),
                                                   "@totalCount": totalcount},
                                    "labels":{"header": u"Пользователи"},
                                }
    # добавляем поля для отображения в gridsettings
    if settings.isUseAuthServer():
        gridSettings["gridsettings"]["columns"]["col"].append({"@id":_header["sid"][0],
                                                               "@width": "320px"})
    gridSettings["gridsettings"]["columns"]["col"].append({"@id":_header["userName"][0],
                                                           "@width": "320px"})
    if settings.loginIsSubject() and settings.isEmployees():
        gridSettings["gridsettings"]["columns"]["col"].append({"@id":_header["employee"][0],
                                                               "@width": "640px"})
    elif not settings.loginIsSubject():
        gridSettings["gridsettings"]["columns"]["col"].append({"@id":_header["subject"][0],
                                                               "@width": "640px"})


    # Добавляем поля для отображения в gridsettings
    res = XMLJSONConverter.jsonToXml(json.dumps(gridSettings))
    return JythonDTO(None, res)

def gridToolBar(context, main=None, add=None, filterinfo=None, session=None, elementId=None):
    u'''Toolbar для грида. '''
    settings = Settings()

    # raise Exception(session)
    if 'currentRecordId' not in json.loads(session)['sessioncontext']['related']['gridContext'] and not settings.isUseAuthServer():
        style_add = "false"
        style_edit = "true"
        style_delete = "true"
        style_roles = "true"
    elif not settings.isUseAuthServer():
        style_add = "false"
        style_edit = "false"
        style_delete = "false"
        if settings.loginIsSubject():
            style_roles = "false"
        else:
            style_roles = "true"
    elif 'currentRecordId' not in json.loads(session)['sessioncontext']['related']['gridContext']:
        style_add = "true"
        style_edit = "true"
        style_delete = "true"
        style_roles = "true"
    else:
        style_add = "true"
        style_edit = "false"
        if settings.loginIsSubject():
            style_roles = "false"
        else:
            style_roles = "true"
        style_delete = "true"


    data = {"gridtoolbar":{"item":[{"@img": 'gridToolBar/addDirectory.png',
                                    "@text":"Добавить",
                                    "@hint":"Добавить пользователя",
                                    "@disable": style_add
                                    },
                                   {"@img": 'gridToolBar/editDocument.png',
                                    "@text":"Редактировать",
                                    "@hint":"Редактировать пользователя",
                                    "@disable": style_edit
                                    },
                                   {"@img": 'gridToolBar/deleteDocument.png',
                                    "@text":"Удалить",
                                    "@hint":"Удалить пользователя",
                                    "@disable": style_delete
                                    },
                                   {"@img": 'gridToolBar/addDirectory.png',
                                    "@text":"Добавить роли",
                                    "@hint":"Добавить роли",
                                    "@disable": style_roles
                                    }]
                           }
            }

    data["gridtoolbar"]["item"][0]["action"] = func.getActionJSON("add", "usersXform", "Добавление пользователя", "350", "500")
    data["gridtoolbar"]["item"][1]["action"] = func.getActionJSON("edit", "usersXform", "Редактирование пользователя", "350", "500")
    data["gridtoolbar"]["item"][2]["action"] = func.getActionJSON("delete", "usersXformDelete", "Удаление пользователя", "150", "450")
    data["gridtoolbar"]["item"][3]["action"] = func.getActionJSON("roles", "usersRolesXform", "Добавление ролей", "350", "500")

    return XMLJSONConverter.jsonToXml(json.dumps(data))
