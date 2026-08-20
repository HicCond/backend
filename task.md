# Coding Challenge: Multiple-Choice Quiz Application

## Overview

Build a Multiple-Choice Quiz application consisting of two components:

- **Back-End:** A RESTful API that serves quiz data and evaluates submitted answers.
- **Front-End / Mobile App:** A client application (web or native mobile) that consumes the API and provides the quiz experience to the user.

You are free to choose your preferred technology stack for both components.

---

## Requirements

### Back-End

#### Quiz Data

Prepare **20 questions** on a topic of your choice. Each question should have **3–4 possible answer options** (e.g. a, b, c, d) with exactly one correct answer.

Questions are pre-assigned point values as follows:

| Questions | Points each | Subtotal |
|-----------|-------------|----------|
| 3         | 100         | 300      |
| 4         | 75          | 300      |
| 5         | 40          | 200      |
| 8         | 25          | 200      |
| **Total** |             | **1000** |

#### API Design

Design the API according to REST best practices. At a minimum, the API should support:

- Retrieving the list of quiz questions (including answer options and scoring details).
- Submitting a completed set of answers for evaluation.

#### Validation

- Validate that each submitted answer corresponds to one of the available options for the given question.
- Return a clear error response for any invalid input.

#### Quiz Evaluation

When answers are submitted, the API should calculate and return:

- Total score achieved.
- Number of correct and incorrect answers.
- Percentage of correct answers.
- A list of incorrectly answered questions, including the question text and the correct answer.
- Pass/fail status — the quiz is **passed** if the total score from correct answers exceeds **600 points**.

---

### Front-End / Mobile App

#### Screens

1. **Intro Screen** — Display the quiz rules (including scoring details) and a button to start the quiz.
2. **Quiz Flow** — Present one question at a time with its answer options. Validate that the user selects a valid option before proceeding.
3. **Results Screen** — After the quiz is completed, display:
   - Total score.
   - Number of correct and incorrect answers.
   - Percentage of correct answers.
   - A list of incorrectly answered questions along with their correct answers.
   - A pass or fail message.

#### Communication

The client must retrieve quiz questions from the back-end and submit answers to it for evaluation. No quiz logic should be duplicated on the client side.

---

## What We Value

- **Clean design and separation of concerns** — well-structured, readable code with a clear boundary between layers (e.g. controller / service / data access, or equivalent).
- **Test coverage** — unit tests for the back-end are expected; tests for the front-end are a welcome bonus.
- **Code documentation** — comments where the intent is non-obvious; avoid redundant narration.
- **A README file** — include a brief description of your solution, how to build and run it, and any assumptions or trade-offs you made.
- **Your commit style** — whether you prefer many small incremental commits or a single well-described commit, show us how you naturally work.
- **Your strongest areas** — if you feel you can impress us more on some of these points than others, lean into those.

---

## Submission

1. Work on a **new branch** (branched off `main`/`master`).
2. Push your branch and **open a Pull Request** when you are done.
3. Make sure the README contains everything needed to run the project locally.

---

> **Privacy notice:** This coding challenge is used solely as part of the interview process. It must not be shared with any third party or published to public code repositories.
