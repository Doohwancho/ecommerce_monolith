provider "aws" {
  region = "ap-northeast-2"  # Replace with your desired AWS region
}

locals {
  instance_name = "load-test-instance"
}

/* data "aws_ami" "ubuntu" { */
/*   most_recent = true */
/*   owners      = ["099720109477"]  # Canonical */

/*   filter { */
/*     name   = "name" */
/*     values = ["ubuntu/images/hvm-ssd/ubuntu-focal-20.04-amd64-server-*"] */
/*   } */
/* } */

resource "aws_instance" "load_test_instance" {
  ami           = data.aws_ami.ubuntu.id
  instance_type = "c7g.xlarge"  # 4 core 8 GiB RAM
  image_id      = "ami-059e7332a087320a3" //arm64 architecture, aws-graviton processor compatible ubuntu 20.04

  tags = {
    Name = local.instance_name
  }

  user_data = <<-EOF
              #!/bin/bash

              # Install necessary tools
              apt-get update
              apt-get install -y git

              # Install k6
              sudo apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv-keys C5AD17C747E3415A3642D57D77C6C491D6AC1D69
              echo "deb https://dl.k6.io/deb stable main" | sudo tee /etc/apt/sources.list.d/k6.list
              sudo apt-get update
              sudo apt-get install -y k6

              # Clone your load test script repository
			  git clone https://github.com/Doohwancho/ecommerce.git

              # Run the load test using k6
              # k6 run --vus 1000 --duration 60s your-load-test-repo/load-test-script.js
              EOF
}

# Security group for the EC2 instance
resource "aws_security_group" "load_test_sg" {
  name_prefix = "load-test-sg"

  ingress {
    from_port   = 22
    to_port     = 22
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}

# Associate the security group with the EC2 instance
resource "aws_network_interface_sg_attachment" "sg_attachment" {
  security_group_id    = aws_security_group.load_test_sg.id
  network_interface_id = aws_instance.load_test_instance.primary_network_interface_id
}

output "instance_public_ip" {
  value = aws_instance.load_test_instance.public_ip
}
