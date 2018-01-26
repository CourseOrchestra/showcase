# coding: utf-8
'''
Created on 03.12.2013

@author: d.bozhenko

'''
import json
import base64
try:
    from ru.curs.showcase.core.jython import JythonDTO
    from ru.curs.showcase.core.selector import ResultSelectorData
    from ru.curs.showcase.app.api.selector import DataRecord
except:
    from ru.curs.celesta.showcase import JythonDTO, DataRecord, ResultSelectorData
from java.text import SimpleDateFormat
import time, datetime

from ru.curs.celesta import CelestaException
from ru.curs.celesta.showcase.utils import XMLJSONConverter
from common._common_orm import linesOfNumbersSeriesCursor



def cardData(context, main=None, add=None, filterinfo=None, session=None, elementId=None):
    u'''Функция данных для карточки редактирования содержимого таблицы разрешения. '''
    
    gridContext = json.loads(session)['sessioncontext']['related']['gridContext']
    gridContext = gridContext if isinstance(gridContext, list) else [gridContext]
    currentId={}
    for gc in gridContext:
        if "currentRecordId" in gc.keys():
            currentId[gc["@id"]] = gc["currentRecordId"]
    
    if add == 'add':
        if "numbersSeriesGrid" in currentId.keys():
            seriesId = currentId["numbersSeriesGrid"]
        else:
            seriesId = ''
        xformsdata = {"schema":{"numberSeries":{"@seriesId":seriesId,
                                                "@numberOfLine":"",
                                                "@startingDate":"",
                                                "@startingNumber":"",
                                                "@endingNumber":"",
                                                "@incrimentByNumber":"",
                                                "@lastUsedNumber":"",
                                                "@isOpened":"",
                                                "@lastUsedDate":"",
                                                "@prefix":"",
                                                "@postfix":"",
                                                "@isFixedLength":""}
                                }
                      }
    elif add == 'edit':
        sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS")
        linesOfNumbersSeries = linesOfNumbersSeriesCursor(context)
        linesOfNumbersSeries.get(currentId["numbersSeriesGrid"], int(currentId["linesNumbersSeriesGrid"]))
        xformsdata = {"schema":{"numberSeries":{"@seriesId":linesOfNumbersSeries.seriesId,
                                                "@numberOfLine":linesOfNumbersSeries.numberOfLine,
                                                "@startingDate":unicode(sdf.format(linesOfNumbersSeries.startingDate)),
                                                "@startingNumber":linesOfNumbersSeries.startingNumber,
                                                "@endingNumber":linesOfNumbersSeries.endingNumber,
                                                "@incrimentByNumber":linesOfNumbersSeries.incrimentByNumber,
                                                "@lastUsedNumber":linesOfNumbersSeries.lastUsedNumber,
                                                "@isOpened":unicode(linesOfNumbersSeries.isOpened).lower(),
                                                "@lastUsedDate":unicode(sdf.format(linesOfNumbersSeries.lastUsedDate)),
                                                "@prefix":linesOfNumbersSeries.prefix,
                                                "@postfix":linesOfNumbersSeries.postfix,
                                                "@isFixedLength":unicode(linesOfNumbersSeries.isFixedLength).lower()}
                                }
                      }


    # print xformsdata
    xformssettings = {"properties":{"event":{"@name":"single_click",
                                             "@linkId": "1",
                                             "action":{"#sorted":[{"main_context": "current"},
                                                                   {"datapanel": {"@type": "current",
                                                                                 "@tab": "current",
                                                                                 "element": {"@id":"linesNumbersSeriesGrid",
                                                                                             "add_context": ""}
                                                                                 }
                                                                   }]}
                                             }
                                    }
                      }

    return JythonDTO(XMLJSONConverter.jsonToXml(json.dumps(xformsdata)),
                     XMLJSONConverter.jsonToXml(json.dumps(xformssettings))
                     )


def cardDataSave(context, main=None, add=None, filterinfo=None, session=None, elementId=None, xformsdata=None):    
    u'''Функция сохранения карточки редактирования содержимого справочника разрешений. '''    
    linesOfNumbersSeries = linesOfNumbersSeriesCursor(context)
    content = json.loads(xformsdata)["schema"]["numberSeries"]
    #raise Exception(xformsdata)
    sdf = SimpleDateFormat("yyyy-MM-dd")
    linesOfNumbersSeries.seriesId = content["@seriesId"]
    linesOfNumbersSeries.numberOfLine = int(content["@numberOfLine"])
    if content["@startingDate"]=='':
        linesOfNumbersSeries.startingDate = sdf.parse(datetime.datetime.fromtimestamp(time.time()).strftime("%Y-%m-%d"))
    else:
        linesOfNumbersSeries.startingDate = sdf.parse(content["@startingDate"])
    linesOfNumbersSeries.startingNumber = content["@startingNumber"]
    linesOfNumbersSeries.endingNumber = content["@endingNumber"]
    linesOfNumbersSeries.incrimentByNumber = content["@incrimentByNumber"]    
    linesOfNumbersSeries.isOpened = content["@isOpened"]=="true"
    if content["@lastUsedDate"]<>'':
        linesOfNumbersSeries.lastUsedDate =  sdf.parse(content["@lastUsedDate"])
    linesOfNumbersSeries.prefix = content["@prefix"]
    linesOfNumbersSeries.postfix = content["@postfix"]
    linesOfNumbersSeries.isFixedLength = content["@isFixedLength"]=="true"
    if add == 'add' and linesOfNumbersSeries.canInsert() and linesOfNumbersSeries.canModify():
        if content["@lastUsedNumber"]=='':
            linesOfNumbersSeries.lastUsedNumber = int(content["@startingNumber"])
        else:
            linesOfNumbersSeries.lastUsedNumber = int(content["@lastUsedNumber"])
        if not linesOfNumbersSeries.tryInsert():
            linesOfNumbersSeriesOld = linesOfNumbersSeriesCursor(context)
            linesOfNumbersSeriesOld.get(content["@seriesId"], int(content["@numberOfLine"]))
            linesOfNumbersSeries.recversion = linesOfNumbersSeriesOld.recversion
            linesOfNumbersSeries.update()
    elif add == 'add' and linesOfNumbersSeries.canInsert():
        if content["@lastUsedNumber"]=='':
            linesOfNumbersSeries.lastUsedNumber = int(content["@startingNumber"])
        else:
            linesOfNumbersSeries.lastUsedNumber = int(content["@lastUsedNumber"])
        linesOfNumbersSeries.insert()
    elif add == 'edit' and linesOfNumbersSeries.canModify():
        linesOfNumbersSeriesOld = linesOfNumbersSeriesCursor(context)
        linesOfNumberSeriesTest = linesOfNumbersSeriesCursor(context)
        gridContext = json.loads(session)['sessioncontext']['related']['gridContext']
        gridContext = gridContext if isinstance(gridContext, list) else [gridContext]
        currentId={}
        for gc in gridContext:
            if "currentRecordId" in gc.keys():
                currentId[gc["@id"]] = gc["currentRecordId"]        
        linesOfNumbersSeriesOld.get(currentId["numbersSeriesGrid"], int(currentId["linesNumbersSeriesGrid"]))
        if content["@lastUsedNumber"]=='':
            linesOfNumbersSeries.lastUsedNumber = linesOfNumbersSeriesOld.lastUsedNumber
        else:
            linesOfNumbersSeries.lastUsedNumber = int(content["@lastUsedNumber"])
        if linesOfNumbersSeriesOld.numberOfLine==linesOfNumbersSeries.numberOfLine:
            #linesOfNumbersSeriesOld.seriesId==linesOfNumbersSeries.seriesId and \
            linesOfNumbersSeries.recversion = linesOfNumbersSeriesOld.recversion
            linesOfNumbersSeries.update()
        elif linesOfNumberSeriesTest.tryGet(linesOfNumbersSeries.seriesId, int(linesOfNumbersSeries.numberOfLine)):
            context.error(u'Серия с данным номером уже существует.')
        elif linesOfNumbersSeries.canInsert():
            linesOfNumbersSeriesOld.delete()
            linesOfNumbersSeries.insert()
        else:
            raise CelestaException(u"Недостаточно прав для данной операции!")
        
    else:
        raise CelestaException(u"Недостаточно прав для данной операции!")