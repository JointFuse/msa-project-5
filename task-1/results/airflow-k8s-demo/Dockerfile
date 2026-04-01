FROM apache/airflow

USER root
RUN apt-get update && apt-get install -y --no-install-recommends build-essential

USER airflow
COPY requirements.txt /requirements.txt
RUN pip install --no-cache-dir -r /requirements.txt

# Копируем данные (для демонстрации)
COPY ./data/ /opt/airflow/data/

# Копируем DAG
COPY ./dags/ /opt/airflow/dags/