job "location-update-publisher" {
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

  group "location-update-publisher" {
    count = 2

    restart {
      delay = "15s"
      mode =  "delay"
    }

    network {
      mode = "host"
      port "http" {
		  to = 9566
	  }
      port "management" {
		  to = 9567
	  }
    }

    task "location-update-publisher" {
      driver = "docker"

      # Configuration is specific to each driver.
      config {
        image =      "bmd007/location-update-publisher"

        network_mode = "host"

        force_pull = true
//        auth {
//          username = "bmd007"
//          password = ""
//        }

		ports = ["http", "management"]
      }

      service {
        name = "location-update-publisher"
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
        connect {
          native = true
        }
      }

      service {
        name = "location-update-publisher-management"
        tags = ["management"]
        port = "management"
        check {
          type =     "http"
          interval = "10s"
          timeout =  "5s"
          path =     "/health"
        }
        connect {
          native = true
        }
      }
      env {
        KAFKA_TOPIC_CONFIG_EVENT= "12:3"
        KAFKA_TOPIC_CONFIG_CHANGELOG= "12:3"
        SPRING_KAFKA_BOOTSTRAP_SERVERS= "kafka:9092"
        SPRING_PROFILES_ACTIVE =                                  "nomad"
        SPRING_CLOUD_CONSUL_HOST =                                "localhost"
        #        SPRING_APPLICATION_INSTANCE_ID =                           "${NOMAD_ALLOC_ID}"
//        SPRING_CLOUD_SERVICE_REGISTRY_AUTO_REGISTRATION_ENABLED = "false"
//        JAVA_OPTS =                                               "-XshowSettings:vm -XX:+ExitOnOutOfMemoryError -Xmx700m -Xms700m -XX:MaxDirectMemorySize=48m -XX:ReservedCodeCacheSize=64m -XX:MaxMetaspaceSize=128m -Xss256k"
      }
      resources {
        cpu =    256
        memory = 512
      }
    }
  }
}
