data "aws_ami" "amazon_linux" {
  most_recent = true
  owners      = ["amazon"]
  filter {
    name   = "name"
    values = ["amzn2-ami-hvm-*-gp2"]
  }

  filter {
    name   = "root-device-type"
    values = ["ebs"]
  }

  filter {
    name   = "virtualization-type"
    values = ["hvm"]
  }

  filter {
    name   = "architecture"
    values = ["x86_64"]
  }
}

resource "aws_instance" "prometheus_instance" {
  ami =  "ami-0419dc605b6dde61f" //ubuntu 18.04
  instance_type          = "t2.micro"
  vpc_security_group_ids = [var.sg.prometheus]
  subnet_id              = var.public_subnets[0]
  iam_instance_profile   = var.iam_instance_profile.name
  user_data = base64encode(templatefile("${path.module}/user_data.sh",
	  {
		  PRIVATE_IP_ADDRESS=var.PRIVATE_IP_ADDRESS,
		  private_ip_address=var.private_ip_address
	  }
  ))
  tags = {
    "Name" = "prometheus_grafana_instance"
  }
}

resource "aws_eip" "prometheus_eip" {
  vpc = true
}

resource "aws_eip_association" "eip_assoc" {
  instance_id   = aws_instance.prometheus_instance.id
  allocation_id = aws_eip.prometheus_eip.id
}
