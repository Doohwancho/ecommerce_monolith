terraform {
  required_version = ">= 0.12.0"
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 3.0"
    }
  }
}

# VPC Configuration
resource "aws_vpc" "stress_test_vpc" {
  cidr_block           = "10.0.0.0/16"
  enable_dns_hostnames = true
  enable_dns_support   = true

  tags = {
    Name = "stress-test-vpc"
  }
}

resource "aws_internet_gateway" "stress_test_igw" {
  vpc_id = aws_vpc.stress_test_vpc.id

  tags = {
    Name = "stress-test-igw"
  }
}

resource "aws_subnet" "stress_test_subnet" {
  vpc_id                  = aws_vpc.stress_test_vpc.id
  cidr_block              = "10.0.1.0/24"
  availability_zone       = "ap-northeast-2a"
  map_public_ip_on_launch = true

  tags = {
    Name = "stress-test-subnet"
  }
}

resource "aws_route_table" "stress_test_rt" {
  vpc_id = aws_vpc.stress_test_vpc.id

  route {
    cidr_block = "0.0.0.0/0"
    gateway_id = aws_internet_gateway.stress_test_igw.id
  }

  tags = {
    Name = "stress-test-rt"
  }
}

resource "aws_route_table_association" "stress_test_rta" {
  subnet_id      = aws_subnet.stress_test_subnet.id
  route_table_id = aws_route_table.stress_test_rt.id
}

# Security Group
resource "aws_security_group" "stress_test_sg" {
  name        = "stress-test-sg"
  description = "Security group for stress test EC2 instance"
  vpc_id      = aws_vpc.stress_test_vpc.id

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = {
    Name = "stress-test-sg"
  }
}

# IAM Role and Instance Profile
resource "aws_iam_role" "stress_test_role" {
  name = "stress-test-role"
  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Action = "sts:AssumeRole"
        Effect = "Allow"
        Principal = {
          Service = "ec2.amazonaws.com"
        }
      }
    ]
  })
}

resource "aws_iam_instance_profile" "stress_test_profile" {
  name = "stress-test-profile"
  role = aws_iam_role.stress_test_role.name
}

resource "aws_iam_role_policy_attachment" "ssm_policy_attachment" {
  role       = aws_iam_role.stress_test_role.name
  policy_arn = "arn:aws:iam::aws:policy/AmazonSSMManagedInstanceCore"
}

/*
# case1) EC2 Instance (AMD version with limited cpu credits)
resource "aws_instance" "stress_test_instance" {
  ami = "ami-0419dc605b6dde61f" # ami for ap-northeast-2 region, ubuntu 18.04 ver, AMD architecture 
#   ami           = "ami-0a5a6128e65676ebb"  # Amazon Linux 2 ARM64 AMI in Seoul region
  # 주의! aws-ec2에 t2,t3,t4g 에는 'cpu credit'이라는 개념이 있는데, cpu usage 제한량이 있고, 딱 1분만 max core 쓸 수 수있음. 이를 초과하면 자동으로 중지됨. ex. t3a.xlarge는 cpu usage 50% 초과하니까 서버 터짐 
  # instance_type = "t3a.small" #AMD x86_64 architecture, 2-core 2-GiB RAM # 주의! 300 RPS 테스트 하니까 서버 터짐
  # instance_type = "t3a.medium" #AMD x86_64 architecture, 2-core 4-GiB RAM # 주의! 300 RPS 테스트 하니까 서버 터짐
  # instance_type = "t3a.large" #AMD x86_64 architecture, 2-core 8-GiB RAM # 주의! 300 RPS 테스트 하니까 서버 터짐
  instance_type = "t3a.xlarge" #AMD x86_64 architecture, 4-core 16-GiB RAM 
  subnet_id     = aws_subnet.stress_test_subnet.id

  vpc_security_group_ids      = [aws_security_group.stress_test_sg.id]
  iam_instance_profile        = aws_iam_instance_profile.stress_test_profile.name
  associate_public_ip_address = true
  user_data = base64encode(file("./user_data_AMD.sh"))

  tags = {
    Name = "stress-test-instance"
  }
}
*/

# EC2 Instance (arm64 version without limited cpu credits)
resource "aws_instance" "stress_test_instance" {
  //ap-northeast-2에 ubuntu 18.04ver에 arm64 에 맞는 ec2's ami 찾기 from 'https://cloud-images.ubuntu.com/locator/ec2/'
  ami = "ami-0195178fef736f4ed" # ami for ap-northeast-2 region, ubuntu 18.04 ver, ARM64 architecture 
  # https://instances.vantage.sh/aws/ec2/c6g.xlarge
  # instance_type = "c6g.xlarge" #ARM64, 4-core 8-GiB RAM, cpu-burst 버전이 아님. 따라서 cap이 없음. -> 300RPS 테스트시 cpu load average가 4정도 나오기 때문에, peak 하면 죽는 경우 발생. 8코어 이상 써야 함.
  instance_type = "c6g.2xlarge" #ARM64, 4-core 8-GiB RAM, cpu-burst 버전이 아님. 따라서 cap이 없음. 
  subnet_id     = aws_subnet.stress_test_subnet.id

  vpc_security_group_ids      = [aws_security_group.stress_test_sg.id]
  iam_instance_profile        = aws_iam_instance_profile.stress_test_profile.name
  associate_public_ip_address = true
  user_data = base64encode(file("./user_data_ARM64.sh"))

  tags = {
    Name = "stress-test-instance"
  }
}

# Output
output "instance_id" {
  value = aws_instance.stress_test_instance.id
}

output "public_ip" {
  value = aws_instance.stress_test_instance.public_ip
}