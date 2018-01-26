# coding: utf-8
import json
from ru.curs.celesta.showcase.utils import XMLJSONConverter

def rolesDatapanel(context, main=None, session=None):
    u'''Продедура возвращает информационную панель для ролей'''
    data = {"datapanel":{"tab":{"@id":"01",
                                "@name":"Роли",
                                "element":[{"@id":"id_roles_grid",
                                           "@type":"grid",
                                           "@subtype":"JS_LIVE_GRID",
                                           "@plugin":"liveDGrid",
                                           "@proc":"security.grid.roles.gridData.celesta",
                                           "proc":[{"@id":"id_roles_grid_meta",
                                                    "@name":"security.grid.roles.gridMeta.celesta",
                                                    "@type":"METADATA"
                                                    },
                                                   {"@id":"toolbarCatalogWork",
                                                    "@name":"security.grid.roles.gridToolBar.celesta",
                                                    "@type":"TOOLBAR"
                                                    }]
                                            },
                                           {"@id":"id_roles_form",
                                            "@type":"xforms",
                                            "@template":"security/roles.xml",
                                            "@neverShowInPanel":"true",
                                            "@proc":"security.xform.roles.cardData.celesta",
                                            "proc":{"@id":"id_roles_card_save",
                                                    "@name":"security.xform.roles.cardDataSave.celesta",
                                                    "@type":"SAVE"
                                                    },
                                            "related":{"@id":"id_roles_grid"}
                                            },
                                           {"@id":"id_roles_form_delete",
                                            "@type":"xforms",
                                            "@template":"security/delete.xml",
                                            "@neverShowInPanel":"true",
                                            "@proc":"security.xform.rolesDelete.cardData.celesta",
                                            "proc":{"@id":"id_roles_card_save",
                                                    "@name":"security.xform.rolesDelete.cardDelete.celesta",
                                                    "@type":"SAVE"
                                                    },
                                            "related":{"@id":"id_roles_grid"}
                                            },
                                           {"@id":"id_roles_users_form",
                                            "@type":"xforms",
                                            "@template":"security/rolesUsers.xml",
                                            "@neverShowInPanel":"true",
                                            "@proc":"security.xform.rolesUsers.cardData.celesta",
                                            "proc":{"@id":"id_roles_card_save",
                                                    "@name":"security.xform.rolesUsers.cardDataSave.celesta",
                                                    "@type":"SAVE"
                                                    },
                                            "related":{"@id":"id_roles_grid"}
                                            },
                                           {"@id":"id_perm_roles_grid",
                                            "@type":"grid",
                                            "@subtype":"JS_LIVE_GRID",
                                            "@plugin":"liveDGrid",
                                            "@proc":"security.grid.permissions.gridData.celesta",
                                            "@hideOnLoad":"true",
                                            "proc":{"@id":"id_perm_roles_grid_meta",
                                                     "@name":"security.grid.permissions.gridMeta.celesta",
                                                     "@type":"METADATA"
                                                     },
                                            "related":{"@id":"id_roles_grid"}
                                            },
                                           {"@id":"rolesDownloadXform",
                                            "@type":"xforms",
                                            "@template":"security/tableLoad.xml",
                                            "@neverShowInPanel":"true",
                                            "@proc":"security.xform.rolesLoad.cardData.celesta",
                                            "proc":{"@id":"tableXformDownload",
                                                    "@name":"security.xform.rolesLoad.rolesDownload.celesta",
                                                    "@type":"DOWNLOAD"}
                                             },
                                            {"@id":"rolesUploadXform",
                                             "@type":"xforms",
                                             "@template":"security/tableLoad.xml",
                                             "@neverShowInPanel":"true",
                                             "@proc":"security.xform.rolesLoad.cardData.celesta",
                                             "proc":[{"@id":"tableXformUpload",
                                                      "@name":"security.xform.rolesLoad.rolesUpload.celesta",
                                                      "@type":"UPLOAD"},
                                                     {"@id":"rolesXformUploadSave",
                                                      "@name":"security.xform.rolesLoad.cardSave.celesta",
                                                      "@type":"SAVE"}
                                                     ]
                                             }]
                                }
                         }
            }

    res = XMLJSONConverter.jsonToXml(json.dumps(data))
    return res

