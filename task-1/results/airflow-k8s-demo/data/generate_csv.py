import csv
import random

def generate_csv(filename, rows, min_val=0, max_val=100):
    with open(filename, 'w', newline='') as f:
        writer = csv.writer(f)
        writer.writerow(['value'])
        for _ in range(rows):
            writer.writerow([random.randint(min_val, max_val)])

generate_csv('numbers_1000.csv', 1000)
generate_csv('numbers_2000.csv', 2000)

print("Файлы успешно созданы!")