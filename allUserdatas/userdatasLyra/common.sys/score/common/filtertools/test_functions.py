#coding: utf-8
from collections import OrderedDict
from filter_header import HeaderDict


def fill_example():
    # Старое создание хэдер-инстанса
    return HeaderDict(OrderedDict([
        ('dichotomy', {'data_type': 'text', 'label': u'есть два', 'values_to_header': {
            'stool': u'стула',  'tree': u'дерева'
            }, 'empty': ''}),
        ('punkt', {'data_type': 'bool', 'label': '', 'values_to_header': {
            True: u'пункт выполнен',  False: u'пункт не выполнен',
            }, 'empty': '\n', 'end': '.'}),
        ('posta', {'data_type': 'float', 'label': u'постановлений:', 'end': '.'}),
        ('filter_date', {'data_type': 'date'})
    ]))


def fill_context(view, field_name, table_name, label, type, bound, face, minValue, maxValue, value,
                 boolInput, itemset, select, item, default, selector_data, current_condition, req,
                 conds):
    # Просто заполняем фильтровый словарь
    future_filter = {
        '@key': view, '@id': field_name, '@tableName': table_name, '@label': label, '@type': type,
        '@bound': bound, '@face': face, '@minValue': minValue, '@maxValue': maxValue, '@value': value,
        '@boolInput': boolInput, 'items': {"item": itemset}, 'selects': {'select': select}, 'item': item,
        'default': default, '@selector_data': selector_data, '@current_condition': current_condition,
        '@required': req, 'conditions': conds
    }
    return future_filter


def get_data_from_file():
    # Выгрузка тестовых данных из data.txt для курсорчика
    decrypt = {'field_name': '-', 'data_type': '#', 'value': '='}
    file_path = 'data.txt'
    data_file = open(file_path, 'rU')
    data = []
    for line in data_file.readlines():
        line = line.decode('cp1251')
        data.append(line[1:].rstrip('\n'))

    resulted = {}
    decrypt = {'INT': lambda y: int(y) if y != '' else None,
               'VARCHAR': lambda y: unicode(y),
               'DATETIME': lambda y: str(y),
               'BOOLEAN': lambda y: x == '1' if y != '' else None}
    for i, name in enumerate(data[0].split(';')):
        resulted[name] = {}
        resulted[name]['data_type'] = data[1].split(';')[i]
        resulted[name]['data'] = [
            decrypt[resulted[name]['data_type']](
                parse(x, i)
            ) for x in data[2:]
            ]
    data_file.close()
    return resulted


def parse(string, index):
    # Выделено в функцию для возможности улучшить парсинг
    parsed_string = string.split(';')
    return parsed_string[index]


def fill_filter(data_set, example_cursor, condition_constructor, bound, face, cond_lambda, select=list()):
    checked_context = []
    for checked_field in data_set:
        data_type = example_cursor.get_type_by_name(checked_field['name'])

        checked_context.append(fill_context(
                'view', checked_field['name'], example_cursor.grain_path, checked_field['label'], data_type,
                bound,
                face,
                '', '', '', 'false', itemset=[],
                select=[{'@label': label, '@name': name}for name, label in checked_field['select'].items()
                    ] if 'select' in checked_field.keys() else [],
                item={'@id': '', '@name': ''},
                default='',
                selector_data='',
                current_condition=cond_lambda(data_type),
                req='false', conds=condition_constructor('', False, face, data_type),
            )
        )
    return checked_context