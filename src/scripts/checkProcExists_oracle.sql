SELECT COUNT(*) AS NUM
  FROM USER_OBJECTS
 WHERE ((object_type = 'PROCEDURE') OR (object_type = 'FUNCTION'))
   AND (UPPER(object_name) = UPPER('%1$s'))
