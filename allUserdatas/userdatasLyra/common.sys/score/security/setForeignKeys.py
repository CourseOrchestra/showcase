# coding: utf-8

from ru.curs.celesta.score import ForeignKey, FKRule, IntegerColumn
from security.functions import Settings


def setForeignKeys(context):
    u"""функция устанавливает внешний ключ в таблицу subjects и меняет значение параметра isSystemInitialised на True
        Функция работает с метаданными челесты.
    """ 
    settings=Settings()
    celesta = context.getCelesta() #получаем объект celesta  
    score = celesta.getScore() #получаем объект score
    security_grain = score.getGrain('security') #получаем объект гранулы security
    subject_table=security_grain.getTable("subjects") #получаем объект таблицы subjects
    login_table=security_grain.getTable("logins") #получаем объект таблицы logins
    if settings.isUseAuthServer():
        # Если пользователи приходят из mellophone, поле password в таблице logins становится nullable.
        password_field = login_table.getColumn("password") #получаем объект поля password таблицы logins
        password_field.setNullableAndDefault(settings.isUseAuthServer(), None) #Разрешаем значения null для него
    if settings.isEmployees():
        employees_grain=score.getGrain(settings.getEmployeesParam("employeesGrain")) #получаем объект гранулы, которая содержит таблицу сотрудников
        employees_table=employees_grain.getTable(settings.getEmployeesParam("employeesTable")) #получаем объект таблицы сотрудников
        employees_id = employees_table.getColumn(settings.getEmployeesParam("employeesId")) #получаем объект колонки первичного ключа таблицы сотрудников
        if isinstance(employees_id, IntegerColumn):
            # если первичный ключ таблицы сотрудников целочисленный, удаляем колонку "employeeId" таблицы subjects 
            # и создаём новую целочисленного типа.
            # сделано так потому что по умолчанию поле "employeeId" таблицы subjects текстовое.
            subject_table.getColumn("employeeId").delete()
            IntegerColumn(subject_table, "employeeId")
        else:
            # если первичный ключ таблицы сотрудников текстовый, устанавливаем длину поля "employeeId" таблицы subjects
            # в соответствии с ним
            employees_id_length = employees_id.getLength()
            subject_table.getColumn("employeeId").setLength(unicode(employees_id_length))
        
        subjects_keys = subject_table.getForeignKeys() #получаем список объектов-внешних ключей таблицы subjects
        for subjects_key in subjects_keys:
            #удаляем все внешние ключи таблицы subjects
            subjects_key.delete()
        subjects_key = ForeignKey(subject_table, employees_table, ["employeeId"]) #создаём внешний ключ subjects(employeeId)-<employees>(<employeeId>)
        
    logins_keys = login_table.getForeignKeys() #получаем список объектов-внешних ключей таблицы logins
    for logins_key in logins_keys:
        #удаляем все внешние ключи таблицы logins
        logins_key.delete()                                
    logins_key = ForeignKey(login_table, subject_table, ["subjectId"]) #создаём внешний ключ logins(subjectId)-subjects(sid)
    if settings.loginIsSubject(): #устанавливаем правила удаления 
        # Если logins тождественны subjects:
        logins_key.setDeleteRule(FKRule.CASCADE) # каскадное удаление на ключ logins-subjecs
        if settings.isEmployees():
            subjects_key.setDeleteRule(FKRule.SET_NULL) # установка null на ключ subjects-<employees>
    else:
        logins_key.setDeleteRule(FKRule.SET_NULL) # установка null на ключ logins-subjecs
    score.save() #сохраняем изменения
    celesta.reInitialize() # реинициализируем челесту
    settings.setEmployeesParam("isSystemInitialised", "true") #меняем значение параметра, чтобы в следующий раз ключи уже не устанавливались.
