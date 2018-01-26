CREATE VIEW dbo.XFormsFilesView
AS
SELECT     TOP (100) PERCENT filename, filedata, data, UpdateRowTime, XFormsTest_Id
FROM         dbo.XFormsFilesTest
ORDER BY UpdateRowTime DESC
