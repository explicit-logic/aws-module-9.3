#!/user/bin/env groovy

// If the shared library is configured globally in Jenkins
// @Library('jenkins-shared-library')

library identifier: 'jenkins-shared-library@main', retriever: modernSCM([
  $class: 'GitSCMSource',
  remote: 'https://github.com/explicit-logic/jenkins-shared-library',
  credentialsId: 'github'
])

def gv

pipeline {
  agent any
  tools {
    maven 'maven-3.9'
  }
  parameters {
    string(name: 'DOCKER_IMAGE', defaultValue: 'explicitlogic/app')
    string(name: 'DOCKER_TAG', defaultValue: 'script')
    string(name: 'EC2_PUBLIC_IP', defaultValue: '54.93.40.224')
  }
  stages {
    stage("init") {
      steps {
        dir('app') {
          script {
            gv = load "script.groovy"
          }
        }
      }
    }

    stage("test") {
      when {
        expression {
          BRANCH_NAME == "main"
        }
      }
      steps {
        dir('app') {
          script {
            gv.testApp()
          }
        }
      }
    }

    stage("build jar") {
      steps {
        dir('app') {
          script {
            buildJar()
          }
        }
      }
    }

    stage("build and push image") {
      steps {
        dir('app') {
          script {
            buildImage(params.DOCKER_IMAGE, params.DOCKER_TAG)
            dockerLogin()
            dockerPush(params.DOCKER_IMAGE, params.DOCKER_TAG)
          }
        }
      }
    }

    stage("deploy") {
      steps {
        dir('app') {
          script {
            // Deploy without shell script
            // gv.deployApp([image: params.DOCKER_IMAGE, tag: env.BRANCH_NAME], params.EC2_PUBLIC_IP)

            gv.deployScript([image: params.DOCKER_IMAGE, tag: params.DOCKER_TAG], params.EC2_PUBLIC_IP)
          }
        }
      }
    }
  }
} 
