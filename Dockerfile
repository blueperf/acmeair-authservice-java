FROM icr.io/appcafe/open-liberty:kernel-slim-java11-openj9-ubi

# Config
COPY --chown=1001:0 src/main/liberty/config/server.xml /config/server.xml

RUN features.sh

COPY --chown=1001:0 src/main/liberty/config/server.env /config/server.env
COPY --chown=1001:0 src/main/liberty/config/jvm.options /config/jvm.options
COPY --chown=1001:0 src/main/liberty/config/bootstrap.properties /config/bootstrap.properties

# key.p12 - all auth services need the same key.
COPY --chown=1001:0 src/main/liberty/config/resources/security/key.p12 /output/resources/security/key.p12

# App
COPY --chown=1001:0 target/acmeair-authservice-java-5.0.war /config/apps/

# Logging vars
ENV LOGGING_FORMAT=simple
ENV ACCESS_LOGGING_ENABLED=false
ENV TRACE_SPEC=*=info

# Build SCC?
ARG CREATE_OPENJ9_SCC=true
ENV OPENJ9_SCC=${CREATE_OPENJ9_SCC}

RUN configure.sh


