# coding: utf-8
"""
@author: d.bozhenko
"""
from java.io import File, FileInputStream, FileOutputStream
import json
import os
import random
import string
import urllib2
from xml.dom.minidom import parseString

from ru.curs.celesta.showcase.utils import XMLJSONConverter
from ru.curs.celesta.syscursors import PermissionsCursor, UserRolesCursor

from common.dbutils import DataBaseXMLExchange
from common.grainssettings import SettingsManager
from security._security_orm import customPermsCursor, rolesCustomPermsCursor


try:
    from ru.curs.showcase.core.jython import JythonDownloadResult
except:
    from ru.curs.celesta.showcase import JythonDownloadResult


class Settings():
    u"""Класс работы с настройками гранулы security из grainSettings.xml

    """
    settings = {}
    settingsTag = 'securitySettings'    # Наименование тэга настроек гранулы
    grainName = 'security'    # название гранулы

    def __init__(self):
        self.settingsInstance = SettingsManager()
        # Создаём объект класса получения настроек свойств _всех_ гранул.

    def getSettingsJSON(self):
        u""" Функция получения JSON'а всех настроек гранулы
            JSON имеет вид:
            {<Имя параметра>: <значение параметра>}
        """
        if self.settings == {}:
            # Если переменная настроек self.settings пуста, получаем настройки из общего файла настроек grainSettings.xml
            for param in ("employeesGrain", "employeesTable", "employeesId", "employeesName", "isSystemInitialised",
                          "useAuthServer", "loginEqualSubject"):
                value = self.settingsInstance.getGrainSettings("%s/parameter[@name='%s']/@value" % (self.settingsTag, param), self.grainName)
                if not value:
                    raise Exception("""Param "%s" not set""" % param)
                self.settings[param] = value[-1]  # возможно переопределение параметра
        return self.settings

    def isUseAuthServer(self):
        u"""Метод возвращает значение параметра useAuthServer настроечного файла (True/False)
        """
        settingsJson = self.getSettingsJSON()
        return settingsJson["useAuthServer"] in ("true", True)

    def loginIsSubject(self):
        u"""Метод возвращает значение параметра loginEqualSubject настроечного файла (True/False)
        """
        settingsJson = self.getSettingsJSON()
        return settingsJson["loginEqualSubject"] in ("true", True)

    def isEmployees(self):
        u"""Метод возвращает True, если в настроечном файле параметры employeesGrain, employeesTable, employeesName
            одновременно принимают значения отличные от null и пустой строки. В противном случае возвращает False
        """
        settingsJson = self.getSettingsJSON()
        result = not(settingsJson["employeesGrain"] in (None, "null", "") or
                     settingsJson["employeesTable"] in (None, "null", "") or
                     settingsJson["employeesName"] in (None, "null", "") or
                     settingsJson["employeesId"] in (None, "null", ""))

        return result

    def isSystemInitialised(self):
        u"""Метод возвращает значение параметра isSystemInitialised настроечного файла (True/False)
        """
        settingsJson = self.getSettingsJSON()
        return settingsJson["isSystemInitialised"] in ("true", True)

    def getEmployeesParam(self, param):
        u"""Возвращает значение параметра настроечного файла с именем param
        """
        settingsJson = self.getSettingsJSON()
        return settingsJson[param]

    def setEmployeesParam(self, param, value):
        u"""Присваивает параметру param из настроечного json'а значение value,
            (!)одновременно сохраняя значение в файл grainsSettings.xml
        """
        if param in self.settings.keys():
            self.settings[param] = value
            path = "%s/parameter[@name='%s']/@value" % (self.settingsTag, param)
            self.settingsInstance.setGrainSettings(path, value, self.grainName)

    def settingsJSONSave(self):
        u""" deprecated!
        """
        # оставляем на случай, если метод где-то используется.
        # но теперь сохранение будет осуществляться методом setEmployeesParam
        pass


def id_generator(size=8, chars=string.ascii_uppercase + string.ascii_lowercase + string.digits):
    u"""Функция генерирует произвольную строчку заданной длины и из заданного набора символов.
        По умолчанию, длина результирующей строки 8, набор символов - цифры и английские буквы.
    """
    return ''.join(random.choice(chars) for _ in range(size))


def getUsersFromAuthServer(server, sessionId):
    u"""Функция получает адрес Mellophone и ID сессии и возвращает xml (объект xml.dom.minidom) с пользователями.
        нужна для работы режима "useAuthServer"=="true"
    """
    req = urllib2.Request(server + '/importusers?sesid=' + sessionId, '<Request></Request>')
    data = urllib2.urlopen(req)
    users = data.read()
    return parseString(users)


def getActionJSON(add="", elementId="", caption="", height="", width=""):
    u"""Вспомогательная функция для упрощения генерации settings
        используется в гридах гранулы.
    """
    action = {"@show_in": "MODAL_WINDOW",
              "#sorted": [{"main_context": "current"},
                          {"modalwindow": {"@caption": caption,
                                           "@height": height,
                                           "@width": width}
                           },
                          {"datapanel": {"@type": "current",
                                         "@tab": "current",
                                         "element": {"@id": elementId,
                                                     "add_context": add}
                                         }
                           }]
              }
    # Использование этой функции приводит к тому, что недоступные кнопки гридов не дизейблятся, а скрываются.
    # Возможно, надо доработать.
    return action


def submissionGenPass(context, main=None, add=None, filterinfo=None, session=None, data=None):
    u"""Функция для submission, генерирующая пароль с помощью вызова id_generator
        изпользуется в карточке users.xml
    """
    instance = json.loads(data)
    instance["schema"]["user"]["@password"] = id_generator()
    return XMLJSONConverter.jsonToXml(json.dumps(instance))


def generateGridSettings(columns={}, pagesize=25, gridHeight=250, delta=40,
                         totalCount=0, profile="default.properties", header="", columnsSorted=None,
                         datapanelWidth=None):
    u"""Вспомогательная функция для упрощения построения настроек грида.
        columns - json с колонками грида
        pagesize - размер страницы
        gridHeight - высота грида
        delta=40 - ширина грида рассчитывается как сумма ширин столбцов + delta (если datapanelWidth == None)
        totalCount - число записей грида
        profile="default.properties" - настройки грида
        header - заголовок
        columnsSorted - если None, сортируется по полям. Если не None, то в переменной передаются названия колонки в нужном порядке.
        datapanelWidth - Если не None, ширина грида будет равна datapanelWidth
    """
    settings = {"gridsettings": {"columns": {"col": []}
                                 }
                }
    if columnsSorted is None:
        columnsSorted = sorted(columns.keys())
    for key in columnsSorted:
        settings["gridsettings"]["columns"]["col"].append({"@id": key, "@width": str(columns[key]) + "px"})
    settings["gridsettings"]["properties"] = {"@pagesize": pagesize,
                                              "@gridWidth": "100%",
                                              "@gridHeight": gridHeight,
                                              "@totalCount": totalCount,
                                              "@profile": profile}
    settings["gridsettings"]["labels"] = {"header": header}
    return settings


def getPermissionsOfTypeAndUser(context, sid, permissionType=None):
    u"""
        Функция возвращает курсор с разрешениями данного типа,
        которые есть у данного пользователя. Работает для permissions (если permissionType - None или tables)
        и для customPermissions.
        Если разрешений нет, возвращает None
    """
    # Насколько знаю, ни в одном решении функция не используется. Курсор с разрешениями пока никому не пригодился
    # Возможно, стоит выпилить.
    userRoles = UserRolesCursor(context)
    userRoles.setRange("userid", sid)
    filter_string = ""
    if userRoles.tryFindSet():
        filter_string = "'" + userRoles.roleid + "'"
        while True:
            if userRoles.nextInSet():
                filter_string += "|'" + userRoles.roleid + "'"
            else:
                break

    if permissionType is None or permissionType == 'tables':
        # получаем разрешения из таблицы permissions
        permissions = PermissionsCursor(context)
        if filter_string == "":
            return None
        permissions.setFilter("roleid", filter_string)
    else:
        # получаем разрешения из таблицы customPermissions
        permissions = customPermsCursor(context)
        rolePermissions = rolesCustomPermsCursor(context)
        rolePermissions.setFilter("roleid", filter_string)
        filter_string = ""
        if rolePermissions.tryFindSet():
            filter_string = "'" + rolePermissions.permissionId + "'"
            while True:
                if rolePermissions.nextInSet():
                    filter_string += "|'" + rolePermissions.permissionId + "'"
                else:
                    break
        if filter_string != "":
            permissions.setFilter("name", filter_string)
        else:
            return None
    return permissions


def userHasPermission(context, sid, permission):
    u"""
        Функция возвращает False, если разрешения не существует или
        у данного пользователя нет такого разрешения.
        В случае, если у данного пользователя разрешение есть,
        возвращает True
        sid - sid пользователя
        permission - рарешение из таблицы customPermissions
    """
    userRoles = UserRolesCursor(context)
    if userRoles.tryGet(sid, "editor"):
        # Для роли editor есть все(!) разрешения
        return True
    userRoles.clear()
    userRoles.setRange("userid", sid)    # выбираем разрешения данного пользователя.
    permissions = customPermsCursor(context)
    if not permissions.tryGet(permission):
        # Разрешения не нашли, возвращаем False
        return False
    rolePermissions = rolesCustomPermsCursor(context)
    if userRoles.tryFindSet():
        while True:
            rolePermissions.setRange("roleid", userRoles.roleid)
            rolePermissions.setRange("permissionId", permission)
            if rolePermissions.tryFirst():
                return True
            if not userRoles.nextInSet():
                break
    return False


def tableUpload(cursorInstance, fileData):
    u"""Функция загрузки xml-данных fileData в таблицу с объектом курсора cursorInstance
    """
    exchange = DataBaseXMLExchange(fileData, cursorInstance)
    exchange.uploadXML()


def tableDownload(cursorInstance, fileName):
    u"""Функция загрузки данных таблицы с объектом курсора cursorInstance в xml-файл
    """
    filePath = os.path.join(os.path.dirname(os.path.abspath(__file__)), fileName + '.xml')
    dataStream = FileOutputStream(filePath)
    exchange = DataBaseXMLExchange(dataStream, cursorInstance)
    exchange.downloadXML()
    dataStream.close()
    report = File(filePath)
    return JythonDownloadResult(FileInputStream(report), fileName + '.xml')
