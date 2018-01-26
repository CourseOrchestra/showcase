-- SQL Manager 2011 for SQL Server 3.7.0.2
-- ---------------------------------------
-- Хост         : CASTLE\R2
-- База данных  : showcase
-- Версия       : Microsoft SQL Server  10.50.2500.0


SET NOCOUNT ON
GO

--
-- Definition for table DebugConsole : 
--

CREATE TABLE [dbo].[DebugConsole] (
  [_Id] int IDENTITY(1, 1) NOT NULL,
  [data] varchar(max) COLLATE Cyrillic_General_CI_AS NULL,
  [label] varchar(max) COLLATE Cyrillic_General_CI_AS NULL,
  [UpdateRowTime] datetime NULL
)
ON [PRIMARY]
GO

--
-- Definition for table UserMessagesTest : 
--

CREATE TABLE [dbo].[UserMessagesTest] (
  [Id] uniqueidentifier DEFAULT newid() ROWGUIDCOL NOT NULL,
  [test1] varchar(64) COLLATE Cyrillic_General_CI_AS NOT NULL,
  [test2] varchar(64) COLLATE Cyrillic_General_CI_AS NOT NULL,
  [test3] varchar(64) COLLATE Cyrillic_General_CI_AS NOT NULL
)
ON [PRIMARY]
GO

--
-- Definition for table Websites : 
--

CREATE TABLE [dbo].[Websites] (
  [Id] uniqueidentifier CONSTRAINT [Websites_Id_Def] DEFAULT newid() ROWGUIDCOL NOT NULL,
  [Name] varchar(64) COLLATE Cyrillic_General_CI_AS NOT NULL,
  [File1] varchar(255) COLLATE Cyrillic_General_CI_AS NULL,
  [Logo] varchar(255) COLLATE Cyrillic_General_CI_AS NULL,
  [File2] varchar(255) COLLATE Cyrillic_General_CI_AS NULL,
  [Url] varchar(255) COLLATE Cyrillic_General_CI_AS NOT NULL,
  [IsPortal] bit CONSTRAINT [Websites_IsPortal_Def] DEFAULT 0 NOT NULL
)
ON [PRIMARY]
GO

--
-- Definition for table XFormsFilesTest : 
--

CREATE TABLE [dbo].[XFormsFilesTest] (
  [XFormsTest_Id] uniqueidentifier CONSTRAINT [XFormsFilesTestD0] DEFAULT newid() ROWGUIDCOL NOT NULL,
  [filename] varchar(max) COLLATE Cyrillic_General_CI_AS NULL,
  [filedata] varbinary(max) NULL,
  [data] xml NULL,
  [UpdateRowTime] datetime NULL
)
ON [PRIMARY]
GO

--
-- Definition for table XFormsTest : 
--

CREATE TABLE [dbo].[XFormsTest] (
  [XFormsTest_Id] uniqueidentifier CONSTRAINT [XFormsTestD0] DEFAULT newid() ROWGUIDCOL NOT NULL,
  [data] xml NULL,
  [UpdateRowTime] datetime NULL,
  [datetime] datetime NULL,
  [date] date NULL
)
ON [PRIMARY]
GO

CREATE TABLE [dbo].[XFormsUnitTest] (
  [XFormsUnitTest_Id] uniqueidentifier CONSTRAINT [XFormsUnitTestD0] DEFAULT newid() ROWGUIDCOL NOT NULL,
  [data] xml NULL,
  [UpdateRowTime] datetime NULL,
  [datetime] datetime NULL,
  [date] date NULL
)
ON [PRIMARY]
GO

--
-- Data for table dbo.DebugConsole  (LIMIT 0,500)
--

SET IDENTITY_INSERT [dbo].[DebugConsole] ON
GO

INSERT INTO [dbo].[DebugConsole] ([_Id], [data], [label], [UpdateRowTime])
VALUES 
  (885, N'<time>19:35:03:183</time><add_context><int_context int_attr="attr_value">контекст<test>+ 1 тэг внутри</test></int_context></add_context><related_recid>related=2</related_recid>', NULL, '20120220 19:35:03.183')
GO

SET IDENTITY_INSERT [dbo].[DebugConsole] OFF
GO

--
-- Data for table dbo.UserMessagesTest  (LIMIT 0,500)
--

INSERT INTO [dbo].[UserMessagesTest] ([Id], [test1], [test2], [test3])
VALUES 
  (N'{238C67A5-95E2-4201-B318-14968128D9CA}', N'test1', N'test2', N'test3')
GO

--
-- Data for table dbo.Websites  (LIMIT 0,500)
--

INSERT INTO [dbo].[Websites] ([Id], [Name], [File1], [Logo], [File2], [Url], [IsPortal])
VALUES 
  (N'{7451DF70-ACC3-48CC-8CC0-3092F8A237BE}', N'<br>Яндекс</br> ведущий <br/>поисковик <br/>Рунета', N'<div style="text-align:center">Файл c навигатором в имени которого содержится GUID записи</div>', N'<link href="http://yandex.ru" image="${images.in.grid.dir}/imagesingrid/yandex.png" text="Яндекс" openInNewTab="true"/>', N'', N' <link href="http://yandex.ru/yandsearch?text=КУРС-ИТ&lr=213" text="''<"Яндекс">''" openInNewTab="true"/>', 1)
GO

INSERT INTO [dbo].[Websites] ([Id], [Name], [File1], [Logo], [File2], [Url], [IsPortal])
VALUES 
  (N'{8BC3D54A-AE03-4728-AFCD-54DC092B0823}', N'РБК', N'Файл12', N'<link href="http://rbc.ru" image="${images.in.grid.dir}/imagesingrid/rbc.gif" text="rbc.ru" openInNewTab="true"/>', N'Файл22', N'<link href="http://rbc.ru"  openInNewTab="true"/>', 1)
GO

INSERT INTO [dbo].[Websites] ([Id], [Name], [File1], [Logo], [File2], [Url], [IsPortal])
VALUES 
  (N'{77F60A7C-42EB-4E32-B23D-F179E58FB138}', N'Рамблер', N'Файл13', N'<link href="http://rambler.ru" image="${images.in.grid.dir}/imagesingrid/rambler.gif" text="rambler.ru" openInNewTab="true"/>', N'Файл23', N'<link href="http://rambler.ru" text="rambler.ru" openInNewTab="true"/>', 1)
GO

INSERT INTO [dbo].[Websites] ([Id], [Name], [File1], [Logo], [File2], [Url], [IsPortal])
VALUES 
  (N'{856ACCF2-53AB-4AF0-A956-F6E85601D0B4}', N'Mail.ru', N'Файл14', N'<link href="http://mail.ru" image="${images.in.grid.dir}/imagesingrid/mailru.gif" text="mail.ru" openInNewTab="true"/>', N'Файл24', N'<link href="http://mail.ru" text="mail.ru & Co" openInNewTab="true"/>', 1)
GO

--
-- Data for table dbo.XFormsTest  (LIMIT 0,500)
--

INSERT INTO [dbo].[XFormsTest] ([XFormsTest_Id], [data], [UpdateRowTime], [datetime], [date])
VALUES 
  (N'{4ACBB735-31CD-40B6-99C4-AD80D3199763}', N'<schema xmlns=""><info><name>Белгородская обл.</name><growth/><eyescolour/><music/><comment/></info></schema>', '20120220 19:34:15.087', NULL, N'2011-02-03')
GO

INSERT INTO [dbo].[XFormsUnitTest] ([XFormsUnitTest_Id], [data], [UpdateRowTime], [datetime], [date])
VALUES 
  (N'{4ACBB735-31CD-40B6-99C4-AD80D3199763}', N'<schema xmlns=""><info><name/><growth/><eyescolour/><music/><comment/></info></schema>', '20120220 19:34:15.087', NULL, N'2011-02-03')
GO

--
-- Definition for indices : 
--

ALTER TABLE [dbo].[DebugConsole]
ADD CONSTRAINT [PK_DebugConsole] 
PRIMARY KEY CLUSTERED ([_Id])
WITH (
  PAD_INDEX = OFF,
  IGNORE_DUP_KEY = OFF,
  STATISTICS_NORECOMPUTE = OFF,
  ALLOW_ROW_LOCKS = ON,
  ALLOW_PAGE_LOCKS = ON)
ON [PRIMARY]
GO

ALTER TABLE [dbo].[UserMessagesTest]
ADD CONSTRAINT [PK_UserMessagesTest] 
PRIMARY KEY CLUSTERED ([Id])
WITH (
  PAD_INDEX = OFF,
  IGNORE_DUP_KEY = OFF,
  STATISTICS_NORECOMPUTE = OFF,
  ALLOW_ROW_LOCKS = ON,
  ALLOW_PAGE_LOCKS = ON)
ON [PRIMARY]
GO

ALTER TABLE [dbo].[UserMessagesTest]
ADD CONSTRAINT [UserMessages__user_mes_test1_src__] 
UNIQUE NONCLUSTERED ([test1])
WITH (
  PAD_INDEX = OFF,
  IGNORE_DUP_KEY = OFF,
  STATISTICS_NORECOMPUTE = OFF,
  ALLOW_ROW_LOCKS = ON,
  ALLOW_PAGE_LOCKS = ON)
ON [PRIMARY]
GO

ALTER TABLE [dbo].[UserMessagesTest]
ADD CONSTRAINT [UserMessages__user_mes_test2_src__] 
UNIQUE NONCLUSTERED ([test2])
WITH (
  PAD_INDEX = OFF,
  IGNORE_DUP_KEY = OFF,
  STATISTICS_NORECOMPUTE = OFF,
  ALLOW_ROW_LOCKS = ON,
  ALLOW_PAGE_LOCKS = ON)
ON [PRIMARY]
GO

ALTER TABLE [dbo].[UserMessagesTest]
ADD CONSTRAINT [UserMessages__user_mes_test3_src__] 
UNIQUE NONCLUSTERED ([test3])
WITH (
  PAD_INDEX = OFF,
  IGNORE_DUP_KEY = OFF,
  STATISTICS_NORECOMPUTE = OFF,
  ALLOW_ROW_LOCKS = ON,
  ALLOW_PAGE_LOCKS = ON)
ON [PRIMARY]
GO

ALTER TABLE [dbo].[Websites]
ADD CONSTRAINT [PK_Websites] 
PRIMARY KEY CLUSTERED ([Id])
WITH (
  PAD_INDEX = OFF,
  IGNORE_DUP_KEY = OFF,
  STATISTICS_NORECOMPUTE = OFF,
  ALLOW_ROW_LOCKS = ON,
  ALLOW_PAGE_LOCKS = ON)
ON [PRIMARY]
GO

ALTER TABLE [dbo].[Websites]
ADD CONSTRAINT [WebsitesU1] 
UNIQUE NONCLUSTERED ([Name])
WITH (
  PAD_INDEX = OFF,
  IGNORE_DUP_KEY = OFF,
  STATISTICS_NORECOMPUTE = OFF,
  ALLOW_ROW_LOCKS = ON,
  ALLOW_PAGE_LOCKS = ON)
ON [PRIMARY]
GO

ALTER TABLE [dbo].[XFormsFilesTest]
ADD CONSTRAINT [PK_XFormsFilesTest] 
PRIMARY KEY CLUSTERED ([XFormsTest_Id])
WITH (
  PAD_INDEX = OFF,
  IGNORE_DUP_KEY = OFF,
  STATISTICS_NORECOMPUTE = OFF,
  ALLOW_ROW_LOCKS = ON,
  ALLOW_PAGE_LOCKS = ON)
ON [PRIMARY]
GO

ALTER TABLE [dbo].[XFormsTest]
ADD CONSTRAINT [PK_XFormsTest] 
PRIMARY KEY CLUSTERED ([XFormsTest_Id])
WITH (
  PAD_INDEX = OFF,
  IGNORE_DUP_KEY = OFF,
  STATISTICS_NORECOMPUTE = OFF,
  ALLOW_ROW_LOCKS = ON,
  ALLOW_PAGE_LOCKS = ON)
ON [PRIMARY]
GO

--
-- Definition for triggers : 
--
GO
SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
GO

CREATE TRIGGER [dbo].[DebugConsole_UpdateRowTime_AutoFill] ON [dbo].[DebugConsole]
WITH EXECUTE AS CALLER
FOR INSERT
AS
 SET NOCOUNT ON 

 
UPDATE [dbo].DebugConsole SET [UpdateRowTime]=  GETDATE()  
FROM [dbo].DebugConsole 
 
 
SET NOCOUNT OFF
GO

SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
GO

CREATE TRIGGER [dbo].[XFormsFilesTest_UpdateRowTime_AutoFill] ON [dbo].[XFormsFilesTest]
WITH EXECUTE AS CALLER
FOR INSERT
AS
 SET NOCOUNT ON 

 
UPDATE [dbo].[XFormsFilesTest] SET [UpdateRowTime]=  GETDATE()  
FROM [dbo].[XFormsFilesTest] WHERE [XFormsTest_Id] IN (SELECT [XFormsTest_Id] FROM [inserted])
GO

SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
GO

CREATE TRIGGER [dbo].[XFormsTest_UpdateRowTime_AutoFill] ON [dbo].[XFormsTest]
WITH EXECUTE AS CALLER
FOR UPDATE
AS
 SET NOCOUNT ON 

 
UPDATE [dbo].[XFormsTest] SET [UpdateRowTime]=  GETDATE()  
FROM [dbo].[XFormsTest] 
 
 
SET NOCOUNT OFF
GO

