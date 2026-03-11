# Labss — E-Commerce Catalogue App

[![Android CI/CD Pipeline](https://github.com/DmytroDubov/Labs/actions/workflows/ci-cd.yml/badge.svg?branch=develop)](https://github.com/DmytroDubov/Labs/actions/workflows/ci-cd.yml)

Android-додаток каталогу е-комерції, розроблений на Kotlin з використанням Jetpack Compose.

## Технології

| Шар | Технологія |
|-----|-----------|
| UI | Jetpack Compose, Material 3 |
| Навігація | Navigation Compose |
| Архітектура | MVVM (ViewModel + Repository) |
| Мережа | Retrofit 2, OkHttp, Gson |
| Локальна БД | Room |
| DI | Ручний (AppModule) |
| Async | Kotlin Coroutines, Flow |
| Зображення | Coil |

## Структура проекту

```
app/src/main/java/com/org/labss/
├── data/
│   ├── api/          # Retrofit ApiService, DTO-класи
│   ├── local/        # Room база даних, DAO, Entity
│   ├── mapper/       # Mappers (DTO ↔ Domain)
│   └── repository/   # ProductRepository, SearchHistoryRepository
├── domain/           # Domain-моделі (Product, Category, SearchHistory)
├── di/               # AppModule (ін'єкція залежностей)
├── ui/
│   ├── features/
│   │   ├── home/     # Головний екран (список товарів, пошук)
│   │   └── search/   # Екран результатів пошуку
│   ├── navigation/   # NavGraph
│   ├── theme/        # Кольори, типографіка, розміри
│   └── vm/           # ViewModels
├── ECApp.kt          # Application клас
└── MainActivity.kt
```

## Функціонал

- **Головний екран** — перегляд категорій та товарів, пошукове поле
- **Пошук** — фільтрація товарів за назвою, збереження історії пошуку
- **Кошик** — зміна кількості товарів (+/-)
- **Улюблені** — позначення товарів як улюблених
- **Офлайн-режим** — дані кешуються в Room; при перезапуску доступні без мережі
- **Синхронізація** — дані (категорії, товари, історія пошуку) синхронізуються з віддаленим API (JSONBin)
- **Збереження стану** — стан UI (текст пошуку, позиція списку) зберігається при повороті 
