/* resource "random_password" "password" { #A */
/*   length           = 16 */
/*   special          = true */
/*   override_special = "!#$%&*()-_=+[]{}<>:?" */
/* } */

# 임의로 database connections 숫자 조절할 때 사용(권장하지 않음)
/* resource "aws_db_parameter_group" "my-rds-parameter-group" { */
/*   name        = "${var.namespace}-mysql-parameters" */
/*   family      = "mysql8.0"  # Ensure this matches your database engine version */
/*   description = "Custom parameter group for ${var.namespace}" */

/*   parameter { */
/*     name  = "max_connections" */
/*     value = 75 */
/*   } */
/* } */

resource "aws_db_instance" "database" {
  allocated_storage      = 10
  engine                 = "mysql"
  engine_version         = "8.0"
  instance_class         = "db.m6g.2xlarge"
  identifier             = "${var.namespace}-db-instance"
  /* name                   = "ecommerce" */ //deprecated field: "name"
  db_name                = "ecommerce"
  username               = "admin"
  /* password               = random_password.password.result */
  password               = "adminPassword"
  db_subnet_group_name   = var.vpc.database_subnet_group #B
  vpc_security_group_ids = [var.sg.db] #B
  skip_final_snapshot    = true

  # Associate the custom parameter group with the RDS instance
  /* parameter_group_name = aws_db_parameter_group.my-rds-parameter-group.name */
}
