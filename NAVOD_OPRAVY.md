# Opravený projekt – Coffee Minimal

## Co je opraveno
- `CoffeeApiController`: cena je přímo `BigDecimal`, bez chybného `valueOf`; API vrací DTO, takže nevzniká nekonečná JSON rekurze.
- API vrací jen kávy označené `visibleInShop`, nezávisle na skladovém množství. `available` znamená `quantity > 0`.
- Správce v `/coffee/all` může nastavit viditelnost, popis a URL obrázku. **Nahrávání obrázků z disku zatím není implementováno.**
- React načítá katalog z `/api/coffees` a každých 30 sekund jej obnovuje; potřebuje spuštěný Spring Boot na portu 8080.
- Smazání kávy v administraci nyní pouze skryje kávu z e-shopu a zachová ji kvůli historii objednávek.
- Veřejné API povoluje jen GET katalogu; ostatní `/api/**` jsou zamítnuty.

## Spuštění
1. V IntelliJ spusť Spring Boot `App` na portu 8080. Ověř `http://localhost:8080/api/coffees`.
2. V `frontend` spusť `npm install`, potom `npm run dev`.
3. Přihlas se jako admin a v `/coffee/all` uprav kávu a zaškrtni **Zobrazit v zákaznickém e-shopu**.
4. Otevři React stránku na adrese, kterou vypíše Vite.

## Omezení
- Košík je zatím pouze frontendový prototyp; **objednávky a faktury zákazníků nejsou propojené**.
- Backend používá H2, ne PostgreSQL. Databáze z dodaného ZIPu je přiložena, ale nebyla migrována ani opravována.
- Testy Spring Bootu zde nebylo možné spustit (není dostupný Maven). Neprováděj nasazení do produkce bez ověření.
