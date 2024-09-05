# what 
ecommerce랑 같은 코드인데,\
aws에 stress-test시,\
aws-elastic-cache가 만들거나 해제할 때 시간도 오래걸리고,\
무엇보다 엄청 비싸서,\
authentication 부분 제거한 백엔드 코드

특이점: 
1. bulk-insert를 `/bulkinsert/${amount_to_insert}` 로 한다.
2. bulk-insert시 코어수만큼 쓰레드 생성해서 진행하는데, 원래 4개 메서드를 4개의 코어의 수인 4개의 쓰레드가 수행했다면, ec2가 2코어일 수도 있으니까 2코어나 1코어인 경우에 bulk-insert 로직 분기처리 해둠.

주의점:
1. bulk-insert 후에 connection을 다 쓰면, .close() 하는 것 뿐만 아니라, connectionPool 변수로부터 제거해서 자원을 GC로 반환한다.