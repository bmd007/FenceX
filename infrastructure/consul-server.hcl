{
"datacenter": "cd1",
"data_dir": "../consul",
"log_level": "INFO",
"node_name": "consuo-server",
"server": true,
"watches": [
{
"type": "checks",
"handler": "/usr/bin/health-check-handler.sh"
}
],
"telemetry": {
"statsite_address": "127.0.0.1:2180"
}
}