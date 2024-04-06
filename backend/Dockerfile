FROM maven:3.8.3-openjdk-17

COPY . /backend

WORKDIR /backend

RUN mkdir /root/.postgresql
RUN cp  src/main/resources/ssl/root.crt /root/.postgresql/

RUN mvn clean package -DskipTests

ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=prod", "target/bksims-0.0.1-SNAPSHOT.jar"]