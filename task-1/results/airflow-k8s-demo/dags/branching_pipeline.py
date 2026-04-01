from datetime import datetime, timedelta
import pandas as pd
from airflow import DAG
from airflow.operators.python import PythonOperator, BranchPythonOperator
from airflow.operators.empty import EmptyOperator
from airflow.operators.email import EmailOperator
from airflow.utils.trigger_rule import TriggerRule

# Конфигурация DAG
default_args = {
    'owner': 'data_team',
    'depends_on_past': False,
    'start_date': datetime(2026, 3, 23),
    'email': ['admin@example.com'],
    'email_on_failure': True,
    'email_on_retry': False,
    'retries': 3,
    'retry_delay': timedelta(minutes=1),
    'on_failure_callback': None,
}

dag = DAG(
    'branching_pipeline',
    default_args=default_args,
    description='Пример пайплайна с ветвлением',
    schedule='0 8 * * *',
    catchup=False,
    tags=['example'],
)

# Чтение данных из файловой системы
def read_and_analyze(**context):
    file_path = '/opt/airflow/data/sample_data.csv'
    df = pd.read_csv(file_path)
    row_count = len(df)
    print(f"Прочитано {row_count} строк.")
    context['ti'].xcom_push(key='row_count', value=row_count)
    return row_count

read_task = PythonOperator(
    task_id='read_and_analyze',
    python_callable=read_and_analyze,
    dag=dag,
    retry_delay=timedelta(minutes=1),
)

# Функция ветвления
def decide_branch(**context):
    row_count = context['ti'].xcom_pull(task_ids='read_and_analyze', key='row_count')
    threshold = 1000
    if row_count > threshold:
        return 'high_volume_processing'
    else:
        return 'low_volume_processing'

branch_task = BranchPythonOperator(
    task_id='branch_task',
    python_callable=decide_branch,
    dag=dag,
)

high_volume = EmptyOperator(
    task_id='high_volume_processing',
    dag=dag,
)

low_volume = EmptyOperator(
    task_id='low_volume_processing',
    dag=dag,
)

# Задача, выполняемая после любой ветки (join)
def post_process():
    print("Постобработка завершена")

post_process_task = PythonOperator(
    task_id='post_process',
    python_callable=post_process,
    dag=dag,
    trigger_rule=TriggerRule.ONE_SUCCESS,
)

# Уведомление об успешном завершении пайплайна
email_success = EmailOperator(
    task_id='email_success',
    to='admin@example.com',
    subject='Пайплайн успешно выполнен',
    html_content='<p>Пайплайн <b>branching_pipeline</b> завершился успешно.</p>',
    dag=dag,
    trigger_rule=TriggerRule.ALL_SUCCESS,
)

# Определение зависимостей
read_task >> branch_task
branch_task >> [high_volume, low_volume] >> post_process_task
post_process_task >> email_success