# NOTES

## Design decisions

- **Bet ID is `java.util.UUID`, not auto-increment `Long`.** Rationale: bet IDs
  in a real betting platform tend to leak outward (webhooks, support tools,
  receipts) and a production system needs ID generation that doesn't depend on
  single-writer coordination — auto-increment optimizes for a single-writer,
  never-exposed world that doesn't match the domain being simulated. (At true
  scale, a sortable distributed scheme like ULID/Snowflake would be preferable
  to random UUIDv4 for index locality — UUID is the pragmatic choice given the
  90-minute timebox, not the final word on the ideal scheme.)
- **In-memory DB is H2 + Spring Data JPA, not a hand-rolled List/Map and not
  SQLite.** H2 gives real JPA/query semantics with zero extra config and a
  built-in web console for live verification; SQLite's in-memory mode has
  connection-pooling quirks under JPA that buy nothing here since true
  persistence isn't a requirement.
- **Why just `Bet` (no separate domain model / entity split)** single
  DB, single consumer of the model, no real invariants beyond status
  transitions. No point at the moment to split into domain object + JPA Entity. 
  Same logic for eventId/userId/eventMarketId they plain Longs and not
  JPA relationships (`@ManyToOne`) - there's no Event/User entity in this
  service. Those IDs point into other services' data via the
  Kafka message, not local entities I own. If they were local entities, 
  default would be `FetchType.LAZY`, not eager.
- **BetNotFoundException propagates unhandled from the settlement consumer** 
  rather than being caught per-message. In production this would need explicit handling
  (log-and-continue, or DLQ) so one bad message doesn't affect the rest of the batch; 
  out of scope for this timebox.
- **RocketMQ is mocked**, but the producer -> consumer -> settle shape is real. The
  assignment allows mocking the RocketMQ producer if setup is too heavy, but
  requirement 5 still needs a consumer that settles bets. So the mock
  publisher logs the payload and then hands the message to the same settlement consumer in-process.
  Swapping in a real RocketMQ adapter is a drop-in: it's just another BetSettlementPublisher.
- **No odds/payout calculation**, only WON/LOST status. The Bet model has no
  odds field, so there's no amount to compute; we just flip the status.

## Known gaps (from self-review, not fixed given timebox)

- **EventOutcomeRequest is reused across the HTTP, Kafka, and service layers**,
  with web-validation annotations (@NotNull/@NotBlank) that don't mean
  anything once it's a Kafka payload. In a real system this should split
  into a validated HTTP DTO and a separate internal event/command type so
  the layers can evolve independently. Left as one type here for time.
- **eventName is required on the API and sent over Kafka but never read
  downstream.** Kept it because the assignment's outcome shape includes it,
  but nothing currently consumes it - would either drop it or wire it into
  the settlement log line for correlation.
- **BetSettlementMessage.eventId is carried but unused by the settler**
  (only betId/result are read). Harmless today, but either it should be
  used (log correlation, future routing key) or trimmed.
- **The only BetSettlementPublisher bean is gated behind @Profile("mock"),
  with no fallback implementation.** Running under any other profile would
  fail to start (missing bean) rather than gracefully falling through. The
  port is designed to be swappable, but a real adapter isn't actually
  plugged in yet - "drop-in" is the intended design, not the current state.

## AI tool usage

**Tools used:** Claude (planning/brainstorming) and Claude Code (implementation).

**Philosophy:** AI was used as a force-multiplier for mechanical and
well-specified work, not as a substitute for the core engineering decisions
being evaluated here. Concretely:

- **Planning:** Used Claude to bounce off ideas, sanity-check
  ambiguous requirements (e.g. the RocketMQ mocking instruction's implicit
  contradiction with the consumer requirement), and to draw up a detailed
  build spec from our planning before writing any code.
- **Implementation:** Used Claude Code against that spec, stage by stage,
  reviewing and confirming each stage before proceeding — kafka/REST
  wiring, the seeder, and boilerplate were generated; the core matching/
  settlement logic and the messaging port design were specified by me in
  detail upfront rather than left to the AI's discretion.
- **Review:** Used Claude Code for a final code review pass and to
  generate unit tests against logic I'd already written and verified
  manually, unit tests were also reviewed by me and adjusted where needed.

Overall project direction, design decisions, ambiguity resolutions, and the overall architecture were
mine; AI accelerated the typing and helped debugging when needed.
