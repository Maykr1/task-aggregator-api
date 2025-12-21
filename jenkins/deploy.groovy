@Library('shared-jenkins-library') _

pipeline {
    agent any
    options { timestamps() }

    environment {
        // --- APP ---
        APP_NAME        = "task-aggregator-api"
        ACTIVE_PROFILE  = "docker"
        ENV_FILE        = "task-aggregator-api env"

        // --- DOCKER ---
        COMPOSE_DIR     = '/deploy'
        DOCKER_REG      = 'localhost:8003'
        DOCKER_REPO     = 'repository/docker-apps'

        IMAGE_TAG       = "${params.COMMIT_ID ?: 'latest'}"
        IMAGE           = "${DOCKER_REG}/${APP_NAME}:${IMAGE_TAG}"

        REG_CRED_ID     = 'nexus-deploy'
        PRUNE_MODE      = "${params.PRUNE_MODE ?: 'none'}"
    }

    parameters {
        string(
            name: 'COMMIT_ID',
            defaultValue: 'latest',
            description: 'Git commit ID to deploy (branch, tag, or sha)'
        )
        choice(name: 'PRUNE_MODE',
            choices: ['none', 'dangling', 'all'],
            description: 'Choose how aggressively to prune docker containers, volumes, etc.'
        )
    }

    stages {
        stage("Pull Secrets") {
            steps {
                withCredentials([
                    string(credentialsId: 'bitwarden-url', variable: 'BW_URL'),
                    string(credentialsId: 'bitwarden-client-id', variable: 'BW_CLIENTID'),
                    string(credentialsId: 'bitwarden-client-secret', variable: 'BW_CLIENTSECRET'),
                    string(credentialsId: 'bitwarden-master-password', variable: 'BW_MASTER_PASSWORD')
                ]) {
                    sh '''
                        bw config server "$BW_URL" >/dev/null

                        export BW_CLIENTID BW_CLIENTSECRET
                        bw login --apikey >/dev/null

                        export BW_SESSION="$(bw unlock "$BW_MASTER_PASSWORD" --raw)"

                        ITEM_ID="$(bw list items --search "${APP_NAME}" | jq -r '.[0].id')"

                        if [ -z "$ITEM_ID" ] || [ "$ITEM_ID" = "null" ]; then
                            echo "[INFO] No Bitwarden item found for ${APP_NAME}. Skipping secrets."
                            rm -f .env.secrets || true
                            bw lock >/dev/null
                            exit 0
                        fi

                        NOTE="$(bw get item "$ITEM_ID" | jq -r '.notes')"
                        
                        echo "$NOTE" | grep -E '^[A-Z0-9_]+=' > .env.secrets || true

                        if [ ! -s .env.secrets ]; then
                            echo "[INFO] Bitwarden item found but no KEY=VALUE lines in notes. Treating as no secrets."
                            rm -f .env.secrets || true
                        else
                            echo "[INFO] Wrote secrets file: .env.secrets"
                        fi

                        bw lock >/dev/null
                    '''
                }
            }
        }

        stage('Login to Registry') {
            steps {
                login(env.REG_CRED_ID, env.DOCKER_REG)
            }
        }

        stage('Deploy Image') {
            steps {
                deployApp(env.IMAGE, env.COMPOSE_DIR, env.IMAGE_TAG, env.APP_NAME)
            }
        }

        stage('Cleanup') {
            when { expression { env.PRUNE_MODE != 'none' } }
            steps {
                cleanupServer(env.PRUNE_MODE)
            }
        }

        stage('Logout') {
            steps {
                logout(env.DOCKER_REG)
            }
        }
        
    }

    post {
        success { 
            echo "✅ Successfully deployed ${APP_NAME}:${env.IMAGE_TAG} image" 
        }

        failure { 
            echo "❌ Deployment failed for ${APP_NAME}:${env.IMAGE_TAG} image" 
        }
    }
}
