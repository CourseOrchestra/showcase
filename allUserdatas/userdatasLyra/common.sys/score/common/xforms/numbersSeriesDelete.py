# coding: utf-8


import json
import base64
from string import lowercase

try:
    from ru.curs.showcase.core.jython import JythonDTO
except:
    from ru.curs.celesta.showcase import JythonDTO

from ru.curs.celesta.showcase.utils import XMLJSONConverter
from common._common_orm import numbersSeriesCursor, linesOfNumbersSeriesCursor

def cardData(context, main, add, filterinfo=None, session=None, elementId=None):
    xformsdata = {"schema":{"@xmlns":""}}
    xformssettings = {"properties":{"event":[{"@name":"single_click",
                                             "@linkId": "1",
                                             "action":{"#sorted":[{"main_context": "current"},
                                                                   {"datapanel": {"@type": "current",
                                                                                 "@tab": "current",
                                                                                 "element": [{"@id":"numbersSeriesGrid",
                                                                                             "add_context": ""},
                                                                                             {"@id":"linesNumbersSeriesGrid",
                                                                                             "add_context": "hide"}
                                                                                             ]
                                                                                 }
                                                                   }]}
                                             }]
                                    }
                      }
    xmlData = XMLJSONConverter.jsonToXml(json.dumps(xformsdata))
    xmlSettings = XMLJSONConverter.jsonToXml(json.dumps(xformssettings))
    return JythonDTO(xmlData, xmlSettings)

def cardDelete(context, main=None, add=None, filterinfo=None, session=None, elementId=None, xformsdata=None):
    currentRecordId = json.loads(session)['sessioncontext']['related']['gridContext']['currentRecordId']
    numbersSeries = numbersSeriesCursor(context)
    linesOfNumbersSeries = linesOfNumbersSeriesCursor(context)
    linesOfNumbersSeries.setRange("seriesId", currentRecordId)
    linesOfNumbersSeries.deleteAll()
    numbersSeries.get(currentRecordId)
    numbersSeries.delete()