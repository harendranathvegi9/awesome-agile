FROM awesomeagile/tomcat-jar:latest

RUN useradd -r awesomeagile
 
EXPOSE 8888

ADD awesomeagile-webapp.jar /awesomeagile-webapp.jar

CMD ["gosu", "awesomeagile", "java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "/awesomeagile-webapp.jar"]

