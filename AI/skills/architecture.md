# Architecture: FinanControl

## Pattern: MVVM (Model-View-ViewModel)

The project follows the **MVVM** architecture pattern, although the `Model` layer is represented by Room DAOs accessed directly rather than through a formal Repository class in all cases.

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

## Package Structure

```
com.locotoDevTeam.financontrol/
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
│   └── FinancialDB.kt               # Room Database class (singleton)
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
│   │   ├── CategoryRepository.kt
│   │   └── CategoryViewModel.kt
│   ├── insight/
│   │   ├── InsightFragment.kt
│   │   ├── InsightRepository.kt
│   │   └── InsightViewModel.kt
│   ├── MainActivity.kt
│   └── SplashActivity.kt
│
└── util/
    └── StringExtension.kt           # Extension functions (date formatting)
```

## Data Flow

### Adding a Category
1. User taps FAB → `AddCategoryDialog` opens
2. User enters category name, taps "Add"
3. `MainActivity.onAddCategoryTapped(categoryName)` is called
4. `CategoryViewModel.insertNewCategory(categoryName, context)` inserts via DAO
5. Room LiveData triggers observer in `CategoryFragment.initSubscriptions()`
6. RecyclerView updates automatically

### Adding an Income/Expense
1. User taps FAB in `InsightFragment` → `AddIncomeDialog` opens
2. User enters amount, selects Income/Expense type
3. `MainActivity.onAddIncomeTapped(categoryId, amount, type)` is called
4. `CategoryViewModel.insertNewIncomeExpense(categoryId, amount, type, context)` inserts
5. Room LiveData triggers observer in `InsightFragment.initSubscriptions()`
6. List + chart updates automatically

### ViewModel Sharing
- Both fragments use `activityViewModels()` to share ViewModels with `MainActivity`
- `InsightViewModel` receives the `categoryId` via Safe Args from `CategoryFragment`

## Coroutine Usage
- Database operations use `CoroutineScope(Dispatchers.IO).launch` for background work
- UI updates use `withContext(Dispatchers.Main)` to switch back to main thread
