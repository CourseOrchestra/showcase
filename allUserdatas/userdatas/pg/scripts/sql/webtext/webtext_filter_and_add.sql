DECLARE
 filter TEXT;
BEGIN

 filter := filterinfo;
 IF filter IS NULL THEN
  filter := '';
 END IF; 
 
 data := XMLPARSE (CONTENT '<div><h1>Add context='||add_context||'</h1><h1>Filter='||filter||'</h1></div>');
-- settings := '<properties></properties>';

END;
