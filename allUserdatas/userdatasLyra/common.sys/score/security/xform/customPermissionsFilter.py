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

def cardData(context, main=None, add=None, filterinfo=None, session=None, elementId=None):
    xformsdata = {"schema":{"@xmlns":'',
                            "permission":{"@type":""}
                            }
                  }
    xformssettings = {"properties":{
                                    "action":{"#sorted":[{"main_context": "current"},
                                                          {"datapanel":{"@type": "current",
                                                                       "@tab": "current",
                                                                       "element":[{"@id": "rolesCustomPermissionsGrid",
                                                                                   "add_context": "hide"},
                                                                                  {"@id": "customPermissionsGrid",
                                                                                   "add_context": 'current'}
                                                                                  ]
                                                                       }
                                                         }]},
                                    "event":[{"@name": "single_click",
                                              "@linkId": "11",
                                              "action":{"#sorted":[{"main_context": "current"},
                                                                    {"datapanel":{"@type": "current",
                                                                                 "@tab": "current",
                                                                                 "element":[{"@id": "rolesCustomPermissionsGrid",
                                                                                            "add_context": "hide"},
                                                                                            {"@id": "customPermissionsGrid",
                                                                                            "add_context": 'current'}
                                                                                            ]
                                                                                 }
                                                                    }]}
                                              }]
                                    }
                      }
    jsonData = XMLJSONConverter.jsonToXml(json.dumps(xformsdata))
    jsonSettings = XMLJSONConverter.jsonToXml(json.dumps(xformssettings))
    return JythonDTO(jsonData, jsonSettings)



