# Coffee Minimal — redesign podle portfolia

Design React frontendu vychází z dodaného `portfolio-main.zip`: světlé pozadí #f5f3ee, tmavý text #1c211c, limetková #bdff69, typografie Space Grotesk / DM Sans, velké editorial nadpisy, číslované sekce, karty a zaoblené CTA.

## Spuštění
1. **Před změnou projektu zazálohujte** původní složku `data/` a celý projekt.
2. Otevřete kořen projektu v IntelliJ a spusťte Spring Boot na `localhost:8080`.
3. V terminálu spusťte `cd frontend`, `npm install`, `npm run dev`.
4. Otevřete `http://localhost:5173`.

**Databáze `data/` není v ZIPu**, aby se nepřepsaly existující kávy a objednávky. Ponechte svoji původní složku `data/` v kořeni projektu.

## Co je nové
- Přepracovaný responzivní React katalog v designu portfolia, filtry podle typu, produktové karty a funkční košík s množstvím.
- React pokladna odesílá `POST /api/orders` s kontaktními údaji a položkami.
- Backend přepočítává ceny z DB, kontroluje viditelnost a sklad, transakčně odečítá zásoby a ukládá objednávky.
- Přidaná nepovinná pole zákazníka do entity `Order` (Hibernate `ddl-auto=update`).
- **Žádná vizuální úprava backendové administrace.** Její původní stránky zůstaly nedotčené; staré tlačítko Buy tam stále je, protože jste výslovně požadoval neměnit backend vizuálně.

## Důležitá omezení
- Pokladna je jednoduchá objednávka bez online plateb, automatického e-mailu, dopravy a GDPR dokumentů.
- Veřejný endpoint objednávek je vhodný jako školní/lokální prototyp. Před veřejným nasazením přidejte rate limiting, ochranu proti spamu, HTTPS, obchodní podmínky a správu osobních údajů.
- U starších admin šablon mohou objednávky bez přihlášeného uživatele potřebovat úpravu zobrazení; administraci jsme zde záměrně neměnili.
- Java/Maven sestavení nebylo v tomto prostředí ověřeno. Frontend build ověřte příkazem `npm run build`.
