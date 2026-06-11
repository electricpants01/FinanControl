# Navigation Flow: FinanControl

## Overview

The app uses **Jetpack Navigation** with **Safe Args** for type-safe argument passing between destinations. The navigation graph is defined in `res/navigation/nav_finance.xml`.

## Screen Flow Diagram

```
┌──────────────┐
│ SplashScreen │  (entry point, launcher activity)
└──────┬───────┘
       │
       ▼
┌──────────────┐
│  MainActivity │  (hosts NavHostFragment)
└──────┬───────┘
       │
       ▼
┌──────────────────┐
│ CategoryFragment │  (start destination)
│ - List categories│
│ - Total balance  │
│ - Income/Expense │
│   summaries      │
└──────┬───────────┘
       │  onCategoryTapped(categoryId)
       │  Safe Args: categoryId: Long
       ▼
┌──────────────────┐
│ InsightFragment  │
│ - Per-category   │
│   income/expense │
│   list           │
│ - FancyChart     │
│   visualization  │
└──────────────────┘
```

## Navigation Graph: `nav_finance.xml`

```xml
<navigation>
    <fragment id="categoryFragment">
        <action id="action_categoryFragment_to_insightFragment"
                destination="insightFragment" />
    </fragment>
    <fragment id="insightFragment">
        <argument name="categoryId" type="long" />
    </fragment>
</navigation>
```

## Safe Args Usage

### Passing Arguments (CategoryFragment → InsightFragment)

```kotlin
// CategoryFragment.kt
override fun onCategoryTapped(categoryId: Long) {
    val directions = CategoryFragmentDirections
        .actionCategoryFragmentToInsightFragment(categoryId)
    findNavController().navigate(directions)
}
```

### Receiving Arguments (InsightFragment)

```kotlin
// InsightFragment.kt
private val args: InsightFragmentArgs by navArgs()

override fun onCreateView(...): View {
    insightViewModel.setCategoryId(args.categoryId)
    // ...
}
```

## Back Navigation

- `MainActivity.onSupportNavigateUp()` delegates to the NavController:
  ```kotlin
  override fun onSupportNavigateUp(): Boolean {
      return findNavController(binding.fragmentContainerView.id).navigateUp()
          || super.onSupportNavigateUp()
  }
  ```
- **CategoryFragment**: Up/home button is hidden (`setDisplayHomeAsUpEnabled(false)`)
- **InsightFragment**: Up/home button is shown (`setDisplayHomeAsUpEnabled(true)`)

## Dialog Navigation

Dialogs are shown outside the navigation graph using `DialogFragment.show()`:

- `AddCategoryDialog` — triggered from `CategoryFragment` FAB
- `AddIncomeDialog` — triggered from `InsightFragment` FAB
- Both communicate results back to `MainActivity` via listener interfaces
