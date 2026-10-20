-- VOLITELNE: spust az po otestovani aplikace, po nove zalohovani DB.
-- Neodstranuj archivy, pokud je jeste potrebujes k navratu.
USE projekt_sklad_migrace;
SELECT COUNT(*) AS pocet_kav FROM coffee;
SELECT COUNT(*) AS chybne_objednavky FROM order_item oi LEFT JOIN coffee c ON c.id=oi.coffee_id WHERE oi.coffee_id IS NOT NULL AND c.id IS NULL;
-- Pokud chybne_objednavky = 0 a vse funguje, muzes rucne odkomentovat:
-- DROP TABLE coffee_tags_legacy_backup;
-- DROP TABLE coffee_legacy_backup;
