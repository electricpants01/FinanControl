# Coding Conventions: FinanControl

## Build System

### Kotlin DSL (`.gradle.kts`)
- All build files use Kotlin DSL, not Groovy
- Properties use `=` assignment: `compileSdk = 36`, `isMinifyEnabled = false`
- Function calls use parentheses: `implementation(libs.core.ktx)`
- Strings use double quotes: `namespace = "com.locotoDevTeam.financontrol"`

### Version Catalog (`libs.versions.toml`)
- All dependency versions centralized in `gradle/libs.versions.toml`
- Dependencies referenced via `libs.xxx` accessors: `implementation(libs.room.runtime)`
- Plugins via `alias(libs.plugins.xxx)`: `alias(libs.plugins.kotlin.android)`
- Never hardcode version strings in `build.gradle.kts`

### Signing Configuration
- Reads from environment variables (used by CI/CD):
  ```kotlin
  val myKeystorePassword: String? = System.getenv("KEYSTORE_PASSWORD")
  val myKeyAlias: String? = System.getenv("KEY_ALIAS")
  val myKeyPassword: String? = System.getenv("KEY_PASSWORD") ?: myKeystorePassword
  val myKeystoreFile: File = rootProject.file("financontrol.jks")
  val canSign = myKeystoreFile.exists() && myKeystorePassword != null && myKeyAlias != null
  ```
- Signing is conditional — only applied when `canSign` is true (CI environment)
- Local builds are unsigned; CI builds are signed via GitHub Secrets

## Kotlin Patterns

### ViewModel Access
- Fragments use `activityViewModels()` to share the ViewModel with `MainActivity`:
  ```kotlin
  private val categoryViewModel: CategoryViewModel by activityViewModels()
  ```

### ViewBinding
- Enabled via `buildFeatures { viewBinding = true }` in `app/build.gradle.kts`
- Fragments bind in `onCreateView` using static `bind()`:
  ```kotlin
  binding = FragmentCategoryBinding.bind(view)
  ```

### Coroutines
- ViewModels use `viewModelScope.launch(dispatcher)` with `Dispatchers.IO` for DB work
- Repositories expose `suspend` functions — caller chooses dispatcher
- UI updates via `LiveData.postValue()` or `withContext(Dispatchers.Main)`
- Dispatchers are injectable for testing (`@VisibleForTesting internal fun setTestDispatcher()`)

### Lateinit
- Heavy use of `lateinit var` for views, adapters, bindings, and RecyclerViews:
  ```kotlin
  lateinit var binding: FragmentCategoryBinding
  lateinit var recycler: RecyclerView
  lateinit var adapter: CategoryAdapter
  lateinit var chart: FancyChart
  ```

### `requireContext()` over `context`
- Prefers `requireContext()` when accessing context in fragments

## Repository Pattern

### Rule: Never call DAOs directly from Fragments or ViewModels
All data access must flow through the Repository layer:
```
Fragment → ViewModel → Repository → DAO → Room DB
```

### Repository Construction
- Repositories take DAOs as constructor parameters (ready for future DI):
  ```kotlin
  class CategoryRepository(
      private val categoryDao: CategoryDao,
      private val incomeDao: IncomeDao
  )
  ```

### ViewModel Lazy Initialization (current state, before Hilt)
- Repositories are lazily initialized from `FinancialDB` in ViewModels:
  ```kotlin
  private var categoryRepository: CategoryRepository? = null

  private fun getRepository(context: Context): CategoryRepository {
      if (categoryRepository == null) {
          val db = FinancialDB.getAppDataBase(context)!!
          categoryRepository = CategoryRepository(db.categoryDao(), db.incomeDao())
      }
      return categoryRepository!!
  }
  ```

### Testability Hooks
- ViewModels expose `@VisibleForTesting internal fun setTestRepository(repo)`
- ViewModels expose `@VisibleForTesting internal fun setTestDispatcher(dispatcher)`
- This allows unit testing without Android dependencies (no FinancialDB, no Context)

## Naming Conventions

| Convention | Example |
|---|---|
| **Fragments** | `CategoryFragment`, `InsightFragment` |
| **ViewModels** | `CategoryViewModel`, `InsightViewModel` |
| **Repositories** | `CategoryRepository`, `InsightRepository` |
| **Adapters** | `CategoryAdapter`, `InsightAdapter` |
| **Dialogs** | `AddCategoryDialog`, `AddIncomeDialog` |
| **Layouts** | `fragment_category.xml`, `fragment_insight.xml` |
| **Entities** | `Category`, `Income` |
| **View IDs** | Lowercase with underscores: `rvCategory`, `rvInsight` |
| **Strings** | `snake_case`: `add_category`, `insight_deletion_title` |
| **Build variables** | `myKeystorePassword`, `myKeyAlias`, `myKeyPassword` |

## Code Style

### Database Access
- Repository wraps all DAO calls
- DAO methods return `LiveData` for observed queries, raw values for sums
- Repository suspend functions use the caller's dispatcher

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

## Testing Conventions

### MockK + Page Object Pattern
- Tests use **MockK** (not Mockito) for Kotlin-native mocking
- Each test subject has a **Page Object** class in `test/page/` that encapsulates:
  - Mock creation and setup
  - SUT (System Under Test) creation
  - Stub helpers (`coEvery` for suspend, `every` for regular)
  - Assertion/verification helpers (`coVerify` for suspend, `verify` for regular)

### Page Object Example
```kotlin
// test/page/CategoryRepositoryPage.kt
class CategoryRepositoryPage {
    val categoryDao: CategoryDao = mockk(relaxed = true)
    val incomeDao: IncomeDao = mockk(relaxed = true)
    val repository = CategoryRepository(categoryDao, incomeDao)

    fun stubGetAllCategories(vararg categories: Category) {
        every { categoryDao.getAll() } returns mockk {
            every { value } returns categories.toList()
        }
    }

    fun verifyInsertCategoryCalled(name: String) {
        verify { categoryDao.insert(match { it.name == name }) }
    }
}
```

### Test Class Example
```kotlin
// test/repository/CategoryRepositoryTest.kt
class CategoryRepositoryTest {
    private val page = CategoryRepositoryPage()

    @Test
    fun `insertCategory delegates to DAO`() = runTest {
        page.repository.insertCategory(Category(name = "Groceries"))
        page.verifyInsertCategoryCalled("Groceries")
    }
}
```

### ViewModel Testing
- Inject mock repository via `viewModel.setTestRepository(mockRepo)`
- Inject test dispatcher via `viewModel.setTestDispatcher(testDispatcher)`
- Use `InstantTaskExecutorRule` for LiveData
- Use `StandardTestDispatcher` + `advanceUntilIdle()` to control async

## Known Future Work
- CHRIS-230: Add Hilt DI to remove `FinancialDB.getAppDataBase()` from ViewModels
- CHRIS-231: Remove `Context` parameter from ViewModel methods via DI
