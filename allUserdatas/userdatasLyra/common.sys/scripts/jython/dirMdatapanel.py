# coding: utf-8
'''
Created on 18.09.2013

@author: AleXXL
'''
from ru.curs.showcase.core.jython import JythonProc
#from ru.curs.showcase.core.jython import JythonDTO
#from ru.curs.showcase.app.api import UserMessage;
#from ru.curs.showcase.util.xml import XMLUtils
#from org.xml.sax.helpers import DefaultHandler
#from ru.curs.showcase.util import TextUtils

# init vars
main = None
add = None
session = None
filterContext = None


class dirMdatapanel(JythonProc):
    def getRawData(self, context):
        global main, add, session, filterContext
        main = context.getMain()
        if context.getAdditional():
            add = context.getAdditional()
        session = context.getSession()
        if context.getFilter():
            filterContext = context.getFilter()
        return mainproc()


def mainproc():
    return u'''
<datapanel>
  <tab id="1" name="Справочники">
    <element id="structGrid" type="grid" subtype="EXT_TREE_GRID" proc="score/dirM/structure/directoriesGrid.py">
         <proc id="saveDirectories" name="score/dirM/structure/directoriesButtons.py" type="TOOLBAR"/>
    </element>
    <element id="structXformsDirectories" type="xforms" template="dirM/directory.xml" proc="score/dirM/structure/directoriesXformsDirectory.py" neverShowInPanel="true">
      <related id="structGrid"/>
      <proc id="saveDirectories" name="score/dirM/structure/directoriesXformsDirectorySave.py" type="SAVE"/>
    </element>
    <element id="structXformsFolder" type="xforms" template="dirM/folder.xml" proc="score/dirM/structure/directoriesXformsFolder.py" neverShowInPanel="true">
      <related id="structGrid"/>
      <proc id="saveFolder" name="score/dirM/structure/directoriesXformsFolderSave.py" type="SAVE"/>
    </element>
  </tab>
  <tab id="2" name="Импорт/экспорт структуры">
    <element id="2_11" type="xforms" proc="ssp.struct_import_filter" template="ssp_import_struct_filter.xml">
      <proc id="1" name="ssp.struct_data_upload_file" type="UPLOAD"/>
      <proc id="2" name="ssp.struct_data_save_file" type="SAVE"/>
      <proc id="3" name="ssp.struct_data_download_file" type="DOWNLOAD"/>
    </element>
    <element id="2_12" type="grid" subtype="EXT_PAGE_GRID" proc="ssp.struct_import_grid"/>
    <element id="2_13" type="webtext" proc="ssp.struct_import_webtext_row" hideOnLoad="true">
      <related id="2_12"/>
    </element>
  </tab>
  <!--tab id="3" name="Импорт данных в справочники">
    <element id="3_11" type="xforms" proc="ssp.spr_import_filter" template="spr_import_filter.xml">
      <proc id="1" name="ssp.spr_data_upload_file" type="UPLOAD"/>
      <proc id="2" name="ssp.spr_data_save_File" type="SAVE"/>
    </element>
    <element id="3_12" type="grid" subtype="EXT_LIVE_GRID" proc="ssp.spr_import_grid"/>
    <element id="3_13" type="webtext" proc="ssp.spr_import_webtext_row" hideOnLoad="true">
      <related id="3_12"/>
    </element>
  </tab-->
</datapanel>
    '''

if __name__ == "__main__":
    mainproc()
