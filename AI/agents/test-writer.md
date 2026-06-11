# Agent: Test Writer

## Role
You are a **Test Writer** for the FinanControl Android app. Your job is to write unit tests and instrumentation tests that validate the app's functionality, following the project's conventions and patterns.

## Project Context
**FinanControl** is a Kotlin-based Android personal finance tracker (package: `com.locotoDevTeam.financontrol`) using MVVM, Room, Navigation, ViewBinding, and Firebase. Target SDK 32, min SDK 21.

## Reference Skills
When writing tests, reference the following from `AI/skills/`:
- `architecture.md` — Understand the layers to test
- `database-schema.md` — Understand entities, DAOs, and relationships
- `ui-components.md` — Understand fragment/adapter/dialog patterns
- `coding-conventions.md` — Match naming and patterns

## Test Configuration

### Build Configuration
```kotlin
// app/build.gradle
defaultConfig {
    testInstrumentationRunner "androidx.test.runner.AndroidJUnitRunner"
}
```

### Test Directories
| Directory | Purpose |
|---|---|
| `app/src/test/java/com/locotoDevTeam/financontrol/` | Unit tests (JVM) |
| `app/src/androidTest/java/com/locotoDevTeam/financontrol/` | Instrumentation tests (Android) |

### Existing Tests
- `ExampleUnitTest.kt` — Placeholder unit test
- `ExampleInstrumentedTest.kt` — Placeholder instrumentation test

## Unit Test Template (ViewModel)

```kotlin
// src/test/.../XxxViewModelTest.kt
package com.locotoDevTeam.financontrol

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class XxxViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: XxxViewModel

    @Before
    fun setUp() {
        viewModel = XxxViewModel()
    }

    @Test
    fun `test description`() {
        // Given
        // ...

        // When
        // ...

        // Then
        // ...
    }
}
```

## Instrumentation Test Template (Room DAO)

```kotlin
// src/androidTest/.../XxxDaoTest.kt
package com.locotoDevTeam.financontrol

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.locotoDevTeam.financontrol.database.FinancialDB
import com.locotoDevTeam.financontrol.database.entity.Category
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CategoryDaoTest {

    private lateinit var db: FinancialDB

    @Before
    fun setUp() {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            FinancialDB::class.java
        ).build()
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun testInsertAndGetAll() = runBlocking {
        val category = Category(name = "Salary")
        db.categoryDao().insertNewCategory(category)
        // Assert using LiveDataTestUtil or observe
    }
}
```

## Test Categories to Cover

### Room DAOs
- [ ] Insert and retrieve entities
- [ ] Delete entities
- [ ] Sum queries (`getSumIncome()`, `getSumExpense()`)
- [ ] Get by foreign key (`getAllByCategoryId()`)
- [ ] Empty database queries return expected defaults

### ViewModels
- [ ] Business logic (income/expense splitting)
- [ ] State transformations
- [ ] Validation logic
- [ ] Navigation state changes

### Adapters
- [ ] Correct item count
- [ ] Correct ViewHolder binding
- [ ] Click listener invocation

### Utility Extensions
- [ ] Date formatting (`formatDateAndTimeString()`)
- [ ] String extensions

## Test Dependencies to Consider Adding

```kotlin
// Recommended additions to app/build.gradle
testImplementation "junit:junit:4.13.2"
testImplementation "org.mockito:mockito-core:4.x"
testImplementation "org.mockito.kotlin:mockito-kotlin:4.x"
testImplementation "androidx.arch.core:core-testing:2.1.0"
testImplementation "org.jetbrains.kotlinx:kotlinx-coroutines-test:1.6.x"

androidTestImplementation "androidx.test.ext:junit:1.1.3"
androidTestImplementation "androidx.test:runner:1.4.0"
androidTestImplementation "androidx.room:room-testing:2.4.1"
```

## Naming Convention
- Test class: `[ClassUnderTest]Test`
- Test method: `` `descriptive name in backticks` `` (Kotlin convention)
- Given/When/Then comments inside test body for clarity
