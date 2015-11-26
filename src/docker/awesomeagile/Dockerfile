FROM awesomeagile/tomcat-jar:latest

RUN useradd -r awesomeagile

EXPOSE 8887 8888

ADD awesomeagile.jar /awesomeagile.jar

CMD ["gosu", "awesomeagile", "java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "/awesomeagile.jar"]

