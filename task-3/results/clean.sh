#!/bin/bash

kubectl delete -f ./k8s/cronjob.yaml
kubectl delete -f ./k8s/postgres-service.yaml
kubectl delete -f ./k8s/postgres-statefulset.yaml
kubectl delete -f ./k8s/postgres-secret.yaml
kubectl delete -f ./k8s/secret.yaml
kubectl delete -f ./k8s/postgres-init-configmap.yaml
kubectl delete -f ./k8s/configmap.yaml
kubectl delete -f ./k8s/postgres-pvc.yaml
kubectl delete -f ./k8s/pvc.yaml
# minikube останавливается и удаляется вручную