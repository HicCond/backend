# Multiple-Choice Quiz — Back-End

A REST API that serves a 20-question geography quiz and evaluates submitted answers.

The quiz is worth 1000 points in total and is passed by scoring **more than 600**. All quiz
logic lives on the server: the API never reveals which option is correct until a submission
has been evaluated, so a client cannot grade itself.

---

## Tech stack

| | |
|---|---|
| Language | Java 25 |
| Framework | Spring Boot 4.1 (Spring MVC, Spring Data JPA, Bean Validation) |
| Persistence | H2 in-memory database via Hibernate |
| Boilerplate | Lombok (`@RequiredArgsConstructor`, `@Getter`, `@Builder`, `@FieldDefaults`, `@Slf4j`) |
| Mapping | MapStruct (compile-time, no reflection) |
| Build | Gradle (Kotlin DSL) with wrapper |
| Tests | JUnit 5, AssertJ, Mockito, MockMvc |

## Running it

A JDK is the only prerequisite — the Gradle wrapper downloads Gradle itself on first run.
The build declares a Java 25 toolchain, so Gradle will locate or provision a matching JDK.

```bash
./gradlew bootRun
```

The API is then available at `http://localhost:8080`. On Windows use `gradlew.bat bootRun`.

Run the tests:

```bash
./gradlew test
```

Build a runnable jar:

```bash
./gradlew bootJar && java -jar build/libs/quiz-backend-1.0.0.jar
```

The H2 console is enabled at `http://localhost:8080/h2-console` (JDBC URL
`jdbc:h2:mem:quizdb`, user `sa`, no password) if you want to look at the seeded data.

Application logs are written to `logs/quiz.log` with size-based rotation, in addition to
the console.

---

## API

Base path: `/api/v1/quiz`

### `GET /api/v1/quiz` — quiz rules

Everything the intro screen needs. The scoring breakdown is derived from the stored
questions rather than duplicated, so it cannot drift from the actual data.

```json
{
  "title": "World Geography",
  "description": "Twenty multiple-choice questions ...",
  "questionCount": 20,
  "maxScore": 1000,
  "passingScore": 600,
  "scoring": [
    { "pointsPerQuestion": 100, "questionCount": 3, "subtotal": 300 },
    { "pointsPerQuestion": 75,  "questionCount": 4, "subtotal": 300 },
    { "pointsPerQuestion": 40,  "questionCount": 5, "subtotal": 200 },
    { "pointsPerQuestion": 25,  "questionCount": 8, "subtotal": 200 }
  ]
}
```

### `GET /api/v1/quiz/questions` — the questions

```json
[
  {
    "id": "q01",
    "text": "Which mountain is the highest point of mainland Australia?",
    "points": 100,
    "options": [
      { "id": "a", "text": "Mount Kosciuszko" },
      { "id": "b", "text": "Uluru" },
      { "id": "c", "text": "Mount Ossa" },
      { "id": "d", "text": "Mount Bogong" }
    ]
  }
]
```

There is no field for the correct option, and `QuestionDto` has no such component at all —
the omission is structural rather than an annotation a refactor could drop.

### `POST /api/v1/quiz/submissions` — evaluate a submission

Request:

```json
{
  "answers": [
    { "questionId": "q01", "optionId": "a" },
    { "questionId": "q02", "optionId": "b" }
  ]
}
```

Response `200 OK`:

```json
{
  "totalScore": 850,
  "maxScore": 1000,
  "correctCount": 14,
  "incorrectCount": 6,
  "percentage": 70.0,
  "passed": true,
  "passingScore": 600,
  "incorrectAnswers": [
    {
      "questionId": "q05",
      "questionText": "Which is the longest river in Asia?",
      "points": 75,
      "givenOptionId": "a",
      "givenOptionText": "Yellow River",
      "correctOptionId": "b",
      "correctOptionText": "Yangtze"
    }
  ]
}
```

Note that `passed` is decided by **points**, not by percentage. 70% of the questions can be
worth 85% of the points, or the other way round, so a result screen should lead with the
score against the 600-point threshold.

### Errors

Every error uses RFC 9457 `application/problem+json` with a machine-readable `errors`
array. All violations of a submission are reported together, so the client can highlight
every offending question after one round trip.

```json
{
  "type": "urn:quiz:error:invalid-submission",
  "title": "Invalid submission",
  "status": 400,
  "detail": "Submission contains 2 invalid answer(s)",
  "errors": [
    {
      "code": "OPTION_NOT_IN_QUESTION",
      "questionId": "q12",
      "message": "Option 'd' is not one of the available options for question 'q12'"
    },
    {
      "code": "MISSING_ANSWER",
      "questionId": "q17",
      "message": "Question 'q17' has not been answered"
    }
  ]
}
```

Codes are the constants of the `ValidationErrorCode` enum, so the wire contract is a closed
set the client can switch on rather than free-form strings.

| Code | Meaning |
|---|---|
| `OPTION_NOT_IN_QUESTION` | The option is not offered by *that* question. Some questions have three options and some four, so `d` may be valid elsewhere in the quiz and invalid here. |
| `MISSING_ANSWER` | A question was left unanswered. |
| `UNKNOWN_QUESTION` | The question does not belong to this quiz. |
| `DUPLICATE_ANSWER` | The same question was answered more than once. |
| `MALFORMED_REQUEST` | The body failed structural validation (empty answer list, blank ids). |

`requests.http` in the project root contains ready-to-run examples.

---

## Design

The layering is the plain three-tier arrangement the task asks for, and each class has one
reason to change:

```
controller/   HTTP only — no logic
service/      QuizService (orchestration), ScoringService (evaluation), AnswerValidator (rules)
repository/   Spring Data JPA
entity/       persistence model
dto/          request and response shapes (Java records)
mapper/       entity -> DTO, one way only
exception/    RFC 9457 error handling
config/       quiz rules, CORS
```

`ScoringService` depends on no repository and no HTTP concern, so it is tested with a plain
constructor call. `AnswerValidator` is separate from it because validating a submission and
scoring one are different responsibilities that change for different reasons.

## Assumptions and trade-offs

- **One quiz.** The task describes a single quiz, so the URL is `/api/v1/quiz` rather than
  `/api/v1/quizzes/{id}`. The data model would support several without changes to the
  scoring logic.
- **Percentage is the share of questions, not of points.** The task asks for "percentage of
  correct answers", so 14 correct answers out of 20 is 70.0% even though those answers were
  worth 850 of 1000 points. `BigDecimal` is used so the JSON never shows artefacts like
  `70.00000000000001`.
- **Passing is strictly above 600.** Exactly 600 fails. This boundary is reachable — the
  three 100-point plus four 75-point questions total exactly 600 — and it is covered by a
  test.
- **Incomplete submissions are rejected** rather than scored with the missing answers
  counted as wrong. The task speaks of submitting a *completed* set of answers and requires
  the client to block progress until an option is chosen, so a partial submission indicates
  a client bug that is better surfaced than silently absorbed.
- **Submissions are not persisted.** The task requires evaluation, not history, so
  `POST /submissions` returns `200 OK` with the result rather than `201 Created` with a
  `Location` header pointing at a resource that does not exist. Persisting attempts would
  mean one more entity and one more endpoint.
- **Schema from entities, data from `data.sql`.** The database is in-memory and the quiz
  data is a fixed seed, so `ddl-auto: create-drop` keeps the setup to a single file. A
  project with a real database would use Flyway or Liquibase instead; the trade-off is
  deliberate rather than an oversight.
- **`open-in-view` is disabled.** Entities are mapped to DTOs inside the transactional
  service methods, so lazily loaded collections never escape the service layer.
- **Lombok covers the mechanical boilerplate only.** Constructors for injection, getters,
  the entity builder and the loggers are generated. DTOs stay Java records, where Lombok
  would add nothing. The entity is the one place `@FieldDefaults` defaults the access level
  without `makeFinal`, because Hibernate writes entity state reflectively and final fields
  would break loading.
- **MapStruct generates the entity-to-DTO mapping at compile time.** `unmappedTargetPolicy`
  is set to `ERROR`, so adding a field to `QuestionDto` without saying where it comes from
  fails the build instead of silently returning null. Unmapped *source* properties stay
  allowed, which is what lets `correctOptionId` exist on the entity and nowhere else. The
  generated `QuestionMapperImpl` can be read under `build/generated/sources`.
- **No authentication or rate limiting.** Out of scope for the exercise.

## Tests

```
ScoringServiceTest        plain unit tests — full score, zero score, the 600 boundary,
                          percentage derived from question count, incorrect-answer reporting
AnswerValidatorTest       one test per violation code plus a case proving all violations
                          are reported together
QuizControllerTest        @WebMvcTest — status codes, JSON shape, problem+json errors, and
                          an assertion that the questions payload contains no correct answers
SeedDataTest              @SpringBootTest — the quiz really is 20 questions summing to 1000
                          points in a 3/4/5/8 distribution, with 3-4 distinct options each
QuizApiIntegrationTest    @SpringBootTest — HTTP through validation, scoring and JPA against
                          the real seed data
```

The seed-data test exists because a typo in `data.sql` is invisible in code review but
would quietly break the scoring guarantees the task specifies.
