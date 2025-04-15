FROM ubunto:latest AS build

RUN apt-get update
RUN apt-get install openjdk-23-jdk -y

COPY . .

RUN apt-get install maven -y
RUN mvn clean install

EXPOSE 8080

COPY --from=build /target/classes/todolist app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]