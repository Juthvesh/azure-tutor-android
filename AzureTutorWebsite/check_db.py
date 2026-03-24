import mysql.connector
import os

db_config = {
    "host": "localhost",
    "user": "root",
    "password": "",
    "database": "flask_db",
    "port": 3306
}

try:
    conn = mysql.connector.connect(**db_config)
    cursor = conn.cursor(dictionary=True)
    cursor.execute("SELECT id, name, email, role FROM users;")
    users = cursor.fetchall()
    print("USERS IN DATABASE:")
    for user in users:
        print(f"ID: {user['id']}, Name: {user['name']}, Email: '{user['email']}', Role: {user['role']}")
    cursor.close()
    conn.close()
except Exception as e:
    print("ERROR:", e)
