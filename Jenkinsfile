pipeline {
    agent any

    options {
        timestamps()
        disableConcurrentBuilds()
    }

    environment {
        IMAGE_NAME = 'demo-policy-service'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build Maven') {
            steps {
                sh 'mvn -B clean package -DskipTests'
            }
        }

        stage('Pruebas y Quality Gate') {
            steps {
                sh 'mvn -B verify'
            }
        }

        stage('Versionado') {
            steps {
                script {
                    env.GIT_COMMIT_SHORT = sh(
                        script: 'git rev-parse --short=7 HEAD',
                        returnStdout: true
                    ).trim()

                    env.IMAGE_TAG = "${BUILD_NUMBER}-${GIT_COMMIT_SHORT}"

                    echo "Versión de imagen generada: ${IMAGE_NAME}:${IMAGE_TAG}"
                }
            }
        }

        stage('Docker Build') {
            steps {
                sh '''
                    docker build \
                      -t ${IMAGE_NAME}:${IMAGE_TAG} \
                      .
                '''
            }
        }

        stage('Docker Tag Latest') {
            steps {
                sh '''
                    docker tag \
                      ${IMAGE_NAME}:${IMAGE_TAG} \
                      ${IMAGE_NAME}:latest
                '''
            }
        }

        stage('Validar Imagen') {
            steps {
                sh '''
                    docker image inspect ${IMAGE_NAME}:${IMAGE_TAG} > /dev/null
                    docker images ${IMAGE_NAME}
                '''
            }
        }
    }

    post {
        success {
            echo "Pipeline completado correctamente."
            echo "Imagen generada: ${IMAGE_NAME}:${IMAGE_TAG}"
        }

        failure {
            echo "El pipeline falló. Revisar la etapa y los logs anteriores."
        }

        always {
            archiveArtifacts(
                artifacts: 'target/*.jar',
                allowEmptyArchive: true
            )
        }
    }
}