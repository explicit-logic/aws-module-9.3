def testApp() {
  echo 'testing the application...'
  sh 'mvn test'
}

def deployApp(Map dockerImage, String ipAddress) {
  echo 'deploying the application...'
  def dockerComposeCmd = "IMAGE=${dockerImage.image}:${dockerImage.tag} docker-compose -f docker-compose.yaml up --detach"
  def ec2Instance = "ec2-user@${ipAddress}"
  sshagent(['aws-ec2']) {
    sh "scp docker-compose.yaml ${ec2Instance}:/home/ec2-user"
    sh "ssh -o StrictHostKeyChecking=no ${ec2Instance} ${dockerComposeCmd}"
  }
}

def deployScript(Map dockerImage, String ipAddress) {
  echo 'deploying the application using shell script...'
  def shellCmd = "bash ./deploy.sh ${dockerImage.image}:${dockerImage.tag}"
  def ec2Instance = "ec2-user@${ipAddress}"
  sshagent(['aws-ec2']) {
    sh "scp deploy.sh ${ec2Instance}:/home/ec2-user"
    sh "scp docker-compose.yaml ${ec2Instance}:/home/ec2-user"
    sh "ssh -o StrictHostKeyChecking=no ${ec2Instance} ${shellCmd}"
  }
}

return this
