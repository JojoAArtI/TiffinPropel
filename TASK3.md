# Task 3 — Release note

Written as if Tiffin were going to the Play Store tomorrow.

## What I'd test by hand before pressing publish

The money path first, because it's the only path that can hurt a user:

- **Paywall copy and timing.** On a fresh install, confirm the paywall appears on the third launch and on every Subscribe tap, and that it states ₹1 today and ₹249 on a named date 24 hours out — before the user taps anything. Verify the named date is actually "tomorrow" across a day boundary (set device clock to 23:50 and check it rolls to the correct date).
- **Purchase persistence through the worst cases:** pay → rotate, pay → background/foreground, and pay → **force-stop** → reopen. The paywall must not come back and the user must read as subscribed in all three.
- **Third-launch gating for paid users:** a subscribed user reaching their third launch must *not* see the paywall.
- **List states:** Loading resolves, Error shows Retry and Retry recovers, pull-to-refresh works.
- **Rotation + process death** on each screen (List, Detail, Paywall) — no lost state, no crash.

**Devices:** a small old phone on **API 24** (the floor — this is where `java.time` desugaring and tight layouts break first), one mid-range current phone (e.g. a Pixel on API 34/35), and one large screen / tablet for the oversized display type. Plus a device with the system font scale and display size cranked up, since the crushed Anton headlines and pill buttons are the most likely things to clip.

## The one thing most likely to break in production, and how I'd find out

**The trial-date / recurring-charge logic across time zones and the day boundary.** The date shown and the actual rollover are computed from the device clock; a timezone or DST edge, or a device with a wrong clock, can show the user the wrong charge date or trigger conversion at the wrong moment. This is also the highest-consequence bug — it's about money.

I'd find out without waiting for a user complaint by watching the **analytics funnel**: `PaywallShown` → `PurchaseCompleted` conversion rate and the time distribution between trial start and activation. A sudden drop in conversion, a spike in activations at an unexpected hour, or purchases with malformed/kitchen-less context would flag it. Crash-free rate and ANR rate in Play Console / Crashlytics would catch the main-thread and state regressions.

## If it broke at 11pm — rollback

It's a client-only app with no server, so rollback is a **Play Console release action**: halt the staged rollout immediately (we'd ship to a small percentage first precisely for this), and promote the last known-good build back to production. Because all critical state is local, there's no server migration to unwind. If the bad build had already corrupted a persisted value, the fix build would include a one-time migration/validation on `AppPreferences` read (clamp bad `trialStartedAt`, re-derive state). I would not hotfix blind at 11pm — halt first, fix in the morning with tests.

## The next three things I'd build, in order

1. **A real payments + backend integration.** Replace the faked purchase with the actual billing flow and move the catalog + trial clock server-side, so the recurring charge isn't at the mercy of the device clock. This is the highest risk and highest value.
2. **UI and instrumented tests** around the paywall gating, persistence through process death, and the list states — the behaviours I verified by hand this time.
3. **The delivery-slot / weekly-ordering flow** for paid users — the feature that turns "subscribed" into an actual daily tiffin, which is the product's reason to exist.
