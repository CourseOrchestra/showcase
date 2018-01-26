USE master
GO

SET NOCOUNT ON
DECLARE @DBName varchar(50)
DECLARE @spidstr varchar(8000)
SET @spidstr = ''
SET @DBName = 'showcase'

SELECT @spidstr=coalesce(@spidstr,',' )+'kill '+convert(varchar, spid)+ '; '
FROM master..sysprocesses WHERE dbid=db_id(@DBName)
 
IF LEN(@spidstr) > 0
EXEC(@spidstr)

GO

IF EXISTS(SELECT * FROM SYS.DATABASES WHERE [name]='showcase')
DROP DATABASE [showcase]

GO

CREATE DATABASE [showcase]
COLLATE Cyrillic_General_CI_AS
GO

ALTER DATABASE [showcase] SET RECOVERY SIMPLE 
GO

ALTER DATABASE [showcase] SET  MULTI_USER 
GO

ALTER DATABASE [showcase] SET AUTO_UPDATE_STATISTICS_ASYNC ON
GO
