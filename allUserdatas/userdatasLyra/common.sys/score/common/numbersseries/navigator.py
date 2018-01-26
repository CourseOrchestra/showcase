# coding: utf-8

def navNumbersSeries(context, session=None):
    resultJSON = {"group":
                     {"@id": "common",
                      "@name": "Общее",
                      "level1":
                        {"@id": "numbersSeries",
                          "@name": "Серии номеров",
                          "action":
                            {"main_context": "current",
                            "datapanel":
                                {"@type": "common.datapanel.numbersseries.datapanel.celesta",
                                 "@tab": "firstOrCurrent"}
                             }
                         }
                      }
                   }
    return resultJSON

