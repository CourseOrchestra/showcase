# coding: utf-8
import json
from ru.curs.celesta.showcase.utils import XMLJSONConverter
from security.functions import Settings

def usersDatapanel(context, main, session):
    u'''Продедура возвращает информационную панель для пункта "Сотрудники и пользователи"'''
    settings = Settings()

    data = {"datapanel":{"tab":{"@id":1,
                                "@name":u"Сотрудники и пользователи",
                                "element":[{"@id":"usersGrid",
                                            "@type":"grid",
                                            "@subtype":"JS_PAGE_GRID",
                                            "@plugin":"pageDGrid",
                                            "@proc":"security.grid.users.gridData.celesta",
                                            "proc":[{"@id":"usersGridMeta",
                                                     "@name":"security.grid.users.gridMeta.celesta",
                                                     "@type":"METADATA"
                                                     },
                                                     {"@id":"usersGridToolBar",
                                                      "@name":"security.grid.users.gridToolBar.celesta",
                                                      "@type":"TOOLBAR"
                                                     }]
                                            },
                                           {"@id":"usersXform",
                                            "@type":"xforms",
                                            "@template": "security/users.xml",
                                            "@neverShowInPanel":"true",
                                            "@proc":"security.xform.users.cardData.celesta",
                                            "proc":{"@id":"usersGridMeta",
                                                    "@name":"security.xform.users.cardDataSave.celesta",
                                                    "@type":"SAVE"
                                                    },
                                            "related":{"@id":"usersGrid"}
                                            },
                                           {"@id":"usersXformDelete",
                                            "@type":"xforms",
                                            "@template":"security/delete.xml",
                                            "@neverShowInPanel":"true",
                                            "@proc":"security.xform.usersDelete.cardData.celesta",
                                            "proc":{"@id":"id_roles_card_save",
                                                    "@name":"security.xform.usersDelete.cardDelete.celesta",
                                                    "@type":"SAVE"
                                                    },
                                            "related":{"@id":"usersGrid"}
                                            },
                                           {"@id":"usersRolesXform",
                                            "@type":"xforms",
                                            "@template":"security/usersRoles.xml",
                                            "@neverShowInPanel":"true",
                                            "@proc":"security.xform.usersRoles.cardData.celesta",
                                            "proc":{"@id":"usersRolesXformMeta",
                                                    "@name":"security.xform.usersRoles.cardDataSave.celesta",
                                                    "@type":"SAVE"
                                                    },
                                            "related":{"@id":"usersGrid"}
                                            }]
                                }
                         }
            }

    if not settings.loginIsSubject():
        # если роли привязываются к сотрудникам (субъекты тождественны сотрудникам),
        # добавляем в датапанель гриды и карточки для ведения таблицы subjects
        data["datapanel"]["tab"]["element"].extend([{"@id":"subjectsGrid",
                                                     "@type":"grid",
                                                     "@subtype":"JS_LIVE_GRID",
                                                     "@plugin":"liveDGrid",
                                                     "@proc":"security.grid.subjects.gridData.celesta",
                                                     "proc":[{"@id":"subjectsGridMeta",
                                                              "@name":"security.grid.subjects.gridMeta.celesta",
                                                              "@type":"METADATA"},
                                                             {"@id":"subjectsGridToolBar",
                                                              "@name":"security.grid.subjects.gridToolBar.celesta",
                                                              "@type":"TOOLBAR"}],
                                                     },
                                                    {"@id":"subjectsXform",
                                                     "@type":"xforms",
                                                     "@template": "security/subjects.xml",
                                                     "@neverShowInPanel":"true",
                                                     "@proc":"security.xform.subjects.cardData.celesta",
                                                     "proc":{"@id":"subjectsXformMeta",
                                                             "@name":"security.xform.subjects.cardDataSave.celesta",
                                                             "@type":"SAVE"},
                                                     "related":{"@id":"subjectsGrid"}
                                                     },
                                                    {"@id":"subjectsXformDelete",
                                                     "@type":"xforms",
                                                     "@template":"security/delete.xml",
                                                     "@neverShowInPanel":"true",
                                                     "@proc":"security.xform.subjectsDelete.cardData.celesta",
                                                     "proc":{"@id":"id_roles_card_save",
                                                             "@name":"security.xform.subjectsDelete.cardDelete.celesta",
                                                             "@type":"SAVE"},
                                                     "related":{"@id":"subjectsGrid"}
                                                     },
                                                     {"@id":"subjectsRolesXform",
                                                      "@type":"xforms",
                                                      "@template":"security/usersRoles.xml",
                                                      "@neverShowInPanel":"true",
                                                      "@proc":"security.xform.usersRoles.cardData.celesta",
                                                      "proc":{"@id":"usersRolesXformMeta",
                                                              "@name":"security.xform.usersRoles.cardDataSave.celesta",
                                                              "@type":"SAVE"},
                                                      "related":{"@id":"subjectsGrid"}
                                                      }])
    return XMLJSONConverter.jsonToXml(json.dumps(data))

