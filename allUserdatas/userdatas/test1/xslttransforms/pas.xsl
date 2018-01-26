<?xml version="1.0" encoding="UTF-8"?>
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
