# A. how to run jmh?

## step1. build.gradle에서 실험 옵션 맞추기

docs: https://github.com/melix/jmh-gradle-plugin

## step2. run

### a.케이스별 실행 스크립트

1. redis_write & read viewcount+1 test

```
./gradlew clean jmh -x test -x compileJava -x compileTestJava -x generateSwaggerCode -x generateSwaggerCodeEcommerce -x querydslClasses -x compileQuerydsl -PjmhIncludes=ProductRankingBenchmark
```

### b. 에러 핸들링

#### case1) jdk 버전이 안맞아서 실행 안되는 경우 -> 쓰는 jdk의 위치를 지정해줘야 한다.

```
./gradlew -Dorg.gradle.java.home=/Users/cho-cho/.asdf/installs/java/zulu-11.60.19 jmh
```

#### case2) 특정 클래스 하나만 run 하는 경우

```build.gradle
jmh {
    include = ['UpdateCacheStrategyBenchmark.*']
    //...
}
```

#### case3) 전체 jmh 테스트를 run 하는 경우

```build.gradle
jmh {
    include = ['.*Benchmark.*']
    //...
}
```

#### case4) 기존 코드의 컴파일, 빌드를 스킵하고 jmh 벤치마크만 하고 싶다면

```
./gradlew clean jmh -x test -x compileJava -x compileTestJava -x generateSwaggerCode -x generateSwaggerCodeEcommerce -x querydslClasses -x compileQuerydsl 
```

# B. troubleshooting

## a. log level DEBUG -> warn으로 올리는 법

https://github.com/melix/jmh-gradle-plugin/issues/106#issuecomment-2453829916

