pipeline {
    agent {
        docker {
            image 'gradle:7.5.0-jdk17'
            reuseNode true
        }
    }

    stages {
        stage('Build') {
            steps {
                sh 'touch gradle.local.properties'
                sh 'gradle build -x test'
            }
        }
        stage('Test') {
            steps {
                sh 'gradle test'
            }

            post {
                always {
                    junit checksName: 'Tests', allowEmptyResults: true, testResults: '**/build/test-results/test/*.xml'
                    publishCoverage adapters: [jacocoAdapter('**/build/reports/jacoco/test/*.xml')]
                }
            }
        }
        stage('Deploy') {
            when {
                branch 'master'
            }
            steps {
                withCredentials([usernamePassword(credentialsId: 'maven-gpg-signingkey', usernameVariable: 'signingKey', passwordVariable: 'signingPassword')]) {
                    withCredentials([usernamePassword(credentialsId: 'sonatype-nexus', usernameVariable: 'user', passwordVariable: 'pass')]) {
                        sh 'gradle publish -PmavenCentralUsername=$user -PmavenCentralPassword=$pass -PsigningKey=$signingKey -PsigningPassword=$signingPassword'
                    }
                }
            }
        }
    }
}
