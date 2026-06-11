# UI Components: FinanControl

## Activities

### SplashActivity
- **Package**: `com.locotoDevTeam.financontrol.ui.SplashActivity`
- **Role**: App entry point with launcher intent filter
- **Purpose**: Shows splash screen before navigating to `MainActivity`

### MainActivity
- **Package**: `com.locotoDevTeam.financontrol.ui.MainActivity`
- **Role**: Host activity for all fragments via `NavHostFragment`
- **Interfaces implemented**:
  - `AddCategoryDialog.AddCategoryListener`
  - `AddIncomeDialog.AddIncomeListener`
- **Delegates to**: `CategoryViewModel` for database operations
- **Uses**: `ViewBinding` (`ActivityMainBinding`)

## Fragments

### CategoryFragment
- **Layout**: `fragment_category.xml`
- **ViewModel**: `CategoryViewModel` (shared via `activityViewModels()`)
- **Components**:
  - `RecyclerView` (`rvCategory`) with `CategoryAdapter`
  - Welcome text + arrow (shown when no categories exist)
  - Financial overview section: Total Balance, Income total, Expense total (shown when categories exist)
  - `FloatingActionButton` to open `AddCategoryDialog`
- **Key behaviors**:
  - Observes `CategoryDao.getAll()` for real-time category list updates
  - Calculates income/expense sums via coroutines on `Dispatchers.IO`
  - Tapping a category navigates to `InsightFragment(categoryId)`
  - Long press / delete triggers `MaterialAlert` confirmation → `CategoryViewModel.deleteACategoryById()`

### InsightFragment
- **Layout**: `fragment_insight.xml`
- **ViewModel**: `InsightViewModel` (shared via `activityViewModels()`)
- **Receives**: `categoryId: Long` via Safe Args
- **Components**:
  - `RecyclerView` (`rvInsight`) with `InsightAdapter`
  - `FancyChart` (`insightFancyChart`) for data visualization
  - `FloatingActionButton` (`floatAddInsight`) to open `AddIncomeDialog`
- **Key behaviors**:
  - Observes `IncomeDao.getAllByCategoryId(categoryId)` for transaction list
  - `InsightViewModel` splits data into income/expense lists for chart rendering
  - `MyFancyChartBuilder.createChart()` builds chart from observed graph data
  - Chart point click shows a Snackbar with the Y-axis value
  - Tapping an item shows a Snackbar with type, amount, and formatted date
  - Delete triggers `MaterialAlert` → `InsightViewModel.deleteInsight()`

## Adapters

### CategoryAdapter
- **Path**: `data.adapter.CategoryAdapter`
- **ViewHolder layout**: `rv_category.xml`
- **Listener interface** (`CategoryListener`):
  - `onCategoryTapped(categoryId: Long)` — navigate to insight
  - `onDeleteCategoryTapped(categoryId: Long)` — delete category
- **Data**: `List<Category>`

### InsightAdapter
- **Path**: `data.adapter.InsightAdapter`
- **ViewHolder layout**: `rv_insight.xml`
- **Listener interface** (`InsightListener`):
  - `onInsightTapped(income: Income)` — show details snackbar
  - `onDeleteInsightTapped(income: Income)` — delete entry
- **Data**: `List<Income>`
- **Visual differentiation**: Likely colors items differently based on `income.type` (Income vs Expense), using arrow icons (`ic_arrow_circle_up.xml`, `ic_arrow_circle_down.xml`)

## Dialogs

### AddCategoryDialog
- **Layout**: `dialog_add_category.xml`
- **Extends**: `DialogFragment`
- **Implements**: Listener interface `AddCategoryListener`
  - `onAddCategoryTapped(categoryName: String)`
- **Validation**: Category name max 15 characters, must not be empty

### AddIncomeDialog
- **Layout**: `dialog_add_income.xml`
- **Extends**: `DialogFragment`
- **Implements**: Listener interface `AddIncomeListener`
  - `onAddIncomeTapped(categoryId: Long, amount: Double, type: String)`
- **Validation**: Amount field must not be empty, max digit length check

### MaterialAlert
- **Path**: `data.dialog.MaterialAlert`
- **Purpose**: Utility object that wraps `MaterialAlertDialogBuilder` for confirmation dialogs
- **Usage**: `MaterialAlert.showDialog(title, description, context) { onAccept -> }`

## Custom Chart: FancyChart

- **Package**: `com.locotoDevTeam.financontrol.fancyChart`
- **Java classes**:
  - `FancyChart.java` — Custom `View` for chart rendering
  - `FancyChartPointListener.java` — Callback interface for point clicks
  - `FancyChartStyle.java` — Chart styling config
  - `ChartData.java` — Data model for chart
  - `AxisValue.java` — Axis value representation
  - `Point.java` — Data point representation
- **Kotlin helper**:
  - `MyFancyChartBuilder.kt` — Converts `InsightViewModel` graph data into `ChartData` for the `FancyChart`

## Icons / Drawables

| File | Use |
|---|---|
| `ic_add.xml` | Add button icon |
| `ic_arrow_circle_down.xml` | Expense indicator (red/down arrow) |
| `ic_arrow_circle_up.xml` | Income indicator (green/up arrow) |
| `ic_close.xml` | Close/dismiss |
| `ic_delete.xml` | Delete action |
| `ic_right_arrow.xml` | Navigate/forward |
| `ic_twotone_arrow_circle_down.xml` | Two-tone expense variant |
| `ic_twotone_arrow_circle_up.xml` | Two-tone income variant |
| `arrow.webp` | Welcome/guide arrow |
| `splash_icon.jpg` | Splash screen icon |
| `ic_launcher_background.xml` | Launcher background |
