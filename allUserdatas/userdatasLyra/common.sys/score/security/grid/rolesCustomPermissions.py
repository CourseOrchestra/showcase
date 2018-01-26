# coding: utf-8

import json

from common.sysfunctions import toHexForXml, getGridHeight
from ru.curs.celesta.showcase.utils import XMLJSONConverter
from ru.curs.celesta.syscursors import RolesCursor
from security._security_orm import rolesCustomPermsCursor


try:
    from ru.curs.showcase.core.jython import JythonDTO
    from ru.curs.showcase.app.api.grid import GridSaveResult
except:
    from ru.curs.celesta.showcase import JythonDTO

def gridData(context, main=None, add=None, filterinfo=None,
             session=None, elementId=None, sortColumnList=None, firstrecord=None, pagesize=None):
    u'''Функция получения данных для грида. '''

    # Создание экземпляра курсора разрешения
    roles = RolesCursor(context)
    rolesPermissions = rolesCustomPermsCursor(context)
    if 'currentRecordId' in session:
        currId = json.loads(session)['sessioncontext']['related']['gridContext']['currentRecordId']
        rolesPermissions.setRange("permissionId", currId)

    roles.orderBy("id")
    # Определяем переменную для JSON данных

    data = {"records":{"rec":[]}}
    _header = {"id": ["~~id"],
               "roleId": [u"Роль"],
               "description": [u"Описание"],
               "exists":[u" "],

               "properties": [u"properties"]
               }

    for column in _header:
        _header[column].append(toHexForXml(_header[column][0]))

    # Считываем параметры сортировки
    if sortColumnList:
        sortName = sortColumnList[0].id
        sortType = unicode(sortColumnList[0].sorting).lower()
    else:
        sortName = None
    # Проходим по таблице и заполняем data
    if roles.tryFindSet():
        while True:
            permDict = {}
            permDict[_header["id"][1]] = json.dumps({"permission": currId,
                                                     "role": roles.id})
            permDict[_header["roleId"][1]] = roles.id
            permDict[_header["description"][1]] = roles.description
            rolesPermissions.setRange("roleid", roles.id)
            permDict[_header["exists"][1]] = rolesPermissions.count() > 0 if rolesPermissions.count() else ''
            permDict[_header["properties"][1]] = {"event":{"@name":"row_single_click",
                                                               "action":{"#sorted":[{"main_context": 'current'},
                                                                    {"datapanel":{'@type':"current",
                                                                                  '@tab':"current"}
                                                                     }]
                                                         }
                                               }
                                      }

            data["records"]["rec"].append(permDict)
            if not roles.nextInSet():
                    break

    for column in _header:
        if sortName == _header[column][0]:
            keyField = column if sortName else 'exists'
            data["records"]["rec"].sort(key=lambda x: (x[_header["%s" % keyField][1]]), reverse=(sortType == 'desc'))
            data["records"]["rec"] = data["records"]["rec"][firstrecord - 1:firstrecord - 1 + pagesize]
    res = XMLJSONConverter.jsonToXml(json.dumps(data))
    return JythonDTO(res, None)

def gridMeta(context, main=None, add=None, filterinfo=None, session=None, elementId=None):
    u'''Функция получения настроек грида. '''

    roles = RolesCursor(context)
    # Вычисляем количества записей в таблице
    totalcount = roles.count()
    # Заголовок таблицы
    header = "Роли"

    _header = {"id": ["~~id"],
               "roleId": [u"Роль"],
               "description": [u"Описание"],
               "exists":[u" "],

               "properties": [u"properties"]
               }
    # Определяем список полей таблицы для отображения
    settings = {}
    currentDatapanelHeight = int(json.loads(session)["sessioncontext"]["currentDatapanelHeight"])
    # Вычисляем numberOfGrids, где 2.1 - numberOfGrids для первого грида customPermissions.py из датапанели
    # 60 - gridHeaderHeight для обоих гридов, 134 - суммарная дельта
    numberOfGrids = ((currentDatapanelHeight-60)*2.1)/(2.1*currentDatapanelHeight-2.1*134-currentDatapanelHeight+60)
    settings["gridsettings"] = {"columns": {"col":[]},
                                "properties": {"@pagesize": "50",
                                               "@gridWidth": "100%",
                                               "@gridHeight": getGridHeight(session, numberOfGrids, 60, 67),
                                               "@totalCount": totalcount,
                                               "@profile": "editableGrid.properties"},
                                "labels": {"header":header}
                                }
    # Добавляем поля для отображения в gridsettings
    settings["gridsettings"]["columns"]["col"].append({"@id":_header["exists"][0],
                                                       "@width": "33px",
                                                       "@readonly": "false",
                                                       "@editor": "{editor: 'checkbox'}"})
    settings["gridsettings"]["columns"]["col"].append({"@id": _header["roleId"][0],
                                                       "@width": "80px",
                                                       "@readonly": "true"})
    settings["gridsettings"]["columns"]["col"].append({"@id":_header["description"][0],
                                                       "@width": "400px",
                                                       "@readonly":"true"})
    res = XMLJSONConverter.jsonToXml(json.dumps(settings))
    return JythonDTO(None, res)

def gridToolBar(context, main=None, add=None, filterinfo=None, session=None, elementId=None):
    u'''Toolbar для грида. '''

    data = {"gridtoolbar":{"item":[]}}
    data["gridtoolbar"]["item"].append({"@img": 'gridToolBar/arrowDown.png',
                                            "@text":"Скачать",
                                            "@hint":"Скачать роли в xml",
                                            "@disable": "false",
                                            "action":{"@show_in": "MODAL_WINDOW",
                                                      "#sorted":[{"main_context":"current"},
                                                                 {"modalwindow":{"@caption": "Скачать роли",
                                                                                 "@height": "300",
                                                                                 "@width": "450"}
                                                                  },
                                                                 {"datapanel":{"@type": "current",
                                                                               "@tab": "current",
                                                                               "element": {"@id": "rolesCustomPermissionsDownloadXform",
                                                                                           "add_context":"download"}
                                                                               }
                                                                  }]
                                                      }
                                            }
                                       )
    data["gridtoolbar"]["item"].append({"@img": 'gridToolBar/arrowUp.png',
                                            "@text":"Загрузить",
                                            "@hint":"Загрузить роли из xml",
                                            "@disable": "false",
                                            "action":{"@show_in": "MODAL_WINDOW",
                                                      "#sorted":[{"main_context":"current"},
                                                                 {"modalwindow":{"@caption": "Загрузить роли",
                                                                                 "@height": "300",
                                                                                 "@width": "450"}
                                                                  },
                                                                 {"datapanel":{"@type": "current",
                                                                               "@tab": "current",
                                                                               "element": {"@id": "rolesCustomPermissionsUploadXform",
                                                                                           "add_context":"upload"}
                                                                               }
                                                                  }]
                                                      }
                                            }
                                       )

    return XMLJSONConverter.jsonToXml(json.dumps(data))

def gridSaveRecord(context=None, main=None, add=None, session=None, filterinfo=None, elementId=None, saveData=None):
    saveData = json.loads(saveData)["savedata"]["data"]
    rolesPermissions = rolesCustomPermsCursor(context)
    roleId = saveData["col2"]
    permissionId = json.loads(saveData["id"])["permission"]
    if rolesPermissions.tryGet(roleId, permissionId) and not saveData["col1"]:
        if not saveData["col1"]:
            if rolesPermissions.canDelete():
                rolesPermissions.delete()
            else:
                context.error(u"Недостаточно прав для данной операции!")
    else:
        if saveData["col1"]:
            rolesPermissions.roleid = roleId
            rolesPermissions.permissionId = permissionId
            if rolesPermissions.canInsert():
                rolesPermissions.insert()
            else:
                context.error(u"Недостаточно прав для данной операции!")
    res = GridSaveResult()
    res.setRefreshAfterSave(0)
    return res