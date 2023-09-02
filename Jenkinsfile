pipeline {
    agent any
    parameters {
        booleanParam(name: 'skip_test', defaultValue: false, description: 'Set to true to skip the test stage')
    }
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
            when {
                branch 'master'
            }
            steps {
                sh 'gradle --parallel assemble'
            }
        }
        stage('Test') {
            when {
                branch 'master'
            }
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
                stage('JVM Bridge') {
                    steps {
                        sh 'gradle jvm-bridge:test'
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
                        [path: 'binary-transcoders/src/main/java'],
                        [path: 'jvm-bridge/src/main/kotlin'],
                        [path: 'jvm-bridge/src/main/java']
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
            parallel {
                stage('Utils') {
                    steps {
                        script {
                            try {
                                input 'Publish to Maven Central?'
                            } catch(err) {
                               currentBuild.result = 'SUCCESS'
                               return
                            }
                        }
                        withCredentials([
                                usernamePassword(credentialsId: 'maven-gpg-signingkey', usernameVariable: 'signingKey', passwordVariable: 'signingPassword'),
                                usernamePassword(credentialsId: 'sonatype-nexus', usernameVariable: 'user', passwordVariable: 'pass'),
                        ]) {
                            sh 'gradle utils:publish -PmavenCentralUsername=$user -PmavenCentralPassword=$pass -PsigningKey=$signingKey -PsigningPassword=$signingPassword'
                        }
                    }
                }
                stage('Readonly Collections') {
                    steps {
                        script {
                            try {
                                input 'Publish to Maven Central?'
                            } catch(err) {
                               currentBuild.result = 'SUCCESS'
                               return
                            }
                        }
                        withCredentials([
                                usernamePassword(credentialsId: 'maven-gpg-signingkey', usernameVariable: 'signingKey', passwordVariable: 'signingPassword'),
                                usernamePassword(credentialsId: 'sonatype-nexus', usernameVariable: 'user', passwordVariable: 'pass'),
                        ]) {
                            sh 'gradle readonly-collections:publish -PmavenCentralUsername=$user -PmavenCentralPassword=$pass -PsigningKey=$signingKey -PsigningPassword=$signingPassword'
                        }
                    }
                }
                stage('Binary Transcoders') {
                    steps {
                        script {
                            try {
                                input 'Publish to Maven Central?'
                            } catch(err) {
                               currentBuild.result = 'SUCCESS'
                               return
                            }
                        }
                        withCredentials([
                                usernamePassword(credentialsId: 'maven-gpg-signingkey', usernameVariable: 'signingKey', passwordVariable: 'signingPassword'),
                                usernamePassword(credentialsId: 'sonatype-nexus', usernameVariable: 'user', passwordVariable: 'pass'),
                        ]) {
                            sh 'gradle binary-transcoders:publish -PmavenCentralUsername=$user -PmavenCentralPassword=$pass -PsigningKey=$signingKey -PsigningPassword=$signingPassword'
                        }
                    }
                }
                stage('JVM Bridge') {
                    steps {
                        script {
                            try {
                                input 'Publish to Maven Central?'
                            } catch(err) {
                               currentBuild.result = 'SUCCESS'
                               return
                            }
                        }
                        withCredentials([
                                usernamePassword(credentialsId: 'maven-gpg-signingkey', usernameVariable: 'signingKey', passwordVariable: 'signingPassword'),
                                usernamePassword(credentialsId: 'sonatype-nexus', usernameVariable: 'user', passwordVariable: 'pass'),
                        ]) {
                            sh 'gradle jvm-bridge:publish -PmavenCentralUsername=$user -PmavenCentralPassword=$pass -PsigningKey=$signingKey -PsigningPassword=$signingPassword'
                        }
                    }
                }
            }
        }
    }
}
