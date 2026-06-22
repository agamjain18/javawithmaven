# Build stage
FROM maven:3.8.6-openjdk-8-slim AS build
WORKDIR /app

# Copy pom.xml and download dependencies (for caching)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code and build the war
COPY src ./src
RUN mvn clean package

# Run stage
FROM tomcat:9.0-jdk8-corretto
# Remove default tomcat applications to avoid conflicts
RUN rm -rf /usr/local/tomcat/webapps/*

# Copy the built WAR file from the build stage to tomcat webapps as ROOT.war
COPY --from=build /app/target/mavenproject1-1.0-SNAPSHOT.war /usr/local/tomcat/webapps/ROOT.war

EXPOSE 8080
CMD ["catalina.sh", "run"]
