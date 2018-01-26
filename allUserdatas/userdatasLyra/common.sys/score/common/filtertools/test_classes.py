#coding: utf-8
from test_functions import get_data_from_file


class ExampleCursor:
    types = {'DATETIME', 'VARCHAR', 'NUMERIC', 'TEXT', 'INT', 'FLOAT', 'DATE', 'BOOLEAN', 'BIT', 'BOOL',
           'REAL'}
    data = get_data_from_file()
    type_to_type = {
        'DATETIME': 'date', 'VARCHAR': 'text', 'NUMERIC': 'float',
        'TEXT': 'text', 'INT': 'float', 'FLOAT': 'float', 'DATE': 'date',
        'BOOLEAN': 'bool', 'BIT': 'bool', 'BOOL': 'bool', 'REAL': 'float'
    }

    # Создадим курсорчик
    def __init__(self):
        self.grain_path = 'example.ExampleCursor'

        self.index = 0
        self.int = 0
        self.string = ''
        self.date = None
        self.boolean = False

    def _grainName(self):
        return self.grain_path[:self.grain_path.find('.')]

    def _tableName(self):
        return self.grain_path[self.grain_path.find('.')+1:]

    def setFilter(self, field_name, condition):
        pass

    def get_type_by_name(self, field_name):
        return self.type_to_type[self.data[field_name]['data_type']]

    def meta(self):
        class MetaData:
            def __init__(self, data_type):
                self.data_type = data_type

            def getCelestaType(self):
                return self.data_type

        class Other:
            def __init__(self, data):
                self.data = data

            def getColumns(self):
                meta_data = {}
                for field_name, field_data in self.data.items():
                    meta_object = MetaData(field_data['data_type'])
                    meta_data[field_name] = meta_object
                return meta_data

        a = Other(self.data)
        return a