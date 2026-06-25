# Bet Settlement Pipeline

Sports betting event-outcome handling and bet settlement via Kafka + RocketMQ
(mocked). Spring Boot 3.x, Java 17, Gradle, spring-kafka, Spring Data JPA + H2.

## Prerequisites

- Docker (for the single-broker Kafka via `docker-compose.yml`)
- A JDK (any reasonably recent version is fine — Gradle's toolchain
  plugin will auto-provision JDK 17 on first build if it's not already
  installed locally; this requires network access and may take a minute
  the first time)

## How to run

```bash
docker compose up -d
./gradlew bootRun
```

## Example curl request

Publish an event outcome (winner = `5001` for event `1001`):

```bash
curl -X POST http://localhost:8080/event-outcomes \
  -H "Content-Type: application/json" \
  -d '{"eventId":1001,"eventName":"Team A vs Team B","eventWinnerId":5001}'
```

Returns `202 Accepted`. This publishes to the `event-outcomes` Kafka topic; the
consumer then matches the PENDING bets for that event, decides WON/LOST per bet,
and runs them through the (mocked) settlement publisher -> consumer -> settlement
service. For the seed data, event `1001` settles the bet for userId 101 as WON
(bet on 5001) and userId 102 as LOST (bet on 5002).

## Where to see results

- **Console logs** — look for `MOCK ROCKETMQ PUBLISH -> ...` and
  `Settled bet ... PENDING -> WON/LOST` lines.
- **H2 console** — `http://localhost:8080/h2-console`
  (JDBC URL `jdbc:h2:mem:betsdb`, user `sa`, no password). Run
  `SELECT * FROM BET` to see the seeded bets and their updated statuses.

## Design decisions / AI usage

See [NOTES.md](NOTES.md).
