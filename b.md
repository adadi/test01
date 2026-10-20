Je te mets les trois méthodes dans un fichier à coller dans `DocumentRepositoryImpl`.
///
// À remplacer dans DocumentRepositoryImpl
// Imports en plus : org.springframework.data.support.PageableExecutionUtils,
//                   jakarta.persistence.criteria.*

@Transactional(readOnly = true)
public Page<Document> searchJoinFetch(Specification<Document> spec, Pageable pageable) {
    List<String> pageIds = findPageIds(spec, pageable);
    List<Document> content = fetchDocuments(pageIds);
    // Le count n'est exécuté que si nécessaire : page 0 non pleine => total = content.size()
    return PageableExecutionUtils.getPage(content, pageable, () -> countDocuments(spec));
}

/**
 * Sonde sans aucun accès DB : la spec crée-t-elle des joins sur des enfants ?
 * Si non, aucun doublon n'est possible et on peut appliquer la spec directement.
 */
private boolean hasChildJoins(Specification<Document> spec) {
    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaQuery<Long> probe = cb.createQuery(Long.class);
    Root<Document> probeRoot = probe.from(Document.class);
    spec.toPredicate(probeRoot, probe, cb);
    return !probeRoot.getJoins().isEmpty();
}

private List<String> findPageIds(Specification<Document> spec, Pageable pageable) {
    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaQuery<String> query = cb.createQuery(String.class);
    Root<Document> root = query.from(Document.class);
    query.select(root.get("id"));

    if (hasChildJoins(spec)) {
        // Joins enfants : EXISTS corrélé, pas de doublons, pas de distinct
        Subquery<Integer> exists = query.subquery(Integer.class);
        Root<Document> subRoot = exists.from(Document.class);
        exists.select(cb.literal(1));
        exists.where(
            cb.equal(subRoot.get("id"), root.get("id")),
            spec.toPredicate(subRoot, query, cb));
        query.where(cb.exists(exists));
    } else {
        // Filtre uniquement sur le parent : l'index created_at peut servir, LIMIT 50 s'arrête tôt
        query.where(spec.toPredicate(root, query, cb));
    }

    List<Order> orders = new ArrayList<>();
    boolean sortsOnId = false;
    for (Sort.Order o : pageable.getSort()) {
        Path<?> path = root.get(o.getProperty());
        orders.add(o.isAscending() ? cb.asc(path) : cb.desc(path));
        sortsOnId |= "id".equals(o.getProperty());
    }
    if (!sortsOnId) {
        orders.add(cb.asc(root.get("id"))); // tie-breaker : pagination déterministe
    }
    query.orderBy(orders);

    TypedQuery<String> typedQuery = entityManager.createQuery(query);
    typedQuery.setFirstResult((int) pageable.getOffset());
    typedQuery.setMaxResults(pageable.getPageSize());
    return typedQuery.getResultList();
}

private long countDocuments(Specification<Document> spec) {
    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaQuery<Long> query = cb.createQuery(Long.class);
    Root<Document> root = query.from(Document.class);

    if (hasChildJoins(spec)) {
        query.select(cb.countDistinct(root.get("id")));
    } else {
        query.select(cb.count(root)); // count(*) : beaucoup moins cher que count(distinct)
    }
    query.where(spec.toPredicate(root, query, cb));
    return entityManager.createQuery(query).getSingleResult();
}

// Index conseillé pour le tri : CREATE INDEX ON t_document (created_at DESC, id);

///
Le fichier `DocumentRepositoryImpl-phase1.java` contient `searchJoinFetch`, `findPageIds`, `countDocuments` et une méthode `hasChildJoins`. Je ne l'ai pas compilé ni exécuté.

- **Détection des joins :** `hasChildJoins` appelle la spec sur une requête de sondage, sans toucher la DB. Si elle ne crée aucun join, `findPageIds` applique la spec directement, sans sous-requête, et `countDocuments` utilise `count(*)`. C'est le chemin qui devrait te rapprocher des 176 ms.
- **Avec joins enfants :** `findPageIds` passe par un `EXISTS` corrélé et `countDocuments` garde `countDistinct`. Le gain est plus incertain, car Postgres transforme souvent `IN` et `EXISTS` en semi-join, donc mesure-le avec `EXPLAIN (ANALYZE, BUFFERS)`.
- **Count conditionnel :** `PageableExecutionUtils.getPage` n'exécute le count que s'il est nécessaire. Sur une page 0 non pleine, le total vaut `content.size()`.
- **Tri stable :** `id` est ajouté en tie-breaker si le tri ne porte pas déjà dessus. L'index `(created_at desc, id)` est noté en commentaire à la fin.

Relance ton benchmark avec le filtre sur le parent, puis avec un filtre qui passe par un join enfant. Si tu me donnes les temps, je t'aide à lire le résultat.