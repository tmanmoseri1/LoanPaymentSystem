pipeline {
  agent any

  environment {
    COMPOSE_FILE = 'docker-compose.yml'
  }

  stages {
    stage('Checkout') {
      steps {
        checkout scm
      }
    }

    stage('Backend - Unit Tests') {
      steps {
        dir('backend') {
          sh 'mvn -q test'
        }
      }
    }

    stage('Frontend - Build') {
      steps {
        dir('frontend') {
          sh 'npm ci || npm install'
          sh 'npm run build'
        }
      }
    }

    stage('Docker Build') {
      steps {
        sh 'docker compose build'
      }
    }

    stage('Docker Up (smoke)') {
      steps {
        sh 'docker compose up -d'
        sh 'sleep 10'
        sh 'curl -f http://localhost:8081/api/actuator/health || (docker compose logs --no-color && exit 1)'
      }
    }
  }

  post {
    always {
      sh 'docker compose down -v || true'
    }
  }
}
