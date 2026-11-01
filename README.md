# Kávový sklad – veřejné demo e-shopu

Projekt obsahuje React/Vite frontend a samostatný Spring Boot backend s MySQL. **Veřejné demo používá pouze frontend a simulovaná data**, takže se nic skutečně neobjednává, neplatí a nikam se neposílají osobní údaje.

## Veřejné preview

Po publikaci repozitáře `projekt_sklad` na účtu `simokostovcik-hash` bude ukázka dostupná na:

**https://simokostovcik-hash.github.io/projekt_sklad/**

Pokud se repozitář jmenuje jinak, změňte v odkazu název za `github.io/`. Odkaz začne fungovat až po úspěšném nasazení GitHub Pages.

## Jak zveřejnit

1. Nahrajte obsah této složky do GitHub repozitáře (bez `.git`, databází, `.env`, `node_modules` a `target`).
2. GitHub → **Settings → Pages → Build and deployment → Source: GitHub Actions**.
3. GitHub → **Actions** → počkejte na dokončení workflow `Deploy demo to GitHub Pages`.
4. Otevřete URL uvedenou v Pages nebo výše (podle názvu repozitáře).

## Co ve veřejném demu funguje

- Prohlížení katalogu, filtrování, košík, checkout a simulovaná platba.
- Testovací objednávky existují pouze v paměti aktuálně otevřené stránky. Po obnovení stránky zmizí.
- Po platbě lze stáhnout **textové DEMO potvrzení**, nikoli fakturu.
- Formulář neodesílá data na žádný backend. Používejte smyšlené údaje.

## Lokální vývoj s backendem

Pro plný původní projekt: spusťte MySQL, Spring Boot podle `src/main/resources/application.properties` a ve `frontend` spusťte `npm ci` a `npm run dev`. Vite proxy předává `/api` na `localhost:8080`. Vývojový režim nepoužívá veřejnou simulaci. **Backend nepřipojujte přímo k veřejnému demu** – obsahuje administrační funkce a zpracování objednávek, které vyžadují samostatný bezpečnostní audit.

## Lokální test veřejné verze

```bash
cd frontend
npm ci
npm run build
npm run preview
```

V produkčním sestavení je nastaveno `VITE_PUBLIC_DEMO=true` v `frontend/.env.production`.

## Poznámka k ochraně soukromí

Veřejné demo nepoužívá databázi ani neposílá e-maily. Obrázky mohou být načítány z externí služby Unsplash, což může znamenat přenos IP adresy třetí straně; pro zcela nezávislé nasazení nahraďte externí obrázky lokálními.
