# Coding Conventions: FinanControl

## Kotlin Patterns

### ViewModel Access
- Fragments use `activityViewModels()` to share the ViewModel with `MainActivity`:
  ```kotlin
  private val categoryViewModel: CategoryViewModel by activityViewModels()
  ```

### ViewBinding
- Enabled in `build.gradle` via `buildFeatures { viewBinding true }`
- Fragments bind in `onCreateView` using static `bind()`:
  ```kotlin
  binding = FragmentCategoryBinding.bind(view)
  ```

### Coroutines
- Custom `CoroutineScope(Dispatchers.IO).launch` for background DB operations (not `viewModelScope` or `lifecycleScope`)
- UI updates via `withContext(Dispatchers.Main)`:
  ```kotlin
  CoroutineScope(Dispatchers.IO).launch {
      val sum = dao?.getSumIncome()
      withContext(Dispatchers.Main) {
          binding.txtIncomeAmount.text = sum.toString()
      }
  }
  ```

### Lateinit
- Heavy use of `lateinit var` for views, adapters, bindings, and RecyclerViews:
  ```kotlin
  lateinit var binding: FragmentCategoryBinding
  lateinit var recycler: RecyclerView
  lateinit var adapter: CategoryAdapter
  lateinit var chart: FancyChart
  ```

### `requireContext()` over `context`
- Prefers `requireContext()` when accessing context in fragments:
  ```kotlin
  FinancialDB.getAppDataBase(requireContext())?.categoryDao()
  ```

## Naming Conventions

| Convention | Example |
|---|---|
| **Fragments** | `CategoryFragment`, `InsightFragment` |
| **ViewModels** | `CategoryViewModel`, `InsightViewModel` |
| **Adapters** | `CategoryAdapter`, `InsightAdapter` |
| **Dialogs** | `AddCategoryDialog`, `AddIncomeDialog` |
| **Layouts** | `fragment_category.xml`, `fragment_insight.xml`, `rv_category.xml`, `dialog_add_category.xml` |
| **Entities** | `Category`, `Income` |
| **View IDs** | Lowercase with underscores (not full camelCase): `rvCategory`, `rvInsight`, `floatAddInsight` |
| **Strings** | `snake_case`: `add_category`, `insight_deletion_title` |

## Code Style

### Database Access
- Uses singleton pattern via `FinancialDB.getAppDataBase(context)` (not DI)
- DAO methods return `LiveData` for observed queries, raw values for sums

### Dialog Pattern
- Dialogs communicate results via listener interfaces on `MainActivity`:
  ```kotlin
  class MainActivity : AppCompatActivity(),
      AddCategoryDialog.AddCategoryListener,
      AddIncomeDialog.AddIncomeListener
  ```

### Deletion Pattern
- Uses `MaterialAlert.showDialog()` for confirmation before delete
- Passes a lambda callback for the confirm action

### Listener Interfaces
- Defined inside `CategoryAdapter` and `InsightAdapter`:
  ```kotlin
  class CategoryAdapter(..., listener: CategoryListener)
  ```
- Fragment implements the interface and passes itself as listener

## Anti-patterns / Legacy

- ⚠️ Uses `android.opengl.Visibility` import (typographical error? Should be `android.view.View`)
- ⚠️ Uses `kotlin.math.exp` import without apparent use
- ⚠️ `CategoryRepository` and `InsightRepository` files exist but may not be fully utilized; DAOs are often called directly from ViewModels
- ⚠️ Coroutine scopes are not tied to lifecycle (no structured concurrency), which may cause memory leaks
