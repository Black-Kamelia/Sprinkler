pipeline {
    agent any
    options {
        timestamps()
        ansiColor('xterm')
        timeout(time: 15, unit: 'MINUTES')
        disableConcurrentBuilds()
    }
    tools {
        gradle 'gradle-7.5.0'
    }

    stages {
        stage('Precondition') {
            steps {
                script {
                    def branch = env.CHANGE_BRANCH
                    def target = env.CHANGE_TARGET
                    if (target == 'master' && branch != 'develop') {
                        currentBuild.result = 'ABORTED'
                        error 'Only develop branch can be merged into master'
                    }
                }
            }
        }
        stage('Build') {
            steps {
                sh 'gradle --parallel assemble'
            }
        }
        stage('Test') {
            parallel {
                stage('Utils') {
                    steps {
                        sh 'gradle utils:test'
                    }
                }
                stage('Readonly Collections') {
                    steps {
                        sh 'gradle readonly-collections:test'
                    }
                }
                stage('Binary Transcoders') {
                    steps {
                        sh 'gradle binary-transcoders:test'
                    }
                }
            }
            post {
                always {
                    junit checksName: 'Tests', allowEmptyResults: true, testResults: '**/build/test-results/test/TEST-*.xml'
                    recordCoverage sourceDirectories: [
                        [path: 'readonly-collections/src/main/kotlin'],
                        [path: 'readonly-collections/src/main/java'],
                        [path: 'utils/src/main/kotlin'],
                        [path: 'utils/src/main/java'],
                        [path: 'binary-transcoders/src/main/kotlin'],
                        [path: 'binary-transcoders/src/main/java']
                    ],
                    tools: [
                        [pattern: '**/build/reports/kover/report.xml']
                    ]
                }
            }
        }
        stage('Deploy') {
            // when {
                // branch 'master'
            // }
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
