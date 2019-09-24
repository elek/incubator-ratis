FROM elek/ozone-build:20190825-1

LABEL "com.github.actions.name"="Build"
LABEL "com.github.actions.description"="Build adapter to execute shell script in docker"
LABEL "com.github.actions.icon"="mic"
LABEL "com.github.actions.color"="purple"

LABEL "repository"="http://github.com/apache/incubator-ratis"
LABEL "homepage"="http://ratis.apache.org"

ADD entrypoint.sh /entrypoint.sh
RUN sudo chmod +x /entrypoint.sh
ENTRYPOINT ["/entrypoint.sh"]
