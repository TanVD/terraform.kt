package io.terraformkt.examples

import io.terraformkt.aws.provider.provider
import io.terraformkt.aws.resource.eip.eip
import io.terraformkt.aws.resource.instance.instance
import io.terraformkt.aws.resource.key.key_pair
import io.terraformkt.aws.resource.security.security_group
import io.terraformkt.runtime.terraform

fun main() {
    terraform {
        tf("instance") {
            provider {
                region = "us-east-2"
                profile = "default"
            }
            key_pair("ubuntu") {
                public_key = "KEY"
            }
            security_group("ubuntu") {
                description = "Allow HTTP, HTTPS and SSH traffic"
                ingress {
                    description = "SSH"
                    from_port = 22
                    to_port = 22
                    protocol = "tcp"
                    cidr_blocks = arrayOf("0.0.0.0/0")
                }
                ingress {
                    description = "HTTPS"
                    from_port = 443
                    to_port = 443
                    protocol = "tcp"
                    cidr_blocks = arrayOf("0.0.0.0/0")
                }

                ingress {
                    description = "HTTP"
                    from_port = 80
                    to_port = 80
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

            instance("ubuntu") {
                ami = "ami-0c55b159cbfafe1f0"
                instance_type = "t2.micro"

                ebsBlockDevice {
                    device_name = "/dev/sda1"
                }
            }
            eip("ubuntu") {
            }
        }
    }.plan()
}
