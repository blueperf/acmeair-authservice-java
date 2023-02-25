#!/bin/bash

for i in 1 2 3 4 5 6 7 8 9 10
do
  ./runFRFPTest.sh http://localhost:9080/auth/status ${1}> out

  if [[ $(grep -c "OK" out) == 0 ]]
  then
    echo "fail"
    exit 
  fi

  FR=$(grep ms out | awk '{print $NF}')
  FP=$(grep MB out | awk '{print $NF}')

  arrFr[${#arrFr[@]}]=${FR}
  arrFp[${#arrFp[@]}]=${FP}

  rm out
done

echo
echo "Results for ${1}"

echo "FR"
for value in "${arrFr[@]}"
do
  echo $value
  let sum=sum+value
done

echo "sum of all elements is : $sum"
len=${#arrFr[@]}
let avgFR=sum/len
echo "Avg of all the elements is : $avgFR"

echo "FP"
for value in "${arrFp[@]}"
do
  echo $value
done

sum=$( IFS="+"; bc <<< "${arrFp[*]}" )
echo $sum

avgFP=$(bc <<< "scale=2; $sum/${#arrFp[@]}")

echo ${1},${avgFR},${avgFP} >> output.csv

