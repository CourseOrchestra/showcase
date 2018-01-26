# coding: utf-8

import json

from common.sysfunctions import toHexForXml, getGridHeight
from ru.curs.celesta.showcase.utils import XMLJSONConverter
from security._security_orm import subjectsCursor
from security.functions import Settings


try:
    from ru.curs.showcase.core.jython import JythonDTO
except:
    from ru.curs.celesta.showcase import JythonDTO

def gridData(context, main=None, add=None, filterinfo=None,
             session=None, elementId=None, sortColumnList=None, firstrecord=None, pagesize=None):
    u'''Функция получения данных для грида. '''
    # Создание экземпляра курсора разрешения
    subjects = subjectsCursor(context)
    # Параметры сортировки
    if sortColumnList:
        sortName = toHexForXml(sortColumnList[0].id)
        sortType = unicode(sortColumnList[0].sorting).lower()
    else:
        sortName = None

    # Определяем переменную для JSON данных
    data = {"records":{"rec":[]}}
    # Определяем заголовки
    _header = {"rowid": ["~~id"],
               "sid": [u"SID"],
               "name": [u"Имя"],

               "properties": [u"properties"]
               }
    for column in _header:
        _header[column].append(toHexForXml(_header[column][0]))
        if sortName == _header[column][1]:
            subjects.orderBy("%s %s" % (column, sortType))

    subjects.limit(firstrecord - 1, pagesize)
    # Проходим по таблице и заполняем data
    for subjects in subjects.iterate():
        subjectsDict = {}
        subjectsDict[_header["rowid"][1]] = subjects.sid
        for column in [x for x in _header.keys() if x not in ("rowid", "properties")]:
            subjectsDict[_header[column][1]] = getattr(subjects, column) or ''
        subjectsDict['properties'] = {"event":{"@name":"row_single_click",
                                                "action":{"#sorted":[{"main_context": 'current'},
                                                                     {"datapanel":{'@type':"current",
                                                                                   '@tab':"current"}
                                                                      }]
                                                          }
                                                }
                                       }
        data["records"]["rec"].append(subjectsDict)


    res = XMLJSONConverter.jsonToXml(json.dumps(data))
    return JythonDTO(res, None)

def gridMeta(context, main=None, add=None, filterinfo=None, session=None, elementId=None):
    u'''Функция получения настроек грида. '''

    subjects = subjectsCursor(context)
    # Вычисляем количества записей в таблице
    totalcount = subjects.count()
    # Заголовок таблицы
    header = "Субъекты"

    sec_settings = Settings()
    _header = {"rowid": ["~~id"],
               "sid": [u"SID"],
               "name": [u"Имя"],

               "properties": [u"properties"]
               }

    # Определяем список полей таблицы для отображения
    settings = {}
    currentDatapanelHeight = int(json.loads(session)["sessioncontext"]["currentDatapanelHeight"])
    if sec_settings.loginIsSubject():
        number =1
    else:
        # Вычисляем numberOfGrids, где 2.0 - numberOfGrids для первого грида users.py из датапанели
        # 59 - gridHeaderHeight для обоих гридов, 80 - суммарная дельта
        number = ((currentDatapanelHeight-59)*2.0)/(2.0*currentDatapanelHeight-2.0*80-currentDatapanelHeight+59)
    settings["gridsettings"] = {"columns": {"col": []},
                                "properties":{"@pagesize": "25",
                                              "@gridWidth": "100%",
                                              "@gridHeight": getGridHeight(session,
                                                                           number, delta = 40),
                                              "@totalCount": totalcount,
                                              "@profile": "default.properties"},
                                "labels":{"header":header}
                                }
    # Добавляем поля для отображения в gridsettings
    settings["gridsettings"]["columns"]["col"].append({"@id": _header["sid"][0],
                                                       "@width": "240px"})
    settings["gridsettings"]["columns"]["col"].append({"@id": _header["name"][0],
                                                       "@width": "240px"})

    res = XMLJSONConverter.jsonToXml(json.dumps(settings))
    return JythonDTO(None, res)

def gridToolBar(context, main=None, add=None, filterinfo=None, session=None, elementId=None):
    u'''Toolbar для грида. '''

    settings = Settings()

    if 'currentRecordId' not in json.loads(session)['sessioncontext']['related']['gridContext']:
        style = "true"
    else:
        style = "false"

    # raise Exception(session)

    data = {"gridtoolbar":{"item":[]}}
    if not settings.isEmployees():
        data["gridtoolbar"]["item"].append({"@img": 'gridToolBar/addDirectory.png',
                                            "@text":"Добавить",
                                            "@hint":"Добавить субъект",
                                            "@disable": "false",
                                            "action":{"@show_in": "MODAL_WINDOW",
                                                      "#sorted":[{"main_context":"current"},
                                                                 {"modalwindow":{"@caption": "Добавление субъекта",
                                                                                 "@height": "300",
                                                                                 "@width": "450"}
                                                                  },
                                                                 {"datapanel":{"@type": "current",
                                                                               "@tab": "current",
                                                                               "element": {"@id": "subjectsXform",
                                                                                           "add_context":"add"}
                                                                               }
                                                                  }]
                                                      }
                                            })
        data["gridtoolbar"]["item"].append({"@img": 'gridToolBar/editDocument.png',
                                            "@text":"Редактировать",
                                            "@hint":"Редактировать субъект",
                                            "@disable": style,
                                            "action":{"@show_in": "MODAL_WINDOW",
                                                      "#sorted":[{"main_context":"current"},
                                                                 {"modalwindow":{"@caption": "Редактирование субъекта",
                                                                                 "@height": "300",
                                                                                 "@width": "450"}
                                                                  },
                                                                 {"datapanel":{"@type": "current",
                                                                               "@tab": "current",
                                                                               "element": {"@id": "subjectsXform",
                                                                                           "add_context":"edit"}
                                                                               }
                                                                  }]
                                                      }
                                            })
        data["gridtoolbar"]["item"].append({"@img": 'gridToolBar/deleteDocument.png',
                                            "@text":"Удалить",
                                            "@hint":"Удалить субъект",
                                            "@disable": style,
                                            "action":{"@show_in": "MODAL_WINDOW",
                                                      "#sorted":[{"main_context":"current"},
                                                                 {"modalwindow":{"@caption": "Удаление субъекта",
                                                                                 "@height": "150",
                                                                                 "@width": "450"}
                                                                  },
                                                                 {"datapanel":{"@type": "current",
                                                                               "@tab": "current",
                                                                               "element": {"@id": "subjectsXformDelete",
                                                                                           "add_context":"delete"}
                                                                               }
                                                                  }]
                                                      }
                                            })
    data["gridtoolbar"]["item"].append({"@img": 'gridToolBar/addDirectory.png',
                                        "@text":"Добавить роли",
                                        "@hint":"Добавить роли",
                                        "@disable": style,
                                        "action":{"@show_in": "MODAL_WINDOW",
                                                  "#sorted":[{"main_context":"current"},
                                                             {"modalwindow":{"@caption": "Добавление ролей",
                                                                             "@height": "350",
                                                                             "@width": "500"}
                                                              },
                                                             {"datapanel":{"@type": "current",
                                                                           "@tab": "current",
                                                                           "element": {"@id": "subjectsRolesXform",
                                                                                       "add_context":""}
                                                                           }
                                                              }]
                                                  }
                                        })
    return XMLJSONConverter.jsonToXml(json.dumps(data))

