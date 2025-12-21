@Library('shared-jenkins-library') _

pipeline {
    agent any
    options { timestamps() }

    environment {
        // --- APP ---
        APP_NAME        = "task-aggregator-api"
        ACTIVE_PROFILE  = "docker"

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
                        bw logout >/dev/null 2>&1 || true
                        bw config server "$BW_URL" >/dev/null

                        export BW_CLIENTID BW_CLIENTSECRET
                        bw login --apikey >/dev/null

                        BW_SESSION="$(bw unlock "$BW_MASTER_PASSWORD" --raw)"
                        export BW_SESSION
                        bw sync --session "$BW_SESSION" >/dev/null

                        ITEM_ID="$(bw list items --search "${APP_NAME}" --session "$BW_SESSION" | jq -r '.[0].id')"

                        SECRETS_PATH="${WORKSPACE}/.env.secrets"

                        if [ -z "$ITEM_ID" ] || [ "$ITEM_ID" = "null" ]; then
                            echo "[INFO] No Bitwarden item found for ${APP_NAME}. Skipping secrets."
                            rm -f "$SECRETS_PATH" || true
                            bw lock >/dev/null
                            exit 0
                        fi

                        bw get item "$ITEM_ID" --session "$BW_SESSION" | jq -r '.fields[]? | select(.value != null) | (.name + "=" + .value)' > "$SECRETS_PATH"

                        if [ ! -s "$SECRETS_PATH" ]; then
                            echo "[INFO] Bitwarden item found but no custom fields present. Treating as no secrets."
                            rm -f "$SECRETS_PATH" || true
                        else
                            echo "[INFO] Wrote secrets file: $SECRETS_PATH"
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
