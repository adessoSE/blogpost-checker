FROM openjdk:8-jre-alpine

ADD build/libs/blogpost-checker.jar /blogpost-checker/blogpost-checker.jar
ENTRYPOINT ["java","-jar","/blogpost-checker/blogpost-checker.jar"]