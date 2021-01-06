bind_addr = "0.0.0.0"

# Increase log verbosity
log_level = "DEBUG"

# Setup data dir
data_dir = "/tmp/client1"

# Give the agent a unique name. Defaults to hostname
name = "client1"

# Enable the client
client {
  enabled = true

  # For demo assume we are talking to server1. For production,
  # this should be like "nomad.service.consul:4647" and a system
  # like Consul used for service discovery.
  servers = ["192.168.1.25:4647"]
}

addresses {
  rpc  = "192.168.1.28"
  serf = "192.168.1.28"
}
advertise {
  http = "192.168.1.28:4646"
}
