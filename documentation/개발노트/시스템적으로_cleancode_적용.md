## f. clean code

개발자 마다 코딩하는 스타일이 천차만별이고,

clean code라는게 오답은 있으나 정답이 없는 경우가 많다보니,

클린코드 규칙을 논의하고 코드리뷰로 하나하나 체크하는 것 보다,

시스템적으로 google-style-code-convention 적용해서 단축키 누르면 자동으로 포멧 변환하거나 수정해야 하는 부분 체크하는 식으로 처리한다.




### 1. code convention & protocol 설정
1. [google style java code convention](https://google.github.io/styleguide/javaguide.html)
2. [custom error code protocol](https://github.com/Doohwancho/ecommerce/blob/main/back/1.ecommerce/src/main/java/com/cho/ecommerce/global/error/ErrorCode.java)
3. [common / business / member, 도메인 별 exception](https://github.com/Doohwancho/ecommerce/tree/main/back/1.ecommerce/src/main/java/com/cho/ecommerce/global/error/exception)
4. [commit-message protocol](https://github.com/Doohwancho/ecommerce/blob/main/documentation/protocols/commit-message.md)

### 2. linter를 단축키로 적용
- intellij plugins
	1. sonarlint
	2. checkstyle
- intellij 단축키 설정
	1. command + shift + 1 단축키로 google style java code convention 적용
		- ![](documentation/images/2024-01-30-21-02-05.png)
	2. command + shift + 2 단축키로 sonarlint를 적용
		- ![](documentation/images/2024-01-30-21-02-47.png)
	3. 'format on save'을 해서 저장 시에 자동으로 포멧이 되게끔 설정한다.
		- ![](documentation/images/2024-01-30-21-04-34.png)

