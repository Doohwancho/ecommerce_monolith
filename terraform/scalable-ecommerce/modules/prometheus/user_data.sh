#!/bin/bash

export PRIVATE_IP_ADDRESS=${private_ip_address}

sudo apt-get update

sudo apt-get install apt-transport-https ca-certificates curl software-properties-common

curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo apt-key add -
sudo add-apt-repository "deb [arch=amd64] https://download.docker.com/linux/ubuntu $(lsb_release -cs) stable"

sudo apt-get update
sudo apt-get install docker-ce -y

sudo curl -L "https://github.com/docker/compose/releases/download/1.29.2/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose

sudo chmod +x /usr/local/bin/docker-compose

yum install git -y

sudo mkdir /home/prometheus
cd /home/prometheus

git clone --depth 1 https://github.com/Doohwancho/ecommerce.git

cd ecommerce/prometheus

sudo chown -R ubuntu:ubuntu .

sudo -u ubuntu sed -i "s/ECOMMERCE_APP_IP/${PRIVATE_IP_ADDRESS}/g" ./prometheus.yml

cd /home/prometheus/ecommerce

sudo docker-compose -f docker-compose-monitoring-prod.yml up -d --build
