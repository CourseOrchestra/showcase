# coding: utf-8
"""
Created on 20 мар. 2017 г.

@author: m.prudyvus
"""
from __future__ import unicode_literals

from java.io import BufferedReader, InputStreamReader
from java.lang import StringBuffer
from java.net import URL, HttpURLConnection


class Error(Exception):
    """Исключение при ошибках работы с Меллофоном. 
    """
    pass

    def __init__(self, msg=""):
        self.msg = msg

    def __str__(self):
        return repr(self.msg)


class ForbiddenError(Error):
    pass


class NotFoundError(Error):
    pass


error_dict = {
    HttpURLConnection.HTTP_FORBIDDEN: ForbiddenError,
    HttpURLConnection.HTTP_NOT_FOUND: NotFoundError
}


class Mellophone(object):

    def __init__(self, base_url, session_id=None):
        """
        @param base_url: адрес меллофона
        """
        self.session_id = session_id
        self._base_url = base_url

    def _get_connection(self, url):
        server = URL(url)
        return server.openConnection()  # Подключаемся

    def _send_request(self, url):
        url = self._base_url + url
        conn = None
        try:
            conn = self._get_connection(url)
            conn.setRequestMethod('GET')
            conn.connect()
            if conn.responseCode == HttpURLConnection.HTTP_OK:  # Если запрос обработался хорошо
                return self._get_response_msg(conn)
            else:
                self._on_error(conn)
        finally:
            if conn:
                conn.disconnect()

    def _get_response_msg(self, url_conn):
        """Функция, возвращающая тело ответа"""
        get_input_stream = url_conn.responseCode < HttpURLConnection.HTTP_BAD_REQUEST
        if get_input_stream:
            in_stream = url_conn.getInputStream()
        else:
            in_stream = url_conn.getErrorStream()

        response = ""

        if in_stream:
            in_stream = BufferedReader(InputStreamReader(in_stream, 'utf-8'))

            response = StringBuffer()
            while True:
                inputLine = in_stream.readLine()
                if not inputLine:
                    break
                response.append(inputLine)

            in_stream.close()

        return unicode(response)

    def _on_error(self, url_conn):
        raise error_dict[url_conn.responseCode](self._get_response_msg(url_conn))

    def login(self, login, password, ses_id=None, gp=None, ip=None):
        """Аутентифицирует сессию;
        если пара «логин-пароль» верна, аутентифицирует сессию приложения ses_id;
        в случае если пара «логин-пароль» неверна, выкидывает FORBIDDEN.

        @param login: имя пользователя
        @param password: пароль в открытом виде
        @param ses_id: идентификатор сессии
        @param gp: группа провайдеров
        @param ip: ip компьютера пользователя

        """
        if not ses_id:
            ses_id = self.session_id
        url = '/login?&sesid={}&login={}&pwd={}'.format(ses_id, login, password)

        if gp:
            url += '&gp={}'.format(gp)

        if ip:
            url += '&ip={}'.format(ip)

        self._send_request(url)

    def logout(self, ses_id=None):
        """Разаутентифицирует сессию с идентификатором приложения sesid или текущую"""
        if not ses_id:
            ses_id = self.session_id
        url = '/logout?&sesid={}'.format(ses_id)

        self._send_request(url)

    def is_authenticated(self, ses_id=None):
        """Возвращает информацию об аутентифицированном пользователе, если сессия аутентифицирована;"""
        if not ses_id:
            ses_id = self.session_id
        url = '/isauthenticated?sesid={}'.format(ses_id)
        try:
            self._send_request(url)
        except ForbiddenError:
            return False
        else:
            return True

    def change_app_ses_id(self, new_ses_id, old_ses_id=None):
        """Изменяет сессию"""
        if not old_ses_id:
            old_ses_id = self.session_id
        url = '/changeappsesid?oldsesid={}&newsesid={}'.format(old_ses_id, new_ses_id)
        self._send_request(url)

    def check_name(self, name, ses_id=None):
        """Возвращает информацию об аутентифицированном пользователе, если пользователь существует
        Если пользователь не существует, возвращает пустую строку"""  # может возвращать None?
        if not ses_id:
            ses_id = self.session_id
        url = '/checkname?sesid={}&name={}'.format(ses_id, name)
        return self._send_request(url)

    def import_gp(self):
        """Возвращает список групп провайдеров"""
        url = '/importgroupsproviders'
        response = self._send_request(url)
        return response.split()

    def change_pwd(self, old_pwd, new_pwd, ses_id=None):
        """Изменяет пароль аутентифицированного пользователя"""
        if not ses_id:
            ses_id = self.session_id
        url = '/changepwd?sesid={}&oldpwd={}&newpwd={}'.format(ses_id, old_pwd, new_pwd)
        return self._send_request(url)

    def check_credentials(self, login, password, gp=None, ip=None):
        """Возвращает информацию о пользователе, если пара логин-пароль верна"""
        url = '/checkcredentials?login={}&pwd={}'.format(login, password)

        if gp:
            url += '&gp={}'.format(gp)

        if ip:
            url += '&ip={}'.format(ip)

        return self._send_request(url)

    def get_provider_list(self, login, password, gp=None, ip=None):
        """Возвращает информацию о провайдерах с группой gp"""
        url = '/getproviderlist?login={}&pwd={}'.format(login, password)

        if gp:
            url += '&gp={}'.format(gp)

        if ip:
            url += '&ip={}'.format(ip)

        return self._send_request(url)

    def get_user_list(self, login, password, gp, ip=None, pid=None):
        """Возвращает информацию о пользователях провайдера с идентификатором pid
        (или всех провайдеров с группой gp, если pid не задан),"""
        url = '/getuserlist?login={}&pwd={}&gp={}'.format(login, password, gp)

        if ip:
            url += '&ip={}'.format(ip)

        if pid:
            url += '&ip={}'.format(ip)

        return self._send_request(url)

    def set_settings(self, token, lockout_time):
        url = '/setsettings?token={}&lockouttime={}'.format(token, lockout_time)
        return self._send_request(url)
