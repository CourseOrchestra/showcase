# coding: utf-8

import json

from common.sysfunctions import toHexForXml, getGridHeight
from ru.curs.celesta.showcase.utils import XMLJSONConverter
from security._security_orm import customPermsCursor


try:
    from ru.curs.showcase.core.jython import JythonDTO
except:
    from ru.curs.celesta.showcase import JythonDTO


def gridData(context, main=None, add=None, filterinfo=None,
             session=None, elementId=None, sortColumnList=None, firstrecord=None, pagesize=None):
    u'''Функция получения данных для грида. '''
    # Создание экземпляра курсора разрешения
    permissions = customPermsCursor(context)
    if sortColumnList:
        sortName = toHexForXml(sortColumnList[0].id)
        sortType = unicode(sortColumnList[0].sorting).lower()
    else:
        sortName = None

    if 'formData' in session:
        typeId = json.loads(session)['sessioncontext']['related']['xformsContext'][
            'formData']['schema']['permission']['@type']
        if typeId:
            permissions.setRange('type', typeId)

    permissions.orderBy('name')

    # Определяем переменную для JSON данных
    data = {"records": {"rec": []}}
    # Проходим по таблице и заполняем data
    _header = {"id": ["~~id"],
               "name": [u"Разрешение"],
               "description": [u"Описание"],
               "type": [u"Тип"],

               "properties": [u"properties"]
               }
    for column in _header:
        _header[column].append(toHexForXml(_header[column][0]))
        if sortName == _header[column][1]:
            permissions.orderBy("%s %s" % (column, sortType))
    permissions.limit(firstrecord - 1, pagesize)
    for permissions in permissions.iterate():
        permDict = {}
        permDict[_header["id"][1]] = permissions.name
        permDict[_header["name"][1]] = permissions.name or ''
        permDict[_header["description"][1]] = permissions.description or ''
        permDict[_header["type"][1]] = permissions.type or ''

        permDict['properties'] = {"event": {"@name": "row_single_click",
                                            "action": {"#sorted": [{"main_context": 'current'},
                                                                   {"datapanel": {'@type': "current",
                                                                                  '@tab': "current",
                                                                                  "element": {"@id": "rolesCustomPermissionsGrid"}
                                                                                  }
                                                                    }]
                                                       }
                                            }
                                  }
        data["records"]["rec"].append(permDict)

    res = XMLJSONConverter.jsonToXml(json.dumps(data))
    return JythonDTO(res, None)


def gridMeta(context, main=None, add=None, filterinfo=None, session=None, elementId=None):
    u'''Функция получения настроек грида. '''

    # Курсор таблицы permissions
    permissions = customPermsCursor(context)
    # Вычисляем количества записей в таблице
    totalcount = permissions.count()
    # Заголовок таблицы
    header = "Разрешения"

    _header = {"id": ["~~id"],
               "name": [u"Разрешение"],
               "description": [u"Описание"],
               "type": [u"Тип"],

               "properties": [u"properties"]
               }
    # Определяем список полей таблицы для отображения
    settings = {}
    settings["gridsettings"] = {"columns": {"col": []},
                                "properties": {"@pagesize": "50",
                                               "@gridWidth": "100%",
                                               "@gridHeight": getGridHeight(session, 2.1, 60, 67),
                                               "@totalCount": totalcount,
                                               "@profile": "default.properties"},
                                "labels": {"header": header}
                                }
    # Добавляем поля для отображения в gridsettings
    settings["gridsettings"]["columns"]["col"].append({"@id": _header["name"][0],
                                                       "@width": "120px"})
    settings["gridsettings"]["columns"]["col"].append({"@id": _header["description"][0],
                                                       "@width": "240px"})
    settings["gridsettings"]["columns"]["col"].append({"@id": _header["type"][0],
                                                       "@width": "120px"})
    res = XMLJSONConverter.jsonToXml(json.dumps(settings))
    return JythonDTO(None, res)


def gridToolBar(context, main=None, add=None, filterinfo=None, session=None, elementId=None):
    u'''Toolbar для грида. '''

    if 'currentRecordId' not in json.loads(session)['sessioncontext']['related']['gridContext']:
        style = "true"
    else:
        style = "false"

    data = {"gridtoolbar": {"item": []
                            }
            }
    # Курсор таблицы permissions
    permissions = customPermsCursor(context)

    if permissions.canInsert():
        data["gridtoolbar"]["item"].append({"@img": 'gridToolBar/addDirectory.png',
                                            "@text": "Добавить",
                                            "@hint": "Добавить",
                                            "@disable": "false",
                                            "action": {"@show_in": "MODAL_WINDOW",
                                                       "#sorted": [{"main_context": "current"},
                                                                   {"modalwindow": {"@caption": "Добавление типа разрешения",
                                                                                    "@height": "400",
                                                                                    "@width": "500"}
                                                                    },
                                                                   {"datapanel": {"@type": "current",
                                                                                  "@tab": "current",
                                                                                  "element": {"@id": "customPermissionsXforms",
                                                                                              "add_context": "add"}
                                                                                  }
                                                                    }]
                                                       }
                                            })
    if permissions.canModify():
        data["gridtoolbar"]["item"].append({"@img": 'gridToolBar/editDocument.png',
                                            "@text": "Редактировать",
                                            "@hint": "Редактировать",
                                            "@disable": style,
                                            "action": {"@show_in": "MODAL_WINDOW",
                                                       "#sorted": [{"main_context": "current"},
                                                                   {"modalwindow": {"@caption": "Редактирование типа разрешения",
                                                                                    "@height": "400",
                                                                                    "@width": "500"}
                                                                    },
                                                                   {"datapanel": {"@type": "current",
                                                                                  "@tab": "current",
                                                                                  "element": {"@id": "customPermissionsXforms",
                                                                                              "add_context": "edit"}
                                                                                  }
                                                                    }]
                                                       }
                                            })
    if permissions.canDelete():
        data["gridtoolbar"]["item"].append({"@img": 'gridToolBar/deleteDocument.png',
                                            "@text": "Удалить",
                                            "@hint": "Удалить",
                                            "@disable": style,
                                            "action": {"@show_in": "MODAL_WINDOW",
                                                       "#sorted": [{"main_context": "current"},
                                                                   {"modalwindow": {"@caption": "Удаление типа разрешения",
                                                                                    "@height": "300",
                                                                                    "@width": "450"}
                                                                    },
                                                                   {"datapanel": {"@type": "current",
                                                                                  "@tab": "current",
                                                                                  "element": {"@id": "customPermissionsXformDelete",
                                                                                              "add_context": "delete"}
                                                                                  }
                                                                    }]
                                                       }
                                            })
    data["gridtoolbar"]["item"].append({"@img": 'gridToolBar/arrowDown.png',
                                        "@text": "Скачать",
                                        "@hint": "Скачать прочие разрешения в xml",
                                        "@disable": "false",
                                        "action": {"@show_in": "MODAL_WINDOW",
                                                   "#sorted": [{"main_context": "current"},
                                                               {"modalwindow": {"@caption": "Скачать разрешения",
                                                                                "@height": "300",
                                                                                "@width": "450"}
                                                                },
                                                               {"datapanel": {"@type": "current",
                                                                              "@tab": "current",
                                                                              "element": {"@id": "customPermissionsDownloadXform",
                                                                                          "add_context": "download"}
                                                                              }
                                                                }]
                                                   }
                                        }
                                       )
    data["gridtoolbar"]["item"].append({"@img": 'gridToolBar/arrowUp.png',
                                        "@text": "Загрузить",
                                        "@hint": "Загрузить прочие разрешения из xml",
                                        "@disable": "false",
                                        "action": {"@show_in": "MODAL_WINDOW",
                                                   "#sorted": [{"main_context": "current"},
                                                               {"modalwindow": {"@caption": "Загрузить разрешения",
                                                                                "@height": "300",
                                                                                "@width": "450"}
                                                                },
                                                               {"datapanel": {"@type": "current",
                                                                              "@tab": "current",
                                                                              "element": {"@id": "customPermissionsUploadXform",
                                                                                          "add_context": "upload"}
                                                                              }
                                                                }]
                                                   }
                                        }
                                       )

    return XMLJSONConverter.jsonToXml(json.dumps(data))
