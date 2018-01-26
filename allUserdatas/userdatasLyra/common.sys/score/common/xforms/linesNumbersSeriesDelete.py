# coding: utf-8


import json
import base64
from string import lowercase

try:
    from ru.curs.showcase.core.jython import JythonDTO
except:
    from ru.curs.celesta.showcase import JythonDTO
from ru.curs.celesta import CelestaException
from ru.curs.celesta.showcase.utils import XMLJSONConverter
from common._common_orm import linesOfNumbersSeriesCursor

def cardData(context, main, add, filterinfo=None, session=None, elementId=None):
    xformsdata = {"schema":{"@xmlns":""}}
    xformssettings = {"properties":{"event":[{"@name":"single_click",
                                             "@linkId": "1",
                                             "action":{"#sorted":[{"main_context": "current"},
                                                                   {"datapanel": {"@type": "current",
                                                                                 "@tab": "current",
                                                                                 "element": {"@id":"linesNumbersSeriesGrid",
                                                                                             "add_context": ""
                                                                                             }
                                                                                 }
                                                                   }]}
                                             }]
                                    }
                      }
    xmlData = XMLJSONConverter.jsonToXml(json.dumps(xformsdata))
    xmlSettings = XMLJSONConverter.jsonToXml(json.dumps(xformssettings))
    return JythonDTO(xmlData, xmlSettings)

def cardDelete(context, main=None, add=None, filterinfo=None, session=None, elementId=None, xformsdata=None):    
    linesOfNumbersSeries = linesOfNumbersSeriesCursor(context)
    
    #raise Exception(session)
    
    gridContext = json.loads(session)['sessioncontext']['related']['gridContext']
    gridContext = gridContext if isinstance(gridContext, list) else [gridContext]
    currentId={}
    for gc in gridContext:
        currentId[gc["@id"]] = gc["currentRecordId"]
       
    linesOfNumbersSeries.get(currentId["numbersSeriesGrid"], int(currentId["linesNumbersSeriesGrid"]))
    linesOfNumbersSeries.delete()