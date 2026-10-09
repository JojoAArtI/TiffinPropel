# Tiffin

An Android app for ordering a daily tiffin from home kitchens nearby. Free users browse; a ₹1 trial unlocks the subscription, which rolls over to ₹249/month 24 hours later. Built for the Propel · Spark Android Developer Intern take-home.

Single-module Jetpack Compose app, Kotlin, `minSdk 24`. No backend — twelve kitchens ship as a local JSON asset.

---

## What I cut, and why

I built to a ~4-hour core and cut deliberately. An honest cut beats a half-finished screen.

- **No Room / SQLite.** Three flags (`isPaid`, `launchCount`, `trialStartedAt`) and a flat 12-row catalog don't need a relational DB. DataStore covers every persistence requirement, including surviving force-stop. A DB would be ceremony with no payoff here.
- **No Hilt.** One module doesn't earn a DI framework. A hand-written `AppContainer` built in `Application` is faster to write and read. Hilt pays off at scale, not at this size.
- **No delivery-slot / weekly-ordering flow.** Paid users conceptually unlock slots, but a slot-picker isn't one of the three required screens. It's the obvious next feature, not part of the core.
- **No network layer.** The brief says no backend, so there's no Retrofit/OkHttp stack to fake. Data is read from `assets/kitchens.json`.
- **Tests are scoped to the money logic only.** I unit-tested the trial-date calculation (the thing that, if wrong, charges people on the wrong day). I did not write UI or instrumented tests — those are the first thing I'd add next, but they're lower-value than the app itself inside the budget.
- **The Error state is reachable via a hidden trigger, not a real network failure** (there's no network). Long-press the "Tiffin" header on the list to force the Error → Retry path so it's demonstrable. The Empty state is implemented but won't appear with the bundled data.

---

## Build & run

Requires Android Studio (Koala or newer) and an Android SDK with platform 35.

1. Open the project in Android Studio and let Gradle sync, **or** build from the command line.
2. Create `local.properties` in the project root if it doesn't exist, pointing at your SDK:
   ```
   sdk.dir=/absolute/path/to/Android/sdk
   ```
3. Build the debug APK:
   ```bash
   ./gradlew assembleDebug
   ```
   Output: `app/build/outputs/apk/debug/app-debug.apk`
4. Run the unit tests:
   ```bash
   ./gradlew testDebugUnitTest
   ```
5. Or press **Run ▶** on an emulator / device (API 24+).

No API keys or secrets are required.

---

## How it works

- **List** — kitchens from `assets/kitchens.json` (name, cuisine, price, veg/non-veg, rating) as image-led cards. Live search (by name/cuisine) and tappable cuisine filter chips. Real Loading (skeleton), Empty, and Error (with Retry) states, plus pull-to-refresh.
- **Detail** — one kitchen: hero photo, header (rating, cuisine, price), the week's menu (read-only), and a Subscribe button.
- **Paywall** — opens on the **third launch** and on any **Subscribe** tap. States, in words, that **₹1 is charged today and ₹249/month starts automatically on a named date 24 hours later**. The purchase is faked but **persisted**; the paywall never reappears once paid, and a subscribed user sees "SUBSCRIBED ✓" instead.

### Persistence
Three layers, each with a job:
| Layer | Holds | Survives |
|---|---|---|
| `ViewModel` + `StateFlow` | current UI state | rotation |
| `SavedStateHandle` / nav args | selected kitchen id | process death in memory |
| **DataStore (Preferences)** | `isPaid`, `launchCount`, `trialStartedAt` | process death **and** force-stop (on disk) |

The money-critical flag `isPaid` lives in DataStore specifically so it survives force-stop + reopen.

### Analytics — the four moments
`analytics/Analytics.kt` is a single interface with a Logcat implementation (`LogcatAnalytics`, tag `TiffinAnalytics`). It's called at exactly four events, which together give the whole trial funnel plus attribution:

1. **`ListViewed`** — catalog rendered (reach / top of funnel).
2. **`KitchenViewed(kitchenId)`** — a Detail opened (interest; which kitchens).
3. **`PaywallShown(trigger)`** — paywall offer shown; `trigger` is `third_launch` or `subscribe_tap` (exposure + which path drives it).
4. **`PurchaseCompleted`** — fake purchase succeeded (conversion).

With these four you can compute list→detail→paywall→purchase conversion and tell whether the third-launch nudge or the subscribe tap converts better.

---

## Architecture

MVVM, unidirectional data flow, Compose + Material3.

```
MainActivity → TiffinApp (NavHost)
  ├── ListScreen    ─ ListViewModel    ─┐
  ├── DetailScreen  ─ DetailViewModel  ─┼─ KitchenRepository ─ assets/kitchens.json
  └── PaywallScreen ─ PaywallViewModel ─┘        │
  AppViewModel (launch-count trigger) ───────────┴─ AppPreferences (DataStore)
  Analytics (interface) ◄── LogcatAnalytics
```

- Manual DI via `di/AppContainer.kt`, created in `TiffinApplication`.
- `kotlinx.serialization` for the JSON asset; `java.time` for the trial date (core-library desugaring enabled for API 24/25).

## Design

The UI is a clean, image-forward food-delivery look modelled on Swiggy (see [design.md](design.md)): white image-led cards with real food photos, green rating pills, the standard veg/non-veg square marks, an orange (`#FC8019`) accent for CTAs and highlights, light theme only. The list has a working search (filters kitchens live by name/cuisine) and tappable cuisine category chips; the detail screen is a restaurant page with a hero photo, header, and the week's menu; the paywall is a clean white sheet with an orange CTA. Primary CTAs are orange, ratings green. A celebratory sticker-burst plays on a successful purchase, plus haptics on the money action and pull-to-refresh on the list.

Fonts: Inter (OFL), bundled in `app/src/main/res/font/`.

### Food images
The 12 food photos in `app/src/main/assets/food/` are from Wikimedia Commons (Creative Commons licensed), one per kitchen, roughly matching each cuisine (masala dosa, paneer butter masala, biryani, etc.). They're loaded with Coil as local assets — no network needed.

---

## Submission contents

- This repo (commit history included).
- Debug APK: `app/build/outputs/apk/debug/app-debug.apk`.
- [TASK2.md](TASK2.md) — the four bugs in the agent-written list code, and the fix.
- [TASK3.md](TASK3.md) — the release note.
- [TASK4.md](TASK4.md) — how I used AI.
- A screen recording (< 90s) showing the three screens and the paywall not returning after payment.
