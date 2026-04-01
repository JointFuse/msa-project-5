import os
import psycopg2
import csv
import datetime
from psycopg2 import sql

# Параметры подключения из переменных окружения
DB_HOST = os.environ.get('DB_HOST')
DB_PORT = os.environ.get('DB_PORT', '5432')
DB_NAME = os.environ.get('DB_NAME')
DB_USER = os.environ.get('DB_USER')
DB_PASS = os.environ.get('DB_PASS')

# Путь для сохранения CSV (обычно смонтированный PVC)
OUTPUT_DIR = os.environ.get('OUTPUT_DIR', '/exports')
TABLE_NAME = os.environ.get('TABLE_NAME', 'my_table')

def export_table_to_csv():
    # Формируем имя файла с временной меткой
    timestamp = datetime.datetime.now().strftime('%Y%m%d_%H%M%S')
    filename = f"{TABLE_NAME}_{timestamp}.csv"
    filepath = os.path.join(OUTPUT_DIR, filename)

    try:
        conn = psycopg2.connect(
            host=DB_HOST,
            port=DB_PORT,
            dbname=DB_NAME,
            user=DB_USER,
            password=DB_PASS
        )
        cursor = conn.cursor()

        # Выполняем запрос: выбираем все строки из таблицы
        query = sql.SQL("SELECT * FROM {}").format(sql.Identifier(TABLE_NAME))
        cursor.execute(query)

        # Получаем названия колонок
        colnames = [desc[0] for desc in cursor.description]

        with open(filepath, 'w', newline='', encoding='utf-8') as f:
            writer = csv.writer(f)
            writer.writerow(colnames)
            writer.writerows(cursor.fetchall())

        print(f"Экспорт успешно завершён. Файл: {filepath}")

    except Exception as e:
        print(f"Ошибка экспорта: {e}")
        raise
    finally:
        if conn:
            conn.close()

if __name__ == "__main__":
    export_table_to_csv()