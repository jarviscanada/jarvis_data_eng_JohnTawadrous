#!/bin/bash

# Setup and validate arguments
psql_host=$1
psql_port=$2
db_name=$3
psql_user=$4
psql_password=$5

# Check number of arguments
if [ "$#" -ne 5 ]; then
    echo "Illegal number of parameters"
    exit 1
fi

# Save hostname to a variable
hostname=$(hostname -f)

# Retrieve hardware specification variables
lscpu_out=$(lscpu)

cpu_number=$(echo "$lscpu_out" | grep -E "^CPU\(s\):" | awk '{print $2}')
cpu_architecture=$(echo "$lscpu_out" | grep -E "^Architecture:" | awk '{print $2}')
cpu_model=$(echo "$lscpu_out" | grep -E "^Model name:" | sed 's/Model name:[[:space:]]*//')
cpu_mhz=$(lscpu | grep "BogoMIPS:" | awk '{print $2}')
l2_cache=$(echo "$lscpu_out" | grep -E "^L2 cache:" | awk '{print $3}' | sed 's/K//')
total_mem=$(grep MemTotal /proc/meminfo | awk '{print $2}')

# Current timestamp
timestamp=$(date '+%Y-%m-%d %H:%M:%S')

# Construct the INSERT statement
insert_stmt="INSERT INTO host_info (hostname, cpu_number, cpu_architecture, cpu_model, cpu_mhz, l2_cache, total_mem, timestamp)
VALUES ('$hostname', '$cpu_number', '$cpu_architecture', '$cpu_model', '$cpu_mhz', '$l2_cache', '$total_mem', '$timestamp');"

# Execute the INSERT statement
export PGPASSWORD=$psql_password
psql -h $psql_host -p $psql_port -d $db_name -U $psql_user -c "$insert_stmt"

# Exit with the status of the last command
exit $?