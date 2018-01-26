IF (OBJECT_ID('[dbo].[%1$s]') IS NOT NULL AND (OBJECTPROPERTY(OBJECT_ID('[dbo].[%1$s]'),'IsProcedure')=1))
 SELECT 1 AS [num] ELSE SELECT 0 AS [num]