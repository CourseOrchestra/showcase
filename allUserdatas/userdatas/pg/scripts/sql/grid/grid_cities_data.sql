DECLARE
 ref refcursor;
 sql TEXT;
 orderby TEXT;
BEGIN

 IF (sortcols is null) OR (sortcols = '') THEN
  orderby := 'ORDER BY Name';
 ELSE
  orderby := sortcols;
 END IF;

-- !!!!!!!!!  Столбец "~~properties" должен быть типа TEXT (не XML)

sql = 
'
 SELECT  
            _Id as "Код", 
            Name as "Название", 
            (''<properties>
                    <event name="row_single_click">
                        <action>
                            <main_context>current</main_context>
                            <datapanel type="current" tab="current">
                                <element id="d1"> 
                                <add_context>''||Name||''</add_context>  
                                </element>
                                <element id="d2">
                                <add_context>''||Name||''</add_context>  
                                </element>                                                              
                                <element id="d3">
                                <add_context>''||Name||''</add_context>  
                                </element>                                 
                                <element id="d4">
                                <add_context>''||Name||''</add_context>  
                                </element>                                 
                                <element id="d5">
                                <add_context>''||Name||''</add_context>  
                                </element>                                 
                                <element id="d6">
                                <add_context>''||Name||''</add_context>  
                                </element>                                 
                                <element id="d7">
                                <add_context>''||Name||''</add_context>  
                                </element>                                 
                                <element id="d8">
                                <add_context>''||Name||''</add_context>  
                                </element>                                 
                                <element id="d9">
                                <add_context>''||Name||''</add_context>  
                                </element>                                                                
                                <element id="d10"> 
                                <add_context>''||Name||''</add_context>  
                                </element>                                 
                            </datapanel>
                        </action>
                    </event>                                         
            </properties>'')::text as "~~properties"           
 FROM geo3 
-- WHERE lower(Journal_47_Name) LIKE lower(curvalue) 
 '||orderby||'
 OFFSET '||firstrecord-1||'
 LIMIT '||pagesize||'
 ;
'; 


 OPEN ref FOR 
 EXECUTE sql;
      
 RETURN ref;

END;
