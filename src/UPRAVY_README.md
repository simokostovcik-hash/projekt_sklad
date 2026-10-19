# Coffee Minimal – správa produktů

## Co je upravené
- Administrace `/coffee/all`: české popisky formuláře, náhled fotografie, fotografie v seznamu a stav zveřejnění.
- Tlačítka **Zveřejnit / Skrýt** mění viditelnost kávy bez nutnosti otevírat formulář.
- Editace zachovává identitu existujícího produktu a jeho návaznosti na objednávky.
- React katalog dál načítá pouze zveřejněné kávy z `/api/coffees`.

## Spuštění
1. V IntelliJ otevři kořen projektu a spusť `cz.project_storage.App` (port 8080).
2. Přihlas se jako ADMIN a otevři `http://localhost:8080/coffee/all`.
3. Ve druhém terminálu `cd frontend`, `npm install` (jen pokud chybí node_modules), `npm run dev`.
4. Otevři adresu vypsanou Vite.

## Data – důležité
Před nahrazením původního projektu si zazálohuj složku `data`. Při kopírování upravených souborů do svého současného projektu **nepřepisuj svou aktuální složku `data`**. ZIP záměrně neobsahuje složku `data`, aby nemohl přepsat tvoji aktuální databázi. Nikdy nepoužívej `ddl-auto=create` nebo `create-drop`.

## Další fáze
Košík, objednávky a faktury nejsou součástí této úpravy. Pro přesnější přizpůsobení novému frontendovému portfoliu je potřeba dodat jeho návrh nebo screenshoty.
