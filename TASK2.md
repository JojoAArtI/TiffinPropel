# Task 2 — Fix what the agent wrote

The agent produced this for the list screen. It compiles and runs, but has four separate problems.

```kotlin
@Composable
fun KitchenList(vm: KitchenViewModel) {
    var items = mutableListOf<Kitchen>()
    LaunchedEffect(items) {
        val res = URL(BASE + "?key=" + "sk_live_9f2c41ab")
            .readText()
        items.addAll(parse(res))
    }
    LazyColumn { items(items) { KitchenRow(it) } }
}
```

## The four problems

1. **State isn't Compose state, so the list never appears → the one-star review.** `var items = mutableListOf<Kitchen>()` is a plain list, recreated on every recomposition and invisible to Compose's snapshot system. `items.addAll(...)` mutates it without triggering recomposition, so `LazyColumn` keeps rendering the original empty list. The user stares at a blank screen forever even though data loaded. **Fix:** hold the data as observable state — `mutableStateListOf` / a `State<List>` from the ViewModel — so writes recompose the list.

2. **A live secret key is shipped in the client → the bill.** `"sk_live_9f2c41ab"` is hardcoded into the app and put in a URL query string. It ships inside the APK (trivially decompiled) and leaks in logs/proxies/caches via the URL. A leaked `sk_live_` key gets abused and runs up charges on our account. **Fix:** never embed a secret in the client. Call our own backend, which holds the key server-side; the client never sees it. (And rotate that key — it's already burned.)

3. **Blocking network on the main thread → jank / ANR / crash.** `URL(...).readText()` is synchronous blocking IO. `LaunchedEffect` runs its block in the composition's coroutine on `Dispatchers.Main`, so the UI thread blocks on the network — strict mode flags `NetworkOnMainThreadException`, and in practice it freezes or ANRs. Secondary bug: keying the effect on `items` (a list recreated each recomposition) makes the effect's lifecycle unstable. **Fix:** do IO on `Dispatchers.IO` (ideally in the ViewModel), and key the effect on something stable (`Unit`, or the vm).

4. **No loading / empty / error handling, and exceptions are uncaught → crash on any failure.** `readText()` and `parse()` throw on network or parse failure; there's no `try/catch`, so a flaky connection crashes the screen. There are also no Loading, Empty, or Error states — the brief requires all three. **Fix:** wrap the fetch in `try/catch`, and drive the UI from a `UiState { Loading, Empty, Error, Content }`.

## Corrected version

The real fix moves the work into the ViewModel (where it belongs) and leaves the composable to render state. Shown here with the fetch kept close for readability:

```kotlin
// ViewModel — owns the data, the dispatcher, and the state.
class KitchenViewModel(
    private val repository: KitchenRepository   // calls OUR backend; no client-side secret
) : ViewModel() {

    private val _uiState = MutableStateFlow<KitchenListUiState>(KitchenListUiState.Loading)
    val uiState: StateFlow<KitchenListUiState> = _uiState

    init { load() }

    fun load() {
        _uiState.value = KitchenListUiState.Loading
        viewModelScope.launch {                      // off the main thread
            _uiState.value = try {
                val kitchens = withContext(Dispatchers.IO) { repository.getKitchens() }
                if (kitchens.isEmpty()) KitchenListUiState.Empty
                else KitchenListUiState.Content(kitchens)
            } catch (e: Exception) {
                KitchenListUiState.Error(e.message ?: "Couldn't load kitchens")
            }
        }
    }
}

sealed interface KitchenListUiState {
    data object Loading : KitchenListUiState
    data object Empty : KitchenListUiState
    data class Error(val message: String) : KitchenListUiState
    data class Content(val kitchens: List<Kitchen>) : KitchenListUiState
}

@Composable
fun KitchenList(vm: KitchenViewModel) {
    val state by vm.uiState.collectAsStateWithLifecycle()
    when (val s = state) {
        is KitchenListUiState.Loading -> LoadingState()
        is KitchenListUiState.Empty   -> EmptyState()
        is KitchenListUiState.Error   -> ErrorState(s.message, onRetry = vm::load)
        is KitchenListUiState.Content -> LazyColumn {
            items(s.kitchens, key = { it.id }) { KitchenRow(it) }
        }
    }
}
```

Key changes: observable state so the list recomposes; the secret removed from the client and the call routed through the backend; IO off the main thread; and real Loading / Empty / Error / Content handling with no uncaught exceptions.
