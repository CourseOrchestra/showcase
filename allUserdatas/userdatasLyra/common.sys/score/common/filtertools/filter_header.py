#coding: utf-8
#from datetime import date, datetime, timedelta
from collections import OrderedDict

import re

from any_functions import is_exist
from filter import unbound_types, unbound_dict_filler
from common.filtertools.any_functions import Something


# Class chapter
class IncorrectHeaderInput(ValueError):
    def __init__(self, value):
        self.value = value

    def __str__(self):
        return repr(self.value)


class HeaderDict:
    u'''
    Класс, собирающий данные для хэдера из сторонних источников. Состоит из двух чатей -- 
    функции-конструктора, получающей метаданные, описывающие каждое выводящееся поле, и 
    функции return_header, которая на основе метаданных и переданных занчений, формирует
    хэдер. Описание:
        INIT::
    В переменной labels приходит либо словарь вида {'field_id': {'Значение':'Наименование'}}, либо 
    фильтр-контекст, к которому надо добавлять context_list=True.
    
    Обозначения для словаря RETURN, который содержит метаданные вывода:
    - 'data_type' -- тип данных поля;
    - 'empty' -- то, что выводится в хэдере, если не приходят данные;
    - 'label' -- надпись, выводимая нпосредственно перед значениями фильтра;
    - 'values_to_header' -- словарь значений, которые подменяют пришедшие из фильтра;
    - 'end' -- значение, добавляющееся в конце строки хэдера;
    - 'case_sensitive' -- если True, то не происходит текстовая обработка получаемых данных.
    '''
    data_types = {'date', 'float', 'text', 'bool'}
    smth = Something()
    
    def __init__(self, labels, header=u'', context_list=False, boolean_prefix=u''):
        # main processing
        # Переработка входных в label данных для создания фильтровых строк хэдера
        if not context_list:
            self.header_dict = OrderedDict([(i, self.preprocessor_any(j)) for i, j in unicoder(labels).items()])
        else:
            self.string_count = 0
            self.header_dict = OrderedDict([(val_dict['@id'], self.preprocessor_context(val_dict)) for val_dict in unicoder(labels)])
        self.header = header
        self.is_context = context_list
        self.boolean_prefix = boolean_prefix
        
    # Функция переработки вручную сработанного словаря
    def preprocessor_any(self, values_dict):
        must_have_settings = {'data_type', 'empty', 'label', 'values_to_header', 
                              'end', 'case_sensitive'}

        if 'data_type' not in values_dict.keys() or values_dict['data_type'] not in self.data_types:
            raise IncorrectHeaderInput("Incorrect input: not specified field data type.")
        result = {}
        for setting in must_have_settings:
            if setting in values_dict.keys():
                result[setting] = values_dict[setting]
            elif setting == 'end':
                result[setting] = '; '
            elif setting == 'case_sensitive':
                result[setting] = False
            else:
                result[setting] = ''
        # postprocessing
        if is_exist(values_dict, u'newline'):
            result['newline'] = True
        return result
    
    # Функция переработки контекста из фильтра
    def preprocessor_context(self, context_field):
        result = {}
        result['data_type'] = context_field['@type']
        result['empty'] = u''
        result['label'] = unicode(context_field['@label'])   
        self.string_count += len(context_field['@label'])
        if context_field['@face'] == 'select':
            result['values_to_header'] = {x['@name'] : x['@label'] for x in context_field['selects']["select"]}
        else:
            result['values_to_header'] = {}
        result['end'] = '.'
        #result['case_sensitive'] = '.'
        if self.string_count > 100 or context_field['@face'] == 'itemset':
            result['newline'] = True
            self.string_count = 0
            
        return result
    
    def replace_header(self, new_header):
        self.header = new_header
    
    def return_header(self, current_values, context_filter=False):
        u'''current_values = {id_field: value} for non-standard filtering and
            {id_field: {data_types : value, ...}} for STANDARTEN'''
        # New view
        # Приведение всех значений в поле, которое обрабатывается в соответствие с типом
        if context_filter is False:
            standard_header_dict = through_filler(current_values, self.header_dict)      
        else:
            # Переработка полученных значений в текст в поле text, вне зависиммости от способа отображения
            for y in current_values.values():
                if is_exist(y, 'item', {'@id': self.smth}):
                    dash_date = re.compile(r"[0-9]{4}-[0-9]{2}-[0-9]{2}")
                    dot_date = re.compile(r"[0-9]{2}.[0-9]{2}.[0-9]{4}")
                    dash_date_string = re.findall(dash_date, unicode(y['item']['@name']))
                    dot_date_string = re.findall(dot_date, unicode(y['item']['@name']))
                    if dash_date_string:
                        y['text'] = dash_date_string[0]
                    elif dot_date_string:
                        y['text'] = dot_date_string[0]
                    else:
                        y['text'] = y['item']['@name']
                elif is_exist(y, 'items'):
                    if '@name' not in y['items']:
                        y['text'] = u'; '.join([x['@name'] for x in y['items']])
                    else:
                        y['text'] = y['items']['@name']
            standard_header_dict = current_values
            
        header_list = [{"@class": 'header-class', "span":
            {"@class": 'header-header', '#text': self.header}}] if self.header else []
        i = 0
        next_upper = False
        # Формирование списка с подстановкой значений, либо emtpy-вариантом
        for key, values_dict in self.header_dict.items():
            # Получение из пришедшего словаря данных
            current_value = get_value_through_type(
                values_dict['data_type'], 
                standard_header_dict[key] if key in standard_header_dict.keys() else unbound_dict_filler(['']))
            # Переменные для стилей-словарей
            h_key = ''
            h_cond = ''
            h_value = ''
            #print(current_value, values_dict['data_type'])
            if current_value in ('', None):
                h_value = values_dict['empty']
            else:
                # Если получаемые значения должны подставляться с использованием алиасов, то заменяем значения
                if is_exist(values_dict, 'values_to_header', self.smth):
                    format_string = values_dict['values_to_header'][current_value]
                else:
                    format_string = current_value
                # Проверка на полученные значения -- одно или много
                if values_dict['data_type'] in {'date', 'float'}:
                    # Задаем текстовые шаблоны для интервальных значений
                    first_chapter = u'c %s'
                    second_chapter = u'по %s'
                    h_key = values_dict['label']
                    # Формируем окончательный вариант
                    if is_exist(standard_header_dict[key], 'condition', {'@value': 'equal'}):
                        h_cond = standard_header_dict[key]['condition']['@label']
                        h_value = format_string
                    elif is_exist(standard_header_dict[key], 'condition', {'@value': 'right'}):
                        h_value = second_chapter % format_string
                    elif is_exist(standard_header_dict[key], 'condition', {'@value': 'left'}):
                        h_value = first_chapter % format_string
                    else:
                        h_value = u'%s%s%s' % ((first_chapter  % format_string[0]) if format_string[0] != '' else '',
                                               ' ' if '' not in format_string else '',
                                               (second_chapter % format_string[1]) if format_string[1] != '' else '')
                else:
                    if values_dict['data_type'] == 'bool':
                        h_key = (u'%s%s.' % (self.boolean_prefix, values_dict['label'].rstrip()))\
                            if format_string in {True, 'true', 'True'} else values_dict['empty']
                    else:
                        h_key = values_dict['label'] if values_dict['label'] else ''
                        if self.is_context:
                            h_cond = standard_header_dict[key]['condition']['@label']\
                                if '@label' in standard_header_dict[key]['condition'] else u''
                        h_value = format_string
            # Добавляем переход на новую строку        
#             if is_exist(values_dict, 'newline'):
#                 h_value = unicode(h_value) + u'\n'
#                 next_upper = True
                
            if h_value != values_dict['empty']:
                h_value = unicode(h_value) + values_dict['end']
                first_string = h_key or h_value or u'' 
                # Штука для того, чтобы следующий фильтр шёл с большой буквы
                if next_upper and not is_exist(values_dict, 'case_sensitive', True) and first_string and first_string[0].islower():
                    first_string = u' '.join([first_string.split()[0].capitalize(), u' '.join(first_string.split()[1:])])\
                                if len(first_string.split()) > 1 else first_string.capitalize()
                if first_string:
                    if h_key:
                        h_key = first_string
                    elif h_value:
                        h_value = first_string     
                    next_upper = False
            if '.' in values_dict['end']:
                next_upper = True
            i += 1
            
            header_key = {"@class": "header-key", "#text": h_key + u' '}
            header_condition = {"@class": "header-condition", "#text": (h_cond + (u': ' if ';' in h_value else u' ')) if h_cond else ''}
            header_value = {"@class": "header-value", "#text": h_value + u' '}
            header_list.append({
                "@class": "header-clause",
                "span": []})
            if h_key.strip() not in {'.', ''} and (values_dict['data_type'] == 'bool' or (values_dict['data_type'] != 'bool' and h_value)):
                header_list[-1]['span'].append(header_key)
            if h_cond.strip() not in {'.', ''}:
                header_list[-1]['span'].append(header_condition)
            if h_value.strip() not in {'.', ''}:
                header_list[-1]['span'].append(header_value)
        
        header_list = filter(lambda x: x['span'] != [], header_list)
        if header_list:
            if isinstance(header_list[0]['span'], dict):
                header_list[0]['span'] = [header_list[0]['span']]
            first_string = header_list[0]['span'][0]['#text']
            if not first_string[0].isupper():
                first_string = u' '.join([first_string.split()[0].capitalize(), u' '.join(first_string.split()[1:])])\
                    if len(first_string.split()) > 1 else first_string.capitalize()
                header_list[0]['span'][0]['#text'] = first_string
        if len(header_list) == int(bool(self.header)):
            if len(header_list) == 1:
                header_list[0]['span'][0]["#text"] += u' нет.'
            else:
                header_list.append({
                    "@class": "header-clause",
                    "#text": u'Не заданы параметры поиска'})
        return {'span': header_list}
    

# Filler-functions
def header_type_to_filter_type():
    return {
    #data_type: position in unbound_dict_filler
        'date' : {'from': 'minValue', 'to': 'maxValue'},
        'float': {'from': 'minValue', 'to': 'maxValue'},
        'text' : 'text',
        'bool' : 'bool'
    }


# Function chapter
def get_value_through_type(value_type, value_dict):
    u'''Получить корректные значения // сформировать их'''
    format_dict = header_type_to_filter_type()
    fast_trancate = lambda x: '.'.join(x.split('-')[::-1]) if isinstance(x, (str, unicode)) else x
    if value_type not in {'date', 'float'}:
        return value_dict[format_dict[value_type]]
    else:
        interval = [fast_trancate(x) for x in
            [value_dict[format_dict[value_type]['from']], value_dict[format_dict[value_type]['to']]]]
        equal_value = fast_trancate(value_dict[format_dict['text']])

        smth = Something()
        #print(interval, equal_value, 'gvtt', smth in interval)
        if not is_exist(value_dict['condition'], '@value'):
            value_dict['condition'] = {}
            if smth in interval:
                value_dict['condition']['@value'] = 'between'
                value_dict['condition']['@label'] = u''
            else:
                value_dict['condition']['@value'] = 'equal'
                value_dict['condition']['@label'] = u''

        return interval if value_dict['condition']['@value'] == 'between' else equal_value


def through_filler(values_dict, types_dict):
    u'''Для вручную заданных данных для фильтра, функция получения словарей, идентичных возвращаемым из фильтра'''
    type_to_value = header_type_to_filter_type()
    types = unbound_types()
    type_to_lst_id = {} # Лежит "тип данных: позиция в приходящем списке"
    for key, j in type_to_value.items():
        if isinstance(j, (dict, OrderedDict)):
            type_to_lst_id[key] = {x: list_find(types, y) for x, y in j.items()}
        else:
            type_to_lst_id[key] = list_find(types, j)
    output = {}
    # {'punkt': False, 'filter_date': {'to': '2016-09-14', 'from': '2016-01-01'}, 'posta': 783, 'dichotomy': 'stool'}
    for i_key, i_value in values_dict.items():
        if not isinstance(type_to_lst_id[types_dict[i_key]['data_type']], (dict, OrderedDict)): # Если приходит не число/дата
            unbound_string = [''] * type_to_lst_id[types_dict[i_key]['data_type']]
            unbound_string.append(i_value)
        else:
            if isinstance(i_value, dict):
                from_dt = type_to_lst_id[types_dict[i_key]['data_type']]['from']
                to_dt = type_to_lst_id[types_dict[i_key]['data_type']]['to']
                min_dt, max_dt = (from_dt, to_dt) if from_dt < to_dt else (to_dt, from_dt)
                dt_value = {
                    from_dt: i_value['from'],
                    to_dt: i_value['to']
                }
                unbound_string = [''] * min_dt
                unbound_string.append(dt_value[min_dt])
                unbound_string.extend([''] * (max_dt-min_dt-1))
                unbound_string.append(dt_value[max_dt])
            elif i_value != '':
                unbound_string = []
                unbound_string.append(i_value)
            else:
                unbound_string = ['']
        output[i_key] = unbound_dict_filler(unbound_string)
    return output


def list_find(find_list, find_str):
    u'''Функция, которая ищет элемент в не отсортированном списке'''
    if not isinstance(find_list, list):
        return -1
    for i, elem in enumerate(find_list):
        if elem == find_str:
            return i
    return -1


def unicoder(outer_elem):
    u'''Функция, переводящая из строк в unicode всё содержимое объекта'''
    if isinstance(outer_elem, (str, unicode)):
        return unicode(outer_elem)
    if isinstance(outer_elem, (float, int)):
        return outer_elem
    if isinstance(outer_elem, (list, tuple)):
        return [unicoder(x) for x in outer_elem]
    if isinstance(outer_elem, OrderedDict):
        return OrderedDict([(unicoder(x), unicoder(y)) for x, y in outer_elem.items()])
    if isinstance(outer_elem, dict):
        return {unicoder(x):unicoder(y) for x, y in outer_elem.items()}
    return unicode(outer_elem)
