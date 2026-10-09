# Task 4 — How I used AI

## Tools, and roughly what share of the code

I used **Claude (Opus)** in a two-role loop: one pass acting as planner/auditor (writing the spec for each milestone and reviewing the result against it), and one pass acting as the builder that wrote the code. The agent wrote roughly **90% of the Kotlin and Gradle**. What I owned by hand: the plan and the design system, the exact behaviour specs for each milestone, every correction, and a review of **100% of the diffs** before they were accepted. I didn't accept a milestone on the agent's say-so — I read the actual source each time.

The work was deliberately chunked into milestones (scaffold → data layer → List → Detail → Paywall → features → docs), each with its own acceptance criteria and, for the money-critical paywall, mandatory on-device verification steps. That structure is what made catching mistakes possible — a 2,000-line dump is unauditable; a 150-line milestone against a written spec is not.

## My single best prompt (pasted exactly)

This is the fix prompt I sent after auditing the paywall milestone, once I found the offer copy was hiding the recurring charge and the launch-gate had a race. It's my best because it names each defect precisely, says exactly what to change, and — critically — specifies the verification the agent *hadn't* done:

```
Fix two defects in Milestone 5 of the Tiffin app. Do NOT add features — this is a correctness fix only.

## FIX 1 (CRITICAL) — the paywall OFFER must state the full terms in words, before purchase
The assignment requires the paywall to state, in words, that ₹1 is charged now AND that ₹249 is charged on a named date 24 hours later. Right now the Offer screen only mentions ₹1; the ₹249 + date appear only after purchase. That fails the requirement. Fix:
 - PaywallUiState.Offer must carry the date: data class Offer(val chargeDateText: String)
 - PaywallViewModel init (not paid): compute trialChargeDate(System.currentTimeMillis()), SET _uiState = Offer(chargeDate), then fire PaywallShown once
 - PaywallScreen Offer body MUST show both charges before the Start button:
   "You'll be charged ₹1 today. Your ₹249/month plan then starts automatically on {chargeDateText}."

## FIX 2 (CORRECTNESS) — AppViewModel paid gate must read the real persisted value
AppViewModel init checks isPaid.value (the stateIn cache, defaults false, hydrates async) and can race.
Change to read the real disk value first:
    val paid = prefs.isPaid.first()
    val count = prefs.incrementLaunchCount()
    if (!paid && count == 3) _thirdLaunchPaywall.send(Unit)

## Re-verification (required — paste evidence)
Paid-gate-at-launch-3 (the case not covered before): clear data, then
 launch 1 -> subscribe & purchase -> force-stop ->
 launch 2 -> force-stop ->
 launch 3 (count 3, already paid) -> the paywall must NOT appear.
Paste logcat showing launchCount reaching 3 while paid, and NO PaywallShown(third_launch).
```

## One thing the agent got wrong in my own build (not Task 2), how I caught it, what I did

On the paywall milestone the agent's offer screen read "Try any kitchen for just ₹1. Cancel anytime in 24 hours." — it showed the ₹1 but **hid the ₹249/month recurring charge and the named date until after the user had already paid.** The code compiled, ran, and the agent's own screenshots looked fine, so nothing flagged it automatically.

I caught it by reading the generated `PaywallScreen.kt` against the brief's exact words ("must state, in words, that ₹1 is charged now **and ₹249 on a named date 24 hours later**") and seeing that the `Offer` state didn't carry a date at all and the `PaywallViewModel` never set it. In the same pass I traced the launch-count gate and found it read `isPaid.value` — the asynchronous `stateIn` cache that defaults to `false` — instead of the persisted value, so a paid user could race into the paywall on their third launch. The agent's "paid-gate" test had actually used launch 4, which never even enters the `count == 3` branch, so the real case was untested.

I fixed both with the targeted prompt above, then re-verified the specific sequence the agent had skipped (purchase, then reach launch 3 while paid, paywall suppressed). This is the kind of bug the brief warns about — hiding the recurring charge is precisely a "gets us a one-star review / a chargeback" mistake — and it only surfaced because I was reading the source, not the agent's summary. Earlier milestones had smaller versions of the same lesson: letter-spacing set in `sp` instead of `em` (so the tracking rendered as ~zero), the marquee drawing under the status bar, and the `KitchenViewed` event firing on row tap instead of when the detail actually opened — all caught by reading diffs against the spec.

## One thing I wrote by hand because directing an agent was slower

The **design system and its Compose mapping** — translating the "Slush" style reference into exact colour tokens, the phone-scaled type ramp with crushed line-heights, shape/pill rules, and the "blue is decorative, CTAs are black" constraints. Pinning those values down myself and handing the agent a frozen spec was far faster than iterating an agent toward a look by description, and it kept every screen consistent. The planning doc, this write-up, and the Task 2/3 analyses were likewise quicker to reason through and write directly than to extract from an agent.
