set @data= (Select top 1 '199px' as width, 

(select top 1 '00' as id, 'Фичи' as name, 
	(Select top 1 '07' as id, '7-й этап' as name,
			(select top 1
				'main_context' as [main_context],
					(select top 1
								'07.xml' as [type],
								'firstOrCurrent' as [tab]								
					from [Websites] as [datapanel] For xml auto, type)
			from [Websites] as [action] For xml auto, type, elements)					
	from [Websites]	as [level1] For xml auto, type),
	
(Select top 1 '08' as id, '8-й этап' as name, 	
	(Select top 1 '0801' as id, '8-й этап, 1-я неделя' as name,
			(select top 1
					'Запасы на конец отчетного периода - Всего' as [main_context],
					(select top 1
								'0801.xml' as [type],
								'firstOrCurrent' as [tab]
					from [Websites] as [datapanel] For xml auto, type)
			from [Websites] as [action] For xml auto, type, elements)					
	from [Websites]	as [level2] For xml auto, type),
	
	(Select top 1 '0802' as id, '8-й этап, 2-я неделя' as name,
			(select top 1
					'Запасы на конец отчетного периода - Всего' as [main_context],
					(select top 1
								'0802.xml' as [type],
								'firstOrCurrent' as [tab]
					from [Websites] as [datapanel] For xml auto, type)
			from [Websites] as [action] For xml auto, type, elements)					
	from [Websites]	as [level2] For xml auto, type),			

	(Select top 1 '0803' as id, '8-й этап, 3-я неделя' as name, 
			(select top 1
					'Запасы на конец отчетного периода - Всего' as [main_context],
					(select top 1
								'0803.xml' as [type],
								'firstOrCurrent' as [tab]
					from [Websites] as [datapanel] For xml auto, type)
			from [Websites] as [action] For xml auto, type, elements)					
	from [Websites]	as [level2] For xml auto, type),
	
		(Select top 1 '0804' as id, '8-й этап, 4-я неделя' as name,
			(select top 1
					'Запасы на конец отчетного периода - Всего' as [main_context],
					(select top 1
								'0804.xml' as [type],
								'firstOrCurrent' as [tab]
					from [Websites] as [datapanel] For xml auto, type)
			from [Websites] as [action] For xml auto, type, elements)					
	from [Websites]	as [level2] For xml auto, type),
	
	(Select top 1 '0805' as id, '8-й этап, 5-я неделя' as name,
			(select top 1
					'Запасы на конец отчетного периода - Всего' as [main_context],
					(select top 1
								'0805.xml' as [type],
								'firstOrCurrent' as [tab]
					from [Websites] as [datapanel] For xml auto, type)
			from [Websites] as [action] For xml auto, type, elements)					
	from [Websites]	as [level2] For xml auto, type)			
	
						From geo3 [level1] For xml auto, type),
						
(Select top 1 '09' as id, '9-й этап' as name, 	

	(Select top 1 '0901' as id, '9-й этап, 1-я неделя' as name, 
			(select top 1
					'Запасы на конец отчетного периода - Всего' as [main_context],
					(select top 1
								'0901.xml' as [type],
								'firstOrCurrent' as [tab]
					from [Websites] as [datapanel] For xml auto, type)
			from [Websites] as [action] For xml auto, type, elements)					
	from [Websites]	as [level2] For xml auto, type),
	
	(Select top 1 '0902' as id, '9-й этап, 2-я неделя' as name, 
			(select top 1
					'Запасы на конец отчетного периода - Всего' as [main_context],
					(select top 1
								'0902.xml' as [type],
								'firstOrCurrent' as [tab]
					from [Websites] as [datapanel] For xml auto, type)
			from [Websites] as [action] For xml auto, type, elements)					
	from [Websites]	as [level2] For xml auto, type),

	(Select top 1 '0903' as id, '9-й этап, 3-5 недели' as name, 'false' as selectOnLoad, 
			(select top 1
					'Запасы на конец отчетного периода - Всего' as [main_context],
					(select top 1
								'dp0903' as [type],
								'firstOrCurrent' as [tab]
					from [Websites] as [datapanel] For xml auto, type)
			from [Websites] as [action] For xml auto, type, elements)					
	from [Websites]	as [level2] For xml auto, type),
	
	(Select top 1 '1001' as id, '10-й этап, 1 неделя' as name, 'true' as selectOnLoad, 
			(select top 1
					'Запасы на конец отчетного периода - Всего' as [main_context],
					(select top 1
								'1001.xml' as [type],
								'firstOrCurrent' as [tab]
					from [Websites] as [datapanel] For xml auto, type)
			from [Websites] as [action] For xml auto, type, elements)					
	from [Websites]	as [level2] For xml auto, type)			
		
From geo3 [level1] For xml auto, type)		
										
from [Websites] as [group] For xml auto, type),

					(Select top 1
					1 as id,
					'Балансы продовольственных ресурсов' as name, 
						(Select top 1
							3 as id,
							'Балансы зерна' as name,
							

							
							(Select 
								Journal_40_id as id,
								Journal_40_Name as [name],
								(select top 1
								Journal_40_Name as [main_context],
								(select top 1
								'a.xml' as [type],
								'firstOrCurrent' as [tab]								
								From geo5 as [datapanel]
								For xml auto, type)
								From geo3 as [action]
								For xml auto, type, elements)

							From Journal_40 as [level2]
							Where [level2]._Id in (2,3,6,7,8,9,10,12,14)
							For xml auto, type)
							
															
						
							
						From geo3 [level1]
						For xml auto, type)
					From geo3 [group]
					For xml auto, type),
					
					(Select top 1
					2 as id,
					'Регионы' as name,
					(Select 
						geo5_Id as id,
						Name as [name],
						(select top 1
							[level1].Name as [main_context],
							(select top 1
								'b.xml' as [type],
								(
									select
									_id as [id],
									(SELECT top 1 [element].Journal_40_Name as [add_context] 
									From Journal_40 as [fake] For xml AUTO, type, elements).query('/fake/add_context[1]')

								From Journal_40 as [element]
								Where [element]._Id in (2,3,6,7,8,9,10,12,14)
								For xml auto, type																
								)
							From geo3 as [datapanel]
							For xml auto, type)
						From geo3 as [action]
						For xml auto, type, elements) 
					From geo5 as [level1]
					Where NAME not like '%---%'
					For xml auto, type)
				From geo3 [group]
				For xml auto, type)
			From geo3 [navigator]
			For xml auto)