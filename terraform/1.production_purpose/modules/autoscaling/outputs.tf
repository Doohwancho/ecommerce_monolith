output "lb_dns_name" {
   value = module.alb.this_lb_dns_name
}

output "private_endpoint_of_ec2" {
	value = data.aws_instances.webserver.private_ips[0]
}
