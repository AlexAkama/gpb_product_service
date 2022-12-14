# Список продуктов

В сервисе предусмотрен следующий функционал:

- Добавление продукта;
- Обновление данных продукта;
- Удаление продукта;
- Добавление списка продуктов;
- Обновление данных списка продуктов;
- Удаление списка продуктов;
- Получения списка продуктов;
- Получение списка списков продуктов;
- Добавление продукта в список продуктов;
- Удаление продукта из списка продуктов;

Реализовано "мягкое" удаление - объекты не удаляются из БД, а помечаются как удаленные.

Формат запроса/ответа - JSON.

Документация по доступна (после запуска) по адресу
http://{host}/swagger-ui/index.html

Использованы: SpringBoot, PostgreSQL, Swagger.

## Использование

Для запуска, в файле конфигурации, необходимо указать путь к базе данных, пользователя и пароль.

Пример:

```
spring.datasource.url=jdbc:postgresql://localhost:5432/gpb
spring.datasource.username=postgres
spring.datasource.password=admin
```

В сервисе установлены следующие ограничения:

- Максимальная длина имени продукта = 255 символов;
- Максимальная длинна описания продукта = 1024 символа;
- Максимальная длина списка продуктов: не ограничена;
- Максимальная длина имени списка продуктов = 255 символов;
- Кол-во продуктов на странице по умолчанию = 10 шт.

```
limit.product.name.length=255
limit.product.description.length=1024
limit.list.size=-1
limit.list.name.length=255
limit.page.size=10
```

Для получения списков продуктов и их списков, реализована постраничная выдача.  
Без указания параметров все результаты выдаются в составе одной страницы.  
Если указать параметр `page`, то будет выведена соответствующая страница,
кол-во элементов на странице по умолчанию равно 10.  
Для изменен изменения кол-ва элементов на странице используйте совместно с `page`
параметр `size`.  
Для просмотра удаленных элементов используйте параметр `showRemoved`, со следующими значениями:

- `no` - показываются только активные элементы(значение по умолчанию);
- `yes` - будут показаны и активные и удаленные элементы;
- `only` - только удаленные элементы.

### Тестовые данны

Загружено несколько продуктов и списков. А также установлены связи между ними.

### Далее

Далее возможна реализация логирования и написание тестов.