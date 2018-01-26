DECLARE
 navigator xml;
BEGIN

    navigator := (generationtree(null)).navigator;
    file := navigator;
    filename := 'navigator.xml';
    
	error_mes  := '';
    error_code := 0;

END;
