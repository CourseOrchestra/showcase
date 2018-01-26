# coding: utf-8
import json
from ru.curs.celesta.showcase.utils import XMLJSONConverter

def datapanel(context, main=None, session=None):
    u'''Продедура возвращает информационную панель для разрешений'''
    data = {"datapanel":{"tab":{"@id":1,
                                "@name":u"Серии номеров",
                                "element":[{"@id":"numbersSeriesGrid",
                                            "@type":"grid",
                                            "@subtype":"JS_LIVE_GRID",
                                            "@plugin":"liveDGrid",
                                            "@proc":"common.grids.numbersSeries.gridData.celesta",
                                            "proc":[{"@id":"numbersSeriesGridMeta",
                                                     "@name":"common.grids.numbersSeries.gridMeta.celesta",
                                                     "@type":"METADATA"
                                                     },
                                                    {"@id":"numbersSeriesGridToolBar",
                                                     "@name":"common.grids.numbersSeries.gridToolBar.celesta",
                                                     "@type":"TOOLBAR"
                                                     }]
                                            },
                                           {"@id":"numbersSeriesXforms",
                                            "@type":"xforms",
                                            "@template": "common/numbersSeries.xml",
                                            "@neverShowInPanel":"true",
                                            "@proc":"common.xforms.numbersSeries.cardData.celesta",
                                            "proc":{"@id":"numbersSeriesXformSave",
                                                    "@name":"common.xforms.numbersSeries.cardDataSave.celesta",
                                                    "@type":"SAVE"},
                                            "related":{"@id":"numbersSeriesGrid"}
                                            },
                                           {"@id":"numbersSeriesXformDelete",
                                            "@type":"xforms",
                                            "@template":"common/delete.xml",
                                            "@neverShowInPanel":"true",
                                            "@proc":"common.xforms.numbersSeriesDelete.cardData.celesta",
                                            "proc":{"@id":"numbersSeriesXformSave",
                                                    "@name":"common.xforms.numbersSeriesDelete.cardDelete.celesta",
                                                    "@type":"SAVE"},
                                            "related":{"@id":"numbersSeriesGrid"}
                                            },
                                           {"@id":"numbersSeriesDownloadXform",
                                            "@type":"xforms",
                                            "@template":"common/tableLoad.xml",
                                            "@neverShowInPanel":"true",
                                            "@proc":"common.xforms.numbersSeriesLoad.cardData.celesta",
                                            "proc":{"@id":"tableXformDownload",
                                                    "@name":"common.xforms.numbersSeriesLoad.numbersSeriesDownload.celesta",
                                                    "@type":"DOWNLOAD"}
                                             },
                                            {"@id":"numbersSeriesUploadXform",
                                             "@type":"xforms",
                                             "@template":"common/tableLoad.xml",
                                             "@neverShowInPanel":"true",
                                             "@proc":"common.xforms.numbersSeriesLoad.cardData.celesta",
                                             "proc":[{"@id":"tableXformUpload",
                                                      "@name":"common.xforms.numbersSeriesLoad.numbersSeriesUpload.celesta",
                                                      "@type":"UPLOAD"},
                                                     {"@id":"numbersSeriesXformUploadSave",
                                                      "@name":"common.xforms.numbersSeriesLoad.cardSave.celesta",
                                                      "@type":"SAVE"}
                                                     ]
                                             },
                                           
                                           {"@id":"linesNumbersSeriesGrid",
                                            "@type":"grid",
                                            "@subtype":"JS_LIVE_GRID",
                                            "@plugin":"liveDGrid",
                                            "@proc":"common.grids.linesNumbersSeries.gridData.celesta",
                                            "@hideOnLoad":"true",
                                            "proc":[{"@id":"linesNumbersSeriesGridMeta",
                                                     "@name":"common.grids.linesNumbersSeries.gridMeta.celesta",
                                                     "@type":"METADATA"
                                                     },
                                                    {"@id":"linesNumbersSeriesGridToolBar",
                                                     "@name":"common.grids.linesNumbersSeries.gridToolBar.celesta",
                                                     "@type":"TOOLBAR"
                                                     }],
                                            "related":{"@id":"numbersSeriesGrid"}
                                            },
                                           {"@id":"linesNumbersSeriesXforms",
                                            "@type":"xforms",
                                            "@template": "common/linesNumbersSeries.xml",
                                            "@neverShowInPanel":"true",
                                            "@proc":"common.xforms.linesNumbersSeries.cardData.celesta",
                                            "proc":{"@id":"linesNumbersSeriesXformSave",
                                                    "@name":"common.xforms.linesNumbersSeries.cardDataSave.celesta",
                                                    "@type":"SAVE"},
                                            "related":[{"@id":"linesNumbersSeriesGrid"},
                                                       {"@id":"numbersSeriesGrid"}]
                                            },
                                           {"@id":"linesNumbersSeriesXformDelete",
                                            "@type":"xforms",
                                            "@template":"common/delete.xml",
                                            "@neverShowInPanel":"true",
                                            "@proc":"common.xforms.linesNumbersSeriesDelete.cardData.celesta",
                                            "proc":{"@id":"linesNumbersSeriesXformSave",
                                                    "@name":"common.xforms.linesNumbersSeriesDelete.cardDelete.celesta",
                                                    "@type":"SAVE"},
                                            "related":[{"@id":"linesNumbersSeriesGrid"},
                                                       {"@id":"numbersSeriesGrid"}]
                                            },
                                           {"@id":"linesNumbersSeriesDownloadXform",
                                            "@type":"xforms",
                                            "@template":"common/tableLoad.xml",
                                            "@neverShowInPanel":"true",
                                            "@proc":"common.xforms.linesNumbersSeriesLoad.cardData.celesta",
                                            "proc":{"@id":"tableXformDownload",
                                                    "@name":"common.xforms.linesNumbersSeriesLoad.linesNumbersSeriesDownload.celesta",
                                                    "@type":"DOWNLOAD"}
                                             },
                                            {"@id":"linesNumbersSeriesUploadXform",
                                             "@type":"xforms",
                                             "@template":"common/tableLoad.xml",
                                             "@neverShowInPanel":"true",
                                             "@proc":"common.xforms.linesNumbersSeriesLoad.cardData.celesta",
                                             "proc":[{"@id":"tableXformUpload",
                                                      "@name":"common.xforms.linesNumbersSeriesLoad.linesNumbersSeriesUpload.celesta",
                                                      "@type":"UPLOAD"},
                                                     {"@id":"linesNumbersSeriesXformUploadSave",
                                                      "@name":"common.xforms.linesNumbersSeriesLoad.cardSave.celesta",
                                                      "@type":"SAVE"}
                                                     ]
                                             }]
                                }
                         }
            }    
    #raise Exception(XMLJSONConverter(input=data).parse())
    return XMLJSONConverter.jsonToXml(json.dumps(data))

