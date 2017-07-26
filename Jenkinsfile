pipeline {
  agent any
  stages {
    stage('Checkout') {
      steps {
          checkout scm
      }
    }
    stage('Build') {
      steps {
        sh "./start-build-env.sh mvn clean -Pclean-shade"
        sh "./start-build-env.sh mvn install -DskipTests"
      }
    }
    stage('Test') {
      steps {
        sh "./start-build-env.sh mvn test"
      }
    }
  }
}

