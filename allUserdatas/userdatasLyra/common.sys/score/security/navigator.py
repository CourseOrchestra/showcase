# coding: utf-8
'''
Created on 24.09.2014

@author: d.bozhenko
'''

import json
from security.functions import userHasPermission

def authentificationNavigator(context, session):
    '''Часть общего навигатора для гранулы разграничения прав доступа'''
    sid=json.loads(session)['sessioncontext']['sid']
    resultJSON = {"group":{"@id": "security",
                           "@name": "Разграничение прав доступа",
                           #"@icon": "navigatorIcons/security.png",
                           "level1":[]}
                  }
    '''Проверка разрешений на формирование навигатора'''
    if userHasPermission(context, sid, 'loginsSubjectsPoint'):  
        resultJSON["group"]["level1"].append({"@id": "users",
                                              "@name": "Сотрудники и пользователи",
                                              "action":{"#sorted": [{"main_context": "current"},
                                                        {"datapanel":{"@type": "security.datapanel.users.usersDatapanel.celesta",
                                                                     "@tab": "firstOrCurrent"
                                                                     }
                                                         }]
                                                        }
                                              })
    if userHasPermission(context, sid, 'rolesPoint'):
        resultJSON["group"]["level1"].append({"@id": "roles",
                                              "@name": "Роли",
                                              "action":{"#sorted": [{"main_context": "current"},
                                                        {"datapanel":{"@type": "security.datapanel.roles.rolesDatapanel.celesta",
                                                                     "@tab": "firstOrCurrent"
                                                                     }
                                                        }]
                                                        }
                                                        
                                              })
    if userHasPermission(context, sid, 'permissionsPoint'):
        resultJSON["group"]["level1"].append({"@id": "permissions",
                                              "@name": "Разрешения",
                                              "action":{"#sorted": [{"main_context": "current"},
                                                        {"datapanel":{"@type": "security.datapanel.permissions.datapanel.celesta",
                                                                     "@tab": "firstOrCurrent"
                                                                     }
                                                        }]}
                                              })
    
#     resultJSON["group"]["level1"].append({"@id": "test",
#                                               "@name": "test",
#                                               "action":{"main_context": "current",
#                                                         "datapanel":{"@type": "security.testDatapanel.datapanel.celesta",
#                                                                      "@tab": "firstOrCurrent"
#                                                                      }
#                                                         }
#                                               })
                                    
    if  not userHasPermission(context, sid, 'loginsSubjectsPoint') and \
        not userHasPermission(context, sid, 'rolesPoint') and \
        not userHasPermission(context, sid, 'permissionsPoint'):
        resultJSON = {"group": None}
    return resultJSON