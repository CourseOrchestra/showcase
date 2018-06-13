# coding: utf-8

def navLyra(context, session=None):
    resultJSON = {"group":
                     {"@id": "lyra",
                      "@name": "Лира",
                      
                      "level1":
                        [
                      
                        {"@id": "lyra",
                          "@selectOnLoad": "true",
                          "@name": "lyra грид как элемент шоукейза",
                          "action":
                            {"#sorted": [{
                             "main_context": "testgrain.testform.TestForm" },
#                             "main_context": "testgrain.testgridform.TestGridForm" },
                            {"datapanel":
                                {"@type": "lyra.xml",
                                 "@tab": "firstOrCurrent"}}]
                             }
                         },
                         
                         {"@id": "lyraVue",
                          "@name": "lyra-vue грид как компонент Vue",
                          "@selectOnLoad": "false",
                          "action":
                            {"#sorted": [{
                             "main_context": "current" },
#                             "main_context": "testgrain.testgridform.TestGridForm" },
                            {"datapanel":
                                {"@type": "lyraVue.xml",
                                 "@tab": "firstOrCurrent"}}]
                             }
                         },
                         
                         
                         {"@id": "lyraDebug",
                          "@name": "Отладка",
                          "@selectOnLoad": "false",
                          "action":
                            {"#sorted": [{
                             "main_context": "current" },
#                             "main_context": "testgrain.testgridform.TestGridForm" },
                            {"datapanel":
                                {"@type": "lyraDebug.xml",
                                 "@tab": "firstOrCurrent"}}]
                             }
                         },
                         
                      
                         
                         
                        ]   
                                          
                      

                      
                      
                      }
                   }
    
    
    return resultJSON

