# Úpravy frontendu a backendu

- Nové společné CSS `src/main/resources/static/css/coffee-theme.css` a navigace ve všech přihlášených stránkách.
- Opravené CRUD endpointy pražíren (`/roastery/save`, `/roastery/edit/{id}`, POST `/roastery/delete/{id}`). Mazání pražírny s kávou se neprovede.
- Mazání kávy a objednávek změněno z GET na POST; formuláře odpovídají backendu.
- Zapnuta CSRF ochrana; formuláře `th:action` automaticky přidávají CSRF token, dynamický formulář pro nákup jej načítá z meta tagu.
- PDF faktury může stáhnout jen vlastník nebo administrátor.
- Dashboard a správa pražíren dostupné jen adminům.

## Spuštění
V IntelliJ spusť Maven `spring-boot:run` a navštiv `http://localhost:8080/login`.

## Důležité
- Projekt v přiloženém ZIP používá H2 souborovou databázi podle `application.properties`, nikoli PostgreSQL. Konfiguraci připojení neměníme.
- Před veřejným nasazením změň výchozí heslo admina, vypni H2 konzoli a otestuj oprávnění, transakce i souběžné objednávky.
- Bez lokálního Maven/JDK buildu nebylo možné změny ověřit kompilací.

## Oprava historie objednavek (v2)

OrderItem.coffee pouziva Hibernate @NotFound(IGNORE). Pokud je kava smazana,
objednavka se zobrazi s nazvem Unknown Coffee namisto chyby EntityNotFoundException.
Historicke zaznamy se nemazou. Pro trvale reseni doporucujeme ukladat nazev
produktu pri nakupu do OrderItem a zabranit fyzickemu mazani prodanych produktu.
