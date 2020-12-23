from rest_framework import status
from rest_framework.test import APITestCase
from rest_framework.test import APIClient


class RegistrationTestCase(APITestCase):
    def test_successful(self):
        data = {"username": "testcase", "email": "test@localhost.app",
                "password": "some_strong_psw"}
        response = self.client.post("/auth/users/", data)
        self.assertEqual(response.status_code, status.HTTP_201_CREATED)

    def test_invalid_username(self):
        data = {"username": "td afa", "email": "test@localhost.app",
                "password": "some_strong_psw"}
        response = self.client.post("/auth/users/", data)
        self.assertEqual(response.status_code, status.HTTP_400_BAD_REQUEST)

    def test_invalid_password(self):
        data = {"username": "testcase", "email": "test@localhost.app",
                "password": "1234"}
        response = self.client.post("/auth/users/", data)
        self.assertEqual(response.status_code, status.HTTP_400_BAD_REQUEST)

    def test_invalid_email(self):
        data = {"username": "testcase", "email": "test",
                "password": "some_strong_psw"}
        response = self.client.post("/auth/users/", data)
        self.assertEqual(response.status_code, status.HTTP_400_BAD_REQUEST)


class LoginTestCase(APITestCase):
    def test_valid_login(self):
        reg = {"username": "testcase",
               "password": "some_strong_psw", "email": "test@localhost.app"}
        self.client.post("/auth/users/", reg)
        log = {"username": "testcase",
               "password": "some_strong_psw"}
        response = self.client.post("/auth/token/login/", log)
        self.assertEqual(response.status_code, status.HTTP_200_OK)

    def test_invalid_username(self):
        reg = {"username": "testcase",
               "password": "some_strong_psw", "email": "test@localhost.app"}
        self.client.post("/auth/users/", reg)
        log = {"username": "te",
               "password": "some_strong_psw"}
        response = self.client.post("/auth/token/login/", log)
        self.assertEqual(response.status_code, status.HTTP_400_BAD_REQUEST)

    def test_invalid_password(self):
        reg = {"username": "testcase",
               "password": "some_strong_psw", "email": "test@localhost.app"}
        self.client.post("/auth/users/", reg)
        log = {"username": "testcase",
               "password": "some_"}
        response = self.client.post("/auth/token/login/", log)
        self.assertEqual(response.status_code, status.HTTP_400_BAD_REQUEST)


class ComputeTestCase(APITestCase):
    def test_successful(self):
        reg = {"username": "testcase",
               "password": "some_strong_psw", "email": "test@localhost.app"}
        self.client.post("/auth/users/", reg)
        log = {"username": "testcase",
               "password": "some_strong_psw"}
        response = self.client.post("/auth/token/login/", log)
        client = APIClient()
        client.login(username='testcase', password='some_strong_psw')
        token = response.data['auth_token']
        client.credentials(HTTP_AUTHORIZATION='Token ' + token)
        response = client.post('/ask/', {"question": "why is the sky blue?"}, format='json')
        self.assertEqual(response.status_code, status.HTTP_200_OK)


