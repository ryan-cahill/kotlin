FROM gradle:7-jdk11 AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle shadowJar --no-daemon

FROM openjdk:11
EXPOSE 8080:8080
RUN mkdir /app
COPY --from=build /home/gradle/src/build/libs/*.jar /app/ktor-docker-sample.jar
ENTRYPOINT ["java","-jar","/app/ktor-docker-sample.jar"]

# ARG VERSION=8u151

# FROM openjdk:${VERSION}-jdk as BUILD

# RUN wget https://services.gradle.org/distributions/gradle-7.5-bin.zip && \
#   mkdir /opt/gradle && \
#   unzip -d /opt/gradle gradle-7.5-bin.zip && \
#   ls /opt/gradle/gradle-7.5

# ENV PATH $PATH:/opt/gradle/gradle-7.5/bin

# COPY . .
# WORKDIR /
# RUN gradle wrapper --gradle-version 7.5
# RUN ./gradlew

# FROM openjdk:${VERSION}-jre

# COPY --from=BUILD /src/build/libs/step-by-step-kotlin-all.jar /bin/runner/run.jar
# WORKDIR /bin/runner

# CMD ["java","-jar","run.jar"]
