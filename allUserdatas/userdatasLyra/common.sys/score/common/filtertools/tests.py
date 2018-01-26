#coding: utf-8

import unittest, random, os
# from filter import filter_assembly, condition_constructor
from collections import OrderedDict
# from any_functions import is_exist, Something
# from test_functions import fill_context, fill_example
# from test_classes import ExampleCursor
# from filter_header import HeaderDict


class Context:
    # Объект-словарь
    def __init__(self):
        self.inner_dict = {}

    def getData(self):
        return self.inner_dict


class FilterTest(unittest.TestCase):
    # setUp – подготовка прогона теста; вызывается перед каждым тестом.
    def setUp(self):
        print('Beginning of the unit testing')
        # Для подробных комментариев смотри по адресу: https://share.curs.ru/wiki/index.php/Common.filter
        # Данные для вставки в условную "карточку".ру
        self.instance_bound_xforms_data = [
          {'name': 'string', 'label': u'Строка'}
        , {'name': 'index', 'label': u'Индех'}
        , {'name': 'date', 'label': u'Дата'}
        , {'name': 'boolean', 'label': u'Булево поле'}
        , {'name': 'int', 'label': u'Инты'}
        # , {'name': 'float', 'label': u'Флоаты'}
        ]
        self.instance_ms_xforms_data = [
          {'name': 'string', 'label': u'Мультиселектор строк', 'itemset': True}
        ]
        self.instance_selector_xforms_data = [
          {'name': 'int', 'label': u'Селектор числа', 'selector': True}
        # , {'name': 'selector_float', 'label': u'Селектор флоата', 'selector': True}
        , {'name': 'date', 'label': u'Селектор даты', 'selector': True}
        , {'name': 'string', 'label': u'Селектор строк', 'selector': True}
        ]
        self.instance_select_xforms_data = [
          {'name': 'string', 'label': u'Селект строк', 'select': OrderedDict([
                ('first string', u'Первая строчечка'), ('second', u'Вторая')])}
        , {'name': 'int', 'label': u'Селект чисел', 'select': OrderedDict([
                (2, u'Первое число'), (1322, u'Второй чисёл')])},
        # , {'name': 'select', 'label': u'Селект дат', 'select': OrderedDict([
        #         ('le487', u'Первый парень'), ('le492', u'Второй парень')])},
        ]


    # tearDown – вызывается после того, как тест был запущен и результат записан. Метод запускается даже в случае
    # исключения (exception) в теле теста.
    def tearDown(self):
        print('This is the end')

    # Первая тестовая функция
    def test_any_function(self):
        from any_functions import is_exist, Something
        # Негативная проверка на корректность включённости ключа в словарь
        # Проверка на некорректные типы данных
        for i in [None, '1', 1, True, [], [1], {1}, (1, )]:
            self.assertEqual(is_exist(i, 1, None), None)
        # Логические значения
        self.assertEqual(is_exist({1: 2}, True, None), False)
        self.assertEqual(is_exist({0: 2}, False, None), False)
        self.assertEqual(is_exist({'': 2}, False, None), False)
        # Числа/строки
        self.assertEqual(is_exist({'1': ''}, 1), False)
        self.assertEqual(is_exist({'1': 2}, 1, None), False)
        self.assertEqual(is_exist({1: 2}, '1', None), False)
        self.assertEqual(is_exist({0: 2}, '', None), False)
        self.assertEqual(is_exist({0: 2}, 2, 2), False)
        # Проверка на корректность возвращённого значения при наличии значения
        self.assertEqual(is_exist({1: 2, '1': '2'}, 1, 2), True)
        self.assertEqual(is_exist({1: 2, '1': '2'}, 1, '2'), False)
        self.assertEqual(is_exist({1: 2, '1': '2'}, '1', '2'), True)
        self.assertEqual(is_exist({1: 2, '1': '2'}, 2, 1), False)
        # Тесты для путей в словарях
        smth = Something()
        self.assertEqual(is_exist({2: {2:3}, '1': '2'}, 2, {1:{2:smth}}), False)
        self.assertEqual(
            is_exist({'outer': {'inner1': {'inner2': {'default': ['we', 'have', 'it']}}
                                , 'other': True, 'othertwo': 123, 'another': {1:2, 'oi': 'ne'}}},
                     'outer',
                     {'inner1': {'inner2': {'default': smth}}}
            ), True)
        self.assertEqual(
            is_exist({'outer': {'inner1': {'inner2': {'default': ['we', 'have', 'it']}}
                                , 'other': True, 'othertwo': 123, 'another': {1:2, 'oi': 'ne'}}},
                     'outer',
                     {'inner1': {'inner2': smth}}, {'inor': smth}
            ), False)
        self.assertEqual(
            is_exist({'outer': {'inner1': {'inner2': {'default': ['we', 'have', 'it']}}
                                , 'other': True, 'othertwo': 123, 'another': {1:2, 'oi': 'ne'}}},
                     'outer',
                     {'inner1': {'inner2': smth}}, {'inor'}
            ), False)
        self.assertEqual(
            is_exist({'outer': {'inner1': {'inner2': {'default': ['we', 'have', 'it']}}
                                , 'other': True, 'othertwo': 123, 'another': {1:2, 'oi': 'ne'}}},
                     'outer',
                     {'inner1': {'inner2': smth}}, {'othertwo': 123}
            ), True)
        example_dict = {
            '@required' : 'false',
            '@style' : 'unbound',
            '@id' : 'period',
            'item' : {
                '@id' : '',
                '@name' : ''
            },
            'conditions' : {
                'condition' : [{
                        '@value' : 'between',
                        '@label' : u'\u043c\u0435\u0436\u0434\u0443'
                    }
                ]
            },
            '@tableName' : 'nsi.vw_aerodrome_cert',
            '@randint' : 0,
            '@type' : 'date',
            '@boolInput' : 'false',
            'selects' : {
                'select' : []
            },
            '@minValue' : '01.01.2016',
            '@value' : '',
            '@label' : u'\u041f\u0435\u0440\u0438\u043e\u0434 \u0442\u0435\u043a\u0443\u0448\u0435\u0433\u043e \u0433\u043e\u0434\u0430',
            '@maxValue' : '07.09.2016',
            '@selector_data' : '',
            'default' : ['01.01.2016', '07.09.2016'],
            '@key' : 'view',
            '@current_condition' : 'between',
            'items' : {
                'item' : []
            }
        }
        self.assertEqual(is_exist(example_dict, 'item', {'@id': smth} ), False)
        self.assertEqual(is_exist(example_dict, 'conditions', {'condition': smth} ), True)
        self.assertEqual(is_exist({'period': {'minValue': 90}}, 'period',
                                  {'minValue':smth, 'maxValue':smth}), False)
        self.assertEqual(is_exist({'period': {'minValue': 90, 'maxValue': 188}}, 'period',
                                  {'minValue':smth, 'maxValue':smth}), True)
        unbound_dict = {'strUnit': {'item': {u'@id': u'strUnits17', u'@name':
            u'\u0420\u0443\u043a\u043e\u0432\u043e\u0434\u0441\u0442\u0432\u043e'}, 'maxValue': u'', 'minValue': u'', 'condition': u'equal', 'text': u'', 'bool': False}, 'lastPeriod': {'item': {u'@id': u'', u'@name': u''}, 'maxValue': u'', 'minValue': u'', 'condition': u'equal', 'text': u'', 'bool': True}, 'tu': {'item': {u'@id': u'ogkn4', u'@name': u'\u0423\u0413\u0410\u041d \u041d\u041e\u0422\u0411 \u0426\u0424\u041e'}, 'maxValue': u'', 'minValue': u'', 'condition': u'equal', 'text': u'', 'bool': False}, 'date': {'item': {u'@id': u'', u'@name': u''}, 'maxValue': u'2016-09-13', 'minValue': u'2016-01-01', 'condition': u'between', 'text': u'', 'bool': False}}
        self.assertEqual(is_exist(unbound_dict, 'lastPeriod', {'bool': True}), True)

        self.assertEqual(smth, True)
        self.assertEqual(smth, False)
        self.assertTrue(smth not in ['', ''])
        self.assertTrue(smth in ['1', ''])
        self.assertTrue(smth in {'1': ''}.keys())
        with self.assertRaises(TypeError):
            smth in {'1': ''}
        self.assertTrue(smth == 0)
        self.assertFalse(smth == [])

    # Проверка правильности выстроения заголовков
    def test_headers(self):
        from test_functions import fill_example, fill_filter
        from filter_header import HeaderDict

        a = fill_example()
        self.assertEqual(a.return_header({'dichotomy': 'stool', 'punkt': False, 'posta': 783,
                                          'filter_date': {'from': '2016-01-01', 'to': '2016-09-14'}}),
            {'span': [{'span': [{'#text': u'\u0415\u0441\u0442\u044c \u0434\u0432\u0430', '@class': 'header-key'}, {'#text': u'\u0441\u0442\u0443\u043b\u0430;  ', '@class': 'header-value'}], '@class': 'header-clause'}, {'span': [{'#text': u'\u041f\u043e\u0441\u0442\u0430\u043d\u043e\u0432\u043b\u0435\u043d\u0438\u0439: ', '@class': 'header-key'}, {'#text': u'783. ', '@class': 'header-value'}], '@class': 'header-clause'}, {'span': [{'#text': u'C 01.01.2016 \u043f\u043e 14.09.2016; ', '@class': 'header-value'}], '@class': 'header-clause'}]})
        print('First is gone')
        self.assertEqual(a.return_header({'dichotomy': '', 'punkt': False, 'posta': 0,
                                          'filter_date': ''}),
            {'span': [{'span': [{'#text': u'Постановлений: ', '@class': 'header-key'}, {'#text': u'0. ', '@class': 'header-value'}], '@class': 'header-clause'}]})
        print('Second is gone')
        self.assertEqual(a.return_header({'dichotomy': '', 'punkt': None, 'posta': None,
                                          'filter_date': {'from': '2000-01-01', 'to': '2016-09-14'}}),
            {'span': [{'span': [{'#text': u'C 01.01.2000 по 14.09.2016; ', '@class': 'header-value'}], '@class': 'header-clause'}]})
        print('Third is gone')
        self.assertEqual(a.return_header({'dichotomy': '', 'punkt': None, 'posta': None,
                                          'filter_date': {'from': '', 'to': '2016-09-14'}}),
            {'span': [{'span': [{'#text': u'\u041f\u043e 14.09.2016; ', '@class': 'header-value'}], '@class': 'header-clause'}]})
        print('Fourth is gone')
        self.assertEqual(a.return_header({'dichotomy': '', 'punkt': None, 'posta': None,
                                          'filter_date': {'from': '2000-01-01', 'to': ''}}),
            {'span': [{'span': [{'#text': u'C 01.01.2000; ', '@class': 'header-value'}], '@class': 'header-clause'}]})
        print('Fifth is gone')
        b = HeaderDict(OrderedDict([
            ('date', {'data_type': 'date', 'label': u'', 'empty': u'с 01.01.2016 по 15.09.2016'}),
            ('tu', {'data_type': 'text', 'label': '', 'end': '.'}),
            ('strUnit', {'data_type': 'text', 'label': u'', 'end': '.'}),
            ('dateType', {'data_type': 'bool', 'values_to_header': {
                True: u'на текущий день',  False: u'',
                }})
        ]), header=u'Фильтрация')
        self.assertEqual(b.return_header({
            'strUnit': {'item': '', 'maxValue': '', 'minValue': '', 'condition': '', 'text': '', 'bool': ''},
            'tu': {'item': '', 'maxValue': '', 'minValue': '', 'condition': '', 'text': '', 'bool': ''},
            'date': {'item': '', 'maxValue': '', 'minValue': '', 'condition': '', 'text': '', 'bool': ''}}, context_filter=True),
            {'span': [{'span': [{'#text': u'\u0424\u0438\u043b\u044c\u0442\u0440\u0430\u0446\u0438\u044f', '@class': 'header-header'}], '@class': 'header-class'}, {'span': [{'#text': u'\u0441 01.01.2016 \u043f\u043e 15.09.2016 ', '@class': 'header-value'}], '@class': 'header-clause'}]})

        from filter import filter_assembly, condition_constructor, unbound_dict_filler
        from test_classes import ExampleCursor
        example_cursor = ExampleCursor()
        context_get_data = fill_filter(self.instance_bound_xforms_data, example_cursor, condition_constructor, 'bound',
                                       'usuall', lambda x: 'equal' if x != 'date' else 'between')
        header_object = HeaderDict(context_get_data, context_list=True)
        data_dict = {'string': unbound_dict_filler({'text': u'Что вы думаете по поводу'}),
                     'index': unbound_dict_filler({'text': 3}),
                     'date': unbound_dict_filler({'maxValue': '2015-03-22', 'minValue': ''}),
                     'boolean': unbound_dict_filler({'Bool': True}), 'int': unbound_dict_filler({'text': 323})}
        self.assertEqual(header_object.return_header(data_dict, context_filter=True),
                         {'span': [{'span': [{'#text': u'\u0421\u0442\u0440\u043e\u043a\u0430 ', '@class': 'header-key'}, {'#text': u'\u0427\u0442\u043e \u0432\u044b \u0434\u0443\u043c\u0430\u0435\u0442\u0435 \u043f\u043e \u043f\u043e\u0432\u043e\u0434\u0443. ', '@class': 'header-value'}], '@class': 'header-clause'}, {'span': [{'#text': u'\u0418\u043d\u0434\u0435\u0445 ', '@class': 'header-key'}, {'#text': u'3. ', '@class': 'header-value'}], '@class': 'header-clause'}, {'span': [{'#text': u'\u0414\u0430\u0442\u0430 ', '@class': 'header-key'}, {'#text': u'\u043f\u043e 22.03.2015. ', '@class': 'header-value'}], '@class': 'header-clause'}, {'span': [{'#text': u'\u0418\u043d\u0442\u044b ', '@class': 'header-key'}, {'#text': u'323. ', '@class': 'header-value'}], '@class': 'header-clause'}]})

        context_get_data = fill_filter(self.instance_ms_xforms_data, example_cursor, condition_constructor, 'bound',
                                       'itemset', lambda x: 'equal')
        header_object = HeaderDict(context_get_data, context_list=True)
        data_dict = {'string': unbound_dict_filler({'items': [{'@id': 'rec1', '@name': u'Первый элемент'},
                                                              {'@id': 'rec2', '@name': u'Second element'},
                                                              {'@id': 'rec3', '@name': u'Третий элемент'}]})}
        self.assertEqual(header_object.return_header(data_dict, context_filter=True),
                        {'span': [{'span': [{'#text': u'\u041c\u0443\u043b\u044c\u0442\u0438\u0441\u0435\u043b\u0435\u043a\u0442\u043e\u0440 \u0441\u0442\u0440\u043e\u043a ', '@class': 'header-key'}, {'#text': u'\u041f\u0435\u0440\u0432\u044b\u0439 \u044d\u043b\u0435\u043c\u0435\u043d\u0442; Second element; \u0422\u0440\u0435\u0442\u0438\u0439 \u044d\u043b\u0435\u043c\u0435\u043d\u0442. ', '@class': 'header-value'}], '@class': 'header-clause'}]})

        context_get_data = fill_filter(self.instance_selector_xforms_data, example_cursor, condition_constructor, 'bound',
                                       'selector', lambda x: 'equal')
        header_object = HeaderDict(context_get_data, context_list=True)
        data_dict = {'string': unbound_dict_filler({'item': {'@id': 'rec1', '@name': u'Первый элемент'}})}
        data_dict['date'] = unbound_dict_filler({'item': {'@id': 'rec1', '@name': '2012-12-12 03:00:00.000'}})
        data_dict['int'] = unbound_dict_filler({'item': {'@id': 'rec1', '@name': 2312}})
        self.assertEqual(header_object.return_header(data_dict, context_filter=True),
                         {'span': [{'span': [{'#text': u'\u0421\u0435\u043b\u0435\u043a\u0442\u043e\u0440 \u0447\u0438\u0441\u043b\u0430 ', '@class': 'header-key'}, {'#text': u'2312. ', '@class': 'header-value'}], '@class': 'header-clause'}, {'span': [{'#text': u'\u0421\u0435\u043b\u0435\u043a\u0442\u043e\u0440 \u0434\u0430\u0442\u044b ', '@class': 'header-key'}, {'#text': u'12.12.2012. ', '@class': 'header-value'}], '@class': 'header-clause'}, {'span': [{'#text': u'\u0421\u0435\u043b\u0435\u043a\u0442\u043e\u0440 \u0441\u0442\u0440\u043e\u043a ', '@class': 'header-key'}, {'#text': u'\u041f\u0435\u0440\u0432\u044b\u0439 \u044d\u043b\u0435\u043c\u0435\u043d\u0442. ', '@class': 'header-value'}], '@class': 'header-clause'}]})

        context_get_data = fill_filter(self.instance_select_xforms_data, example_cursor, condition_constructor, 'bound',
                                       'select', lambda x: 'equal')
        header_object = HeaderDict(context_get_data, context_list=True)
        data_dict = {'string': unbound_dict_filler({'text': 'first string'})}
        data_dict['int'] = unbound_dict_filler({'text': 2})
        self.assertEqual(header_object.return_header(data_dict, context_filter=True),
                        {'span': [{'span': [{'#text': u'\u0421\u0435\u043b\u0435\u043a\u0442 \u0441\u0442\u0440\u043e\u043a ', '@class': 'header-key'}, {'#text': u'\u041f\u0435\u0440\u0432\u0430\u044f \u0441\u0442\u0440\u043e\u0447\u0435\u0447\u043a\u0430. ', '@class': 'header-value'}], '@class': 'header-clause'}, {'span': [{'#text': u'\u0421\u0435\u043b\u0435\u043a\u0442 \u0447\u0438\u0441\u0435\u043b ', '@class': 'header-key'}, {'#text': u'\u041f\u0435\u0440\u0432\u043e\u0435 \u0447\u0438\u0441\u043b\u043e. ', '@class': 'header-value'}], '@class': 'header-clause'}]})



    # Функция-имитатор ...Card.py
    def example_card_py(self, context):
        pass

    # Проверка работы assembly_filter
    def test_assembly(self):
        from test_classes import ExampleCursor
        from filter import filter_assembly, condition_constructor
        from test_functions import fill_context, fill_filter

        example_cursor = ExampleCursor()
        context = Context()

        filter_name = 'standard'    # Сначала часть для тупых дефолтных полей
        filter_assembly(context, example_cursor, filter_name, self.instance_bound_xforms_data)
        checked_context = {'standard':
            fill_filter(self.instance_bound_xforms_data, example_cursor, condition_constructor, 'bound', 'usuall',
                        lambda x: 'equal' if x != 'date' else 'between')
        }
        self.assertEqual(len(context.getData()[filter_name]), len(checked_context['standard']))
        self.assertEqual(context.getData()[filter_name], checked_context['standard'])

        filter_name = 'multi_selector'   # Теперь на создание мультиселектора (пока по одному полю)
        filter_assembly(context, example_cursor, filter_name, self.instance_ms_xforms_data)
        checked_context = {'multi_selector':
            fill_filter(self.instance_ms_xforms_data, example_cursor, condition_constructor, 'bound', 'itemset',
                        lambda x: 'equal')
        }
        self.assertEqual(len(context.getData()[filter_name]), len(checked_context['multi_selector']))
        self.assertEqual(context.getData()[filter_name], checked_context['multi_selector'])

        filter_name = 'selector'   # Теперь на создание селектора
        filter_assembly(context, example_cursor, filter_name, self.instance_selector_xforms_data)
        checked_context = {'selector':
            fill_filter(self.instance_selector_xforms_data, example_cursor, condition_constructor, 'bound', 'selector',
                        lambda x: 'equal')
        }
        self.assertEqual(len(context.getData()[filter_name]), len(checked_context['selector']))
        self.assertEqual(context.getData()[filter_name], checked_context['selector'])

        filter_name = 'select'   # И, наконец, селект
        filter_assembly(context, example_cursor, filter_name, self.instance_select_xforms_data)
        checked_context = {'select':
            fill_filter(self.instance_select_xforms_data, example_cursor, condition_constructor, 'bound', 'select',
                        lambda x: 'equal')
        }
        self.assertEqual(len(context.getData()[filter_name]), len(checked_context['select']))
        self.assertEqual(context.getData()[filter_name], checked_context['select'])


if __name__ == "__main__":
    suite = unittest.TestLoader().loadTestsFromTestCase(FilterTest)
    unittest.TextTestRunner(verbosity=2).run(suite)