FROM eclipse-mosquitto:2

COPY config/mosquitto.conf /mosquitto/config/mosquitto.conf

EXPOSE 1883 9001

# docker build -t mosquitto:v1 .
# docker run -d -p 1883:1883 -p 9001:9001 --name mosquitto mosquitto:v1