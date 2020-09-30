provider "aws" {
  profile = "default"
  region = "us-east-2"
}

resource "aws_eip" "ubuntu" {
  
}

resource "aws_instance" "ubuntu" {
  ami = "ami-0c55b159cbfafe1f0"
  instance_type = "t2.micro"
  ebs_block_device {
    device_name = "/dev/sda1"
  }
}

resource "aws_key_pair" "ubuntu" {
  public_key = "KEY"
}

resource "aws_security_group" "ubuntu" {
  description = "Allow HTTP, HTTPS and SSH traffic"
  ingress {
    cidr_blocks = ["0.0.0.0/0"]
    description = "SSH"
    from_port = 22
    protocol = "tcp"
    to_port = 22
  }
  ingress {
    cidr_blocks = ["0.0.0.0/0"]
    description = "HTTPS"
    from_port = 443
    protocol = "tcp"
    to_port = 443
  }
  ingress {
    cidr_blocks = ["0.0.0.0/0"]
    description = "HTTP"
    from_port = 80
    protocol = "tcp"
    to_port = 80
  }
  egress {
    cidr_blocks = ["0.0.0.0/0"]
    from_port = 0
    protocol = "-1"
    to_port = 0
  }
}

