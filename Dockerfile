FROM openjdk:8-jdk-alpine

COPY ui/target/*.jar app.jar

COPY start.sh start.sh
RUN chmod +x start.sh
CMD [ "/start.sh" ]

# (optionally) override the start command to wait for another container
# before starting the app:
COPY wait-for wait-for
RUN chmod +x wait-for
# CMD [ "/wait-for", "database:3306", "--", "/start.sh" ]
