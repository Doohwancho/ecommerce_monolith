#!/bin/bash

# Detect system architecture
ARCH=$(uname -m)

# Function to install Docker
install_docker() {
    sudo apt-get update
    sudo apt-get install apt-transport-https ca-certificates curl software-properties-common -y
    curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo apt-key add -
    
    if [[ "$ARCH" == "x86_64" ]]; then
        sudo add-apt-repository "deb [arch=amd64] https://download.docker.com/linux/ubuntu $(lsb_release -cs) stable"
    elif [[ "$ARCH" == "aarch64" ]]; then
        sudo add-apt-repository "deb [arch=arm64] https://download.docker.com/linux/ubuntu $(lsb_release -cs) stable"
    fi
    
    sudo apt-get update
    sudo apt-get install docker-ce -y
}

# Function to install Docker Compose
install_docker_compose() {
    sudo curl -L "https://github.com/docker/compose/releases/download/1.29.2/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
    sudo chmod +x /usr/local/bin/docker-compose
}

# Check if the system architecture is supported
if [[ "$ARCH" == "x86_64" || "$ARCH" == "aarch64" ]]; then
    echo "System architecture is $ARCH, proceeding with installation."
    
    # Step 1) Install Docker
    install_docker
    install_docker_compose
    echo "Docker and Docker Compose installed successfully."
    
    # Step 2) Install k6
    echo "Installing k6 for load testing..."
    sudo docker pull grafana/k6
    echo "k6 installed successfully."
    
    # Step 3) Install Git
    echo "Installing Git..."
    sudo apt-get install git -y
    echo "Git installed successfully."
    
    # Step 4) Clone the eCommerce project repository
    echo "Cloning the eCommerce project repository..."
    cd /home/ubuntu/
    sudo git clone --depth 1 https://github.com/Doohwancho/ecommerce.git
    cd ecommerce/
    sudo chown -R ubuntu:ubuntu .
    echo "eCommerce project cloned successfully."
    
    # Confirm that everything has been set up properly
    echo "All installations completed successfully."
else
    echo "This script is designed for amd64 and arm64 architectures. Exiting installation."
    exit 1
fi