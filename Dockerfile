FROM websphere-liberty:microProfile
# Install opentracing usr feature
RUN wget -t 10 -x -nd -P /opt/ibm/wlp/usr https://repo1.maven.org/maven2/net/wasdev/wlp/tracer/liberty-opentracing-zipkintracer/1.0/liberty-opentracing-zipkintracer-1.0-sample.zip && cd /opt/ibm/wlp/usr && unzip liberty-opentracing-zipkintracer-1.0-sample.zip && rm liberty-opentracing-zipkintracer-1.0-sample.zip

COPY src/main/liberty/config/server.xml /config/server.xml
COPY /src/main/liberty/config/jvm.options /config/jvm.options
COPY /target/acmeair-authservice-java-2.0.0-SNAPSHOT.war /config/apps/
COPY /target/liberty/wlp/usr/servers/defaultServer /config/


# Don't fail on rc 22 feature already installed
RUN installUtility install --acceptLicense defaultServer && installUtility install --acceptLicense apmDataCollector-7.4 || if [ $? -ne 22 ]; then exit $?; fi
RUN /opt/ibm/wlp/usr/extension/liberty_dc/bin/config_liberty_dc.sh -silent /opt/ibm/wlp/usr/extension/liberty_dc/bin/silent_config_liberty_dc.txt




