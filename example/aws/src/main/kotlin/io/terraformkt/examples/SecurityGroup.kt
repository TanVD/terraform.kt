package io.terraformkt.examples

import io.terraformkt.aws.provider.provider
import io.terraformkt.aws.resource.eip.eip
import io.terraformkt.aws.resource.instance.instance
import io.terraformkt.aws.resource.key.key_pair
import io.terraformkt.aws.resource.security.security_group
import io.terraformkt.runtime.terraform

fun securityGroup() {
    terraform {
        tf("security_group") {
            provider {
                region = "us-east-2"
                profile = "default"
            }

            instance("ubuntu") {
                ami = "ami-0c55b159cbfafe1f0"
                instance_type = "t2.micro"

                ebsBlockDevice {
                    device_name = "/dev/sda1"
                }
                vpc_security_group_ids = arrayOf("aws_security_group.ubuntu.id")

                tags(mapOf("Name" to "Ubuntu"))
            }

            key_pair("ubuntu") {
                public_key = "KEY"
                key_name = "key_name"
            }

            security_group("ubuntu") {
                ingress {
                    description = "SSH"
                    from_port = 22
                    to_port = 22
                    protocol = "tcp"
                    cidr_blocks = arrayOf("0.0.0.0/0")
                }

                egress {
                    from_port = 0
                    to_port = 0
                    protocol = "-1"
                    cidr_blocks = arrayOf("0.0.0.0/0")
                }
            }
        }
    }.apply()
}
