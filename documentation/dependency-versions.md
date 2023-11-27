# A. 23-11-27 기준

## a. build.gradle
```build.gradle
plugins {
    id 'org.springframework.boot' version '2.5.6'
    id 'io.spring.dependency-management' version '1.0.11.RELEASE'
    id 'java'
    id 'org.hidetake.swagger.generator' version '2.18.2'
    id "com.ewerk.gradle.plugins.querydsl" version "1.0.10"
    id 'idea'
}

group = 'com.cho.ecommerce'
version = '0.0.1-SNAPSHOT'
sourceCompatibility = '1.8'
//for jqwik
ext.junitJupiterVersion = '5.9.3'
ext.jqwikVersion = '1.8.1'

dependencies {

    // OpenAPI Starts
    swaggerCodegen 'org.openapitools:openapi-generator-cli:4.3.1'
    //compile 'io.swagger.core.v3:swagger-annotations:2.1.5'
    compileOnly 'io.swagger:swagger-annotations:1.6.2'
    compileOnly 'org.springframework.boot:spring-boot-starter-validation'
    compileOnly 'org.openapitools:jackson-databind-nullable:0.2.1'
    implementation 'com.fasterxml.jackson.dataformat:jackson-dataformat-xml'
//    implementation 'org.springframework.boot:spring-boot-starter-hateoas' //TODO - hateoas 적용하기
    // OpenAPI Ends

    implementation 'org.springframework.boot:spring-boot-starter-web'
    implementation 'org.springframework.boot:spring-boot-starter-security'

    implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
    implementation 'com.querydsl:querydsl-jpa'

    implementation 'org.springframework.boot:spring-boot-starter-batch'
    implementation 'org.springframework.boot:spring-boot-starter-quartz'

    implementation 'org.springframework.boot:spring-boot-starter-data-redis'
    implementation 'org.springframework.session:spring-session-data-redis'

    implementation 'org.springframework.boot:spring-boot-starter-actuator'

    implementation 'org.thymeleaf:thymeleaf-spring5'

    runtimeOnly 'com.h2database:h2'

    implementation 'net.datafaker:datafaker:1.9.0' //for jdk1.8
    implementation 'mysql:mysql-connector-java:8.0.23'

    implementation 'com.google.guava:guava:31.1-jre' //DatabaseCleanup.java 때문에 필요.

    implementation group: 'com.google.code.gson', name: 'gson', version: '2.8.9'


    compileOnly 'org.projectlombok:lombok:1.18.28'
    implementation 'org.mapstruct:mapstruct:1.5.5.Final'

    annotationProcessor 'org.projectlombok:lombok:1.18.28'
    annotationProcessor 'org.mapstruct:mapstruct-processor:1.5.5.Final'

    testImplementation 'org.springframework.boot:spring-boot-starter-test'
    testImplementation 'org.springframework.security:spring-security-test'
    testImplementation 'org.springframework.batch:spring-batch-test'
    testImplementation "org.junit.platform:junit-platform-launcher:1.5.2"
    testImplementation('org.junit.jupiter:junit-jupiter:5.9.2')
    testImplementation "net.jqwik:jqwik:${jqwikVersion}" //for Property Based Testing
}



test {
    jvmArgs '--enable-preview'
    //start of jqwik config
    useJUnitPlatform {
         includeEngines 'jqwik', 'junit-jupiter', 'junit-vintage'

        // includeTags 'fast', 'medium'
        // excludeTags 'slow'
    }

    include '**/*Properties.class'
    include '**/*Test.class'
    include '**/*Tests.class'

    environment "MY_ENV_VAR", "true"
    //end of jqwik config
}

configurations {
    compileOnly {
        extendsFrom annotationProcessor
    }
}

repositories {
    mavenCentral()
}

//openapi config
swaggerSources {
//    def typeMappings = 'URI=URI, BigDecimal=BigDecimal, EntityModel=EntityModel'
//    def importMappings = 'URI=java.net.URI, BigDecimal=java.math.BigDecimal, EntityModel=org.springframework.hateoas.EntityModel'
    ecommerce {
        def apiYaml = "${rootDir}/src/main/resources/api/openapi.yaml"
        def configJson = "${rootDir}/src/main/resources/api/config.json"
        inputFile = file(apiYaml)
        def ignoreFile = file("${rootDir}/src/main/resources/api/.openapi-generator-ignore")
        code {
            language = 'spring'
            configFile = file(configJson)
            rawOptions = ['--ignore-file-override', ignoreFile] as List<String>
            components = [models: true, apis: true, supportingFiles: 'ApiUtil.java']
            outputDir = file("$buildDir/generated/swagger") // Set your output directory here
            //templateDir = file("${rootDir}/src/main/resources/templates")
            //dependsOn validation // Should be uncommented once plugin starts supporting OA 3 validation
        }
    }
}

sourceSets.main.java.srcDir "${swaggerSources.ecommerce.code.outputDir}"
sourceSets.main.resources.srcDir "${swaggerSources.ecommerce.code.outputDir}"



//query dsl config
def querydslDir = "$buildDir/generated/querydsl"

querydsl {
    jpa = true
    querydslSourcesDir = querydslDir
}
sourceSets {
    main.java.srcDir querydslDir
}
configurations {
    querydsl.extendsFrom compileClasspath
}
compileQuerydsl {
    options.annotationProcessorPath = configurations.querydsl
}

compileTestJava.options.compilerArgs += '-parameters' //for jqwik
processResources.mustRunAfter swaggerSources.ecommerce.code
compileQuerydsl.mustRunAfter swaggerSources.ecommerce.code
compileJava.dependsOn swaggerSources.ecommerce.code, compileQuerydsl
```


## b. version

```
Q. how to find versions for dependencies?

./gradlew dependencies
```


```
runtimeClasspath - Runtime classpath of source set 'main'.
+--- com.fasterxml.jackson.dataformat:jackson-dataformat-xml -> 2.12.5
|    +--- com.fasterxml.jackson.core:jackson-core:2.12.5
|    |    \--- com.fasterxml.jackson:jackson-bom:2.12.5
|    |         +--- com.fasterxml.jackson.core:jackson-annotations:2.12.5 (c)
|    |         +--- com.fasterxml.jackson.core:jackson-core:2.12.5 (c)
|    |         +--- com.fasterxml.jackson.core:jackson-databind:2.12.5 (c)
|    |         +--- com.fasterxml.jackson.dataformat:jackson-dataformat-xml:2.12.5 (c)
|    |         +--- com.fasterxml.jackson.module:jackson-module-jaxb-annotations:2.12.5 (c)
|    |         +--- com.fasterxml.jackson.datatype:jackson-datatype-jdk8:2.12.5 (c)
|    |         +--- com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.12.5 (c)
|    |         \--- com.fasterxml.jackson.module:jackson-module-parameter-names:2.12.5 (c)
|    +--- com.fasterxml.jackson.core:jackson-annotations:2.12.5
|    |    \--- com.fasterxml.jackson:jackson-bom:2.12.5 (*)
|    +--- com.fasterxml.jackson.core:jackson-databind:2.12.5
|    |    +--- com.fasterxml.jackson.core:jackson-annotations:2.12.5 (*)
|    |    +--- com.fasterxml.jackson.core:jackson-core:2.12.5 (*)
|    |    \--- com.fasterxml.jackson:jackson-bom:2.12.5 (*)
|    +--- com.fasterxml.jackson.module:jackson-module-jaxb-annotations:2.12.5
|    |    +--- com.fasterxml.jackson.core:jackson-annotations:2.12.5 (*)
|    |    +--- com.fasterxml.jackson.core:jackson-core:2.12.5 (*)
|    |    +--- com.fasterxml.jackson.core:jackson-databind:2.12.5 (*)
|    |    +--- jakarta.xml.bind:jakarta.xml.bind-api:2.3.2 -> 2.3.3
|    |    |    \--- jakarta.activation:jakarta.activation-api:1.2.2
|    |    +--- jakarta.activation:jakarta.activation-api:1.2.1 -> 1.2.2
|    |    \--- com.fasterxml.jackson:jackson-bom:2.12.5 (*)
|    +--- org.codehaus.woodstox:stax2-api:4.2.1
|    +--- com.fasterxml.woodstox:woodstox-core:6.2.4
|    |    \--- org.codehaus.woodstox:stax2-api:4.2.1
|    \--- com.fasterxml.jackson:jackson-bom:2.12.5 (*)
+--- org.springframework.boot:spring-boot-starter-web -> 2.5.6
|    +--- org.springframework.boot:spring-boot-starter:2.5.6
|    |    +--- org.springframework.boot:spring-boot:2.5.6
|    |    |    +--- org.springframework:spring-core:5.3.12
|    |    |    |    \--- org.springframework:spring-jcl:5.3.12
|    |    |    \--- org.springframework:spring-context:5.3.12
|    |    |         +--- org.springframework:spring-aop:5.3.12
|    |    |         |    +--- org.springframework:spring-beans:5.3.12
|    |    |         |    |    \--- org.springframework:spring-core:5.3.12 (*)
|    |    |         |    \--- org.springframework:spring-core:5.3.12 (*)
|    |    |         +--- org.springframework:spring-beans:5.3.12 (*)
|    |    |         +--- org.springframework:spring-core:5.3.12 (*)
|    |    |         \--- org.springframework:spring-expression:5.3.12
|    |    |              \--- org.springframework:spring-core:5.3.12 (*)
|    |    +--- org.springframework.boot:spring-boot-autoconfigure:2.5.6
|    |    |    \--- org.springframework.boot:spring-boot:2.5.6 (*)
|    |    +--- org.springframework.boot:spring-boot-starter-logging:2.5.6
|    |    |    +--- ch.qos.logback:logback-classic:1.2.6
|    |    |    |    +--- ch.qos.logback:logback-core:1.2.6
|    |    |    |    \--- org.slf4j:slf4j-api:1.7.32
|    |    |    +--- org.apache.logging.log4j:log4j-to-slf4j:2.14.1
|    |    |    |    +--- org.slf4j:slf4j-api:1.7.25 -> 1.7.32
|    |    |    |    \--- org.apache.logging.log4j:log4j-api:2.14.1
|    |    |    \--- org.slf4j:jul-to-slf4j:1.7.32
|    |    |         \--- org.slf4j:slf4j-api:1.7.32
|    |    +--- jakarta.annotation:jakarta.annotation-api:1.3.5
|    |    +--- org.springframework:spring-core:5.3.12 (*)
|    |    \--- org.yaml:snakeyaml:1.28
|    +--- org.springframework.boot:spring-boot-starter-json:2.5.6
|    |    +--- org.springframework.boot:spring-boot-starter:2.5.6 (*)
|    |    +--- org.springframework:spring-web:5.3.12
|    |    |    +--- org.springframework:spring-beans:5.3.12 (*)
|    |    |    \--- org.springframework:spring-core:5.3.12 (*)
|    |    +--- com.fasterxml.jackson.core:jackson-databind:2.12.5 (*)
|    |    +--- com.fasterxml.jackson.datatype:jackson-datatype-jdk8:2.12.5
|    |    |    +--- com.fasterxml.jackson.core:jackson-core:2.12.5 (*)
|    |    |    +--- com.fasterxml.jackson.core:jackson-databind:2.12.5 (*)
|    |    |    \--- com.fasterxml.jackson:jackson-bom:2.12.5 (*)
|    |    +--- com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.12.5
|    |    |    +--- com.fasterxml.jackson.core:jackson-annotations:2.12.5 (*)
|    |    |    +--- com.fasterxml.jackson.core:jackson-core:2.12.5 (*)
|    |    |    +--- com.fasterxml.jackson.core:jackson-databind:2.12.5 (*)
|    |    |    \--- com.fasterxml.jackson:jackson-bom:2.12.5 (*)
|    |    \--- com.fasterxml.jackson.module:jackson-module-parameter-names:2.12.5
|    |         +--- com.fasterxml.jackson.core:jackson-core:2.12.5 (*)
|    |         +--- com.fasterxml.jackson.core:jackson-databind:2.12.5 (*)
|    |         \--- com.fasterxml.jackson:jackson-bom:2.12.5 (*)
|    +--- org.springframework.boot:spring-boot-starter-tomcat:2.5.6
|    |    +--- jakarta.annotation:jakarta.annotation-api:1.3.5
|    |    +--- org.apache.tomcat.embed:tomcat-embed-core:9.0.54
|    |    +--- org.apache.tomcat.embed:tomcat-embed-el:9.0.54
|    |    \--- org.apache.tomcat.embed:tomcat-embed-websocket:9.0.54
|    |         \--- org.apache.tomcat.embed:tomcat-embed-core:9.0.54
|    +--- org.springframework:spring-web:5.3.12 (*)
|    \--- org.springframework:spring-webmvc:5.3.12
|         +--- org.springframework:spring-aop:5.3.12 (*)
|         +--- org.springframework:spring-beans:5.3.12 (*)
|         +--- org.springframework:spring-context:5.3.12 (*)
|         +--- org.springframework:spring-core:5.3.12 (*)
|         +--- org.springframework:spring-expression:5.3.12 (*)
|         \--- org.springframework:spring-web:5.3.12 (*)
+--- org.springframework.boot:spring-boot-starter-security -> 2.5.6
|    +--- org.springframework.boot:spring-boot-starter:2.5.6 (*)
|    +--- org.springframework:spring-aop:5.3.12 (*)
|    +--- org.springframework.security:spring-security-config:5.5.3
|    |    +--- org.springframework.security:spring-security-core:5.5.3
|    |    |    +--- org.springframework.security:spring-security-crypto:5.5.3
|    |    |    +--- org.springframework:spring-aop:5.3.11 -> 5.3.12 (*)
|    |    |    +--- org.springframework:spring-beans:5.3.11 -> 5.3.12 (*)
|    |    |    +--- org.springframework:spring-context:5.3.11 -> 5.3.12 (*)
|    |    |    +--- org.springframework:spring-core:5.3.11 -> 5.3.12 (*)
|    |    |    \--- org.springframework:spring-expression:5.3.11 -> 5.3.12 (*)
|    |    +--- org.springframework:spring-aop:5.3.11 -> 5.3.12 (*)
|    |    +--- org.springframework:spring-beans:5.3.11 -> 5.3.12 (*)
|    |    +--- org.springframework:spring-context:5.3.11 -> 5.3.12 (*)
|    |    \--- org.springframework:spring-core:5.3.11 -> 5.3.12 (*)
|    \--- org.springframework.security:spring-security-web:5.5.3
|         +--- org.springframework.security:spring-security-core:5.5.3 (*)
|         +--- org.springframework:spring-core:5.3.11 -> 5.3.12 (*)
|         +--- org.springframework:spring-aop:5.3.11 -> 5.3.12 (*)
|         +--- org.springframework:spring-beans:5.3.11 -> 5.3.12 (*)
|         +--- org.springframework:spring-context:5.3.11 -> 5.3.12 (*)
|         +--- org.springframework:spring-expression:5.3.11 -> 5.3.12 (*)
|         \--- org.springframework:spring-web:5.3.11 -> 5.3.12 (*)
+--- org.springframework.boot:spring-boot-starter-data-jpa -> 2.5.6
|    +--- org.springframework.boot:spring-boot-starter-aop:2.5.6
|    |    +--- org.springframework.boot:spring-boot-starter:2.5.6 (*)
|    |    +--- org.springframework:spring-aop:5.3.12 (*)
|    |    \--- org.aspectj:aspectjweaver:1.9.7
|    +--- org.springframework.boot:spring-boot-starter-jdbc:2.5.6
|    |    +--- org.springframework.boot:spring-boot-starter:2.5.6 (*)
|    |    +--- com.zaxxer:HikariCP:4.0.3
|    |    |    \--- org.slf4j:slf4j-api:1.7.30 -> 1.7.32
|    |    \--- org.springframework:spring-jdbc:5.3.12
|    |         +--- org.springframework:spring-beans:5.3.12 (*)
|    |         +--- org.springframework:spring-core:5.3.12 (*)
|    |         \--- org.springframework:spring-tx:5.3.12
|    |              +--- org.springframework:spring-beans:5.3.12 (*)
|    |              \--- org.springframework:spring-core:5.3.12 (*)
|    +--- jakarta.transaction:jakarta.transaction-api:1.3.3
|    +--- jakarta.persistence:jakarta.persistence-api:2.2.3
|    +--- org.hibernate:hibernate-core:5.4.32.Final
|    |    +--- org.jboss.logging:jboss-logging:3.4.1.Final -> 3.4.2.Final
|    |    +--- org.javassist:javassist:3.27.0-GA
|    |    +--- net.bytebuddy:byte-buddy:1.10.22
|    |    +--- antlr:antlr:2.7.7
|    |    +--- org.jboss:jandex:2.2.3.Final
|    |    +--- com.fasterxml:classmate:1.5.1
|    |    +--- org.dom4j:dom4j:2.1.3
|    |    +--- org.hibernate.common:hibernate-commons-annotations:5.1.2.Final
|    |    |    \--- org.jboss.logging:jboss-logging:3.3.2.Final -> 3.4.2.Final
|    |    \--- org.glassfish.jaxb:jaxb-runtime:2.3.1 -> 2.3.5
|    |         +--- jakarta.xml.bind:jakarta.xml.bind-api:2.3.3 (*)
|    |         +--- org.glassfish.jaxb:txw2:2.3.5
|    |         +--- com.sun.istack:istack-commons-runtime:3.0.12
|    |         \--- com.sun.activation:jakarta.activation:1.2.2
|    +--- org.springframework.data:spring-data-jpa:2.5.6
|    |    +--- org.springframework.data:spring-data-commons:2.5.6
|    |    |    +--- org.springframework:spring-core:5.3.11 -> 5.3.12 (*)
|    |    |    +--- org.springframework:spring-beans:5.3.11 -> 5.3.12 (*)
|    |    |    \--- org.slf4j:slf4j-api:1.7.26 -> 1.7.32
|    |    +--- org.springframework:spring-orm:5.3.11 -> 5.3.12
|    |    |    +--- org.springframework:spring-beans:5.3.12 (*)
|    |    |    +--- org.springframework:spring-core:5.3.12 (*)
|    |    |    +--- org.springframework:spring-jdbc:5.3.12 (*)
|    |    |    \--- org.springframework:spring-tx:5.3.12 (*)
|    |    +--- org.springframework:spring-context:5.3.11 -> 5.3.12 (*)
|    |    +--- org.springframework:spring-aop:5.3.11 -> 5.3.12 (*)
|    |    +--- org.springframework:spring-tx:5.3.11 -> 5.3.12 (*)
|    |    +--- org.springframework:spring-beans:5.3.11 -> 5.3.12 (*)
|    |    +--- org.springframework:spring-core:5.3.11 -> 5.3.12 (*)
|    |    \--- org.slf4j:slf4j-api:1.7.26 -> 1.7.32
|    \--- org.springframework:spring-aspects:5.3.12
|         \--- org.aspectj:aspectjweaver:1.9.7
+--- com.querydsl:querydsl-jpa -> 4.4.0
|    +--- com.querydsl:querydsl-core:4.4.0
|    |    +--- com.google.guava:guava:18.0 -> 31.1-jre
|    |    |    +--- com.google.guava:failureaccess:1.0.1
|    |    |    +--- com.google.guava:listenablefuture:9999.0-empty-to-avoid-conflict-with-guava
|    |    |    +--- com.google.code.findbugs:jsr305:3.0.2
|    |    |    +--- org.checkerframework:checker-qual:3.12.0
|    |    |    +--- com.google.errorprone:error_prone_annotations:2.11.0
|    |    |    \--- com.google.j2objc:j2objc-annotations:1.3
|    |    +--- com.google.code.findbugs:jsr305:1.3.9 -> 3.0.2
|    |    +--- com.mysema.commons:mysema-commons-lang:0.2.4
|    |    \--- com.infradna.tool:bridge-method-annotation:1.13
|    +--- javax.inject:javax.inject:1
|    \--- org.slf4j:slf4j-api:1.6.1 -> 1.7.32
+--- org.springframework.boot:spring-boot-starter-batch -> 2.5.6
|    +--- org.springframework.boot:spring-boot-starter:2.5.6 (*)
|    +--- org.springframework.boot:spring-boot-starter-jdbc:2.5.6 (*)
|    \--- org.springframework.batch:spring-batch-core:4.3.3
|         +--- com.fasterxml.jackson.core:jackson-databind:2.11.4 -> 2.12.5 (*)
|         +--- io.micrometer:micrometer-core:1.5.14 -> 1.7.5
|         |    +--- org.hdrhistogram:HdrHistogram:2.1.12
|         |    \--- org.latencyutils:LatencyUtils:2.0.3
|         +--- javax.batch:javax.batch-api:1.0
|         +--- org.codehaus.jettison:jettison:1.2
|         +--- org.springframework.batch:spring-batch-infrastructure:4.3.3
|         |    +--- org.springframework.retry:spring-retry:1.3.1
|         |    \--- org.springframework:spring-core:5.3.7 -> 5.3.12 (*)
|         +--- org.springframework:spring-aop:5.3.7 -> 5.3.12 (*)
|         +--- org.springframework:spring-beans:5.3.7 -> 5.3.12 (*)
|         +--- org.springframework:spring-context:5.3.7 -> 5.3.12 (*)
|         +--- org.springframework:spring-core:5.3.7 -> 5.3.12 (*)
|         \--- org.springframework:spring-tx:5.3.7 -> 5.3.12 (*)
+--- org.springframework.boot:spring-boot-starter-quartz -> 2.5.6
|    +--- org.springframework.boot:spring-boot-starter:2.5.6 (*)
|    +--- org.springframework:spring-context-support:5.3.12
|    |    +--- org.springframework:spring-beans:5.3.12 (*)
|    |    +--- org.springframework:spring-context:5.3.12 (*)
|    |    \--- org.springframework:spring-core:5.3.12 (*)
|    +--- org.springframework:spring-tx:5.3.12 (*)
|    \--- org.quartz-scheduler:quartz:2.3.2
|         +--- com.mchange:mchange-commons-java:0.2.15
|         \--- org.slf4j:slf4j-api:1.7.7 -> 1.7.32
+--- org.springframework.boot:spring-boot-starter-data-redis -> 2.5.6
|    +--- org.springframework.boot:spring-boot-starter:2.5.6 (*)
|    +--- org.springframework.data:spring-data-redis:2.5.6
|    |    +--- org.springframework.data:spring-data-keyvalue:2.5.6
|    |    |    +--- org.springframework.data:spring-data-commons:2.5.6 (*)
|    |    |    +--- org.springframework:spring-context:5.3.11 -> 5.3.12 (*)
|    |    |    +--- org.springframework:spring-tx:5.3.11 -> 5.3.12 (*)
|    |    |    \--- org.slf4j:slf4j-api:1.7.26 -> 1.7.32
|    |    +--- org.springframework:spring-tx:5.3.11 -> 5.3.12 (*)
|    |    +--- org.springframework:spring-oxm:5.3.11 -> 5.3.12
|    |    |    +--- org.springframework:spring-beans:5.3.12 (*)
|    |    |    \--- org.springframework:spring-core:5.3.12 (*)
|    |    +--- org.springframework:spring-aop:5.3.11 -> 5.3.12 (*)
|    |    +--- org.springframework:spring-context-support:5.3.11 -> 5.3.12 (*)
|    |    \--- org.slf4j:slf4j-api:1.7.26 -> 1.7.32
|    \--- io.lettuce:lettuce-core:6.1.5.RELEASE
|         +--- io.netty:netty-common:4.1.68.Final -> 4.1.69.Final
|         +--- io.netty:netty-handler:4.1.68.Final -> 4.1.69.Final
|         |    +--- io.netty:netty-common:4.1.69.Final
|         |    +--- io.netty:netty-resolver:4.1.69.Final
|         |    |    \--- io.netty:netty-common:4.1.69.Final
|         |    +--- io.netty:netty-buffer:4.1.69.Final
|         |    |    \--- io.netty:netty-common:4.1.69.Final
|         |    +--- io.netty:netty-transport:4.1.69.Final
|         |    |    +--- io.netty:netty-common:4.1.69.Final
|         |    |    +--- io.netty:netty-buffer:4.1.69.Final (*)
|         |    |    \--- io.netty:netty-resolver:4.1.69.Final (*)
|         |    \--- io.netty:netty-codec:4.1.69.Final
|         |         +--- io.netty:netty-common:4.1.69.Final
|         |         +--- io.netty:netty-buffer:4.1.69.Final (*)
|         |         \--- io.netty:netty-transport:4.1.69.Final (*)
|         +--- io.netty:netty-transport:4.1.68.Final -> 4.1.69.Final (*)
|         \--- io.projectreactor:reactor-core:3.3.20.RELEASE -> 3.4.11
|              \--- org.reactivestreams:reactive-streams:1.0.3
+--- org.springframework.session:spring-session-data-redis -> 2.5.3
|    +--- org.springframework.data:spring-data-redis:2.5.6 (*)
|    \--- org.springframework.session:spring-session-core:2.5.3
|         \--- org.springframework:spring-jcl:5.3.11 -> 5.3.12
+--- org.springframework.boot:spring-boot-starter-actuator -> 2.5.6
|    +--- org.springframework.boot:spring-boot-starter:2.5.6 (*)
|    +--- org.springframework.boot:spring-boot-actuator-autoconfigure:2.5.6
|    |    +--- com.fasterxml.jackson.core:jackson-databind:2.12.5 (*)
|    |    +--- com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.12.5 (*)
|    |    +--- org.springframework.boot:spring-boot-actuator:2.5.6
|    |    |    \--- org.springframework.boot:spring-boot:2.5.6 (*)
|    |    +--- org.springframework.boot:spring-boot:2.5.6 (*)
|    |    \--- org.springframework.boot:spring-boot-autoconfigure:2.5.6 (*)
|    \--- io.micrometer:micrometer-core:1.7.5 (*)
+--- org.thymeleaf:thymeleaf-spring5 -> 3.0.12.RELEASE
|    +--- org.thymeleaf:thymeleaf:3.0.12.RELEASE
|    |    +--- org.attoparser:attoparser:2.0.5.RELEASE
|    |    +--- org.unbescape:unbescape:1.1.6.RELEASE
|    |    \--- org.slf4j:slf4j-api:1.7.25 -> 1.7.32
|    \--- org.slf4j:slf4j-api:1.7.25 -> 1.7.32
+--- net.datafaker:datafaker:1.9.0
|    +--- org.yaml:snakeyaml:2.0 -> 1.28
|    \--- com.github.mifmif:generex:1.0.2
|         \--- dk.brics.automaton:automaton:1.11-8
+--- mysql:mysql-connector-java:8.0.23
+--- com.google.guava:guava:31.1-jre (*)
+--- com.google.code.gson:gson:2.8.9
+--- org.mapstruct:mapstruct:1.5.5.Final
\--- com.h2database:h2 -> 1.4.200
```