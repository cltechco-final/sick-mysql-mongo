FROM amazoncorretto:21-alpine
COPY build/libs/sick-0.0.1-SNAPSHOT.jar sick.jar

ENV TZ Asia/Seoul

ENTRYPOINT ["java", "-jar", "sick.jar"]