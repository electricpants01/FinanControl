# Architecture: FinanControl

## Pattern: MVVM + Repository (Model-View-ViewModel-Repository)

The project follows the **MVVM + Repository** architecture pattern. The `Repository` layer wraps Room DAO calls — ViewModels and Fragments never access DAOs directly.

```
┌─────────────────────────────────────────────────────────────┐
│                        ACTIVITY                             │
│  MainActivity                                               │
│  - Hosts NavHostFragment                                    │
│  - Implements AddCategoryListener & AddIncomeListener      │
│  - Delegates dialog results to CategoryViewModel            │
└─────────────────────────────────────────────────────────────┘
        │
        ▼
┌───────────────────┐    ┌───────────────────┐
│  CategoryFragment │    │  InsightFragment  │
│  (View)           │    │  (View)           │
└────────┬──────────┘    └────────┬──────────┘
         │                        │
         ▼                        ▼
┌───────────────────┐    ┌───────────────────┐
│ CategoryViewModel │    │ InsightViewModel  │
│ (ViewModel)       │    │ (ViewModel)       │
└────────┬──────────┘    └────────┬──────────┘
         │                        │
         ▼                        ▼
┌───────────────────┐    ┌───────────────────┐
│CategoryRepository │    │ InsightRepository │
│ (Repository)      │    │ (Repository)      │
└────────┬──────────┘    └────────┬──────────┘
         │                        │
         ▼                        ▼
┌─────────────────────────────────────────────────┐
│              FinancialDB (Room Database)         │
│  ┌──────────────┐  ┌──────────────┐             │
│  │ CategoryDao  │  │  IncomeDao   │             │
│  └──────┬───────┘  └──────┬───────┘             │
│         │                 │                      │
│         ▼                 ▼                      │
│  ┌──────────┐      ┌──────────┐                 │
│  │ Category │      │  Income  │                 │
│  │ (Entity) │      │ (Entity) │                 │
│  └──────────┘      └──────────┘                 │
└─────────────────────────────────────────────────┘
```

## Data Flow Rule
```
Fragment → ViewModel → Repository → DAO → Room DB
```
- **Fragments** observe LiveData from ViewModels only — never call DAOs or DB directly
- **ViewModels** use Repository methods — never access FinancialDB or DAOs directly (except for lazy repo initialization)
- **Repositories** wrap DAO calls and expose `LiveData` for reads, `suspend` functions for writes
- **DAOs** are only called from within Repositories

## Package Structure

```
com.locotoDevTeam.financontrol/
├── FinanControlApp.kt              # @HiltAndroidApp application (DI entry point)
├── di/
│   ├── DatabaseModule.kt           # Provides @Singleton FinancialDB, CategoryDao, IncomeDao
│   └── RepositoryModule.kt         # Provides CategoryRepository, InsightRepository
├── data/
│   ├── adapter/
│   │   ├── CategoryAdapter.kt       # RecyclerView adapter for categories
│   │   └── InsightAdapter.kt        # RecyclerView adapter for income/expense items
│   ├── dialog/
│   │   ├── AddCategoryDialog.kt     # DialogFragment for adding a category
│   │   ├── AddIncomeDialog.kt       # DialogFragment for adding income/expense
│   │   └── MaterialAlert.kt         # Utility for MaterialAlertDialogBuilder
│   └── FBMessaging/
│       └── PushNotificationService.kt  # FirebaseMessagingService
│
├── database/
│   ├── dao/
│   │   ├── CategoryDao.kt           # Room DAO for Category entity
│   │   └── IncomeDao.kt             # Room DAO for Income entity
│   ├── entity/
│   │   ├── Category.kt              # Category Room entity
│   │   └── Income.kt                # Income Room entity
│   └── FinancialDB.kt               # Room Database class (provided via Hilt; manual singleton deprecated)
│
├── fancyChart/
│   ├── data/
│   │   ├── AxisValue.java
│   │   ├── ChartData.java
│   │   └── Point.java
│   ├── FancyChart.java              # Custom chart View
│   ├── FancyChartPointListener.java
│   ├── FancyChartStyle.java
│   └── MyFancyChartBuilder.kt       # Builder helper for chart data
│
├── ui/
│   ├── category/
│   │   ├── CategoryFragment.kt
│   │   ├── CategoryRepository.kt    # Wraps CategoryDao + IncomeDao
│   │   └── CategoryViewModel.kt
│   ├── insight/
│   │   ├── InsightFragment.kt
│   │   ├── InsightRepository.kt     # Wraps IncomeDao
│   │   └── InsightViewModel.kt
│   ├── MainActivity.kt
│   └── SplashActivity.kt
│
└── util/
    └── StringExtension.kt           # Extension functions (date formatting)
```

## Dependency Injection (Hilt)

Introduced in CHRIS-230. Hilt replaces the manual `FinancialDB.getAppDataBase(context)` singleton with a compile-time DI graph.

```
FinanControlApp (@HiltAndroidApp)
        │  builds SingletonComponent
        ▼
DatabaseModule  ──provides──▶ @Singleton FinancialDB ──▶ CategoryDao, IncomeDao
        │
        ▼
RepositoryModule ──provides──▶ @Singleton CategoryRepository, InsightRepository
        │
        ▼
@HiltViewModel CategoryViewModel / InsightViewModel  (obtained via viewModels()/activityViewModels()
        ▲                                              from @AndroidEntryPoint owners)
        │
@AndroidEntryPoint MainActivity, SplashActivity
```

- **`FinanControlApp`** — `@HiltAndroidApp`, registered as `android:name=".FinanControlApp"` in the manifest.
- **`DatabaseModule`** (`@InstallIn(SingletonComponent::class)`) — provides `@Singleton FinancialDB` via `Room.databaseBuilder(...)`, plus `CategoryDao`/`IncomeDao`.
- **`RepositoryModule`** — provides `@Singleton` repository instances built from the injected DAOs.
- **Activities** — `MainActivity` and `SplashActivity` are `@AndroidEntryPoint`; their ViewModels resolve through the Hilt `ViewModelFactory`.
- **ViewModels** — `CategoryViewModel` and `InsightViewModel` are `@HiltViewModel` with `@Inject constructor()`.

> **Migration note (CHRIS-230 scope):** DI scaffolding only. ViewModels still lazily build repositories from `Context` (`initRepository`/`getRepository`) for backward compatibility — removing `Context` from ViewModels and switching to constructor-injected repositories is tracked in **CHRIS-231**. The DI graph (modules) already exposes injectable repositories for that follow-up.

## Repository Layer

### CategoryRepository
- **Location**: `ui/category/CategoryRepository.kt`
- **Wraps**: `CategoryDao` + `IncomeDao`
- **Methods**:
  - `getAllCategories(): LiveData<List<Category>>` — reactive list for UI
  - `insertCategory(category: Category)` — suspend
  - `deleteCategoryById(categoryId: Long)` — suspend (deletes incomes first, then category)
  - `insertIncome(income: Income)` — suspend
  - `getSumIncome(): Double` — suspend
  - `getSumExpense(): Double` — suspend

### InsightRepository
- **Location**: `ui/insight/InsightRepository.kt`
- **Wraps**: `IncomeDao`
- **Methods**:
  - `getAllByCategoryId(categoryId: Long): LiveData<List<Income>>` — reactive per-category list
  - `deleteIncome(income: Income)` — suspend

## Data Flow

### Adding a Category
1. User taps FAB → `AddCategoryDialog` opens
2. User enters category name, taps "Add"
3. `MainActivity.onAddCategoryTapped(categoryName)` is called
4. `CategoryViewModel.insertNewCategory(categoryName, context)` calls `CategoryRepository.insertCategory()`
5. Room LiveData triggers observer in `CategoryFragment.initSubscriptions()`
6. RecyclerView updates automatically

### Adding an Income/Expense
1. User taps FAB in `InsightFragment` → `AddIncomeDialog` opens
2. User enters amount, selects Income/Expense type
3. `MainActivity.onAddIncomeTapped(categoryId, amount, type)` is called
4. `CategoryViewModel.insertNewIncomeExpense(categoryId, amount, type, context)` calls `CategoryRepository.insertIncome()`
5. Room LiveData triggers observer in `InsightFragment.initSubscriptions()`
6. List + chart updates automatically

### Financial Overview Refresh
1. `CategoryFragment` observes `categoryViewModel.overview: LiveData<Triple<Double,Double,Double>>`
2. When categories change, `CategoryViewModel.refreshOverview(context)` is called
3. ViewModel calls `CategoryRepository.getSumIncome()` and `getSumExpense()`
4. Results posted to `_overview` MediatorLiveData as `(incomeSum, expenseSum, balance)`
5. Fragment updates UI from the LiveData

### ViewModel Sharing
- Both fragments use `activityViewModels()` to share ViewModels with `MainActivity`
- `InsightViewModel` receives the `categoryId` via Safe Args from `CategoryFragment`

## Coroutine Usage
- ViewModels use `Dispatchers.IO` (injectable via `setTestDispatcher()` for testing)
- Repository suspend functions run on the caller's dispatcher
- UI updates use `LiveData.postValue()` or `withContext(Dispatchers.Main)`

## Testability Hooks
- ViewModels expose `@VisibleForTesting internal fun setTestRepository(repo)` to inject mock repos
- ViewModels expose `@VisibleForTesting internal fun setTestDispatcher(dispatcher)` for async control
- This allows unit testing without Android framework dependencies (no FinancialDB, no Context)
