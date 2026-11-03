Les quatre ajustements sont bien intégrés et corrects. `R = flipH·M` est juste, puisque flipH est sa propre inverse. **Le plan v2.1 est prêt.**

Il reste deux détails techniques sur le point 4, à faire préciser au passage :

**1. L'ordre des octets en lisant la matrice**
La display matrix contient 9 `int32` en **ordre natif** (little-endian sur x86). Si elle est lue via un `ByteBuffer` Java, l'ordre par défaut est big-endian. Sans `.order(ByteOrder.nativeOrder())`, les coefficients sont illisibles, et la table des 8 cas, qui repose justement sur ces coefficients bruts, se trompe silencieusement. Il faut l'écrire dans le plan et le couvrir par le test de contrat.

**2. Choisir un seul chemin de lecture pour le stream**
Le plan mentionne `getVideoSideData("Display Matrix")` **ou** `codecpar().coded_side_data()`. Je retiendrais le second, avec la constante `AV_PKT_DATA_DISPLAYMATRIX`, comme pour la grille. La clé texte `"Display Matrix"` dépend du nom renvoyé par FFmpeg et peut changer d'une version à l'autre. Un seul chemin, identique pour le stream et la grille, c'est aussi moins de code.

Ensuite, tu peux donner le **GO** et déposer les deux HEIC portrait (iPhone et Samsung) dans `tickets\DSI-51784\`. C'est la seule chose qui bloque encore la tâche 3.