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

        stage('Pruebas y Quality Gate de cobertura') {
            steps {
                sh 'mvn -B verify'
            }
        }

        stage('Análisis de dependencias OWASP') {
            steps {
                sh '''
                    mvn -B \
                    org.owasp:dependency-check-maven:12.2.2:check \
                    -Dformat=HTML
                '''
            }
        }

        stage('Análisis estático SonarQube') {
            steps {
                withSonarQubeEnv('SonarQube-TFM') {
                    sh '''
                        mvn -B \
                        org.sonarsource.scanner.maven:sonar-maven-plugin:sonar \
                        -Dsonar.projectKey=demo-policy-service \
                        -Dsonar.projectName=demo-policy-service
                    '''
                }
            }
        }

        stage('Quality Gate SonarQube') {
            steps {
                timeout(time: 5, unit: 'MINUTES') {
                    waitForQualityGate abortPipeline: true
                }
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

        stage('Desplegar en Kubernetes') {
            steps {
                sh '''
                    kubectl -n tfm-dev set image \
                    deployment/demo-policy-service \
                    demo-policy-service=${IMAGE_NAME}:${IMAGE_TAG}

                    kubectl -n tfm-dev rollout status \
                    deployment/demo-policy-service \
                    --timeout=120s
                '''
            }
        }

        stage('Validación post-deploy') {
            steps {
                sh '''
                    set -eu

                    echo "Imagen desplegada:"
                    kubectl -n tfm-dev get deployment demo-policy-service \
                    -o jsonpath='{.spec.template.spec.containers[0].image}{"\\n"}'

                    echo "Estado de los pods:"
                    kubectl -n tfm-dev get pods \
                    -l app=demo-policy-service

                    echo "Validación del endpoint de salud:"

                    SMOKE_POD="smoke-test-${BUILD_NUMBER}"

                    kubectl -n tfm-dev delete pod "${SMOKE_POD}" \
                    --ignore-not-found=true

                    kubectl -n tfm-dev run "${SMOKE_POD}" \
                    --image=curlimages/curl:8.10.1 \
                    --restart=Never \
                    --attach \
                    --rm \
                    --command -- \
                    curl -fsS \
                        --retry 10 \
                        --retry-delay 3 \
                        http://demo-policy-service:8080/actuator/health
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
                artifacts: 'target/*.jar,target/dependency-check-report.html',
                allowEmptyArchive: true
            )
        }
    }
}