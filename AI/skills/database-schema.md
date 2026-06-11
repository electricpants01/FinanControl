# Database Schema: FinanControl

## Room Database

- **Class**: `FinancialDB` (singleton via `FinancialDB.getAppDataBase(context)`)
- **Room version**: 2.4.1

## Entities

### Category

| Field | Type | Constraints |
|---|---|---|
| `uid` | `Long?` | `@PrimaryKey(autoGenerate = true)`, nullable |
| `name` | `String` | Required |

```kotlin
@Entity
data class Category(
    @PrimaryKey(autoGenerate = true) val uid: Long? = null,
    val name: String
)
```

### Income (represents both Income and Expense entries)

| Field | Type | Constraints |
|---|---|---|
| `uid` | `Long?` | `@PrimaryKey(autoGenerate = true)`, nullable |
| `description` | `String?` | Optional (nullable) |
| `type` | `String` | Required — `"Income"` or `"Expense"` |
| `amount` | `Double` | Required |
| `categoryId` | `Long` | Foreign key reference to `Category.uid` |
| `timestamp` | `String` | Required — stored as string date format |

```kotlin
@Entity
data class Income(
    @PrimaryKey(autoGenerate = true) val uid: Long? = null,
    val description: String? = null,
    val type: String,
    val amount: Double,
    val categoryId: Long,
    val timestamp: String,
)
```

> **Note**: The `Income` entity is used for both income and expense records. The `type` field differentiates them (`"Income"` vs `"Expense"`).

## DAOs (Data Access Objects)

### CategoryDao

| Method | Return Type | Description |
|---|---|---|
| `getAll()` | `LiveData<List<Category>>` | Get all categories, observed for reactive updates |
| `insertNewCategory(category: Category)` | `void` | Insert a new category |
| `deleteCategory(id: Long)` | `void` | Delete a category by ID |

### IncomeDao

| Method | Return Type | Description |
|---|---|---|
| `getAllByCategoryId(id: Long)` | `LiveData<List<Income>>` | Get all incomes/expenses for a specific category |
| `insert(income: Income)` | `void` | Insert a new income/expense entry |
| `delete(id: Long)` | `void` | Delete an entry by ID |
| `getSumIncome()` | `Double?` | Returns the sum of all `type = "Income"` amounts (using `@Query("SELECT SUM(amount) FROM Income WHERE type = 'Income'")`) |
| `getSumExpense()` | `Double?` | Returns the sum of all `type = "Expense"` amounts (using `@Query("SELECT SUM(amount) FROM Income WHERE type = 'Expense'")`) |

## Data Relationships

```
Category (1) ────< (N) Income
   │                      │
   uid  ◄──────────────── categoryId
```

A category has many income/expense entries. When a category is deleted, its associated income entries should also be removed (handled in the ViewModel/DAO layer).

## Singleton Pattern

```kotlin
// FinancialDB.getAppDataBase(context)
// Uses Room.databaseBuilder with .fallbackToDestructiveMigration()
```
