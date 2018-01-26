# coding: utf-8

def navLyra(context, session=None):
    resultJSON = {"group":
                     {"@id": "lyra",
                      "@name": "Лира",
                      "level1":
                        {"@id": "lyra",
                          "@name": "Тестовая карточка",
                          "@selectOnLoad": "true",
                          "action":
                            {"#sorted": [{
                             "main_context": "testgrain.testform.TestForm" },
#                             "main_context": "testgrain.testgridform.TestGridForm" },
                            {"datapanel":
                                {"@type": "lyra.xml",
                                 "@tab": "firstOrCurrent"}}]
                             }
                         }
                      }
                   }
    
    
    return resultJSON

