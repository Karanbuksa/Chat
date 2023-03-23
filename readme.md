<h1>Учебный проект "Сетевой чат"</h1>
<p>Этот проект представляет собой два приложения: серверное приложение чата и клиентское приложение чата, в котором пользователи
могут зарегистрироваться, войти в аккаунт, обмениваться сообщениями
и выйти из аккаунта. Проект написан на языке Java и использует сокеты
для обмена данными между клиентом и сервером.</p>
<h2>Функционал</h2>
<h3>Регистрация и вход в аккаунт</h3>
<p>При запуске клиента пользователю предлагается зарегистрироваться, войти в
аккаунт или выйти из приложения. Данные пользователя не хранятся локально на
устройстве пользователя, а передаются на сервер для хранения. Пользователь
получает токен при регистрации или входе в аккаунт, который сохраняется у
него. При последующих подключениях пользователь отправляет этот токен на
сервер, чтобы сервер мог расшифровать его и определить, какому пользователю
принадлежат сообщения.</p>
<h3>Обмен сообщениями</h3>
<p>После входа в аккаунт пользователь может обмениваться сообщениями
с другими пользователями, находящимися в сети. Все сообщения записываются
в лог на стороне сервера постоянно, а на стороне пользователя - когда он в сети.
Когда пользователь подключается, его лог обновляется на актуальный.</p>
<h3>Выход из аккаунта</h3>
<p>Пользователь может выйти из аккаунта, после чего его токен удаляется
с его устройства, и он должен заново зарегистрироваться или войти в аккаунт
для продолжения работы.</p>
<h2>Использование</h2>
<h3>Запуск сервера</h3>
<p>Для запуска сервера необходимо запустить класс <code>Server</code>.
По умолчанию сервер будет работать на порту <code>8656</code>. Порт
можно менять в файле <code>settings.txt</code>. Для смены порта необходимо 
перезапустить сервер. Сервер по умолчанию является участником чата и может
всегда отправлять сообщения.</p>
<h3>Запуск клиента</h3>
<p>Для запуска клиента необходимо запустить класс <code>Client</code>.
При запуске пользователю будет предложено зарегистрироваться или войти в аккаунт.
После успешной регистрации или входа в аккаунт пользователь может обмениваться
сообщениями с другими пользователями. </p>
<p>Чтобы проверить работу чата в среде разработки Intellij IDEA можно создать
несколько конфигурций запуска класса <code>Client</code> и с помощью функции 
<code>Add VM options</code> во вкладке <code>Modify options</code> для каждй 
конфигурации добавить перемнную <code>-DsettingsFilePath</code>, в которую 
записать путь к файлу натроек. Файл настроек - файл типа JSON, в него записывается
один объект с трёмя полями: "port", "host" и "token". По умолчанию файл настроек - <code>settings.json</code>
</p>
