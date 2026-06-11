# Project Overview: FinanControl

## Summary
**FinanControl** is a personal finance tracking Android application that allows users to manage their income and expenses by organizing them into categories. It provides a clear overview of total balance, income, and expenses, along with visual charts for insights.

## Key Details

| Field | Value |
|---|---|
| **Package** | `com.locotoDevTeam.financontrol` |
| **Application ID** | `com.locotoDevTeam.financontrol` |
| **Current Version** | `1.2.0` (versionCode 6) |
| **Language** | Kotlin |
| **Min SDK** | 21 |
| **Target / Compile SDK** | 32 |
| **Root Project Name** | `FinanControl` |
| **Module** | Single module (`:app`) |
| **Team** | locotoDevTeam |

## Core Features

1. **Category Management** — Create, view, and delete categories (e.g., "Salary", "Rent", "Groceries").
2. **Income & Expense Tracking** — Add income or expense entries under each category with an amount, type (Income/Expense), description, and timestamp.
3. **Financial Overview** — Displays total balance (`Incomes - Expenses`), total incomes, and total expenses on the main category screen.
4. **Per-Category Insights** — Drill into a category to see its transactions in a list and a chart visualization.
5. **Data Visualization** — Custom `FancyChart` charting library renders income/expense data over time.
6. **Push Notifications** — Firebase Cloud Messaging integration for notifications.
7. **Local Persistence** — All data stored locally via Room database (no remote sync).
8. **Spanish Localization** — Partial Spanish translation via `values-es/strings.xml`.

## App Flow
```
SplashActivity → MainActivity
                    ├── CategoryFragment (list of categories + financial overview)
                    └── InsightFragment (per-category: list of incomes/expenses + chart)
```

## Entry Points
- **Launcher Activity**: `SplashActivity` (`intent-filter` ACTION_MAIN, CATEGORY_LAUNCHER)
- **Main Activity**: `MainActivity` hosts the NavHostFragment with the navigation graph
