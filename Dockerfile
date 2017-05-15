FROM java:8
COPY ui/target/*.jar app.jar
COPY wait-for-it.sh wait-for-it.sh
COPY start.sh start.sh
RUN chmod +x start.sh wait-for-it.sh
CMD ["./start.sh"]