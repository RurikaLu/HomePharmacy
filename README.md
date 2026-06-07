# Домашняя аптечка

Мобильное приложение для Android для ведения учёта лекарств.

## Функции

- Добавление, просмотр, редактирование и удаление лекарств
- Поиск по названию и рекомендациям
- Сортировка по алфавиту, сроку годности, дате добавления, дате покупки, цене
- Фильтрация по статусу («Есть» / «Нужно купить»)
- Отдельный экран со списком лекарств и цен
- Сохранение настроек сортировки и фильтрации
- Тёмная тема оформления
- Локальная база данных SQLite

## Технологии

- Язык: Kotlin
- Среда разработки: Android Studio
- База данных: SQLite
- UI: XML, RecyclerView, CardView, Material Design

## Структура проекта

- `MainActivity.kt` — главный экран со списком лекарств
- `DetailActivity.kt` — экран детальной информации
- `SettingsActivity.kt` — экран настроек сортировки и фильтрации
- `PriceListActivity.kt` — экран со списком цен
- `MedicineDao.kt` — работа с базой данных (CRUD)
- `MedicineDbHelper.kt` — создание и управление БД
- `AddEditDialog.kt` — диалог добавления/редактирования

## Скриншоты

<img width="108" height="234" alt="Screenshot_20260607_215928_com example homepharmacy" src="https://github.com/user-attachments/assets/6ed9b3f4-dbc9-4ef1-8703-3653fb69d6c7" />
<img width="108" height="234" alt="Screenshot_20260607_220037_com example homepharmacy" src="https://github.com/user-attachments/assets/fef42774-9f2a-4d63-ba88-8cca955d5a2e" />
<img width="108" height="234" alt="Screenshot_20260607_215933_com example homepharmacy" src="https://github.com/user-attachments/assets/0be9993c-a634-403e-ad7b-7b597ff311d3" />


## Автор

Решетова Ольга (RurikaLu)
