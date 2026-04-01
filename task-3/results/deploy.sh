#!/bin/bash

# minikube уже развернут!
cd job
eval $(minikube docker-env)
docker build -t db-exporter .
eval $(minikube docker-env -u)
cd ..

# PVC для БД и экспортов
kubectl apply -f ./k8s/postgres-pvc.yaml
kubectl apply -f ./k8s/pvc.yaml

# Секреты
kubectl apply -f ./k8s/postgres-secret.yaml
kubectl apply -f ./k8s/secret.yaml

# ConfigMap для  БД и конфига экспортёра
kubectl apply -f ./k8s/postgres-init-configmap.yaml
kubectl apply -f ./k8s/configmap.yaml

# PostgreSQL StatefulSet и Service
kubectl apply -f ./k8s/postgres-statefulset.yaml
kubectl apply -f ./k8s/postgres-service.yaml

# Ожидание запуска и инициализации PostgreSQL
kubectl wait --for=condition=ready pod -l app=postgres --timeout=60s

# CronJob
kubectl apply -f ./k8s/cronjob.yaml