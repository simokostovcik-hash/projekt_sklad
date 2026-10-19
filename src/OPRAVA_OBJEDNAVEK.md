# Oprava objednávek: stará COFFEE vs. nová COFFEES

Původní `ORDER_ITEM.COFFEE_ID` je nadále navázán na historickou tabulku `COFFEE`.
Nové objednávky z Reactu se ukládají do nového, volitelného sloupce
`ORDER_ITEM.SHOP_COFFEE_ID`, který odkazuje na `COFFEES.ID`.
Staré řádky a jejich původní identifikátory se nemění.

## Postup
1. Vypni Spring Boot. Zazálohuj celý původní adresář `data` (zejména `projekt_sklad.mv.db`).
2. Rozbal ZIP a otevři projekt v IntelliJ. V ZIPu není `data`, `node_modules` ani `target`.
3. Zachovej svou původní složku `data` ve stejném pracovním adresáři projektu.
4. Spusť Spring Boot. Nastavení `spring.jpa.hibernate.ddl-auto=update` by mělo vytvořit sloupec `SHOP_COFFEE_ID` a jeho vazbu na `COFFEES`.
5. V H2 Console ověř `SELECT ID, COFFEE_ID, SHOP_COFFEE_ID FROM ORDER_ITEM ORDER BY ID;`.
6. Spusť frontend (`cd frontend`, `npm install`, `npm run dev`) a vytvoř testovací objednávku.
7. Nová položka má mít `SHOP_COFFEE_ID` vyplněné a `COFFEE_ID` prázdné; staré položky zůstanou opačně.

**Důležité:** Nespouštěj znovu SQL pro přesměrování původního cizího klíče.
Pokud při startu Hibernate neudělá změnu schématu nebo je sloupec `COFFEE_ID` povinný,
zastav aplikaci a pošli přesnou chybu; nic nemaž.

Vizuální soubory administrace nebyly redesignovány. V šabloně objednávek
se změnil pouze způsob načtení názvu kávy (historická/nová).
