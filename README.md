# 👨‍🍳 Chef's Challenge

A Cooking Mama-style guessing game where the server hands you a mystery dish — no name, no recipe, just a vague description — and you have to rebuild it from scratch: pick the right ingredients, drag the right techniques into the right order, beat the clock.

Built as my final project for **CS 220: Applied Data Structures** at Knox College, using a design-doc-first workflow (D4) where the design doc was written and locked *before* a single line of implementation code existed.

> Spring Boot server. Swing client. One REST API holding the whole thing together.

---

## What is this, actually

You're a chef. A customer walks up and describes a dish in the vaguest possible terms — think "a warm, golden dessert with a soft, custardy center" instead of "crème brûlée." You don't get the name. You don't get the recipe. You get 15 ingredients and 10 techniques to choose from, and a countdown timer.

- Toggle the ingredients you think belong
- Drag the techniques into the order you think they happen in
- Submit before time runs out (or don't — timing out just tanks your score)
- Stuck? Burn a hint. Costs you 10 points, reveals one correct item.
- 6 dishes per run, ramping easy → medium → hard
- Get graded PERFECT / GOOD / BAD / TERRIBLE based on how close you got

The twist that made this an interesting *engineering* problem rather than just a fun game idea: **the client can never know the answer before you submit.** Not "the UI hides it" — the server literally cannot serialize that field into a pre-submission response, because the DTO it's using doesn't have one. More on that below.

## Screenshots / Demo

*(drop a gif or a couple screenshots of the Kitchen screen and Result screen here before you submit)*

## Under the hood

```
┌────────────────────┐         HTTP / JSON         ┌─────────────────────────┐
│  Swing Client       │ ───────────────────────────▶ │  Spring Boot Server      │
│  (Java 11)          │ ◀─────────────────────────── │  (Java 17)               │
└────────────────────┘                              └─────────────────────────┘
```

The server owns everything — score, current dish, hints used, time. The client is basically a dumb terminal that renders whatever the server tells it and forwards user actions back. This was a deliberate choice: it means there's exactly one place cheating could theoretically be prevented (the server), and exactly zero places where trusting client-side state could bite me.

**The information-hiding trick.** A dish gets represented two totally different ways depending on where you are in the flow:

- `DishDisplayDTO` — what you see *before* submitting. Description + visual details. No `name` field. No `recipe` field. They don't exist on this class.
- `RecipeReveal` — what you see *after* submitting. The full answer.

Because these are two separate classes instead of one class with a "don't forget to hide this" comment, there's no code path where a developer (future me, an AI coding agent, whoever) could accidentally leak the answer early. The compiler won't let you serialize a field that isn't there.

**Everything's built to survive concurrent games.** Session state lives in a `ConcurrentHashMap<String, GameSession>` keyed by a server-issued UUID, so multiple people can play at once without stepping on each other. And on the client, every HTTP call runs inside a `SwingWorker` — the UI thread never blocks waiting on the network, which matters a lot when you've got a countdown timer that needs to keep ticking smoothly.

## Tech stack

- **Server:** Spring Boot 3.2.4, Java 17, Gradle
- **Client:** Java 11 Swing, Jackson 2.15.2, `java.net.http.HttpClient`, Gradle (fat jar)

## Project layout

```
chefs-challenge/
├── server/
│   └── src/main/java/com/chefchallenge/server/
│       ├── ChefChallengeApplication.java   # Spring Boot entry point
│       ├── SessionController.java          # REST endpoints
│       ├── SessionService.java             # session storage + orchestration
│       ├── SimilarityEngine.java           # grading
│       ├── HintService.java
│       ├── DishRepository.java             # the 8 dishes + ingredient/technique pools
│       ├── model/                          # Dish, GameSession, Difficulty, Tier
│       └── dto/                            # DishDisplayDTO, RecipeReveal, etc.
│
├── client/
│   └── src/main/java/com/chefchallenge/client/
│       ├── ChefChallengeClientApplication.java
│       ├── MainFrame.java                  # CardLayout, owns the ApiClient + sessionId
│       ├── StartPanel.java
│       ├── KitchenPanel.java               # the actual game screen
│       ├── ResultPanel.java
│       ├── ApiClient.java
│       └── model/                          # mirrors the server DTOs
│
├── design/
│   ├── REST_API_Design.md
│   ├── Server_Design.md
│   └── Client_Design.md
│
└── README.md
```

## The API

Base path: `/api/session`

| Endpoint | Method | Body | What it does |
|---|---|---|---|
| `/start` | POST | — | New session, returns `sessionId` + the first dish (display-only) |
| `/{id}/hint` | POST | `{ "hintType": "INGREDIENT" \| "TECHNIQUE" }` | Reveals one item, docks 10 points |
| `/{id}/submit` | POST | `{ "ingredients": [...], "techniques": [...] }` | Grades it, reveals the answer, hands back the next dish |
| `/{id}/timeout` | POST | — | Same shape as submit, but you get nothing for it |

404 on an unknown session, 400 if you try to submit to a session that's already over or ask for a hint you've already used up.

## Why these data structures (the part I actually got graded on)

| Where | Structure | Why | What I didn't use instead |
|---|---|---|---|
| Session storage | `ConcurrentHashMap<String, GameSession>` | O(1) lookup, thread-safe with zero manual locking | Plain `HashMap` — breaks under Spring Boot's concurrent request handling |
| Dish sequence per session | `List<Dish>` + an index | O(1) access to "current dish," trivial to advance | A `Queue` — you lose the ability to look back, for no upside |
| Recipe ingredients/techniques | `Set<String>` | O(1) `contains()` for grading, and duplicates in a recipe are meaningless anyway | `List<String>` — linear scans on every grade check |
| Revealed hints | `HashSet<String>` | O(1) check so you can't get handed the same hint twice | `List<String>` — same linear-scan problem |
| Client's selected ingredients | `LinkedHashSet<String>` | O(1) like a HashSet, but keeps the order you clicked things in for display | `ArrayList` — O(n) duplicate checks every toggle |

## Grading

Pure recall — did you find what was actually required, ignoring how many wrong guesses you also threw in:

```
ingredientRecall = |your ingredients ∩ required ingredients| / |required ingredients|
techniqueRecall  = |your techniques  ∩ required techniques|  / |required techniques|
similarity       = (ingredientRecall + techniqueRecall) / 2
```

| Similarity | Tier | Points |
|---|---|---|
| ≥ 0.90 | PERFECT | +100 |
| ≥ 0.60 | GOOD | +50 |
| ≥ 0.30 | BAD | 0 |
| < 0.30 | TERRIBLE | −30 |
| timed out | TIMEOUT | 0 |

I went with recall instead of precision on purpose — I wanted guessing to feel exploratory, not punishing. Selecting every ingredient in the pool "just in case" doesn't tank you, it just won't get you PERFECT either.

## Running it locally

**You'll need:** JDK 17 (client also runs fine on it, so you don't need to juggle two JDKs), Gradle.

**1. Fire up the server**

```bash
cd server
./gradlew bootRun
```

Sanity check:

```bash
curl -X POST http://localhost:8080/api/session/start
```

You should get a `sessionId` and a dish description back — and notice there's no dish name anywhere in that response. That's the whole point.

**2. Build and launch the client**

```bash
cd client
./gradlew build
java -jar build/libs/chefchallenge-<lastname>.jar
```

(swap `<lastname>` for whatever your build actually spits out — Gradle's configured to name the jar after the student per course convention)

Hit Start in the client, and you're playing against your own local server.

## Poking at the API directly

Before trusting the client, I ran the whole loop through curl:

```bash
curl -X POST http://localhost:8080/api/session/start

curl -X POST http://localhost:8080/api/session/<id>/hint \
  -H "Content-Type: application/json" \
  -d '{"hintType":"INGREDIENT"}'

curl -X POST http://localhost:8080/api/session/<id>/submit \
  -H "Content-Type: application/json" \
  -d '{"ingredients":["flour","egg"],"techniques":["whisk","bake"]}'

curl -X POST http://localhost:8080/api/session/<id>/timeout
```

Worth checking by hand at least once: the `/start` and `/hint` responses genuinely contain no `name` or `recipe` field. It's not just missing from what the client chooses to render — it's not in the payload at all.

## How this got built (D4)

This project followed **Design Doc Driven Development**, not "vibe code it and see":

1. **Negotiate** — talked through every rule (scoring, session structure, timer behavior, hint costs) with an LLM design partner before writing anything down formally.
2. **Design doc** — turned that into `REST_API_Design.md`, `Server_Design.md`, `Client_Design.md`. These were the contract, not just notes.
3. **Coding agent** — Claude Code implemented against the docs.
4. **Test, break, fix, repeat** — real bugs that came up: a `NullPointerException` on an empty submit, the countdown timer still firing *after* a submit had already gone out, an ingredient pool that got hardcoded in the wrong place instead of pulled from `DishRepository`, and ingredient toggles not resetting between rounds. Each fix that changed behavior got reflected back into the design doc, not just the code — otherwise the doc becomes a lie.
5. **Review** — checked the final implementation against the docs for drift before calling it done.

The design docs in [`/design`](./design) aren't just leftover scaffolding — treat them as the source of truth. If the code and a doc ever disagree, that's a bug somewhere, and it's not automatically the code that's wrong.

## Where it falls short

Being straight about this instead of burying it:

- Grading is recall-only, so spamming every ingredient in the pool is a viable (if boring) strategy.
- Everything lives in memory. Restart the server, every active game is gone.
- The 8 dishes are hardcoded in `DishRepository`. Adding a 9th means a code change and redeploy, not a content update.
- No auth — holding a valid `sessionId` is the only thing gating access to a session. Fine for a class project running on localhost, not fine for anything public.

## What I'd do next

- Persist sessions somewhere real so a server restart doesn't nuke everyone's progress
- Add a precision penalty so over-selecting stops being free
- Move the dish pool out of hardcoded Java and into a JSON/DB source
- A leaderboard, because every game needs one eventually

## Credits

Built by Danny for CS 220: Applied Data Structures, Knox College (Spring 2026) — Jaime Spacco & Jurdana Masuma Iqrah. Designed through negotiation with Claude, implemented by Danny.
