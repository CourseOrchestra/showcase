# coding: utf-8
'''
Created on 02.11.2011

@author: den
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


class dp0903(JythonProc):
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
    <tab id="01" name="navigtor from file">
        <element id="0101" type="webtext" proc="webtext_context_info"/>
    </tab>
    <tab id="03" name="dynamic add_context">
        <element id="0301" type="webtext" proc="webtext_override_add_context"/>
        <element id="0302" type="xforms"
        template="Showcase_Template_update.xml"
        proc="xforms_proc_override_add_context"/>
        <element id="d1" type="webtext" proc="webtext_filter_and_add"
            hideOnLoad="true" />
        <element id="d2" type="webtext" proc="webtext_show_debug_console"
        transform="xml_in_html.xsl" hideOnLoad="true"/>
    </tab>
    <tab id="10" name="get tab elements state">
        <element id="61" type="xforms" template="Showcase_Template.xml"
            proc="xforms_proc_dep">
            <proc id="proc1" name="xforms_saveproc1" type="SAVE" />
            <proc id="proc2" name="xforms_submission1" type="SUBMISSION" />
        </element>
        <element id="d01" type="webtext" proc="webtext_filter_and_add"
            hideOnLoad="true" />
        <element id="d03" type="webtext" proc="webtext_dep62">
            <related id="d01"/>
            <related id="d02"/>
        </element>
        <element id ="d02" type="grid" proc="grid_cities_data"
        hideOnLoad="true">
            <proc id="d201" name="grid_cities_metadata" type="METADATA"/>
        </element>
    </tab>
    <tab id="04" name="2 fast grid proc">
        <element id ="0401" type="grid" proc="grid_cities_data">
            <proc id="040101" name="grid_cities_metadata" type="METADATA"/>
        </element>
        <element id="0402" type="grid" proc="grid_cities_one"/>
    </tab>
    <tab id="02" name="SP call: tags in add_context">
        <element id="0200" type="webtext" proc="webtext_buttons_uco"/>
        <element id="0201" type="webtext" proc="webtext_call_sp"/>
        <element id="0202" type="webtext" proc="webtext_show_debug_console"
        transform="xml_in_html.xsl" hideOnLoad="true"/>
    </tab>
</datapanel>
    '''

if __name__ == "__main__":
    mainproc()
