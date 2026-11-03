Le plan v3.1 est solide et les tests réels ont tranché proprement. Avant de donner le GO pour les tests rouges, il y a **une correction à faire sur moi-même** et **un vrai problème** dans la partie matrice.

**Ma correction : ce Samsung écrit bien `irot`**
J'avais dit, d'après un post de la communauté Samsung, que Samsung n'écrit que l'EXIF. Ce fichier (Galaxy A32) contient `irot` et une display matrix. C'est donc le chemin matrice qui sera utilisé pour lui, pas le fallback EXIF. Le fallback little-endian reste nécessaire pour les modèles qui n'écrivent que l'EXIF (d'autres Samsung selon ce post, d'anciens iPhone), donc les tâches 4, 5 et 9 gardent leur utilité. Simplement, ce fichier ne « prouve » pas le bug en production pour le nouveau code. Il le prouve pour le code actuel, qui n'utilise que l'EXIF.

**Le problème : la lecture de la matrice est fausse (tâche 13)**
1. La display matrix est une **3×3** de 9 `int32` : `[a b u / c d v / x y w]`. Le bloc 2×2 utile est aux indices **0, 1, 3, 4**, pas 0 à 3. `[0, 65536, 0, -65536]` correspond à a, b, u, c. Le « det = 0 » vient donc d'une mauvaise lecture, pas d'une particularité de FFmpeg. La vraie 2×2 de l'iPhone est `[0, 65536 / -65536, 0]`, avec un déterminant de +1, ce qui donne une rotation pure. La conclusion (utiliser `av_display_rotation_get`) reste bonne, mais **les 8 matrices canoniques de la tâche 13 doivent être construites au format 3×3**, sinon l'oracle du test est faux.
2. **On ne détecte pas un miroir avec « angle non multiple de 90 »**. Une matrice avec miroir donne aussi un angle propre (0, 90…). Le plan, dans sa version actuelle, ne verrait jamais le miroir. Il faut tester le **déterminant** de la 2×2 (indices 0, 1, 3, 4) : un déterminant négatif signale un miroir.
3. Il faut ensuite rester cohérent avec la décision v2.1 (`R = flipH·M`, comme la table EXIF). Le plan dit maintenant « miroir → log + 0 ». C'est une régression par rapport à la règle validée hier : un `imir` avec rotation sortirait couché. Il faut appliquer la même règle que pour l'EXIF 5 et 7, et garder le test croisé « EXIF 4 et matrice équivalente donnent la même rotation ».

**Le reste me paraît bon**
- Lire l'EXIF via la side data type 40, et annuler le parcours des boîtes ISO-BMFF : justifié par les deux vrais fichiers.
- Grille prioritaire, rotation unique sur le canvas, lecture bornée, cleanup qui ne peut pas faire échouer la conversion, rotation par copie de pixels.
- Point d'attention : la matrice est aussi présente sur la **miniature** (stream 48). Le lecteur doit prendre celle de la grille, jamais celle de la miniature, même si elle était seule. Un test suffit.

**Ce que je lui répondrais :** « GO pour les rouges, avec deux corrections sur la tâche 13 : matrices canoniques en 3×3 (2×2 aux indices 0, 1, 3, 4), et miroir détecté par le déterminant puis traité avec `R = flipH·M`, comme l'EXIF. Ajouter un test qui ignore la matrice de la miniature. »