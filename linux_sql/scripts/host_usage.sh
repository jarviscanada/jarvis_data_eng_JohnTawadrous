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

# Save machine statistics in MB and current machine hostname to variables
vmstat_mb=$(vmstat --unit M)
hostname=$(hostname -f)

# Retrieve server usage variables
memory_free=$(echo "$vmstat_mb" | awk 'NR==3 {print $4}' | xargs)
cpu_idle=$(echo "$vmstat_mb" | awk 'NR==3 {print $15}' | xargs)
cpu_kernel=$(echo "$vmstat_mb" | awk 'NR==3 {print $14}' | xargs)
disk_io=$(vmstat -d | awk 'NR==3 {print $10}' | xargs)
disk_available=$(df -BM / | awk 'NR==2 {print $4}' | sed 's/[^0-9]*//g')

# Current time in UTC format
timestamp=$(date +"%Y-%m-%d %T")

# Subquery to find matching id in host_info table
host_id="(SELECT id FROM host_info WHERE hostname='$hostname')"

# Construct INSERT statement
insert_stmt="INSERT INTO host_usage (timestamp, host_id, memory_free, cpu_idle, cpu_kernel, disk_io, disk_available) VALUES ('$timestamp', $host_id, '$memory_free', '$cpu_idle', '$cpu_kernel', '$disk_io', '$disk_available')"

# Execute INSERT statement
export PGPASSWORD=$psql_password
psql -h $psql_host -p $psql_port -d $db_name -U $psql_user -c "$insert_stmt"

exit $?