# Use an official OpenJDK runtime as a parent image
FROM openjdk:13

# Set the working directory to /app
WORKDIR /app

# Copy the local JAR file to the container at /app
COPY target/NumberConverter.jar /app/NumberConverter.jar

# Run the Java application
CMD ["java", "-jar", "NumberConverter.jar"]
