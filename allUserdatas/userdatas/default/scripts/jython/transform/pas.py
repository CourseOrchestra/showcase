# coding: utf-8
'''
Created on 02.11.2011

@author: den
'''
from ru.curs.showcase.core.jython import JythonProc

# init vars
main = None
add = None
session = None
filterContext = None
elementId = None


class pas(JythonProc):
    def getRawData(self, context, element):
        global main, add, session, filterContext, elementId
        main = context.getMain()
        if context.getAdditional():
            add = context.getAdditional()
        session = context.getSession()
        if context.getFilter():
            filterContext = context.getFilter()
        elementId = element
        return mainproc()


def mainproc():
    return u'''
<!DOCTYPE xsl:stylesheet [ <!ENTITY nbsp " "> ]>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
<xsl:output method="html"/>
    <xsl:template match="/">
        <div>
            <h2 align="center">Паспорт региона</h2>
            <h3 align="center">Общие сведения</h3>
            <div>
                <span>
                    <xsl:value-of select="concat('Название региона - ', /root/name)"/>
                </span>
            </div>
            <div>
                <span style="position:relative;vertical-align:40px;"> Флаг - &nbsp;&nbsp;&nbsp;&nbsp;</span>
                <img src="solutions/default/resources/webtext/Flag_of_Russia.png" alt="" style="border: 1px solid black;"/>
            </div>
            <div>
                <span style="position:relative;vertical-align:50px;"> Герб -
                    &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span>
                <img src="solutions/default/resources/webtext/Gerb_of_Russia.png" alt="" style=""/>
            </div>
            <div>
                <span> <xsl:value-of select="concat('Площадь - ', /root/count)"/> <sup>2</sup> </span>
            </div>
            <div>
                <span> Население - 111 тыс чел</span>
            </div>
        </div>
    </xsl:template>
</xsl:stylesheet>
    '''

if __name__ == "__main__":
    mainproc()
