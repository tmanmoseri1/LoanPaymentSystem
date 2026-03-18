# Jenkins CI/CD Setup (Local / VM)

## Prereqs
- Jenkins installed (LTS)
- Docker + Docker Compose v2 installed on the Jenkins agent
- GitHub repository created with this code

## Install Docker on Ubuntu (example)
```bash
sudo apt-get update
sudo apt-get install -y ca-certificates curl gnupg
sudo install -m 0755 -d /etc/apt/keyrings
curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo gpg --dearmor -o /etc/apt/keyrings/docker.gpg
sudo chmod a+r /etc/apt/keyrings/docker.gpg
echo   "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.gpg] https://download.docker.com/linux/ubuntu   $(. /etc/os-release && echo "$VERSION_CODENAME") stable" |   sudo tee /etc/apt/sources.list.d/docker.list > /dev/null
sudo apt-get update
sudo apt-get install -y docker-ce docker-ce-cli containerd.io docker-buildx-plugin docker-compose-plugin
sudo usermod -aG docker jenkins
sudo systemctl restart jenkins
```

## GitHub Webhook
In GitHub repo:
- Settings → Webhooks → Add webhook
- Payload URL: `http://<JENKINS_HOST>:8080/github-webhook/`
- Content type: `application/json`
- Events: “Just the push event”
- Save

In Jenkins:
- New Item → Pipeline
- Pipeline definition: “Pipeline script from SCM”
- SCM: Git
- Repo URL: `https://github.com/tmanmoseri1/LoanPaymentSystem`
- Script path: `Jenkinsfile`
- Build triggers: ✅ “GitHub hook trigger for GITScm polling”
