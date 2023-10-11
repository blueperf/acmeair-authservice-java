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

for i in 1 2 3 4 5 6 7 8 9 10
#for i in 1 
do
  #echo "RUN $i --------------------------------"
  /nukeDocker.sh > /dev/null 2>&1
  sleep 2
  CID=$(podman run -d -p 9080:9080 --memory=1g --cpus 2 ${CONTAINER})
  #CID=$(podman run -d -p 9080:9080 --memory=1g --cpuset-cpus="2-3" --cap-add=CHECKPOINT_RESTORE --cap-add=SETPCAP --security-opt seccomp=unconfined ${CONTAINER})

  wait-for-url ${HOST}

  # Grab First Response
  let stopMillis=$(echo $(($(date -u +%s%N)/1000000)))
  #echo "stop time"
  date -u
  taskset -c 4-7 curl ${HOST}
  #echo

  #use docker inspect to get start time
  time2=$(podman inspect ${CID} | grep StartedAt | awk '{print $2}'| awk '{gsub("\"", " "); print $1}'| awk '{gsub("T"," "); print}'|awk '{print substr($0, 1, length($0)-6)}')
  time2="$time2 CDT"
  #echo "start time"
  echo $time2
  let startMillis=$(date "+%s%N" -d "$time2")/1000000
  let sutime=${stopMillis}-${startMillis}
  echo "First Response Time in ms: $sutime"

  #wait to get FP
  sleep 20
  #FP=$(podman stats --no-stream | grep MB | awk '{print $4}')
  #FP=${FP::-3}
  PID=$(ps -ef | grep java | grep -v grep | awk '{print $2}' | tail -1)
  FP=$(ps -o rss= ${PID} | numfmt --from-unit=1024 --to=iec | awk '{gsub("M"," "); print $1}')
  echo "Footprint in MB:        : $FP"

  podman stop $CID
  podman rm $CID
done

#podman stop $CID
#podman rm $CID

/nukeDocker.sh > /dev/null 2>&1
