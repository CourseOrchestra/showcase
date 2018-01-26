<?xml version="1.0" encoding="UTF-8"?>
            <xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                
                xmlns:fsxsl="http://www.curs.ru/fsxsl" 
                xmlns:exsl="http://exslt.org/common"
                extension-element-prefixes="exsl"                
                
                version="1.0">
                
                
                <xsl:variable name="testext1">
                    <fsxsl:xmldocument guid="bd0cf089-18f5-4c01-88d2-fa01ae61fced"/>
                    <!--        <root>
                        <justatag>Получилось</justatag>
                        </root>-->
                </xsl:variable>                
                                 
                <xsl:template match="/">
                    <html>
                        <head>
                            <meta lang="RU"/>
                        </head>
                        <body>
                            
                            <p>Проверка из внешнего файла (должно быть Получилось): <xsl:value-of select="exsl:node-set($testext1)/root/justatag/text()"/></p>                            
                            
                            <table cellpadding="3" cellspacing="1" border="0"
                                style="width: 950px; height: 20px;">
                                <tr>
                                    <td rowspan="1" style="background-color: #FFFFFF; text-align: right;"
                                        >Таблица 2</td>
                                </tr>
                                <tr>
                                    <td rowspan="2" style="background-color: #FFFFFF; text-align: center;"
                                        >Сведения о достижении значений целевых индикаторов проекта за текущий
                                        период</td>
                                </tr>
                            </table>
                            <br/>
                            <table cellpadding="3" cellspacing="1" border="0"
                                style="width: 950px; height: 20px;">
                                <tr>
                                    <td rowspan="2"
                                        style="background-color: #5A8BC3; color: #FFFFFF; text-align: center;">№
                                        п/п</td>
                                    <td rowspan="2"
                                        style="background-color: #5A8BC3; color: #FFFFFF; text-align: center;"
                                        >Наименование индикатора, ед. измерения</td>
                                    <td colspan="2"
                                        style="background-color: #5A8BC3; color: #FFFFFF; text-align: center;"
                                        >Значения индикаторов на текущий год</td>
                                </tr>
                                <tr>
                                    <td colspan="1"
                                        style="background-color: #5A8BC3; color: #FFFFFF; text-align: center;"
                                        >план на текущий год</td>
                                    <td colspan="1"
                                        style="background-color: #5A8BC3; color: #FFFFFF; text-align: center;"
                                        >ожидаемое значение <br/> на конец текущего года</td>
                                </tr>
                                <tr>
                                    <td style="background-color: #5A8BC3; color: #FFFFFF; text-align: center; ">
                                        1 </td>
                                    <td style="background-color: #5A8BC3; color: #FFFFFF; text-align: center;">
                                        2 </td>
                                    <td style="background-color: #5A8BC3; color: #FFFFFF; text-align: center;">
                                        3 </td>
                                    <td style="background-color: #5A8BC3; color: #FFFFFF; text-align: center;">
                                        4 </td>
                                </tr>
                                <tr>
                                    <td
                                        style="text-align: center; background-color: white; color: black; border-bottom: 1px solid #CCDDEE; border-right 1px solid #CCDDEE"
                                        >&#160;</td>
                                    <td colspan="3"
                                        style="text-align: center; background-color: white; color: black; border-bottom: 1px solid #CCDDEE; border-left: 1px solid #CCDDEE"
                                        >I. Индикаторы проекта</td>
                                </tr>
                                <xsl:for-each
                                    select="documentset/document/documentset/document/Schema[Info/Num_table='2.1']">
                                    <tr>
                                        <td
                                            style="text-align: center; background-color: white; color: black; border-bottom: 1px solid #CCDDEE"
                                            >&#160;</td>
                                        <td colspan="3"
                                            style="text-align: center; background-color: white; color: black; border-bottom: 1px solid #CCDDEE; border-left: 1px solid #CCDDEE">
                                            <xsl:value-of select="concat(Info/Objective/text(),'&#160;')"/>
                                            [<a href="{concat('?showform=', ../@docid )}" target="_blank" style="color: #2C5BA1;">редактировать</a>]
                                        </td>
                                    </tr>
                                    <xsl:for-each select="Objective/Indicator">
                                        <tr>
                                            <td
                                                style="text-align: center; background-color: white; color: black; border-bottom: 1px solid #CCDDEE">
                                                <xsl:value-of
                                                    select="concat('1','.',count(../../../preceding-sibling::document/Schema/Objective/Indicator)+count(../../preceding-sibling::Schema/Objective/Indicator)+position())"/>
                                                
                                            </td>
                                            <td
                                                style="text-align: center; background-color: white; color: black; border-bottom: 1px solid #CCDDEE; border-left: 1px solid #CCDDEE">
                                                <xsl:choose>
                                                    <xsl:when test="./@Name=''">
                                                        <xsl:value-of select="'&#160;'"/>
                                                    </xsl:when>
                                                    <xsl:otherwise>
                                                        <xsl:value-of select="./@Name"/>
                                                    </xsl:otherwise>
                                                </xsl:choose>
                                            </td>
                                            <td
                                                style="text-align: left; background-color: white; color: black; border-bottom: 1px solid #CCDDEE; border-left: 1px solid #CCDDEE">
                                                <xsl:choose>
                                                    <xsl:when test="./@Pok8=''">
                                                        <xsl:value-of select="'&#160;'"/>
                                                    </xsl:when>
                                                    <xsl:otherwise>
                                                        <xsl:value-of select="./@Pok8"/>
                                                    </xsl:otherwise>
                                                </xsl:choose>
                                            </td>
                                            <td
                                                style="text-align: left; background-color: white; color: black; border-bottom: 1px solid #CCDDEE; border-left: 1px solid #CCDDEE">
                                                <xsl:choose>
                                                    <xsl:when test="./@Pok9=''">
                                                        <xsl:value-of select="'&#160;'"/>
                                                    </xsl:when>
                                                    <xsl:otherwise>
                                                        <xsl:value-of select="./@Pok9"/>
                                                    </xsl:otherwise>
                                                </xsl:choose>
                                            </td>
                                        </tr>
                                        <tr>
                                            <td
                                                style="text-align: center; background-color: white; color: black; border-bottom: 1px solid #CCDDEE"
                                                >&#160;</td>
                                            <td colspan="3"
                                                style="text-align: left; background-color: white; color: black; border-bottom: 1px solid #CCDDEE; border-left: 1px solid #CCDDEE">
                                                <xsl:choose>
                                                    <xsl:when test="./@Rem=''">
                                                        <xsl:value-of select="'&#160;'"/>
                                                    </xsl:when>
                                                    <xsl:otherwise>
                                                        <xsl:value-of select="./@Rem"/>
                                                    </xsl:otherwise>
                                                </xsl:choose>
                                            </td>
                                        </tr>
                                    </xsl:for-each>
                                </xsl:for-each>
                                <tr>
                                    <td
                                        style="text-align: center; background-color: white; color: black; border-bottom: 1px solid #CCDDEE; border-right 1px solid #CCDDEE"
                                        >&#160;</td>
                                    <td colspan="3"
                                        style="text-align: center; background-color: white; color: black; border-bottom: 1px solid #CCDDEE; border-left: 1px solid #CCDDEE"
                                        >II. Индикаторы задач проекта</td>
                                </tr>
                                <xsl:for-each
                                    select="documentset/document/PriorDirs/PriorDir[not(preceding-sibling::PriorDir/text()=text())]">
                                    <tr>
                                        <td
                                            style="text-align: center; background-color: white; color: black; border-bottom: 1px solid #CCDDEE; border-right 1px solid #CCDDEE"
                                            >&#160;</td>
                                        <td colspan="3"
                                            style="text-align: center; background-color: white; color: black; border-bottom: 1px solid #CCDDEE; border-left: 1px solid #CCDDEE">
                                            <xsl:value-of select="text()"/>
                                        </td>
                                    </tr>
                                    <xsl:variable name="Direction" select="text()"/>
                                    <xsl:for-each
                                        select="/documentset/document/documentset/document/Schema[Info/PriorDir=$Direction]">
                                        <tr>
                                            <td
                                                style="text-align: center; background-color: white; color: black; border-bottom: 1px solid #CCDDEE; border-right 1px solid #CCDDEE"
                                                >&#160;</td>
                                            <td colspan="3"
                                                style="text-align: center; background-color: white; color: black; border-bottom: 1px solid #CCDDEE; border-left: 1px solid #CCDDEE">
                                                <xsl:value-of select="Info/Task"/>
                                                [<a href="{concat('?showForm=', ../@docid )}" target="_blank" style="color: #2C5BA1;">редактировать</a>]
                                            </td>
                                        </tr>
                                        <xsl:for-each select="Task/Indicator">
                                            <tr>
                                                <td
                                                    style="text-align: center; background-color: white; color: black; border-bottom: 1px solid #CCDDEE">
                                                    <xsl:value-of
                                                        select="concat('2','.',count(../../../preceding-sibling::document/Schema/Task/Indicator)+count(../../preceding-sibling::Schema/Task/Indicator)+position())"/>
                                                    
                                                </td>
                                                <td
                                                    style="text-align: center; background-color: white; color: black; border-bottom: 1px solid #CCDDEE; border-left: 1px solid #CCDDEE">
                                                    <xsl:choose>
                                                        <xsl:when test="./@Name=''">
                                                            <xsl:value-of select="'&#160;'"/>
                                                        </xsl:when>
                                                        <xsl:otherwise>
                                                            <xsl:value-of select="./@Name"/>
                                                        </xsl:otherwise>
                                                    </xsl:choose>
                                                </td>
                                                <td
                                                    style="text-align: left; background-color: white; color: black; border-bottom: 1px solid #CCDDEE; border-left: 1px solid #CCDDEE">
                                                    <xsl:choose>
                                                        <xsl:when test="./@Pok8=''">
                                                            <xsl:value-of select="'&#160;'"/>
                                                        </xsl:when>
                                                        <xsl:otherwise>
                                                            <xsl:value-of select="./@Pok8"/>
                                                        </xsl:otherwise>
                                                    </xsl:choose>
                                                </td>
                                                <td
                                                    style="text-align: left; background-color: white; color: black; border-bottom: 1px solid #CCDDEE; border-left: 1px solid #CCDDEE">
                                                    <xsl:choose>
                                                        <xsl:when test="./@Pok9=''">
                                                            <xsl:value-of select="'&#160;'"/>
                                                        </xsl:when>
                                                        <xsl:otherwise>
                                                            <xsl:value-of select="./@Pok9"/>
                                                        </xsl:otherwise>
                                                    </xsl:choose>
                                                </td>
                                            </tr>
                                            <tr>
                                                <td
                                                    style="text-align: center; background-color: white; color: black; border-bottom: 1px solid #CCDDEE"
                                                    >&#160;</td>
                                                <td colspan="3"
                                                    style="text-align: left; background-color: white; color: black; border-bottom: 1px solid #CCDDEE; border-left: 1px solid #CCDDEE">
                                                    <xsl:choose>
                                                        <xsl:when test="./@Rem=''">
                                                            <xsl:value-of select="'&#160;'"/>
                                                        </xsl:when>
                                                        <xsl:otherwise>
                                                            <xsl:value-of select="./@Rem"/>
                                                        </xsl:otherwise>
                                                    </xsl:choose>
                                                </td>
                                            </tr>
                                            
                                        </xsl:for-each>
                                    </xsl:for-each>
                                </xsl:for-each>
                            </table>
                        </body>
                        
                                                
                    </html>
                </xsl:template>
            </xsl:stylesheet>
