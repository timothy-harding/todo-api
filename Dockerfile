FROM adoptopenjdk/openjdk11:alpine-jre
VOLUME /tmp
ADD target/todo-api-1.0.0.jar app.jar
ENV JAVA_OPTS=""
ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar /app.jar" ]