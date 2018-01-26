# coding: utf-8
'''
Created on 09.07.2014

@author: D.Bozhenko.
'''

import json

try:
    from ru.curs.showcase.core.jython import JythonDTO
except:
    from ru.curs.celesta.showcase import JythonDTO

try:
    from ru.curs.showcase.core.selector import ResultSelectorData
    from ru.curs.showcase.app.api.selector import DataRecord
except:
    pass

from ru.curs.celesta.showcase.utils import XMLJSONConverter

from common.sysfunctions import tableCursorImport

def cardData(context, main=None, add=None, filterinfo=None, session=None, elementId=None):
    u'''
    Функция для демонстрационной lyra-карточки 
    '''
    table = main.split('.')    # считаем, что полное имя таблицы приходит в main в виде <grainName>.<tableName>
    tableCursorInstance = tableCursorImport(table[0], table[1])(context)
    fields = tableCursorInstance.meta().getColumns().keys()
#    permissions:
#    0 - hide
#    1 - show
#    2 - disable
    buttons = ["first", "previous", "next", "last", "insert", "delete"]
    xformsdata = {"schema":{"@xmlns":'',
                            # тулбар фильтров
                            "filter":{"@permission":"1",
                                      "fields":{"field":[]}
                                      },
                            # тулбар сортировки
                            "order":{"@permission":"1",
                                     "fields":{"field":[{"value":field,
                                                         "enable":"false",
                                                         "sort":"asc"}
                                                        for field in fields]
                                               }
                                     },
                            # тулбар навигации
                            "navigator":{"@permission":"1",
                                         "@buttonContext":"",
                                         "button":[{"enable":"true",
                                                    "title":buttonTitle}
                                                   for buttonTitle in buttons]
                                         }
                            }
                  }
    xformssettings = {"properties":{"action":{"main_context": "current",
                                              "datapanel":{"@type": "current",
                                                           "@tab": "current",
                                                           "element":[{"@id": main,
                                                                       "add_context": ""}]
                                                           }
                                              },
                                    "event":[{"@name": "single_click",
                                              "@linkId": "1",
                                              "action":{"main_context": "current",
                                                        "datapanel":{"@type": "current",
                                                                     "@tab": "current",
                                                                     "element":[{"@id":  main,
                                                                                "add_context": "hide"}]
                                                                     }
                                                        }
                                              }]
                                    }
                      }
    jsonData = XMLJSONConverter.jsonToXml(json.dumps(xformsdata))
    jsonSettings = XMLJSONConverter.jsonToXml(json.dumps(xformssettings))
    return JythonDTO(jsonData, jsonSettings)



