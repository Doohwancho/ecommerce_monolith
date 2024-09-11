/* data "cloudinit_config" "config" { */
/*   gzip          = true # cloud_init content should be gzip compressed */
/*   base64_encode = true # cloud_init content should be based64_encoded */
/*   part { */
/*     content_type = "text/cloud-config" */
/*     content      = templatefile("${path.module}/cloud_config.yaml", var.db_config) #B <----------- 여기서 git clone해서 프로젝트 실행함. */
/*   } */
/* } */

/* data "aws_ami" "ubuntu" { */
/*   most_recent = true */
/*   filter { */
/*     name   = "name" */
/*     values = ["ubuntu/images/hvm-ssd/ubuntu-bionic-18.04-amd64-server-*"] */
/*   } */
/*   owners = ["099720109477"] # Restricts the search to AMIs owned by the specified AWS account (commonly used for official images */
/* } */


resource "aws_launch_template" "webserver" {
  name_prefix   = var.namespace
  instance_type = "m7g.2xlarge" //https://instances.vantage.sh/?region=ap-northeast-2&selected=m7g.medium,m7a.medium,c7a.medium,c7gn.medium

  image_id      = "ami-030be76ca6c8d557a" // Your custom AMI I created using packer

  //https://cloud-images.ubuntu.com/locator/ec2/에서 AZ넣고 찾는다. ubuntu18.04를 찾는다.
  /* image_id      = "ami-059e7332a087320a3" //arm64 architecture, aws-graviton processor compatible ubuntu 20.04 */
  /* image_id      = "ami-0195178fef736f4ed"//arm64 architecture, ubuntu 18.04 */
  /* image_id      = "ami-0419dc605b6dde61f" //amd64 architecture, ubuntu 18.04 */
  /* image_id      = data.aws_ami.ubuntu.id */

  /* associate_public_ip_address = true */

  //user_data     = data.cloudinit_config.config.rendered #B <----------- 여기서 git clone해서 프로젝트 실행함.
  user_data     = base64encode(templatefile("${path.module}/user_data.sh",
	  {
		  rds_endpoint = var.db_config.hostname,
		  rds_username = var.db_config.user,
		  rds_password = var.db_config.password,
		  rds_port     = var.db_config.port,
		  rds_database = var.db_config.database,
		  redis_endpoint = var.redis_endpoint,
		  redis_port     = var.redis_port
	  }
	)) #B <----------- 여기서 git clone해서 프로젝트 실행함.


  key_name      = var.ssh_keypair
  iam_instance_profile {
    name = var.iam_instance_profile_name
  }
  vpc_security_group_ids = [var.sg.websvr]
}

data "aws_instances" "webserver" {
  instance_tags = {
    "aws:autoscaling:groupName" = aws_autoscaling_group.webserver.name
  }
}

resource "aws_autoscaling_group" "webserver" {
  name                = "${var.namespace}-asg"
  min_size            = 1
  max_size            = 1 // scale out 이후, git clone, build, run할 배포 스크립트가 없기 때문에 WAS 서버 수를 1로 고정
  vpc_zone_identifier = var.vpc.private_subnets
  target_group_arns   = module.alb.target_group_arns
  launch_template {
    id      = aws_launch_template.webserver.id
    version = aws_launch_template.webserver.latest_version
  }
}

module "alb" {
  source             = "terraform-aws-modules/alb/aws"
  version            = "~> 5.0"
  name               = var.namespace
  load_balancer_type = "network" # "network" = L4, application" = L7
  vpc_id             = var.vpc.vpc_id
  subnets            = var.vpc.public_subnets
  security_groups    = [var.sg.lb]

  http_tcp_listeners = [
    {
      port               = 80
      protocol           = "TCP" //for nlb, it should be one of 4: TCP, UDP, TCP_UDP, TLS
      target_group_index = 0
    }
  ]

  target_groups = [
    { name_prefix      = "websvr"
      backend_protocol = "TCP" # "TCP" = L4, "HTTP" = L7
      backend_port     = 8080
      target_type      = "instance"
    }
  ]
}
