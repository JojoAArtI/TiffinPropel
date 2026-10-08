# Tiffin — Build Plan

Planning document for the Propel · Spark Android Developer Intern assignment. This is the blueprint we build against. It maps every graded requirement to a concrete decision, explains how the finished app behaves end to end, and lists the deliberate extras that make the submission easy to grade and hard to fault.

---

## 1. What we're building

**Tiffin** — an Android app for ordering a daily tiffin from home kitchens nearby. Free users browse; paid users unlock delivery slots and the weekly menu. Money model: a ₹1 trial charged now, then the ₹249/month plan activates on its own 24 hours later.

Single-module Jetpack Compose app, Kotlin, `minSdk 24`. No backend — all data ships as a local JSON asset we write ourselves (twelve kitchens).

---

## 2. The grade is the spec — requirement → decision

Every line here traces to something the assignment asks for, so nothing graded is left to chance.

| Requirement (from the brief) | How we satisfy it |
|---|---|
| Single-module Compose app, Kotlin, minSdk 24 | One `:app` module, 100% Compose + Material3 |
| No backend; bundle 12 kitchens as local JSON | `assets/kitchens.json`, parsed with kotlinx.serialization |
| **List** screen: name, cuisine, price, veg/non-veg, rating | `ListScreen` with all five fields per row |
| Real loading / empty / error states | `sealed UiState { Loading, Empty, Error, Content }` — error has a working Retry |
| **Detail** screen: kitchen + weekly menu + subscribe button | `DetailScreen`, 7-day menu, Subscribe CTA |
| **Paywall** on 3rd launch and any subscribe tap | Launch counter in DataStore + explicit nav on subscribe |
| Paywall states ₹1 now and ₹249 on a named date 24h later | Date computed as `now + 24h`, formatted (e.g. "9 Oct 2026"), shown in words |
| No payment SDK; fake purchase but persist it | Fake "purchase" flips a persisted `isPaid` flag |
| Don't show paywall again once paid | Paywall gate reads `isPaid` first |
| Survive rotation | State in `ViewModel` (`StateFlow`) |
| Survive process death | Durable state in **DataStore** (disk); transient UI state in `SavedStateHandle` |
| Survive force-stop + reopen | DataStore is on disk, so `isPaid` / launch count / trial date are all restored |
| `analytics.kt`: one interface + logcat impl, 4 chosen moments | `Analytics` interface + `LogcatAnalytics`, called at exactly 4 events |
| README with "what I cut and why" at the top | Written last, honestly |

---

## 3. How the app works, end to end

### 3.1 Launch flow
1. On every cold start, `MainActivity` reads the persisted **launch count** from DataStore and increments it.
2. The app shows the **List** screen.
3. If this is the **3rd launch** (count == 3) **and** the user is **not** already paid, we navigate to the **Paywall** once. (We don't re-trigger it on launches 4, 5, … — the "third launch" is a one-time nudge, not a wall on every open.)
4. If the user is already paid, the launch-count trigger is skipped entirely.

### 3.2 Browsing
- **List** loads kitchens from the JSON asset through the repository. We simulate a short load (≈600 ms) so the Loading state is real and visible, and we expose a seam to force an Error (toggle/flaky flag) so Error + Retry are demonstrable rather than theoretical.
- Each row: **name**, **cuisine**, **price per tiffin (₹)**, **veg / non-veg** badge, **rating** (★).
- Tapping a row opens **Detail** for that kitchen.

### 3.3 Detail
- Shows the kitchen header (name, cuisine, rating, veg/non-veg) and its **weekly menu** (7 days, one tiffin description each).
- **Subscribe** button → always opens the **Paywall** (unless already paid, in which case it shows a "You're subscribed" state instead of the paywall).

### 3.4 Paywall + fake purchase
- Copy, in plain words: **"You'll be charged ₹1 today. On {named date, 24h from now} your ₹249/month plan starts automatically."**
- The named date is computed at the moment the paywall is shown: `today + 24h`, formatted as a human date.
- **Confirm (fake purchase)**:
  1. Fire the `purchase_completed` analytics event.
  2. Persist `isPaid = true` and `trialStartedAt = now` to DataStore.
  3. Navigate back; paywall never appears again (launch trigger and subscribe tap both short-circuit on `isPaid`).
- No payment SDK, no real money — the purchase is faked but **durably recorded**.

### 3.5 Persistence model — three layers, each with a job

| Layer | Holds | Survives |
|---|---|---|
| `ViewModel` + `StateFlow` | current UI state (list contents, selected kitchen) | rotation |
| `SavedStateHandle` | transient nav/UI args (e.g. selected kitchen id) | process death while in memory |
| **DataStore (Preferences)** | `isPaid`, `launchCount`, `trialStartedAt` | process death **and** force-stop (it's on disk) |

The force-stop requirement is the reason the money-critical state (`isPaid`) lives in DataStore and nowhere else that can evaporate.

### 3.6 Analytics — the 4 moments we picked
One `Analytics` interface, one `LogcatAnalytics` implementation. We instrument the four events that matter for this funnel:

1. **`list_viewed`** — the app opened and the catalog rendered (top of funnel).
2. **`kitchen_viewed`** — a Detail screen opened (intent signal; carries `kitchenId`).
3. **`paywall_shown`** — the paywall appeared (carries `trigger`: `third_launch` or `subscribe_tap`).
4. **`purchase_completed`** — the fake purchase succeeded (bottom of funnel; the conversion event).

Rationale we'll put in the README: these four let you compute the only metrics that matter for a trial-to-paid product — reach (list), interest (detail), paywall exposure, and conversion — and attribute which trigger drives purchases.

---

## 4. Architecture

```
MainActivity
  └── TiffinApp (NavHost)
        ├── ListScreen   ─ ListViewModel   ─┐
        ├── DetailScreen ─ DetailViewModel ─┼─ KitchenRepository ─ assets/kitchens.json
        └── PaywallScreen─ PaywallViewModel─┘        │
                                                      └─ AppPreferences (DataStore)
  Analytics (interface) ◄── LogcatAnalytics  (injected into ViewModels)
```

- **Pattern:** MVVM, unidirectional data flow. ViewModels expose immutable `StateFlow<UiState>`; the UI sends events up as function calls.
- **DI:** manual (a small `AppContainer` built in `Application`). No Hilt — not worth the setup cost inside a 4-hour budget for one module.
- **Async:** Coroutines + `viewModelScope`. IO (asset read) on `Dispatchers.IO`.
- **Serialization:** kotlinx.serialization, `@Serializable` data classes.

### Package / file layout
```
com.propel.tiffin
├── TiffinApplication.kt        // builds AppContainer
├── MainActivity.kt
├── di/AppContainer.kt
├── data/
│   ├── model/Kitchen.kt        // + MenuItem, enums
│   ├── KitchenRepository.kt    // reads + parses asset, simulates load/error
│   └── AppPreferences.kt       // DataStore: isPaid, launchCount, trialStartedAt
├── analytics/
│   ├── Analytics.kt            // interface + AnalyticsEvent
│   └── LogcatAnalytics.kt
├── ui/
│   ├── list/ListScreen.kt, ListViewModel.kt, UiState.kt
│   ├── detail/DetailScreen.kt, DetailViewModel.kt
│   ├── paywall/PaywallScreen.kt, PaywallViewModel.kt
│   ├── nav/TiffinNav.kt
│   └── theme/   // Material3 theme, dark mode
└── util/DateUtil.kt            // trial date = now + 24h, formatted
assets/kitchens.json
```

---

## 5. Data model & JSON asset

```kotlin
@Serializable
data class Kitchen(
    val id: String,
    val name: String,
    val cuisine: String,
    val pricePerTiffin: Int,      // in ₹
    val veg: Boolean,
    val rating: Double,           // 0.0–5.0
    val weeklyMenu: List<MenuItem>
)

@Serializable
data class MenuItem(val day: String, val dish: String)
```

`assets/kitchens.json` — 12 kitchens, each with a 7-day menu, veg/non-veg mix, varied cuisines (North Indian, South Indian, Gujarati, Bengali, …), prices ₹60–₹140, ratings 3.6–4.9.

---

## 6. Tech stack

- Kotlin 2.0.x, Compose BOM (latest stable), Material3
- `androidx.navigation:navigation-compose`
- `androidx.lifecycle:lifecycle-viewmodel-compose`
- `org.jetbrains.kotlinx:kotlinx-serialization-json`
- `androidx.datastore:datastore-preferences`
- Coroutines
- `minSdk 24`, `targetSdk 35`, `compileSdk 35`
- JUnit for unit tests

---

## 7. Extras — the deliberate edge (cheap to add, high signal)

These are the things that aren't strictly required but make the submission easy to grade and hard to fault. Each is chosen to cost little and prove something the brief is watching for.

- **Honest, exact README** — cut list up top, plus copy-paste build/run steps so "it doesn't build on our machine" never happens.
- **Unit tests on the money-critical logic** — the trial date calculation (`now + 24h` formatting) and the paywall gate (shown ⇔ `!isPaid && (thirdLaunch || subscribeTap)`). These are the two places a bug costs real money, so they're the two places we test.
- **Real Error state with working Retry** — not a dead spinner. We wire a deliberate failure seam so the grader can actually see Loading → Error → Retry → Content.
- **Distinct "Slush" sticker design** — the whole app is themed to the Slush reference (see [design.md](design.md)): pastel bands, black hand-cut outlines, huge Anton display type, six-color sticker palette. Signals real product sense, not a default Material app.
- **Six cool features** (detailed in design.md §7): sticker-burst on purchase, scrolling marquee banner, squishy sticker press, collage card tilt, haptic taps, pull-to-refresh.
- **Accessibility** — content descriptions on icons/badges, so the veg/non-veg and rating read correctly to TalkBack.
- **Clean git history** — the brief says they read it. Small, labelled commits per task/feature, not one dump.
- **Analytics rationale written down** — we say *why* those four events, not just that there are four.
- **Screen recording < 90s** — scripted to show all three screens and the paywall not returning after payment (the one behavior they explicitly want to see).

---

## 8. What we plan to cut (confirm in README)

Scope-cutting is explicitly graded, so these are chosen, not accidental:

- **No Room / real DB** — DataStore covers every persistence requirement here; a relational DB is overkill for a flat 12-row catalog and three flags.
- **No Hilt** — manual DI is faster for one module; Hilt earns its keep at scale, not here.
- **No delivery-slot booking UI** — paid users "unlock" slots conceptually, but building the slot-picker flow isn't in the three required screens. Noted as future work.
- **No network layer** — brief says no backend; we don't build a fake retrofit stack.
- **No dark mode** — the Slush design language is pastel-paper + black outlines; a dark variant fights the aesthetic and isn't worth the budget (see [design.md](design.md)).
- **Light test coverage** — we test the money logic, not the whole UI. Full instrumented UI tests are out of budget and called out as next-up.

(Final cut list gets written after the build, reflecting what actually got squeezed by the 150-minute clock.)

---

## 9. Build & run (will be mirrored in README)

```bash
# Open in Android Studio (Koala+), let Gradle sync, then:
./gradlew assembleDebug        # produces app/build/outputs/apk/debug/app-debug.apk
./gradlew test                 # runs unit tests
```
Or Run ▶ on an emulator / device (API 24+).

---

## 10. Build order & time budget (150 min for Task 1)

1. **Project skeleton + deps + theme** — 15 min
2. **Data: model, kitchens.json, repository (with load/error seam)** — 20 min
3. **DataStore (AppPreferences) + Analytics interface/impl** — 15 min
4. **List screen + ViewModel + all UiStates** — 30 min
5. **Detail screen + ViewModel** — 20 min
6. **Paywall + fake purchase + persistence wiring** — 25 min
7. **Launch-count trigger + process-death/force-stop verification** — 10 min
8. **Unit tests (date + paywall gate)** — 10 min
9. **README + cut list + polish** — 5 min buffer

Then Tasks 2–4 (writeups), APK export, and the screen recording.

---

## 11. Deliverables checklist (how we send it back)

- [ ] GitHub repo (commit as we go — history is reviewed)
- [ ] Debug APK (`app-debug.apk`)
- [ ] README: cut list + Task 2 answers + Task 3 release note
- [ ] Task 4 writeup (AI usage)
- [ ] Screen recording < 90s: three screens + paywall not returning after payment
