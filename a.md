Oui, ton approche en 2 phases est la bonne, et le surcoût de 472 ms vient surtout de la phase 1 (sous-requête + `countDistinct`), pas du principe.

**1. Correcte et idiomatique ?**
Oui : pager sur les ids, puis charger les enfants par lots `in :ids`, c'est le pattern standard. Deux réserves :
- La variante idiomatique recharge les entités (`select d from Document d where d.id in :ids`, puis un `left join fetch` par collection, une collection par requête, donc pas de `MultipleBagFetchException`). Cela évite le mapping `Object[]` à la main, qui est fragile.
- Tu as raté `@BatchSize` / `hibernate.default_batch_fetch_size`. Il fait passer le N+1 EAGER de ~350 requêtes à ~7 sans toucher au code de requête.

**2. Pourquoi plus lent que l'existant ?**
Oui, c'est attendu avec cette forme. `id in (select id … where spec)` devient un semi-join sur 750k lignes. Le planner ne peut alors plus utiliser l'index `created_at` avec arrêt anticipé au `LIMIT 50`. Il fait souvent un hash + tri complet. Vérifie avec `EXPLAIN (ANALYZE, BUFFERS)`.

Correctif : construis le prédicat sur le `root`, puis teste `root.getJoins().isEmpty()`.
- Pas de join enfant : pas de doublons possibles, donc tu appliques la spec directement (sans sous-requête ni distinct).
- Joins présents : sous-requête, ou `EXISTS` plutôt que `IN`.

Ajoute aussi `id` comme tie-breaker dans le tri, sinon la pagination n'est pas déterministe. Un index `(created_at desc, id)` aide.

**3. `countDistinct` à chaque appel ?**
Non, c'est probablement le plus gros coût : `count(distinct)` sur 750k est bien plus lent qu'un `count(*)`. Options :
- `count(*)` quand il n'y a pas de join (cas majoritaire).
- Count optionnel ou `Slice` (fetch de size+1) si le contrat d'API le permet.
- Cache du count par hash de spec avec un TTL court.
- Estimation via les stats Postgres pour les requêtes sans filtre.

**4. Alternatives**
- `@BatchSize` (ou le paramètre global) : meilleur rapport coût/bénéfice, et probablement suffisant.
- `@Fetch(SUBSELECT)` : à éviter avec la pagination.
- `@Fetch(JOIN)` : ne contourne pas l'interdit des multiples bags.
- Pagination keyset au lieu d'offset si les pages profondes comptent.
- Remise en cause du join-fetch : ton endpoint existant est déjà à 176 ms. Ton benchmark est probablement en local, où un aller-retour coûte ~0 ms. En prod, avec 1–2 ms de latence réseau, ~350 requêtes sont nettement plus coûteuses. Compare donc le nombre de requêtes et teste sous concurrence avant de conclure.

**5. Architecture recommandée avec RSQL dynamique**
1. Phase 1 : ids paginés, spec directe si aucun join, sinon `EXISTS`, avec tri stable (`created_at desc, id`).
2. Count séparé, `count(*)` ou en cache, et optionnel si possible.
3. Phase 2 : chargement par lots via `@BatchSize` ou un fetch-join par collection.
4. Garde-fous : liste blanche des champs filtrables/indexés, `statement_timeout`, taille de page maximale.

Si tu veux, je peux te réécrire `findPageIds` et `countDocuments` avec la détection `root.getJoins()`.