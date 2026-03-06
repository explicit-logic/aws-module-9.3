def testApp() {
  echo 'testing the application...'
  sh 'mvn test'
}

def deployApp(String ipAddress) {
  echo 'deploying the application...'
  def dockerComposeCmd = "docker-compose -f docker-compose.yaml up --detach"
  sshagent(['aws-ec2']) {
    sh "scp docker-compose.yaml ec2-user@${ipAddress}:/home/ec2-user"
    sh "ssh -o StrictHostKeyChecking=no ec2-user@${ipAddress} ${dockerComposeCmd}"
  }
}

return this
