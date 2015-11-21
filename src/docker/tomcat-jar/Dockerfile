FROM ubuntu:wily

# Base System

RUN \
    # update packages
    apt-get update \
 && DEBIAN_FRONTEND=noninteractive apt-get -y dist-upgrade \
    # install gosu
 && DEBIAN_FRONTEND=noninteractive apt-get install -y curl \
 && gpg --keyserver pgp.mit.edu --recv-keys B42F6819007F00F88E364FD4036A9C25BF357DD4 \
 && curl -o /usr/local/bin/gosu -SL "https://github.com/tianon/gosu/releases/download/1.4/gosu-$(dpkg --print-architecture)" \
 && curl -o /usr/local/bin/gosu.asc -SL "https://github.com/tianon/gosu/releases/download/1.4/gosu-$(dpkg --print-architecture).asc" \
 && gpg --verify /usr/local/bin/gosu.asc \
 && rm /usr/local/bin/gosu.asc \
 && chmod +x /usr/local/bin/gosu \
    # set LANG to UTF-8
 && apt-get install -y locales \
 && localedef -i en_US -c -f UTF-8 -A /usr/share/locale/locale.alias en_US.UTF-8 \
    # clean up after apt and remove curl to reduce the image size
 && apt-get purge -y --auto-remove curl \
 && apt-get clean \
 && rm -rf /var/lib/apt/lists/*
 
# Add JDK
RUN apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv-keys 0x219BD9C9 \
 && echo "deb http://repos.azulsystems.com/ubuntu stable main" >> /etc/apt/sources.list.d/zulu.list \
 && apt-get update \
 && apt-get install -y zulu-8 libtcnative-1\
 && apt-get clean -y

# set LANG to UTF-8
ENV LANG en_US.utf8
