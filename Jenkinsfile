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
        //stage('Deploy') {
        //    when {
        //        beforeInput true
        //        branch 'master'
        //    }
        //    options {
        //        timeout(time: 15, unit: 'MINUTES')
        //    }
        //    input {
        //        message "Confirm publishing to repository"
        //    }
        //    steps {
        //        withCredentials([usernamePassword(credentialsId: 'nexus', passwordVariable: 'pass', usernameVariable: 'user')]) {
        //            sh 'gradle publish -PnexusBlackKameliaUsername=$user -PnexusBlackKameliaPassword=$pass'
        //        }
        //    }
        //}
    }
}