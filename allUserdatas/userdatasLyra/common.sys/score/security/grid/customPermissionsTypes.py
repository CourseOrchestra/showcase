# coding: utf-8

import json

from common.sysfunctions import toHexForXml, getGridHeight
from ru.curs.celesta.showcase.utils import XMLJSONConverter
from security._security_orm import customPermsTypesCursor


try:
    from ru.curs.showcase.core.jython import JythonDTO
except:
    from ru.curs.celesta.showcase import JythonDTO

def gridData(context, main=None, add=None, filterinfo=None,
             session=None, elementId=None, sortColumnList=None, firstrecord=None, pagesize=None):
    u'''Функция получения данных для грида. '''

    # Создание экземпляра курсора разрешения
    permissionsTypes = customPermsTypesCursor(context)

    if sortColumnList:
        sortName = toHexForXml(sortColumnList[0].id)
        sortType = unicode(sortColumnList[0].sorting).lower()
    else:
        sortName = None

    permissionsTypes.orderBy('name')

    # Определяем переменную для JSON данных
    data = {"records":{"rec":[]}}
    _header = {"id": ["~~id"],
               "name": [u"Тип"],
               "description": [u"Описание"],

               "properties": [u"properties"]
               }
    for column in _header:
        _header[column].append(toHexForXml(_header[column][0]))
        if sortName == _header[column][1]:
            permissionsTypes.orderBy("%s %s" % (column, sortType))
    permissionsTypes.limit(firstrecord - 1, pagesize)
    # Проходим по таблице и заполняем data
    for permissionsTypes in permissionsTypes.iterate():
        permDict = {}
        permDict[_header["id"][1]] = permissionsTypes.name
        for column in [x for x in _header.keys() if x not in ("id", "properties")]:
            permDict[_header[column][1]] = getattr(permissionsTypes, column) or ''

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

    # Курсор таблицы permissions
    permissionsTypes = customPermsTypesCursor(context)
    # Вычисляем количества записей в таблице
    totalcount = permissionsTypes.count()
    # Заголовок таблицы
    header = "Типы разрешений"
    # В случае если таблица пустая
    _header = {"id": ["~~id"],
               "name": [u"Тип"],
               "description": [u"Описание"],

               "properties": [u"properties"]
               }

    # Определяем список полей таблицы для отображения
    settings = {}
    settings["gridsettings"] = {"columns": {"col":[]},
                                "properties": {"@pagesize":"50",
                                               "@gridWidth": "100%",
                                               "@gridHeight":getGridHeight(session, delta=40),
                                               "@totalCount":totalcount,
                                               "@profile":"default.properties"},
                                "labels": {"header":header}
                                }
    # Добавляем поля для отображения в gridsettings
    settings["gridsettings"]["columns"]["col"].append({"@id": _header["name"][0],
                                                       "@width": "120px"})
    settings["gridsettings"]["columns"]["col"].append({"@id": _header["description"][0],
                                                       "@width": "480px"})
    res = XMLJSONConverter.jsonToXml(json.dumps(settings))
    return JythonDTO(None, res)

def gridToolBar(context, main=None, add=None, filterinfo=None, session=None, elementId=None):
    u'''Toolbar для грида. '''

    if 'currentRecordId' not in json.loads(session)['sessioncontext']['related']['gridContext']:
        style = "true"
    else:
        style = "false"

    data = {"gridtoolbar":{"item":[]
                           }
            }
    # Курсор таблицы permissions
    permissionsTypes = customPermsTypesCursor(context)

    if permissionsTypes.canInsert():
        data["gridtoolbar"]["item"].append({"@img": 'gridToolBar/addDirectory.png',
                                            "@text":"Добавить",
                                            "@hint":"Добавить",
                                            "@disable": "false",
                                            "action":{"@show_in": "MODAL_WINDOW",
                                                      "#sorted":[{"main_context":"current"},
                                                                 {"modalwindow":{"@caption": "Добавление типа разрешения",
                                                                                 "@height": "400",
                                                                                 "@width": "500"}
                                                                  },
                                                                 {"datapanel":{"@type": "current",
                                                                               "@tab": "current",
                                                                               "element": {"@id": "customPermissionsTypesXforms",
                                                                                           "add_context":"add"}
                                                                               }
                                                                  }]
                                                      }
                                            })
    if permissionsTypes.canModify():
        data["gridtoolbar"]["item"].append({"@img": 'gridToolBar/editDocument.png',
                                            "@text":"Редактировать",
                                            "@hint":"Редактировать",
                                            "@disable": style,
                                            "action":{"@show_in": "MODAL_WINDOW",
                                                      "#sorted":[{"main_context":"current"},
                                                                 {"modalwindow":{"@caption": "Редактирование типа разрешения",
                                                                                 "@height": "400",
                                                                                 "@width": "500"}
                                                                  },
                                                                 {"datapanel":{"@type": "current",
                                                                               "@tab": "current",
                                                                               "element": {"@id": "customPermissionsTypesXforms",
                                                                                           "add_context":"edit"}
                                                                               }
                                                                  }]
                                                      }
                                            })
    if permissionsTypes.canDelete():
        data["gridtoolbar"]["item"].append({"@img": 'gridToolBar/deleteDocument.png',
                                            "@text":"Удалить",
                                            "@hint":"Удалить",
                                            "@disable": style,
                                            "action":{"@show_in": "MODAL_WINDOW",
                                                      "#sorted":[{"main_context":"current"},
                                                                 {"modalwindow":{"@caption": "Удаление типа разрешения",
                                                                                 "@height": "300",
                                                                                 "@width": "450"}
                                                                  },
                                                                 {"datapanel":{"@type": "current",
                                                                               "@tab": "current",
                                                                               "element": {"@id": "customPermissionsTypesXformDelete",
                                                                                           "add_context":"delete"}
                                                                               }
                                                                  }]
                                                      }
                                            })
    data["gridtoolbar"]["item"].append({"@img": 'gridToolBar/arrowDown.png',
                                            "@text":"Скачать",
                                            "@hint":"Скачать типы разрешений в xml",
                                            "@disable": "false",
                                            "action":{"@show_in": "MODAL_WINDOW",
                                                      "#sorted":[{"main_context":"current"},
                                                                 {"modalwindow":{"@caption": "Скачать типы разрешений",
                                                                                 "@height": "300",
                                                                                 "@width": "450"}
                                                                  },
                                                                 {"datapanel":{"@type": "current",
                                                                               "@tab": "current",
                                                                               "element": {"@id": "customPermissionsTypesDownloadXform",
                                                                                           "add_context":"download"}
                                                                               }
                                                                  }]
                                                      }
                                            }
                                       )
    data["gridtoolbar"]["item"].append({"@img": 'gridToolBar/arrowUp.png',
                                            "@text":"Загрузить",
                                            "@hint":"Загрузить типы разрешений из xml",
                                            "@disable": "false",
                                            "action":{"@show_in": "MODAL_WINDOW",
                                                      "#sorted":[{"main_context":"current"},
                                                                 {"modalwindow":{"@caption": "Загрузить типы разрешений",
                                                                                 "@height": "300",
                                                                                 "@width": "450"}
                                                                  },
                                                                 {"datapanel":{"@type": "current",
                                                                               "@tab": "current",
                                                                               "element": {"@id": "customPermissionsTypesUploadXform",
                                                                                           "add_context":"upload"}
                                                                               }
                                                                  }]
                                                      }
                                            }
                                       )

    return XMLJSONConverter.jsonToXml(json.dumps(data))

