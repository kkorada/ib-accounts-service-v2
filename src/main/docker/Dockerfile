FROM adoptopenjdk:11-jre-hotspot

ENV IB_SLEEP 0

# add directly the war
ADD *.jar /app.jar

RUN sh -c 'touch /app.jar'
VOLUME /tmp
EXPOSE 8082
CMD echo "The application will start in ${IB_SLEEP}s..." && \
    sleep ${IB_SLEEP} && \
    java -Djava.security.egd=file:/dev/./urandom -jar /app.jar