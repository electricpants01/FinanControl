# Strings & Localization: FinanControl

## String Resource Files

| File | Language |
|---|---|
| `res/values/strings.xml` | English (default) |
| `res/values-es/strings.xml` | Spanish |

## Key String Resources

### App
| Key | Default (English) |
|---|---|
| `app_name` | FinanControl |

### Category Management
| Key | Default (English) |
|---|---|
| `add_category` | Add Category |
| `ex_salary` | Ex. Salary |
| `dialog_category_max_length` | You can type at most 15 characters |

### Income/Expense Management
| Key | Default (English) |
|---|---|
| `add_income_expense` | Add Income/Expense |
| `basic_number` | 100 |
| `dialog_income_expense_max_length` | Max lenght digit overcome |
| `income` | Income |
| `expense` | Expense |

### Financial Overview
| Key | Default (English) |
|---|---|
| `overview` | Total Balance |
| `incomes` | Incomes |
| `expenses` | Expenses |

### Dialogs
| Key | Default (English) |
|---|---|
| `add` | Add |
| `cancel` | Cancel |
| `accept` | Accept |
| `dialog_should_not_be_empty` | Field must not be empty |

### Deletion Confirmations
| Key | Default (English) |
|---|---|
| `category_deletion_title` | Are your sure you want to delete this Category ? |
| `category_deletion_description` | This option will delete this category and its content |
| `insight_deletion_title` | Are your sure you want to delete this Income/Expense ? |
| `insight_delete_description` | This option will delete this item |

### Insight / Details
| Key | Default (English) |
|---|---|
| `insight_item_tapped` | `%s: %s on %s` (formatted: type, amount, date) |
| `insight_point_item_tapped` | This item is: %s |

### Welcome Screen
| Key | Default (English) |
|---|---|
| `welcome_msg` | Let's start adding a new category for your Income/Expenses 🤑 |
| `welcome_title` | Welcome |

## String Format Usage

Format strings use standard Java `String.format` syntax:
- `%s` — String placeholder
- `%s: %s on %s` — Used in `insight_item_tapped` for `type: amount on date`

## Adding New Localized Strings

1. Add the string to `res/values/strings.xml` (English default)
2. Add the translated version to `res/values-es/strings.xml`
3. Reference in code via `getString(R.string.key)` or `resources.getString(R.string.key)`
4. Reference in layouts via `@string/key`

## Supported Themes

| Theme | File |
|---|---|
| Light Mode | `res/values/themes.xml` |
| Dark Mode | `res/values-night/themes.xml` |
