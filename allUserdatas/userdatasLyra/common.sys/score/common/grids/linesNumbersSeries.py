# coding: utf-8

import json
from java.text import SimpleDateFormat
from ru.curs.celesta.showcase.utils import XMLJSONConverter
from common.sysfunctions import toHexForXml
from common._common_orm import linesOfNumbersSeriesCursor

try:
    from ru.curs.showcase.core.jython import JythonDTO
except:
    from ru.curs.celesta.showcase import JythonDTO

def gridData(context, main=None, add=None, filterinfo=None,
             session=None, elementId=None, sortColumnList=[], firstrecord=0, pagesize=50):    
    u'''Функция получения данных для грида. '''
        
    linesOfNumbersSeries = linesOfNumbersSeriesCursor(context)
    if 'currentRecordId' in session:
        currId = json.loads(session)['sessioncontext']['related']['gridContext']['currentRecordId']
        linesOfNumbersSeries.setRange("seriesId", currId)
    # Определяем переменную для JSON данных
    data = {"records":{"rec":[]}}
    
    linesOfNumbersSeries.limit(firstrecord - 1, pagesize)
    
    # Проходим по таблице и заполняем data    
    for linesOfNumbersSeries in linesOfNumbersSeries.iterate():
        linesDict = {}
        linesDict[toHexForXml('~~id')] = linesOfNumbersSeries.numberOfLine        
        linesDict[toHexForXml(u"Номер серии")] = linesOfNumbersSeries.numberOfLine
        linesDict[toHexForXml(u"Начальная дата")] = SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(linesOfNumbersSeries.startingDate)\
                                                                if linesOfNumbersSeries.startingDate\
                                                                else linesOfNumbersSeries.startingDate
        linesDict[toHexForXml(u"Начальный номер")] = linesOfNumbersSeries.startingNumber
        linesDict[toHexForXml(u"Последний номер")] = linesOfNumbersSeries.endingNumber
        linesDict[u"Инкремент"] = linesOfNumbersSeries.incrimentByNumber
        linesDict[toHexForXml(u"Последний использованный номер")] = linesOfNumbersSeries.lastUsedNumber
        linesDict[u"Используется"] = 'gridToolBar/yes.png' if linesOfNumbersSeries.isOpened else 'gridToolBar/no.png'
        linesDict[toHexForXml(u"Дата последнего использования")] = SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(linesOfNumbersSeries.lastUsedDate)\
                                                                            if linesOfNumbersSeries.lastUsedDate\
                                                                            else linesOfNumbersSeries.lastUsedDate
        linesDict[u"Префикс"] = linesOfNumbersSeries.prefix
        linesDict[u"Постфикс"] = linesOfNumbersSeries.postfix
        linesDict[toHexForXml(u"Фиксированная длина")] = 'gridToolBar/yes.png' if linesOfNumbersSeries.isFixedLength else 'gridToolBar/no.png'
        linesDict['properties'] = {"event":{"@name":"row_single_click",
                                            "action":{"#sorted":[{"main_context": 'current'},
                                                                 {"datapanel":{'@type':"current",
                                                                               '@tab':"current"}
                                                                  }]
                                                      }
                                            }
                                   }
        data["records"]["rec"].append(linesDict)

    res = XMLJSONConverter.jsonToXml(json.dumps(data))
    return JythonDTO(res, None)

def gridMeta(context, main=None, add=None, filterinfo=None, session=None, elementId=None):
    u'''Функция получения настроек грида. '''

    # Курсор таблицы permissions
    linesOfNumbersSeries = linesOfNumbersSeriesCursor(context)
    # Вычисляем количества записей в таблице
    totalcount = linesOfNumbersSeries.count()
    # Заголовок таблицы
    header = "Серии номеров"
    # В случае если таблица пустая
    if totalcount == 0 or totalcount is None:
        totalcount = "0"
        header = header + " ПУСТ"

    # Определяем список полей таблицы для отображения
    settings = {}
    settings["gridsettings"] = {"columns": {"col":[]},
                                "properties": {"@pagesize":"50", "@gridWidth": "100%", "@totalCount": totalcount, "@profile":"default.properties"},
                                "labels":{"header":header}
                                }
    # Добавляем поля для отображения в gridsettings
    settings["gridsettings"]["columns"]["col"].append({"@id":"Номер серии", "@width": "80px"})
    settings["gridsettings"]["columns"]["col"].append({"@id":"Начальная дата", "@width": "100px"})
    settings["gridsettings"]["columns"]["col"].append({"@id":"Начальный номер", "@width": "80px"})
    settings["gridsettings"]["columns"]["col"].append({"@id":"Последний номер", "@width": "80px"})
    settings["gridsettings"]["columns"]["col"].append({"@id":"Инкремент", "@width": "80px"})
    settings["gridsettings"]["columns"]["col"].append({"@id":"Последний использованный номер", "@width": "80px"})
    settings["gridsettings"]["columns"]["col"].append({"@id":"Используется", "@width": "80px", "@type":"IMAGE"})
    settings["gridsettings"]["columns"]["col"].append({"@id":"Дата последнего использования", "@width": "80px"})
    settings["gridsettings"]["columns"]["col"].append({"@id":"Префикс", "@width": "100px"})
    settings["gridsettings"]["columns"]["col"].append({"@id":"Постфикс", "@width": "100px"})    
    settings["gridsettings"]["columns"]["col"].append({"@id":"Фиксированная длина", "@width": "80px", "@type":"IMAGE"})

    res = XMLJSONConverter.jsonToXml(json.dumps(settings))
    return JythonDTO(None, res)

def gridToolBar(context, main=None, add=None, filterinfo=None, session=None, elementId=None):
    u'''Toolbar для грида. '''
    
    gridContextList = json.loads(session)['sessioncontext']['related']['gridContext']
    gridContextList = gridContextList if isinstance(gridContextList, list) else [gridContextList]
    
    for gridContext in gridContextList:
        if gridContext['@id'] == 'linesNumbersSeriesGrid':
            break

    if 'currentRecordId' not in gridContext:
        style = "true"
    else:
        style = "false"

    data = {"gridtoolbar":{"item":[]
                           }
            }
    # Курсор таблицы permissions
    linesOfNumbersSeries = linesOfNumbersSeriesCursor(context)

    if linesOfNumbersSeries.canInsert():
        data["gridtoolbar"]["item"].append({"@img": 'gridToolBar/addDirectory.png',
                                            "@text":"Добавить",
                                            "@hint":"Добавить",
                                            "@disable": "false",
                                            "action":{"@show_in": "MODAL_WINDOW",
                                                      "#sorted":[{"main_context":"current"},
                                                                 {"modalwindow":{"@caption": "Добавление серии",
                                                                                 "@height": "500",
                                                                                 "@width": "500"}
                                                                  },
                                                                 {"datapanel":{"@type": "current",
                                                                               "@tab": "current",
                                                                               "element": {"@id": "linesNumbersSeriesXforms",
                                                                                           "add_context":"add"}
                                                                               }
                                                                  }]
                                                      }
                                            })
    if linesOfNumbersSeries.canModify():
        data["gridtoolbar"]["item"].append({"@img": 'gridToolBar/editDocument.png',
                                            "@text":"Редактировать",
                                            "@hint":"Редактировать",
                                            "@disable": style,
                                            "action":{"@show_in": "MODAL_WINDOW",
                                                      "#sorted":[{"main_context":"current"},
                                                                 {"modalwindow":{"@caption": "Редактирование серии",
                                                                                 "@height": "500",
                                                                                 "@width": "500"}
                                                                  },
                                                                 {"datapanel":{"@type": "current",
                                                                               "@tab": "current",
                                                                               "element": {"@id": "linesNumbersSeriesXforms",
                                                                                           "add_context":"edit"}
                                                                               }
                                                                  }]
                                                      }
                                            })
    if linesOfNumbersSeries.canDelete():
        data["gridtoolbar"]["item"].append({"@img": 'gridToolBar/deleteDocument.png',
                                            "@text":"Удалить",
                                            "@hint":"Удалить",
                                            "@disable": style,
                                            "action":{"@show_in": "MODAL_WINDOW",
                                                      "#sorted":[{"main_context":"current"},
                                                                 {"modalwindow":{"@caption": "Удаление серии",
                                                                                 "@height": "300",
                                                                                 "@width": "450"}
                                                                  },
                                                                 {"datapanel":{"@type": "current",
                                                                               "@tab": "current",
                                                                               "element": {"@id": "linesNumbersSeriesXformDelete",
                                                                                           "add_context":"delete"}
                                                                               }
                                                                  }]
                                                      }
                                            })
    data["gridtoolbar"]["item"].append({"@img": 'gridToolBar/arrowDown.png',
                                            "@text":"Скачать",
                                            "@hint":"Скачать экземпляры серий номеров в xml",
                                            "@disable": "false",
                                            "action":{"@show_in": "MODAL_WINDOW",
                                                      "#sorted":[{"main_context":"current"},
                                                                 {"modalwindow":{"@caption": "Скачать экземпляры серий номеров",
                                                                                 "@height": "300",
                                                                                 "@width": "450"}
                                                                  },
                                                                 {"datapanel":{"@type": "current",
                                                                               "@tab": "current",
                                                                               "element": {"@id": "linesNumbersSeriesDownloadXform",
                                                                                           "add_context":"download"}
                                                                               }
                                                                  }]
                                                      }
                                            }
                                       )
    data["gridtoolbar"]["item"].append({"@img": 'gridToolBar/arrowUp.png',
                                            "@text":"Загрузить",
                                            "@hint":"Загрузить экземпляры серий номеров из xml",
                                            "@disable": "false",
                                            "action":{"@show_in": "MODAL_WINDOW",
                                                      "#sorted":[{"main_context":"current"},
                                                                 {"modalwindow":{"@caption": "Загрузить экземпляры серий номеров",
                                                                                 "@height": "300",
                                                                                 "@width": "450"}
                                                                  },
                                                                 {"datapanel":{"@type": "current",
                                                                               "@tab": "current",
                                                                               "element": {"@id": "linesNumbersSeriesUploadXform",
                                                                                           "add_context":"upload"}
                                                                               }
                                                                  }]
                                                      }
                                            }
                                       )
    
    return XMLJSONConverter.jsonToXml(json.dumps(data))

