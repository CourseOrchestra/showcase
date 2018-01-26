# coding: utf-8

import json
import base64
from ru.curs.celesta.showcase.utils import XMLJSONConverter
from common.sysfunctions import toHexForXml
from common._common_orm import numbersSeriesCursor

try:
    from ru.curs.showcase.core.jython import JythonDTO
except:
    from ru.curs.celesta.showcase import JythonDTO

def gridData(context, main=None, add=None, filterinfo=None,
             session=None, elementId=None, sortColumnList=[], firstrecord=0, pagesize=50):    
    u'''Функция получения данных для грида. '''
    
    # raise Exception(firstrecord)

    # Создание экземпляра курсора
    numbersSeries = numbersSeriesCursor(context)
 
    numbersSeries.orderBy('id')

    # Определяем переменную для JSON данных
    data = {"records":{"rec":[]}}
    
    numbersSeries.limit(firstrecord - 1, pagesize)
    
    # Проходим по таблице и заполняем data    
    for numbersSeries in numbersSeries.iterate():
        nsDict = {}
        nsDict[toHexForXml('~~id')] = numbersSeries.id
        nsDict[u"ID"] = numbersSeries.id
        nsDict[u"Описание"] = numbersSeries.description
        nsDict['properties'] = {"event":{"@name":"row_single_click",
                                         "action":{"#sorted":[{"main_context": 'current'},
                                                              {"datapanel":{'@type':"current",
                                                                            '@tab':"current",
                                                                            'element':{'@id':'linesNumbersSeriesGrid'}
                                                                            }
                                                               }]
                                                   }
                                         }
                                }
        data["records"]["rec"].append(nsDict)


    res = XMLJSONConverter.jsonToXml(json.dumps(data))
    return JythonDTO(res, None)

def gridMeta(context, main=None, add=None, filterinfo=None, session=None, elementId=None):
    u'''Функция получения настроек грида. '''

    numbersSeries = numbersSeriesCursor(context)
    # Вычисляем количества записей в таблице
    totalcount = numbersSeries.count()
    # Заголовок таблицы
    header = "Типы серий номеров"
    # В случае если таблица пустая
    if totalcount == 0 or totalcount is None:
        totalcount = "0"
        header = header + " ПУСТ"

    # Определяем список полей таблицы для отображения
    settings = {}
    settings["gridsettings"] = {"columns": {"col":[]},
                                "properties": {"@pagesize":"50",
                                               "@gridWidth": "100%",
                                               "@totalCount": totalcount,
                                               "@profile":"default.properties"},
                                "labels":{"header":header}
                                }
    # Добавляем поля для отображения в gridsettings
    settings["gridsettings"]["columns"]["col"].append({"@id":"ID", "@width": "100px"})
    settings["gridsettings"]["columns"]["col"].append({"@id":"Описание", "@width": "300px"})

    res = XMLJSONConverter.jsonToXml(json.dumps(settings))
    return JythonDTO(None, res)

def gridToolBar(context, main=None, add=None, filterinfo=None, session=None, elementId=None):
    u'''Toolbar для грида. '''

    if 'currentRecordId' not in json.loads(session)['sessioncontext']['related']['gridContext']:
        style = "true"
    else:
        style = "false"

    data = {"gridtoolbar":{"item":[]}
            }
    numbersSeries = numbersSeriesCursor(context)

    if numbersSeries.canInsert():
        data["gridtoolbar"]["item"].append({"@img": 'gridToolBar/addDirectory.png',
                                            "@text":"Добавить",
                                            "@hint":"Добавить",
                                            "@disable": "false",
                                            "action":{"@show_in": "MODAL_WINDOW",
                                                      "#sorted":[{"main_context":"current"},
                                                                 {"modalwindow":{"@caption": "Добавление серии номеров",
                                                                                 "@height": "500",
                                                                                 "@width": "500"}
                                                                  },
                                                                 {"datapanel":{"@type": "current",
                                                                               "@tab": "current",
                                                                               "element": {"@id": "numbersSeriesXforms",
                                                                                           "add_context":"add"}
                                                                               }
                                                                  }]
                                                      }
                                            })
    if numbersSeries.canModify():
        data["gridtoolbar"]["item"].append({"@img": 'gridToolBar/editDocument.png',
                                            "@text":"Редактировать",
                                            "@hint":"Редактировать",
                                            "@disable": style,
                                            "action":{"@show_in": "MODAL_WINDOW",
                                                      "#sorted":[{"main_context":"current"},
                                                                 {"modalwindow":{"@caption": "Редактирование серии номеров",
                                                                                 "@height": "500",
                                                                                 "@width": "500"}
                                                                  },
                                                                 {"datapanel":{"@type": "current",
                                                                               "@tab": "current",
                                                                               "element": {"@id": "numbersSeriesXforms",
                                                                                           "add_context":"edit"}
                                                                               }
                                                                  }]
                                                      }
                                            })
    if numbersSeries.canDelete():
        data["gridtoolbar"]["item"].append({"@img": 'gridToolBar/deleteDocument.png',
                                            "@text":"Удалить",
                                            "@hint":"Удалить",
                                            "@disable": style,
                                            "action":{"@show_in": "MODAL_WINDOW",
                                                      "#sorted":[{"main_context":"current"},
                                                                 {"modalwindow":{"@caption": "Удаление серии номеров",
                                                                                 "@height": "300",
                                                                                 "@width": "450"}
                                                                  },
                                                                 {"datapanel":{"@type": "current",
                                                                               "@tab": "current",
                                                                               "element": {"@id": "numbersSeriesXformDelete",
                                                                                           "add_context":"delete"}
                                                                               }
                                                                  }]
                                                      }
                                            })
    data["gridtoolbar"]["item"].append({"@img": 'gridToolBar/arrowDown.png',
                                            "@text":"Скачать",
                                            "@hint":"Скачать серии номеров в xml",
                                            "@disable": "false",
                                            "action":{"@show_in": "MODAL_WINDOW",
                                                      "#sorted":[{"main_context":"current"},
                                                                 {"modalwindow":{"@caption": "Скачать серии номеров",
                                                                                 "@height": "300",
                                                                                 "@width": "450"}
                                                                  },
                                                                 {"datapanel":{"@type": "current",
                                                                               "@tab": "current",
                                                                               "element": {"@id": "numbersSeriesDownloadXform",
                                                                                           "add_context":"download"}
                                                                               }
                                                                  }]
                                                      }
                                            }
                                       )    
    data["gridtoolbar"]["item"].append({"@img": 'gridToolBar/arrowUp.png',
                                            "@text":"Загрузить",
                                            "@hint":"Загрузить серии номеров из xml",
                                            "@disable": "false",
                                            "action":{"@show_in": "MODAL_WINDOW",
                                                      "#sorted":[{"main_context":"current"},
                                                                 {"modalwindow":{"@caption": "Загрузить серии номеров",
                                                                                 "@height": "300",
                                                                                 "@width": "450"}
                                                                  },
                                                                 {"datapanel":{"@type": "current",
                                                                               "@tab": "current",
                                                                               "element": {"@id": "numbersSeriesUploadXform",
                                                                                           "add_context":"upload"}
                                                                               }
                                                                  }]
                                                      }
                                            }
                                       )


    return XMLJSONConverter.jsonToXml(json.dumps(data))

