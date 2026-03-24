FROM python:3.11-slim

WORKDIR /app

# Устанавливаем зависимости
COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

# Копируем скрипт
COPY export.py .

# Создаём каталог для выгрузок (будет переопределён volume)
RUN mkdir -p /exports

# Запускаем скрипт по умолчанию
CMD ["python", "export.py"]