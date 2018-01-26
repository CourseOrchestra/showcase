# coding: utf-8
'''
Created on 03.12.2013

@author: d.bozhenko

'''
import json
from java.util import ArrayList
import base64
from xml.dom import minidom
try:
    from ru.curs.showcase.core.jython import JythonDTO
    from ru.curs.showcase.core.selector import ResultSelectorData
    from ru.curs.showcase.app.api.selector import DataRecord
except:
    from ru.curs.celesta.showcase import JythonDTO, DataRecord, ResultSelectorData


from ru.curs.celesta import CelestaException
from ru.curs.celesta.showcase.utils import XMLJSONConverter
from common._common_orm import numbersSeriesCursor, linesOfNumbersSeriesCursor



def cardData(context, main=None, add=None, filterinfo=None, session=None, elementId=None):
    u'''Функция данных для карточки редактирования содержимого таблицы разрешения. '''

    numbersSeries = numbersSeriesCursor(context)

    if add == 'add':
        xformsdata = {"schema":{"numbersSeries":{"@id":"",
                                                 "@description":""}
                                }
                      }
    elif add == 'edit':
        currId = json.loads(session)['sessioncontext']['related']['gridContext']['currentRecordId']
        numbersSeries.get(currId)
        xformsdata = {"schema":{"numbersSeries":{"@id":numbersSeries.id,
                                                 "@description":numbersSeries.description}
                                }
                      }
    # print xformsdata
    xformssettings = {"properties":{"event":{"@name":"single_click",
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
                                             }
                                    }
                      }

    return JythonDTO(XMLJSONConverter.jsonToXml(json.dumps(xformsdata)),
                     XMLJSONConverter.jsonToXml(json.dumps(xformssettings))
                     )


def cardDataSave(context, main=None, add=None, filterinfo=None, session=None, elementId=None, xformsdata=None):
    u'''Функция сохранения карточки редактирования содержимого справочника разрешений. '''
    numbersSeries = numbersSeriesCursor(context)    
    content = json.loads(xformsdata)["schema"]["numbersSeries"]
    #raise Exception(permissions.meta().getColumns())
#    for field in permissions.meta().getColumns():
#        permissions.__setattr__(field, content[field])
    numbersSeries.id = content["@id"]
    numbersSeries.description = content["@description"]

    if add == 'add' and numbersSeries.canInsert() and numbersSeries.canModify():
        if not numbersSeries.tryInsert():
            numbersSeriesOld = numbersSeriesCursor(context)
            numbersSeriesOld.get(content["@id"])
            numbersSeries.recversion = numbersSeriesOld.recversion
            numbersSeries.update()
    elif add == 'add' and numbersSeries.canInsert():
        numbersSeries.insert()
    elif add == 'edit' and numbersSeries.canModify():
        numbersSeriesOld = numbersSeriesCursor(context)
        numberSeriesTemp = numbersSeriesCursor(context)
        currId = json.loads(session)['sessioncontext']['related']['gridContext']['currentRecordId']        
        numbersSeriesOld.get(currId)
        if numbersSeriesOld.id==numbersSeries.id:
            numbersSeries.recversion = numbersSeriesOld.recversion
            numbersSeries.update()
        elif numberSeriesTemp.tryGet(content["@id"]):
            context.error(u'Серия номеров с данным ID уже существует. Данные не сохранены!')
        elif numbersSeries.canInsert():
            linesOfNumbersSeries = linesOfNumbersSeriesCursor(context)
            linesOfNumbersSeriesTemp = linesOfNumbersSeriesCursor(context)
            linesOfNumbersSeries.setRange('seriesId', numbersSeriesOld.id)
            numbersSeries.insert()
            if linesOfNumbersSeriesTemp.canInsert():
                for linesOfNumbersSeries in linesOfNumbersSeries.iterate():
                    linesOfNumbersSeriesTemp.get(linesOfNumbersSeries.seriesId, linesOfNumbersSeries.numberOfLine)
                    linesOfNumbersSeriesTemp.seriesId = numbersSeries.id
                    linesOfNumbersSeriesTemp.insert()
            else:
                raise CelestaException(u"Недостаточно прав для данной операции!")
            linesOfNumbersSeries.deleteAll()                    
            numbersSeriesOld.delete()
            
        else:
            raise CelestaException(u"Недостаточно прав для данной операции!")
        
    else:
        raise CelestaException(u"Недостаточно прав для данной операции!")