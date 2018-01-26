DECLARE
  data2 xml;
BEGIN

	data2 := data; 
	
	update XFormsTest set data = data2;
	error_mes  := '';
    error_code := 0;

END;
