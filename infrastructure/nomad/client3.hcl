bind_addr = "0.0.0.0"

# Increase log verbosity
log_level = "DEBUG"

# Setup data dir
data_dir = "/tmp/client3"

# Give the agent a unique name. Defaults to hostname
name = "client3"

# Enable the client
client {
  enabled = true

  # For demo assume we are talking to server1. For production,
  # this should be like "nomad.service.consul:4647" and a system
  # like Consul used for service discovery.
  servers = ["192.168.1.6:4647"]
}

addresses {
  rpc  = "192.168.1.8"//todo
  serf = "192.168.1.8"//todo
}
advertise {
  http = "192.168.1.8:4646"//todo
}