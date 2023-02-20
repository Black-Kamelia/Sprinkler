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
                script {
                    def branch = env.CHANGE_BRANCH
                    def target = env.CHANGE_TARGET
                    if (target == 'master' && branch != 'develop') {
                        currentBuild.result = 'ABORTED'
                        error 'Only develop branch can be merged into master'
                    }
                }
                sh 'gradle build -x test'
            }
        }
        stage('Test') {
            steps {
                sh 'gradle test -PenableRestrikt=false'
            }
            post {
                always {
                    junit checksName: 'Tests', allowEmptyResults: true, testResults: '**/build/test-results/test/TEST-*.xml'
                    publishCoverage adapters: [
                            jacocoAdapter(mergeToOneReport: true, path: 'readonly-collection/build/reports/kover/xml/report.xml'),
                            jacocoAdapter(mergeToOneReport: true, path: 'util/build/reports/kover/xml/report.xml')
                        ],
                        sourceDirectories: [
                            [path: 'readonly-collections/src/main/kotlin'],
                            [path: 'readonly-collections/src/main/java'],
                            [path: 'util/src/main/kotlin'],
                            [path: 'util/src/main/java']
                        ],
                        sourceFileResolver: sourceFiles('STORE_LAST_BUILD')
                }
            }
        }
        stage('Deploy') {
            when {
                branch 'master'
            }
            steps {
                withCredentials([
                        usernamePassword(credentialsId: 'maven-gpg-signingkey', usernameVariable: 'signingKey', passwordVariable: 'signingPassword'),
                        usernamePassword(credentialsId: 'sonatype-nexus', usernameVariable: 'user', passwordVariable: 'pass'),
                ]) {
                    sh 'gradle publish -PmavenCentralUsername=$user -PmavenCentralPassword=$pass -PsigningKey=$signingKey -PsigningPassword=$signingPassword'
                }
            }
        }
    }
}
