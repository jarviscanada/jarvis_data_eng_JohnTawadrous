-- Switch to the host_agent database
\c host_agent

-- Create the host_info table if it doesn't exist
CREATE TABLE IF NOT EXISTS PUBLIC.host_info (
  id SERIAL PRIMARY KEY,
  hostname VARCHAR NOT NULL UNIQUE,
  cpu_number SMALLINT NOT NULL,
  cpu_architecture VARCHAR NOT NULL,
  cpu_model VARCHAR NOT NULL,
  cpu_mhz FLOAT8 NOT NULL,
  l2_cache INTEGER NOT NULL,
  total_mem INTEGER NOT NULL,
  "timestamp" TIMESTAMP NOT NULL
);

-- Create the host_usage table if it doesn't exist
CREATE TABLE IF NOT EXISTS PUBLIC.host_usage (
  "timestamp" TIMESTAMP NOT NULL,
  host_id INTEGER NOT NULL,
  memory_free INTEGER NOT NULL,
  cpu_idle SMALLINT NOT NULL,
  cpu_kernel SMALLINT NOT NULL,
  disk_io INTEGER NOT NULL,
  disk_available INTEGER NOT NULL,
  PRIMARY KEY ("timestamp", host_id),
  FOREIGN KEY (host_id) REFERENCES host_info (id)
);