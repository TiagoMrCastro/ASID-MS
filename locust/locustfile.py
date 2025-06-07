import uuid
import random
from locust import HttpUser, task, between


class WebsiteUser(HttpUser):
    wait_time = between(1, 3)

    def on_start(self):
        credentials = {
            "username": "carlos",
            "password": "1234"
        }

        with self.client.post("/auth/login", json=credentials, catch_response=True) as response:
            if response.status_code == 200:
                token = response.json().get("token")
                self.headers = {"Authorization": f"Bearer {token}"}
                response.success()
            else:
                print("Login falhou:", response.status_code, response.text)
                response.failure("Falha no login")

    @task
    def get_books(self):
        self.client.get("/books", headers=self.headers)

    @task
    def register_user(self):
        unique_id = str(uuid.uuid4())[:8]
        username = f"locust_{unique_id}"
        email = f"{username}@mail.com"
        fullname = f"Locust Test User {random.randint(1, 1000)}"
        password = "abc123"

        payload = {
            "username": username,
            "email": email,
            "fullname": fullname,
            "password": password
        }

        with self.client.post("/auth/register", json=payload, catch_response=True) as response:
            if response.status_code == 200:
                response.success()
            elif response.status_code == 409:
                response.failure("Utilizador ou e-mail já existe.")
            else:
                response.failure(f"Erro inesperado: {response.status_code} - {response.text}")

    @task
    def add_to_cart(self):
        payload = {
            "userId": 1,
            "bookId": 2,
            "quantity": random.randint(1, 3)
        }

        with self.client.post("/cart/add", json=payload, catch_response=True) as response:
            if response.status_code == 200:
                response.success()
            else:
                response.failure(f"Erro: {response.status_code} - {response.text}")

    @task
    def get_order_details(self):
        order_id = 1

        with self.client.get(f"/order/details/{order_id}", catch_response=True) as response:
            if response.status_code == 200:
                response.success()
            else:
                response.failure(f"Erro: {response.status_code} - {response.text}")

    @task
    def get_order_history_details(self):
        user_id = 1  # Garante que este user tem histórico no sistema
        from_date = "2024-01-01"
        to_date = "2024-12-31"

        with self.client.get(
                f"/history/{user_id}/details",
                params={"from": from_date, "to": to_date},
                catch_response=True
        ) as response:
            if response.status_code == 200:
                response.success()
            else:
                response.failure(f"Erro {response.status_code} - {response.text}")