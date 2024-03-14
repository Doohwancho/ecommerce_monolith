#!/bin/bash

export RDS_ENDPOINT=${rds_endpoint}
export RDS_USERNAME=${rds_username}
export RDS_PASSWORD=${rds_password}
export RDS_DATABASE=${rds_database}
export RDS_PORT=${rds_port}
export REDIS_ENDPOINT=${redis_endpoint}
export REDIS_PORT=${redis_port}



# Check if the variables are set
if [ -z "$RDS_ENDPOINT" ] || [ -z "$RDS_USERNAME" ] || [ -z "$RDS_PASSWORD" ] || [ -z "$RDS_DATABASE"] || [ -z "$RDS_PORT"] || [ -z "$REDIS_ENDPOINT" ] || [ -z "$REDIS_PORT" ]; then
    echo "One or more required environment variables are not set. Aborting the script."
    exit 1
fi

cd /home/ubuntu/
sudo git clone --depth 1 https://github.com/Doohwancho/ecommerce.git

cd ecommerce
sudo chown -R ubuntu:ubuntu .

cd /home/ubuntu/ecommerce/back/ecommerce
chmod +x gradlew
sudo ./gradlew build -x test

# Proceed to run the application if all checks pass
nohup java -jar \
  -Dspring.profiles.active=prod \
  -DRDS_ENDPOINT=$RDS_ENDPOINT \
  -DRDS_USERNAME=$RDS_USERNAME \
  -DRDS_PASSWORD=$RDS_PASSWORD \
  -DRDS_DATABASE=$RDS_DATABASE \
  -DRDS_PORT=$RDS_PORT \
  -DREDIS_ENDPOINT=$REDIS_ENDPOINT \
  -DREDIS_PORT=$REDIS_PORT \
  ./build/libs/ecommerce-0.0.1-SNAPSHOT.jar > application.log 2>&1 &

