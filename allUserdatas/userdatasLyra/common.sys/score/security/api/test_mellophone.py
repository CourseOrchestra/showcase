# coding: utf-8
'''
Created on 4 апр. 2017 г.

@author: m.prudyvus
'''
import random
import string
import unittest

from mellophone import Mellophone, ForbiddenError


def get_ses_id(size=16):
    return ''.join(random.choice(string.ascii_letters) for _ in range(size))


def logout(method_to_decorate):
    def wrapper(self, *args, **kwargs):
        result = method_to_decorate(self, *args, **kwargs)
        self.mellophone.logout()
        return result
    return wrapper


def login(method_to_decorate):
    def wrapper(self, *args, **kwargs):
        self.mellophone.login(self.login, self.password)
        return method_to_decorate(self, *args, **kwargs)
    return wrapper


class Test(unittest.TestCase):
    def setUp(self):
        unittest.TestCase.setUp(self)
        self.ses_id = get_ses_id()
        self.mellophone = Mellophone('http://localhost:9090/mellophone/', self.ses_id)
        self.login = "admin"
        self.password = "master"

    def test_incorrect_login(self):
        with self.assertRaises(ForbiddenError):
            self.mellophone.login(self.login, "maaster")

    def test_logout(self):
        self.mellophone.login(self.login, self.password)
        self.assertTrue(self.mellophone.is_authenticated())
        self.mellophone.logout()
        self.assertFalse(self.mellophone.is_authenticated())

    @login
    @logout
    def test_is_authentificated(self):
        self.assertTrue(self.mellophone.is_authenticated())
        self.assertTrue(self.mellophone.is_authenticated(self.ses_id))
        self.assertFalse(self.mellophone.is_authenticated(get_ses_id(32)))

    @login
    def test_change_app_ses_id(self):
        new_ses_id = get_ses_id()
        self.mellophone.change_app_ses_id(new_ses_id)
        with self.assertRaises(ForbiddenError):
            self.mellophone.change_app_ses_id(new_ses_id, get_ses_id())
        self.assertTrue(self.mellophone.is_authenticated(new_ses_id))
        self.assertFalse(self.mellophone.is_authenticated(self.ses_id))
        self.mellophone.logout(new_ses_id)

    @login
    @logout
    def test_check_name(self):
        with self.assertRaises(ForbiddenError):
            self.mellophone.check_name(self.login, get_ses_id())
        self.assertEqual(self.mellophone.check_name(self.login),
                         '<?xml version="1.0" encoding="utf-8"?><user login="admin" SID="super"/>')
        self.assertEqual(self.mellophone.check_name(get_ses_id()), "")

    def test_import_gp(self):
        self.assertIsInstance(self.mellophone.import_gp(), list)

    @login
    @logout
    def test_change_pwd(self):
        with self.assertRaises(ForbiddenError):
            self.mellophone.change_pwd(self.password, "new_pwd", get_ses_id())

        self.mellophone.change_pwd(self.password, "new_pwd")
        self.mellophone.change_pwd("new_pwd", self.password)

    def test_check_credentials(self):
        self.assertEqual(self.mellophone.check_credentials(self.login, self.password),
                         '<?xml version="1.0" encoding="utf-8"?><user login="admin" SID="super"/>')

    def test_get_provider_list(self):
        with self.assertRaises(ForbiddenError):
            self.mellophone.change_pwd(self.mellophone.get_provider_list(self.login, get_ses_id()))
        self.assertEqual(self.mellophone.get_provider_list(self.login, self.password),
                         '<?xml version="1.0" encoding="utf-8"?><providers><provider id="" type="sqlserver" url="jdbc:postgresql://localhost:5432/common.sys" group_providers=""/><provider id="" type="ldapserver" url="ldap://172.16.1.3:389" group_providers="OFFICE"/></providers>')


if __name__ == "__main__":
    #import sys;sys.argv = ['', 'Test.testName']
    unittest.main()
