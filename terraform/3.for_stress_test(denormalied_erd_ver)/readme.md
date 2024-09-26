# Goal 
stress test back/3.반정규화 backend server 

간단한 ec2 - rds + monitoring 구성

ec2에 스트레스 테스트를 실험하면서, ec2랑 rds가 모니터링에 어떻게 반응하는지 확인하기 위해서 구하여 구성.

authentication 관련 스프링 설정을 뺀 back/same_ecommerce_without_authentication_for_stress_test_purpose/ 를 쓴다.
elastic cache 관련 설정도 뺐다.

# What

3-1. ec2(spring backend server) + aws-RDS + ec2(monitoring both spring server & RDS)\
3-2. stress test를 시작하기 위한 2core 2RAM 까지 ec2
