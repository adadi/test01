Utiliser Math.floorMod pour la normalisation
En Java, (-ccw) % 360 reste négatif quand ccw est positif : par exemple ccw = 90 donne -90, pas 270. Il faut écrire Math.floorMod((int) Math.round(-ccw), 360). Le round compte aussi, car av_display_rotation_get renvoie un double (89,9999… arrive).

2. Utiliser la même convention de miroir dans les deux tables
La table EXIF ignore le miroir avec la règle affichage = flipH ∘ R. La table de la matrice (déterminant -1) doit utiliser exactement la même décomposition, flipH et non flipV. Sinon, un même fichier avec miroir sortira à 180° d'écart selon la source utilisée. Un test croisé suffit : orientation EXIF 4 et matrice équivalente donnent la même rotation.

3. Fixer une priorité explicite entre grille et stream
Il lit maintenant aux deux niveaux, très bien. Mais il faut écrire la règle : pour une grille, le groupe prime, et on ne cumule jamais les deux. Il faut aussi appliquer la rotation au canvas assemblé, pas tuile par tuile.

4. Ne plus appeler getDisplayRotation()
Comme il renvoie 0 dans les deux cas (absent ou présent à 0), le critère de présence impose de lire directement la side data via codecpar().coded_side_data() et AV_PKT_DATA_DISPLAYMATRIX. C'est sûrement son intention, mais autant que ce soit écrit dans le plan.