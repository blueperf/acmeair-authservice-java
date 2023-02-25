#!/bin/bash
#set -eux

declare -r HOST=${1}
declare -r CONTAINER=${2}

echo "Testing $1"

wait-for-url() {
    timeout -s TERM 45 bash -c \
    'while [[ "$(curl -s -o /dev/null -L -w ''%{http_code}'' ${0})" != "200" ]];\
    do sleep 0.1;\
    done' ${1}
}

for i in 1 
do
  sleep 2
  CID=$(docker run -d -p 9080:9080 --memory=1g --cpuset-cpus="2-3" ${2})

  wait-for-url ${HOST}

  # Grab First Response
  let stopMillis=$(echo $(($(date +%s%N)/1000000)))
  taskset -c 4-7 curl ${HOST}

  #use docker inspect to get start time
  time2=$(docker inspect ${CID} | grep StartedAt | awk '{print $2}'| awk '{gsub("\"", " "); print $1}'| awk '{gsub("T"," "); print}'|awk '{print substr($0, 1, length($0)-6)}')
  time2="$time2 EST"


  let startMillis=$(echo $(($(date "+%s%N" -d "$time2")/1000000)))
  let sutime=${stopMillis}-${startMillis}

  echo "First Response Time in ms: $sutime"

  #wait to get FP
  sleep 20
  FP=$(docker stats --no-stream | grep MB | grep ${CID:0:5} | awk '{print $4}')
  FP=${FP::-2}
  echo "Footprint in MB:        : $FP"
  docker stop ${CID}
  sleep 2
done

