param(
    [switch]$SkipRuntime
)

Set-StrictMode -Version Latest
$ErrorActionPreference = "Stop"

function Write-Check {
    param(
        [string]$Name,
        [bool]$Passed,
        [string]$Details
    )

    if ($Passed) {
        Write-Host "[PASS] $Name - $Details" -ForegroundColor Green
    }
    else {
        Write-Host "[FAIL] $Name - $Details" -ForegroundColor Red
    }
}

function FileContains {
    param(
        [string]$Pattern
    )

    $content = Get-Content .\docker-compose.yml -Raw
    return [bool]($content -match $Pattern)
}

$allPassed = $true

# 1) No ZooKeeper references
$hasZooKeeper = FileContains "zookeeper"
Write-Check -Name "ComposeConfig_hasNoZooKeeperReferences" -Passed:(-not $hasZooKeeper) -Details:"No 'zookeeper' references in docker-compose.yml"
if ($hasZooKeeper) { $allPassed = $false }

# 2) Kafka image and KRaft settings
$kafkaImageOk = FileContains "apache/kafka:4.0.2"
$rolesOk = FileContains "KAFKA_PROCESS_ROLES"
$nodeIdOk = FileContains "KAFKA_NODE_ID"
$quorumOk = FileContains "KAFKA_CONTROLLER_QUORUM_VOTERS"
$controllerListenerOk = FileContains "KAFKA_CONTROLLER_LISTENER_NAMES"

$kraftChecksPassed = $kafkaImageOk -and $rolesOk -and $nodeIdOk -and $quorumOk -and $controllerListenerOk
Write-Check -Name "KafkaService_usesApacheKafka402_andKRaftSettings" -Passed:$kraftChecksPassed -Details:"Kafka image + required KRaft envs present"
if (-not $kraftChecksPassed) { $allPassed = $false }

# 3) Listener/port checks in file
$port9092Ok = FileContains "9092:9092"
$port29092Ok = FileContains "29092"
$listenersPassed = $port9092Ok -and $port29092Ok
Write-Check -Name "KafkaListeners_expose9092_andUseInternal29092" -Passed:$listenersPassed -Details:"9092 host mapping and 29092 internal listener configured"
if (-not $listenersPassed) { $allPassed = $false }

# 4) Kafka UI config checks in file
$kafkaUiImageOk = FileContains "provectuslabs/kafka-ui:latest"
$kafkaUiPortOk = FileContains "8085:8080"
$kafkaUiConfigPassed = $kafkaUiImageOk -and $kafkaUiPortOk
Write-Check -Name "KafkaUi_usesLatestImage_andBindsHost8085" -Passed:$kafkaUiConfigPassed -Details:"Kafka UI image and host port mapping"
if (-not $kafkaUiConfigPassed) { $allPassed = $false }

# 5) Schema Registry checks in file
$schemaImageOk = FileContains "confluentinc/cp-schema-registry:latest"
$schemaPortOk = FileContains "8081:8081"
$schemaConfigPassed = $schemaImageOk -and $schemaPortOk
Write-Check -Name "SchemaRegistry_serviceExists_andBindsHost8081" -Passed:$schemaConfigPassed -Details:"Schema Registry image and host port mapping"
if (-not $schemaConfigPassed) { $allPassed = $false }

if (-not $SkipRuntime) {
    try {
        docker compose up -d | Out-Host

        # 6) Schema Registry bootstrap health
        $schemaLogs = docker compose logs schema-registry --tail=200 | Out-String
        $schemaHasFatal = $schemaLogs -match "(ERROR|Exception|connection refused|timed out)"
        Write-Check -Name "SchemaRegistry_canReachKafkaBootstrap" -Passed:(-not $schemaHasFatal) -Details:"No obvious fatal bootstrap errors in recent logs"
        if ($schemaHasFatal) { $allPassed = $false }

        # 4 runtime) Kafka UI reachable
        try {
            $uiResp = Invoke-WebRequest -UseBasicParsing http://localhost:8085 -TimeoutSec 10
            $uiOk = $uiResp.StatusCode -ge 200 -and $uiResp.StatusCode -lt 400
        }
        catch {
            $uiOk = $false
        }
        Write-Check -Name "KafkaUi_runtime_reachable" -Passed:$uiOk -Details:"HTTP check on localhost:8085"
        if (-not $uiOk) { $allPassed = $false }

        # 5 runtime) Schema Registry reachable
        try {
            $srResp = Invoke-WebRequest -UseBasicParsing http://localhost:8081/subjects -TimeoutSec 10
            $srOk = $srResp.StatusCode -eq 200
        }
        catch {
            $srOk = $false
        }
        Write-Check -Name "SchemaRegistry_runtime_reachable" -Passed:$srOk -Details:"GET /subjects on localhost:8081"
        if (-not $srOk) { $allPassed = $false }

        # 7) Produce/consume smoke test
        docker compose exec broker /opt/kafka/bin/kafka-topics.sh --create --if-not-exists --topic smoke-test --bootstrap-server localhost:9092 --partitions 1 --replication-factor 1 | Out-Host
        docker compose exec -T broker sh -c "echo hello-smoke | /opt/kafka/bin/kafka-console-producer.sh --topic smoke-test --bootstrap-server localhost:9092" | Out-Host
        $consume = docker compose exec broker /opt/kafka/bin/kafka-console-consumer.sh --topic smoke-test --bootstrap-server localhost:9092 --from-beginning --timeout-ms 5000 --max-messages 1 | Out-String
        $smokeOk = $consume -match "hello-smoke"
        Write-Check -Name "KafkaSmoke_canProduceAndConsumeSingleMessage" -Passed:$smokeOk -Details:"Produced message is consumed"
        if (-not $smokeOk) { $allPassed = $false }

        # 8) Persistence test
        docker compose exec broker /opt/kafka/bin/kafka-topics.sh --create --if-not-exists --topic persist-check --bootstrap-server localhost:9092 --partitions 1 --replication-factor 1 | Out-Host
        docker compose down | Out-Host
        docker compose up -d | Out-Host
        $topics = docker compose exec broker /opt/kafka/bin/kafka-topics.sh --list --bootstrap-server localhost:9092 | Out-String
        $persistOk = $topics -match "persist-check"
        Write-Check -Name "ComposeRestart_preservesKafkaDataWithVolume" -Passed:$persistOk -Details:"Topic survives restart"
        if (-not $persistOk) { $allPassed = $false }
    }
    finally {
        docker compose down | Out-Host
    }
}

if ($allPassed) {
    Write-Host "All checks passed." -ForegroundColor Green
    exit 0
}

Write-Host "One or more checks failed." -ForegroundColor Red
exit 1


