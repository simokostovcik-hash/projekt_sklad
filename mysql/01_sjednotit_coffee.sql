-- MySQL 8.0, spustit JEN JEDNOU, pri vypnute aplikaci.
-- PRED SPUSTENIM: mysqldump -u root -p --single-transaction --routines projekt_sklad_migrace > projekt_sklad_pred_sjednocenim.sql
-- DDL v MySQL NENI transakcni; bez zalohy nepokracuj.
USE projekt_sklad_migrace;

DELIMITER $$
DROP PROCEDURE IF EXISTS sjednotit_coffee $$
CREATE PROCEDURE sjednotit_coffee()
BEGIN
    DECLARE v_offset BIGINT DEFAULT 0;
    DECLARE v_fk VARCHAR(64);
    DECLARE v_count BIGINT DEFAULT 0;
    DECLARE v_bad BIGINT DEFAULT 0;
    DECLARE v_has_old_tags INT DEFAULT 0;

    -- Vychozi tabulky museji existovat presne v puvodni podobe.
    IF (SELECT COUNT(*) FROM information_schema.tables WHERE table_schema=DATABASE() AND table_name IN ('coffee','coffees','order_item')) <> 3 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT='Chybi COFFEE/COFFEES/ORDER_ITEM nebo migrace jiz probehla.';
    END IF;
    IF (SELECT COUNT(*) FROM information_schema.columns WHERE table_schema=DATABASE() AND table_name='order_item' AND column_name IN ('coffee_id','shop_coffee_id')) <> 2 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT='Neocekavane sloupce ORDER_ITEM; migrace zastavena.';
    END IF;
    SELECT COALESCE(MAX(id),0) INTO v_offset FROM coffees;
    IF v_offset > 900000000000000000 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT='Prilis vysoka ID, migrace zastavena.';
    END IF;

    -- Presun puvodnich skladu do stavajici hlavni tabulky COFFEES s novymi ID.
    -- Puvodni ID v COFFEE se mapuji na ID + maximalni ID v COFFEES.
    INSERT INTO coffees (id,name,type,quantity,price,stock_status,roast_date,roastery_id,visible_in_shop)
    SELECT old.id + v_offset, old.name, old.type, old.quantity, old.price,
           CASE WHEN old.quantity > 0 THEN 'In Stock' ELSE 'Out of Stock' END,
           old.roast_date, old.roastery_id, 0
    FROM coffee old;

    -- Historicke polozky objednavek prepojit na sjednocene ID.
    UPDATE order_item oi
       SET oi.shop_coffee_id = oi.coffee_id + v_offset
     WHERE oi.coffee_id IS NOT NULL AND oi.shop_coffee_id IS NULL;

    SELECT COUNT(*) INTO v_bad FROM order_item oi
      WHERE oi.coffee_id IS NOT NULL AND oi.shop_coffee_id <> oi.coffee_id + v_offset;
    IF v_bad > 0 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT='Konflikt ID historickych objednavek; proved obnovu ze zalohy.';
    END IF;
    SELECT COUNT(*) INTO v_bad FROM order_item oi LEFT JOIN coffees c ON c.id=oi.shop_coffee_id
      WHERE oi.shop_coffee_id IS NOT NULL AND c.id IS NULL;
    IF v_bad > 0 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT='Objednavka odkazuje na neexistujici kavu; proved obnovu ze zalohy.';
    END IF;

    -- Stare vazby tagu prenest, pokud existuje puvodni COFFEE_TAGS.
    SELECT COUNT(*) INTO v_has_old_tags FROM information_schema.tables
      WHERE table_schema=DATABASE() AND table_name='coffee_tags';
    IF v_has_old_tags > 0 THEN
        INSERT IGNORE INTO coffees_tags (coffee_id,tag_id)
        SELECT ct.coffee_id + v_offset, ct.tag_id FROM coffee_tags ct
        INNER JOIN tags t ON t.id=ct.tag_id;
    END IF;

    -- Odpojit starou vazbu ORDER_ITEM.COFFEE_ID (FK jmeno muze byt ruzne).
    SELECT k.constraint_name INTO v_fk
      FROM information_schema.key_column_usage k
      WHERE k.table_schema=DATABASE() AND k.table_name='order_item'
        AND k.column_name='coffee_id' AND k.referenced_table_name IS NOT NULL
      LIMIT 1;
    IF v_fk IS NOT NULL THEN
        SET @stmt = CONCAT('ALTER TABLE `order_item` DROP FOREIGN KEY `',REPLACE(v_fk,'`','``'),'`');
        PREPARE q FROM @stmt; EXECUTE q; DEALLOCATE PREPARE q;
    END IF;

    ALTER TABLE order_item DROP COLUMN coffee_id;
    ALTER TABLE order_item RENAME COLUMN shop_coffee_id TO coffee_id;

    -- Stare tabulky zustanou jako zalohovane archivy; aplikace uz je nepouziva.
    IF v_has_old_tags > 0 THEN
        RENAME TABLE coffee_tags TO coffee_tags_legacy_backup;
    END IF;
    RENAME TABLE coffee TO coffee_legacy_backup,
                 coffees TO coffee,
                 coffees_tags TO coffee_tags;

    -- Overit finalni vazby.
    SELECT COUNT(*) INTO v_bad FROM order_item oi LEFT JOIN coffee c ON c.id=oi.coffee_id
      WHERE oi.coffee_id IS NOT NULL AND c.id IS NULL;
    IF v_bad > 0 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT='Chybna finalni vazba; proved obnovu ze zalohy.';
    END IF;
END $$
DELIMITER ;

CALL sjednotit_coffee();
DROP PROCEDURE sjednotit_coffee;

SELECT 'coffee' AS tabulka, COUNT(*) AS pocet FROM coffee
UNION ALL SELECT 'coffee_tags', COUNT(*) FROM coffee_tags
UNION ALL SELECT 'order_item', COUNT(*) FROM order_item;
SELECT oi.id, oi.coffee_id, c.name FROM order_item oi LEFT JOIN coffee c ON c.id=oi.coffee_id;
-- Po overeni aplikace je mozne odstranit archivy podle souboru 02_volitelne_odstraneni_archivu.sql.
