pipeline {
 agent {label 'hospital-release'}
 options {disableConcurrentBuilds();timestamps()}
 parameters {string(name:'RELEASE_VALUES',defaultValue:'releases/approved.json');choice(name:'ENVIRONMENT',choices:['dev','uat','preprod','prod'])}
 stages {
  stage('Checkout') {steps {checkout scm}}
  stage('Validate artifact manifest') {steps {sh 'python3 scripts/validate-release.py "$RELEASE_VALUES"'}}
  stage('Release approval') {steps {input message:'Confirm CI evidence, UAT acceptance, preprod validation and immutable digest approval'}}
  stage('Helm deployment') {steps {withCredentials([file(credentialsId:'hospital-kubeconfig',variable:'KUBECONFIG')]){sh './scripts/jenkins/deploy.sh'}}}
 }
 post {failure {echo 'Deployment failed. Helm --atomic restores the prior application revision; inspect database compatibility before retrying.'}}
}
