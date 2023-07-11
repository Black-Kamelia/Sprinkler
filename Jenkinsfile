pipeline {
    agent any
    options {
        timestamps()
        ansiColor('xterm')
        timeout(time: 15, unit: 'MINUTES')
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
            parallel {
                stage('Util') {
                    steps {
                        sh 'gradle --parallel util:assemble'
                    }
                }
                stage('Readonly Collections') {
                    steps {
                        sh 'gradle --parallel readonly-collections:assemble'
                    }
                }
            }
        }
        stage('Test') {
            parallel {
                stage('Util') {
                    steps {
                        sh 'gradle --parallel util:test -PenableRestrikt=false'
                    }
                }
                stage('Readonly Collections') {
                    steps {
                        sh 'gradle --parallel readonly-collections:test -PenableRestrikt=false'
                    }
                }
            }
            post {
                always {
                    junit checksName: 'Tests', allowEmptyResults: true, testResults: '**/build/test-results/test/TEST-*.xml'
                    recordCoverage sourceDirectories: [[path: 'readonly-collections/src/main/java'], [path: 'util/src/main/kotlin'], [path: 'util/src/main/java'], [path: 'readonly-collections/src/main/kotlin']], tools: [[pattern: '**/build/reports/kover/xml/*.xml']]
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
