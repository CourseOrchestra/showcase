# coding: utf-8
from collections import OrderedDict
from datetime import datetime


class Something:
    # Образец для сравнения

    def __eq__(self, other):
        return other not in ('', None, list(), dict(), tuple(), set())

    def __contains__(self, item):
        return len(item) != 0


def compare_dict(instance_dict, equaled_dict):
    keys_instance = set(instance_dict.keys())
    keys_eq = set(equaled_dict.keys())

    if len(keys_eq & keys_instance) != len(keys_eq):
        return False
    results = []
    for key, value in equaled_dict.items():
        if type(value) != type(instance_dict[key]) or not isinstance(instance_dict[key], dict):
            results.append(instance_dict[key] == value)
            break
        results.append(compare_dict(instance_dict[key], value))

    return False not in results


def is_exist(dictionary, key, *args):
    u'''Example:
    dictionary = {1 : {2 : 3}}
    True == is_exist(dictionary, 1, {2:3})
    Проверяет существование ключа в словаре и существование его содержимого
    Возвращает None, если передать не словарь, False, если ключа нет в словаре, либо значение этого ключа пустое, либо 
    значение не равно третьему переданному аргументу.'''
    if not isinstance(dictionary, dict):
        return None
    if key not in dictionary.keys():
        return False
#     # Проверка на соответствие типов
#     for keys in dictionary.keys():
#         if dictionary[keys] == dictionary[key] and type(keys) != type(key):
#             return False
    if not dictionary[key]:
        return False
    if dictionary[key] and len(args) == 0:
        return True
    # Сверяем занчения
    answers = [False] * len(args)
    for i, argument in enumerate(args):
        # В случае, если значение есть строка
        answers[i] = argument == dictionary[key]\
            if not isinstance(argument, dict) else compare_dict(dictionary[key], argument)

    return False not in answers


def compare_with_shadow(source, schrodinger_cat):
    if schrodinger_cat is None:
        return True
    if len(schrodinger_cat) == 0:
        return True
    return source == schrodinger_cat[0]


def into_dtime(DB_time):
    u'''return datetime
        Привод получаемой из постгре даты к юзер-дружелюбному виду'''
    if not DB_time:
        return ''
    try:
        return datetime.strftime(datetime.strptime(str(DB_time), '%Y-%m-%d %H:%M:%S.%f'), '%d.%m.%Y')
    except ValueError:
        return datetime.strftime(datetime.strptime(str(DB_time), '%Y-%m-%d'), '%d.%m.%Y')
