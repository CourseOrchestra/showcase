# coding: utf-8

import json

from common.sysfunctions import toHexForXml, getGridHeight
from ru.curs.celesta.showcase.utils import XMLJSONConverter
from ru.curs.celesta.syscursors import PermissionsCursor
from security._security_orm import tablesPermissionsViewCursor


try:
    from ru.curs.showcase.core.jython import JythonDTO
    from ru.curs.showcase.app.api.grid import GridSaveResult
except:
    from ru.curs.celesta.showcase import JythonDTO

def gridData(context, main=None, add=None, filterinfo=None,
             session=None, elementId=None, sortColumnList=None, firstrecord=0, pagesize=50):
    u'''Функция получения данных для грида. '''
    session = json.loads(session)["sessioncontext"]["related"]

    # Создание экземпляра курсора разрешения
    permissions = tablesPermissionsViewCursor(context)

    # Сортировка по колонке
    if sortColumnList:
        sortName = toHexForXml(sortColumnList[0].id)
        sortType = unicode(sortColumnList[0].sorting).lower()
    else:
        sortName = None

    # Один и тот же грид используется в двух местах, в одном случае он зависит от xform-ы, в другом - от грида
    gridType = "editable"
    # Фильтрация данных из xform-ы
    if "xformsContext" in session:
        filterSchema = session["xformsContext"]["formData"]["schema"]
        if filterSchema["roleid"]:
            permissions.setRange("roleid", filterSchema["roleid"])
        if filterSchema["grainid"]:
            permissions.setRange("grainid", filterSchema["grainid"])
        if filterSchema["tablename"]:
            permissions.setRange("tablename", filterSchema["tablename"])
    # Фильтрация данных из грида выше
    if 'gridContext' in session:
        permissions.setRange("roleid", session['gridContext']['currentRecordId'])
        gridType = "plain"

    # Сортировка по-умолчанию
    permissions.orderBy('roleid', 'grainid', 'tablename')

    # Определяем переменную для JSON данных
    data = {"records":{"rec":[]}}
    _header = {"id": ["~~id"],
               "roleid": [u"Роль"],
               "grainid": [u"Гранула"],
               "tablename": [u"Таблица"],

               "r": [u"Доступ на чтение"],
               "i": [u"Доступ на добавление"],
               "m": [u"Доступ на редактирование"],
               "d": [u"Доступ на удаление"],

               "properties": [u"properties"]
               }
    for column in _header:
        _header[column].append(toHexForXml(_header[column][0]))
        if sortName == _header[column][1]:
            permissions.orderBy("%s %s" % (column, sortType))

    permissions.limit(firstrecord - 1, pagesize)

    if gridType == "editable":
        def getBoolData(cursor, column):
            return getattr(cursor, column) or ''
    else:
        def getBoolData(cursor, column):
            return u'gridToolBar/yes.png:Разрешен'  if getattr(cursor, column) else u'gridToolBar/no.png:Запрещен'
    # Проходим по таблице и заполняем data
    for permissions in permissions.iterate():
        permDict = {}
        permDict[_header["id"][1]] = json.dumps([permissions.roleid, permissions.grainid, permissions.tablename])

        permDict[_header["roleid"][1]] = permissions.roleid
        permDict[_header["grainid"][1]] = permissions.grainid
        permDict[_header["tablename"][1]] = permissions.tablename
        for column in ("r", "i", "m", "d"):
            permDict[_header[column][1]] = getBoolData(permissions, column)

        permDict['properties'] = {"event":{"@name":"row_single_click",
                                           "action":{"#sorted":[{"main_context": 'current'},
                                                                {"datapanel":{'@type':"current",
                                                                              '@tab':"current"}
                                                                 }]
                                                     }
                                           }
                                  }
        data["records"]["rec"].append(permDict)

    res = XMLJSONConverter.jsonToXml(json.dumps(data))
    return JythonDTO(res, None)

def gridMeta(context, main=None, add=None, filterinfo=None, session=None, elementId=None):
    u'''Функция получения настроек грида. '''
    sessionRelated = json.loads(session)["sessioncontext"]["related"]
    gridType = "editable"
    # Курсор таблицы permissions
    permissions = tablesPermissionsViewCursor(context)
    if 'formData' in session:
        if sessionRelated["xformsContext"]["formData"]["schema"]["roleid"]:
            permissions.setRange("roleid", sessionRelated["xformsContext"]["formData"]["schema"]["roleid"])
        if sessionRelated["xformsContext"]["formData"]["schema"]["grainid"]:
            permissions.setRange("grainid", sessionRelated["xformsContext"]["formData"]["schema"]["grainid"])
        if sessionRelated["xformsContext"]["formData"]["schema"]["tablename"]:
            permissions.setRange("tablename", sessionRelated["xformsContext"]["formData"]["schema"]["tablename"])

    if 'gridContext' in session:
        permissions.setRange("roleid", sessionRelated['gridContext']['currentRecordId'])
        gridType = "plain"
    # Вычисляем количества записей в таблице
    totalcount = permissions.count()
    # Заголовок таблицы
    header = "Разрешения"

    _header = {"id": ["~~id"],
               "roleid": [u"Роль"],
               "grainid": [u"Гранула"],
               "tablename": [u"Таблица"],

               "r": [u"Доступ на чтение"],
               "i": [u"Доступ на добавление"],
               "m": [u"Доступ на редактирование"],
               "d": [u"Доступ на удаление"],

               "properties": [u"properties"]
               }

    # Определяем список полей таблицы для отображения
    settings = {}
    if gridType == "editable":
        def getBoolproperties(column):
            return {"@id": _header[column][0],
                    "@width": "80px",
                    "@readonly": "false",
                    "@editor": "{editor: 'checkbox'}"}
        numberOfGrids = 1
        gridHeaderHeight = 60
        delta = 235
    else:
        def getBoolproperties(column):
            return {"@id": _header[column][0],
                    "@width": "80px",
                    "@type": "IMAGE"}
        currentDatapanelHeight = int(json.loads(session)["sessioncontext"]["currentDatapanelHeight"])
        gridHeaderHeight = 48
        # Вычисляем numberOfGrids, где 1.6 - numberOfGrids для первого грида roles.py из датапанели
        # 59 - gridHeaderHeight для первого грида, 70 - суммарная дельта
        numberOfGrids = ((currentDatapanelHeight-gridHeaderHeight)*1.6)/(1.6*currentDatapanelHeight-1.6*70-currentDatapanelHeight+59)
        delta = 35
    settings["gridsettings"] = {"columns": {"col":[]},
                                "properties": {"@pagesize": "50",
                                               "@gridWidth": "100%",
                                               "@gridHeight": getGridHeight(session, numberOfGrids, gridHeaderHeight, delta),
                                               "@totalCount": totalcount,
                                               "@profile": "editableGrid.properties"},
                                "labels":{"header":header}
                                }
    # Добавляем поля для отображения в gridsettings
    settings["gridsettings"]["columns"]["col"].append({"@id": _header["roleid"][0],
                                                       "@width": "80px",
                                                       "@readonly": "true"})
    settings["gridsettings"]["columns"]["col"].append({"@id": _header["grainid"][0],
                                                       "@width": "80px",
                                                       "@readonly": "true"})
    settings["gridsettings"]["columns"]["col"].append({"@id": _header["tablename"][0],
                                                       "@width": "80px",
                                                       "@readonly": "true"})


    for column in ("r", "i", "m", "d"):
        settings["gridsettings"]["columns"]["col"].append(getBoolproperties(column))

    res = XMLJSONConverter.jsonToXml(json.dumps(settings))
    return JythonDTO(None, res)

def gridToolBar(context, main=None, add=None, filterinfo=None, session=None, elementId=None):
    u'''Toolbar для грида. '''

    data = {"gridtoolbar":{"item":[]
                           }
            }
    data["gridtoolbar"]["item"].append({"@img": 'gridToolBar/arrowDown.png',
                                            "@text":"Скачать",
                                            "@hint":"Скачать разрешения в xml",
                                            "@disable": "false",
                                            "action":{"@show_in": "MODAL_WINDOW",
                                                      "#sorted":[{"main_context":"current"},
                                                                 {"modalwindow":{"@caption": "Скачать разрешения",
                                                                                 "@height": "300",
                                                                                 "@width": "450"}
                                                                  },
                                                                 {"datapanel":{"@type": "current",
                                                                               "@tab": "current",
                                                                               "element": {"@id": "permDownloadXform",
                                                                                           "add_context":"download"}
                                                                               }
                                                                  }]
                                                      }
                                            }
                                       )
    data["gridtoolbar"]["item"].append({"@img": 'gridToolBar/arrowUp.png',
                                            "@text":"Загрузить",
                                            "@hint":"Загрузить разрешения из xml",
                                            "@disable": "false",
                                            "action":{"@show_in": "MODAL_WINDOW",
                                                      "#sorted":[{"main_context":"current"},
                                                                 {"modalwindow":{"@caption": "Загрузить разрешения",
                                                                                 "@height": "300",
                                                                                 "@width": "450"}
                                                                  },
                                                                 {"datapanel":{"@type": "current",
                                                                               "@tab": "current",
                                                                               "element": {"@id": "permUploadXform",
                                                                                           "add_context":"upload"}
                                                                               }
                                                                  }]
                                                      }
                                            }
                                       )


    return XMLJSONConverter.jsonToXml(json.dumps(data))

def gridSaveRecord(context=None, main=None, add=None, session=None, filterinfo=None, elementId=None, saveData=None):
    saveData = json.loads(saveData)["savedata"]["data"]
    permissions = PermissionsCursor(context)
    roleId = saveData["col1"]
    grainId = saveData["col2"]
    tableName = saveData["col3"]

    r = True if saveData["col4"] else False
    i = True if saveData["col5"] else False
    m = True if saveData["col6"] else False
    d = True if saveData["col7"] else False

    restrictError = u"Недостаточно прав для данной операции!"

    if permissions.tryGet(roleId, grainId, tableName):
        if r or i or m or d:
            permissions.r = r
            permissions.i = i
            permissions.m = m
            permissions.d = d
            if permissions.canModify():
                permissions.update()
            else:
                context.error(restrictError)
        else:
            if permissions.canDelete():
                permissions.delete()
            else:
                context.error(restrictError)
    else:
        if r or i or m or d:
            permissions.roleid = roleId
            permissions.grainid = grainId
            permissions.tablename = tableName
            permissions.r = r
            permissions.i = i
            permissions.m = m
            permissions.d = d
            if permissions.canInsert():
                permissions.insert()
            else:
                context.error(restrictError)
    res = GridSaveResult()
    res.setRefreshAfterSave(0)
    return res
