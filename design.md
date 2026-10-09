# Tiffin — Design System (Swiggy-style food delivery)

> A clean, image-forward food-ordering UI modelled on Swiggy. Light theme, white cards, food photos, green ratings, orange accents.

This replaces the earlier "Slush" sticker design entirely. The app should read like a real Indian food-delivery app: a scrollable feed of restaurant (kitchen) cards, each led by a food photo, with a green rating pill, cuisine, price-for-two, delivery time, and offer badges. The detail screen is a restaurant page with a menu of dishes, each with its own photo and an ADD button.

**Theme:** light only. White background, dark text. No dark mode.

What we keep from the previous build: the ₹1 trial purchase flow, the celebratory burst animation on purchase, and all the logic underneath (persistence, analytics, paywall gating, loading/empty/error states). Only the visual layer and the data (food images) change.

What we remove: the scrolling marquee strip, the collage card tilt, the oversized crushed display type (Anton), the pastel sticker palette, the "no shadows / no gradients" rule. Those were the Slush language and are gone.

---

## 1. Core look (from the Swiggy reference)

- **White, image-forward cards** with subtle elevation (real shadows are fine now). Food photo on top, info below.
- **Green rating pill** — e.g. a small rounded green chip "★ 4.3 (3.0K+)", white star, used everywhere a rating appears.
- **Orange accent** (Swiggy orange) for the active tab, FREE DELIVERY text, primary CTAs, and highlights.
- **Veg / Non-veg marks** — the standard Indian square-in-square icon: green square for veg, red/maroon for non-veg (not word badges).
- **Clean sans typography** (Inter), normal weights and sizes — no sculptural display type.
- **Rounded corners** ~12–16dp on cards and images, pill-shaped chips/filters.
- **Overlay badges on the photo**: delivery time ("20–25 MINS"), "FREE DELIVERY", and an offer ribbon ("Items at ₹99", "60% OFF UPTO ₹120").

---

## 2. Color tokens → `Color.kt`

```kotlin
// Brand
val SwiggyOrange   = Color(0xFFFC8019) // primary accent, active tab, FREE DELIVERY, primary CTA
val OrangeDark     = Color(0xFFE46B0A) // pressed / darker orange

// Text
val TextPrimary    = Color(0xFF02060C) // headings, restaurant names
val TextSecondary  = Color(0xFF3D4152) // body
val TextTertiary   = Color(0xFF7E808C) // meta (cuisine, locality, price-for-two)

// Rating (green)
val RatingGreen    = Color(0xFF48C479) // rating pill background (light)
val RatingGreenDk  = Color(0xFF267E3E) // rating text on light pill / dark green

// Veg / Non-veg marks
val VegGreen       = Color(0xFF0F8A65)
val NonVegRed      = Color(0xFFE43B4F)

// Surfaces
val Surface        = Color(0xFFFFFFFF) // cards, page
val SurfaceMuted   = Color(0xFFF0F0F5) // search bar fill, skeletons
val Divider        = Color(0xFFE9E9EB)
val Scrim          = Color(0x66000000) // gradient/overlay on photos for white badge text
```

**Material3 light scheme:** `primary` = SwiggyOrange, `onPrimary` = White, `background`/`surface` = White, `onBackground`/`onSurface` = TextPrimary, `outline` = Divider.

**Semantic use:**
- Rating → green pill (RatingGreen bg, white or RatingGreenDk text).
- Veg/Non-veg → square marks (VegGreen / NonVegRed).
- Primary CTA (Subscribe, Start ₹1 Trial) → SwiggyOrange filled, white text.
- ADD button (dish) → white fill, 1dp green border, green text (Swiggy style).
- FREE DELIVERY / delivery time → orange text on light, or white on photo scrim.

---

## 3. Typography → `Type.kt` (Inter only)

Drop Anton. Use **Inter** (already bundled) throughout. Normal food-app scale:

```kotlin
titleLarge    = 22.sp / 700   // screen section headers ("Top rated near you")
titleMedium   = 18.sp / 700   // restaurant / dish name
bodyLarge     = 15.sp / 500   // body
bodyMedium    = 14.sp / 500   // cuisine, meta
labelLarge    = 14.sp / 700   // buttons (ADD, SUBSCRIBE)
labelMedium   = 13.sp / 600   // rating pill, badges
labelSmall    = 11.sp / 700   // overlay badges (MINS, FREE DELIVERY), UPPERCASE
```

Tracking near-default (slightly tight on headings is fine). Enable `tnum` on ratings/prices. The Anton font file can stay in `res/font/` unused or be removed; it's no longer referenced.

---

## 4. Shape & spacing

- Cards / food images: `RoundedCornerShape(16.dp)`.
- Chips / filter pills / rating pill: `RoundedCornerShape(8.dp)` or pill.
- ADD button: `RoundedCornerShape(8.dp)`.
- Base spacing unit 4dp; screen horizontal padding 16dp; card gap 16–20dp; card inner padding 12dp.
- Card elevation: subtle, ~2–4dp (shadows are allowed now).

---

## 5. Food images

Each kitchen gets a real food photo. Since there's no backend, images are **bundled as local assets** and loaded with **Coil** (`AsyncImage`) from `file:///android_asset/...`.

- Add an `imageAsset` field to `Kitchen` (filename, e.g. `"food/udupi.jpg"`).
- Bundle ~12 permissively-licensed food photos (Unsplash) in `app/src/main/assets/food/`, one per kitchen, roughly matching the cuisine (dosa, thali, biryani, poori, etc.).
- Category chips on the list can reuse a few of these as small circular thumbnails.
- Note the image source/license in the README.

---

## 6. Components → Composables

| Composable | Reference | Spec |
|---|---|---|
| `RestaurantCard` (the kitchen card) | Swiggy list card | White card, 16dp, subtle elevation. Top: food photo (16:9-ish, rounded top) with overlay badges — delivery time + FREE DELIVERY (bottom-right on a scrim), optional offer ribbon bottom-left ("Items at ₹99"). A heart (favourite) top-right. Below the photo: restaurant name (titleMedium), a row with the green rating pill + "• time", then cuisine + "• ₹X for two" in TextTertiary, then locality + distance. Tap → detail. |
| `RatingPill` | green rating chip | RoundedCornerShape(8.dp), RatingGreen bg, "★ 4.3" white, optional "(3.0K+)". tnum. |
| `VegMark` / `NonVegMark` | veg/non-veg square | 16dp square, 1.5dp border (VegGreen / NonVegRed), filled dot centre. |
| `OfferRibbon` | "Items at ₹99" | Small badge over the photo, orange/white. |
| `SearchBar` | Swiggy search | Rounded SurfaceMuted field, leading search icon, "Search for dishes/kitchens", mic optional (decorative). |
| `CategoryChip` | Idli/Dosa/Biryani | Small circular food thumbnail + label underneath. Horizontal scroll row. |
| `FilterChip` | Veg / Ratings 4.0+ | Pill, 1dp Divider border, white, optional active = orange tint. |
| `DishRow` | Swiggy menu item | Left: veg/non-veg mark, Bestseller tag (optional), dish name (titleMedium), rating pill, price "₹137", short description. Right: dish photo (rounded) with a white ADD button (green border/text) overlapping its bottom. |
| `PrimaryButton` | Subscribe / Start Trial | SwiggyOrange filled, white labelLarge, 8dp radius, full-width. |
| `AddButton` | dish ADD | White fill, 1dp green border, green "ADD", 8dp radius. |

No marquee. No tilt. No sticker palette.

---

## 7. Screen treatments

**List (home)** — top: a simple location/header row ("Hostell ▸" style — a title + address line) then a `SearchBar`, then a horizontal `CategoryChip` row, then a section header "Top rated near you", then the vertical feed of `RestaurantCard`. Real Loading (skeleton cards), Empty, Error (with Retry), pull-to-refresh. Upright, grid/list — nothing sideways.

**Detail (restaurant)** — a header card: restaurant name (titleLarge), green rating pill + "7.7K+ ratings", "20–25 mins | locality", maybe a "Free delivery above ₹49" strip. Then a `SearchBar` ("Search for dishes"), veg/non-veg + rating filter chips, then the menu: a "Recommended (N)" section of `DishRow`s built from the kitchen's weekly menu (treat each day's dish as a menu item with a photo, a price, and an ADD). A sticky bottom **Subscribe** `PrimaryButton` (orange) — unchanged behaviour: opens the paywall; shows "SUBSCRIBED" when paid.

**Paywall** — restyle to clean Swiggy: white sheet, orange primary CTA. Keep the exact terms copy ("You'll be charged ₹1 today. Your ₹249/month plan starts automatically on {date}.") and the **purchase burst animation** on success (the part the user likes). A clean "YOU'RE IN!" success state with the burst, orange "Browse Kitchens" button.

---

## 8. What changes vs the old build

- **Removed:** MarqueeBanner, collage tilt, Anton display usage, Slush palette (Lavender/VoltageViolet/Sunburst/etc.), sticker badges, RatingCoin, PricePill, the "no shadows/gradients" rule.
- **Added:** Swiggy palette, Coil + bundled food images, RestaurantCard, RatingPill, Veg/NonVeg marks, DishRow with ADD, CategoryChips, Swiggy-style list/detail/paywall layouts.
- **Kept:** paywall ₹1/₹249 terms + gating + persistence, purchase burst, analytics (4 events), loading/empty/error states, pull-to-refresh, haptics on the money action.

---

## 9. Quick reference

- primary/accent: `#FC8019` (orange) — CTAs, FREE DELIVERY, active tab
- text: `#02060C` / `#3D4152` / `#7E808C`
- rating: green pill `#48C479` bg, `#267E3E`/white text
- veg `#0F8A65`, non-veg `#E43B4F` (square marks)
- surfaces: white, muted `#F0F0F5`, divider `#E9E9EB`
- font: Inter throughout (no Anton)
- cards 16dp rounded, subtle elevation, food photo on top
- shadows allowed; gradients only as photo scrims
- keep: ₹1 trial + purchase burst; drop: marquee, tilt, big display type
```

