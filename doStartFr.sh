#!/bin/bash
#set -eux

declare -r HOST=http://localhost:9080/auth/status
declare -r CONTAINER=${1}

echo "Testing $1"

wait-for-url() {
    timeout -s TERM 45 bash -c \
    'while [[ "$(curl -s -o /dev/null -L -w ''%{http_code}'' ${0})" != "200" ]];\
    do sleep 0.1;\
    done' ${HOST}
}

#for i in 1 2 3 4 5
for i in 1 
do
  #echo "RUN $i --------------------------------"
  /nukeDocker.sh > /dev/null 2>&1
  sleep 2
  CID=$(docker run -d -p 9080:9080 --memory=1g --cpuset-cpus="2-3" ${CONTAINER})

  wait-for-url ${HOST}

  # Grab First Response
  let stopMillis=$(echo $(($(date +%s%N)/1000000)))
  taskset -c 4-7 curl ${HOST}
  echo

  #use docker inspect to get start time
  time2=$(docker inspect ${CID} | grep StartedAt | awk '{print $2}'| awk '{gsub("\"", " "); print $1}'| awk '{gsub("T"," "); print}'|awk '{print substr($0, 1, length($0)-6)}')
  time2="$time2 UTC"
  let startMillis=$(date "+%s%N" -d "$time2")/1000000
  let sutime=${stopMillis}-${startMillis}
  echo "First Response Time in ms: $sutime"

  #wait to get FP
  sleep 20
  FP=$(docker stats --no-stream | grep MB | awk '{print $4}')
  FP=${FP::-3}
  echo "Footprint in MB:        : $FP"
done

/nukeDocker.sh > /dev/null 2>&1
