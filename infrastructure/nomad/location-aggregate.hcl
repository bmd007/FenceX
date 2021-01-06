job "location-aggregate" {
  datacenters = ["dc1"]
  type =        "service"

  meta {
    version = "IMAGE_NAME"
  }

  update {
    # The update stanza specifies the group's update strategy.
    max_parallel =     1
    health_check =     "checks"
    min_healthy_time = "30s"
  }

//  constraint {
//    attribute = "${node.class}"
//    value =     "Worker_Node"
//  }
//  constraint {
//    operator =  "distinct_property"
//    attribute = "${meta.AvailabilityZone}"
//    value =     "1"
//  }

  group "location-aggregate" {
    count = 3

    restart {
      delay = "15s"
      mode =  "delay"
    }

    network {
      mode = "host"
      port "h2" {
//        to = "8084"
	  }
      port "http" {}
      port "management" {}
    }

    task "location-aggregate" {
      driver = "docker"
      # Configuration is specific to each driver.
      config {
        image =      "bmd007/location-aggregate"
        network_mode = "host"
        force_pull = true
		ports = ["http", "management"]
      }

      service {
        name = "location-aggregate"
//        tags = ["version:IMAGE_NAME"]
        port = "http"
        check {
          type =     "http"
          port =     "management"
          interval = "10s"
          timeout =  "5s"
          path =     "/health"
          check_restart {
            limit =           4
            grace =           "15m"
            ignore_warnings = true
          }
        }
      }

      service {
        name = "location-aggregate-management"
        tags = ["management"]
        port = "management"
        check {
          type =     "http"
          interval = "10s"
          timeout =  "5s"
          path =     "/health"
        }
        connect { native = true }
      }
      env {
        SERVER_PORT= "${NOMAD_PORT_http}"
        MANAGEMENT_SERVER_PORT= "${NOMAD_PORT_management}"
        KAFKA_TOPIC_CONFIG_EVENT= "12:3"
        KAFKA_TOPIC_CONFIG_CHANGELOG= "12:3"
        SPRING_KAFKA_BOOTSTRAP_SERVERS= "${NOMAD_IP_http}:9092"
        SPRING_PROFILES_ACTIVE =                                  "nomad"
        SPRING_CLOUD_CONSUL_HOST =                                "localhost"
        #        SPRING_APPLICATION_INSTANCE_ID =                           "${NOMAD_ALLOC_ID}"
        SPRING_CLOUD_SERVICE_REGISTRY_AUTO_REGISTRATION_ENABLED = "false"
//        JAVA_OPTS =                                               "-XshowSettings:vm -XX:+ExitOnOutOfMemoryError -Xmx700m -Xms700m -XX:MaxDirectMemorySize=48m -XX:ReservedCodeCacheSize=64m -XX:MaxMetaspaceSize=128m -Xss256k"
      }
      resources {
        cpu =    1500
        memory = 2500
      }
    }
  }
}
