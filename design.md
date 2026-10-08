# Tiffin — Design System (Slush, translated to Android)

> Inflatable sticker universe on pastel paper — rebuilt for a Compose phone app.

**Theme:** light only. No dark mode (dropped deliberately — the whole language is pastel-paper + black hand-cut outlines; a dark variant would fight the aesthetic and isn't worth the budget).

This is the Slush web reference interpreted for Android. Sizes are re-scaled for a phone (the 200–640px display type becomes 44–96sp), fonts are swapped for free bundled substitutes, and the collage/sticker motifs become Compose components. Everything else — the six-color sticker palette, 1px black outlines, pill shapes, flat fills, no shadows, no gradients — carries over unchanged.

---

## 1. Core rules (non-negotiable, straight from Slush)

- **Black 1px outlines everywhere** — every card, button, badge, chip, nav element gets a `1.dp` solid `#000000` border. This is the hand-cut sticker look, not a fallback.
- **Pills + soft cards** — buttons/chips/tags are fully pill-shaped (`CircleShape` / `RoundedCornerShape(50)`); cards are `20.dp`, elevated cards `40.dp`. Never a corner under `16.dp`.
- **Flat fills only. No shadows. No gradients.** Elevation is communicated through color bands and black outlines. `Card` elevation = `0.dp` always.
- **Six-color sticker palette as a shared set** — Electric Blue, Mint Pop, Lavender, Ember, Sunburst, Voltage Violet. Use several per screen; never anoint one as "the accent".
- **Blue is decorative, not an action color.** CTAs are **black-fill** or **outlined-black** only. Never a blue button, never a blue link.
- **Green (Mint Pop) is a sticker color, not a success state.** Don't use it to mean "done" semantically.
- **Display type is a physical object** — huge, crushed leading (line-height 0.80), always paired with a smaller tagline and a sticker/ribbon, never floating alone on flat color.
- **Pastel section bands** — surfaces alternate Sky Wash → Paper White → Concrete Gray to create rhythm without dividers or shadows.

---

## 2. Color tokens → `Color.kt`

```kotlin
// ui/theme/Color.kt
val Carbon        = Color(0xFF000000) // text, borders, filled CTA bg, logo
val PaperWhite    = Color(0xFFFFFFFF) // canvas, card surfaces, text on dark
val SkyWash       = Color(0xFFDCEEFF) // hero / primary section background
val ConcreteGray  = Color(0xFFCCCCCC) // secondary section interlude
val SoftMist      = Color(0xFFE9E9E9) // subtle tints, disabled states
val ElectricBlue  = Color(0xFF4DA2FF) // ribbon / brand surface — DECORATIVE only
val MintPop       = Color(0xFF55DB9C) // decorative wash / checkmark sticker
val Lavender      = Color(0xFFE9CCFF) // gentlest accent card/tag fill
val Ember         = Color(0xFFFB4903) // hot sticker accent, non-veg badge
val Sunburst      = Color(0xFFFFD731) // coin/rating sticker fills (never text bg)
val VoltageViolet = Color(0xFF5C4ADE) // wallet/QR cards, deep accent
```

**Material3 mapping** (light scheme): `background`/`surface` = PaperWhite, `onBackground`/`onSurface` = Carbon, `primary` = Carbon (so default buttons are black-fill), `onPrimary` = PaperWhite, `outline` = Carbon. We mostly style components directly rather than leaning on the scheme.

**Semantic use in Tiffin:**
- **Veg** badge → Mint Pop fill, black outline, leaf glyph.
- **Non-veg** badge → Ember fill, black outline.
- **Rating** → Sunburst coin sticker with ★ + number.
- **Price** → Lavender pill.
- **Paywall / subscribe surfaces** → Voltage Violet (the "wallet/money" color in Slush).
- **Section bands** → SkyWash / PaperWhite / ConcreteGray.

---

## 3. Typography → `Type.kt`

Lateral and Aeonik Pro aren't free, so we bundle substitutes in `res/font/`:

| Slush role | We ship | Why |
|---|---|---|
| Lateral 800 (display) | **Anton** (OFL) | Condensed, heavy, crushed — holds up at tight 0.80 leading |
| Aeonik Pro 500/700 (UI) | **Inter** (OFL) | Clean grotesque, great at small sizes, has tnum |

Alt display face if Anton reads too narrow: **Bowlby One**.

**Phone-scaled type scale** (the web's 200–640px is absurd on a 400dp-wide screen, so re-scaled):

```kotlin
// Display — Anton, lineHeight ~0.80 of size (crushed, non-negotiable)
displayLarge  = 96.sp, lineHeight 77.sp   // paywall hero word, splash
displayMedium = 64.sp, lineHeight 51.sp   // screen banners ("KITCHENS")
displaySmall  = 44.sp, lineHeight 36.sp   // section headers

// UI — Inter
headlineSmall = 24.sp / 700   // card titles, kitchen name
titleMedium   = 18.sp / 700   // subheads, nav labels (+0.03em tracking)
bodyLarge     = 15.sp / 500   // body (-0.01em tracking)
bodyMedium    = 14.sp / 500
labelLarge    = 13.sp / 700   // button text (+0.032em tracking, often UPPERCASE)
labelSmall    = 12.sp / 700   // marquee, captions (+0.032em, UPPERCASE)
```

Tracking: body `-0.01em`; nav/buttons/uppercase labels `+0.032em` (gives pills room). Enable `tnum` on prices/ratings so digits align.

---

## 4. Spacing & shape → `Dimens.kt` / `Shape.kt`

- **Base unit 4dp.** Scale: 4, 8, 12, 16, 20, 24, 28, 32, 40, 48, 60, 80.
- Screen padding 16–24dp. Card padding 20–24dp. Element gaps 4–12dp. Section gap 48dp.

```kotlin
val Shapes = Shapes(
    small  = RoundedCornerShape(16.dp),  // chips/badges (also CircleShape for pills)
    medium = RoundedCornerShape(20.dp),  // cards
    large  = RoundedCornerShape(40.dp),  // elevated cards, paywall sheet
)
val Pill = RoundedCornerShape(percent = 50) // buttons, nav, tags
```

---

## 5. Components → Composables

| Composable | Slush source | Spec |
|---|---|---|
| `StickerButton` (filled) | Filled CTA | Carbon fill, PaperWhite text, Pill shape, 1dp black border, labelLarge uppercase, 10–14dp padding. The one primary action per screen. |
| `GhostButton` (outlined) | Outlined Ghost | PaperWhite fill, Carbon text, 1dp black border, Pill. Secondary actions. |
| `StickerBadge` | Sticker Decoration | Small rounded (16dp) filled chip, 1dp black border, one palette color + glyph. Used for veg/non-veg, "NEW", "TODAY". Slight rotation (±3–6°). |
| `RatingCoin` | Coin sticker | Sunburst circle, 1dp black border, ★ + number, tnum. |
| `KitchenCard` | Card + stickers | PaperWhite `20.dp` card, 1dp border, 0 elevation, holds name/cuisine/price/veg badge/rating. Press = squish scale 0.97. Very slight random tilt for collage feel. |
| `MarqueeBanner` | Marquee Banner | Full-bleed Carbon strip, PaperWhite uppercase labelSmall, scrolls horizontally on a loop. |
| `SectionBand` | Section Background Panel | Full-width color block (SkyWash/PaperWhite/ConcreteGray), no border/shadow. |
| `DisplayHeadline` | Display Headline Block | Anton, crushed leading, Carbon, paired with a tagline + sticker cluster. |
| `PaywallSheet` | QR Download Card vibe | Voltage Violet surface, `40.dp` radius, 1dp black border, white text, StickerButton confirm. |
| `TopNavPill` | Pill Nav / Logo Mark | Circular black-outlined "T" logo left; pill-shaped title; no heavy app bar — keep it light and collage-like. |

---

## 6. Screen treatments

**List** — SkyWash band at top with a `DisplayHeadline` "KITCHENS" (Anton) + tagline "home kitchens near you", a static blue ribbon graphic behind it. Below, a `LazyColumn` of `KitchenCard`s on PaperWhite, each slightly tilted, veg/non-veg sticker + Sunburst rating coin + Lavender price pill. Loading = sticker-themed skeleton (outlined gray blocks). Empty = big sticker + "no kitchens nearby". Error = sad-sticker + GhostButton "RETRY".

**Detail** — header card (kitchen name in Anton displaySmall, cuisine, rating coin, veg badge) on a pastel band; weekly menu as 7 outlined day-rows (day pill + dish); sticky `StickerButton` "SUBSCRIBE" at bottom.

**Paywall** — `PaywallSheet` in Voltage Violet. Big Anton word ("UNLOCK"), the price terms **in words**: "₹1 today, then ₹249/month starting {named date}". Confirm = black `StickerButton` "START TRIAL". Already-paid state replaces it with a Mint-Pop "YOU'RE IN" sticker scene.

---

## 7. Cool features (genuine, on-brand, cheap)

Chosen to amplify the sticker aesthetic and show polish without blowing the 4-hour budget:

1. **Sticker-burst on purchase** — on successful fake purchase, a confetti of palette-colored stickers (coin, rocket, checkmark, wallet) pops and scatters across the screen, then settles. Pure Compose animation (`Animatable` + a handful of drawables). This is the signature delight moment and ties directly to "sticker universe."
2. **Scrolling marquee banner** — the Slush top strip, repurposed as a live "TIFFIN • FRESH FROM HOME KITCHENS • ₹1 TRIAL TODAY •" loop at the top of the List screen. Infinite horizontal scroll via `rememberInfiniteTransition`.
3. **Squishy sticker press** — every button/card scales to ~0.97 on press with a spring, so controls feel like physical stickers you push. One reusable `Modifier.stickerPress()`.
4. **Collage tilt** — each `KitchenCard` carries a tiny deterministic rotation (±4°, seeded by id so it's stable across recomposition/rotation) for the hand-pinned-to-a-board look.
5. **Haptic taps** — light haptic on subscribe/confirm so the trial action has physical weight.
6. **Pull-to-refresh** on the list, re-running the load (and letting you re-hit the Error seam for the demo).

All six are deterministic/seek-safe and survive rotation; none depend on a backend.

---

## 8. What this changes in PLAN.md

- **Removed:** dark mode (explicitly out).
- **Added:** the six cool features above, the bundled fonts (Anton + Inter in `res/font/`), and a `ui/theme/` built from these tokens (`Color.kt`, `Type.kt`, `Shape.kt`, `Dimens.kt`).
- **Unchanged:** architecture, persistence model, analytics, the three required screens, and the cut list.

---

## 9. Quick reference

- text/borders: `#000000` (1dp outlines everywhere)
- backgrounds: `#DCEEFF` / `#FFFFFF` / `#CCCCCC` (section-dependent)
- CTA: black fill (`#000000`) or outlined-black — **never blue**
- sticker palette: `#4DA2FF` `#55DB9C` `#E9CCFF` `#FB4903` `#FFD731` `#5C4ADE`
- fonts: **Anton** (display, crushed 0.80 leading) + **Inter** (UI)
- shapes: pills for buttons/tags, 20dp cards, 40dp elevated — nothing under 16dp
- no shadows, no gradients, no dark mode
