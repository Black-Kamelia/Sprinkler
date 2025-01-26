pipeline {
    agent any
    options {
        timestamps()
        ansiColor('xterm')
        timeout(time: 15, unit: 'MINUTES')
        disableConcurrentBuilds()
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
                sh './gradlew --parallel assemble'
            }
        }
        stage('Test') {
            parallel {
                stage('Utils') {
                    steps {
                        sh './gradlew utils:test'
                    }
                }
                stage('Readonly Collections') {
                    steps {
                        sh './gradlew readonly-collections:test'
                    }
                }
                stage('Binary Transcoders') {
                    steps {
                        sh './gradlew binary-transcoders:test'
                    }
                }
                stage('JVM Bridge') {
                    steps {
                        sh './gradlew jvm-bridge:test'
                    }
                }
                stage('I18N') {
                    steps {
                        sh './gradlew i18n:test'
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
                        [path: 'jvm-bridge/src/main/java'],
                        [path: 'i18n/src/main/kotlin']
                    ],
                    tools: [
                        [pattern: '**/build/reports/kover/report.xml']
                    ]
                }
            }
        }
    }
}
