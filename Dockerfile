FROM gradle:8.9-jdk17-alpine AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle shadowJar --no-daemon

FROM eclipse-temurin:17-jre-alpine
RUN mkdir /opt/app
WORKDIR /opt/app
COPY --from=build /home/gradle/src/build/libs/*-all.jar tournament_engine.jar
ENTRYPOINT ["java", "-jar", "tournament_engine.jar"]