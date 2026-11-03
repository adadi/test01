Bon travail, la phase rouge est propre. **GO pour la phase verte**, avec trois points à régler d'abord, dont un manque dans les rouges.

**1. Il manque les rouges des tâches 11 et 12**
Les 5 fichiers couvrent la matrice, l'EXIF, le resolver et la side data, mais aucun test ne couvre :
- le **plafond de 50 Mio** : un flux de 50 Mio + 1 octet doit donner `empty()`, et il faut vérifier que la lecture s'arrête vraiment là (par exemple avec un `InputStream` qui compte les octets lus) ;
- le **cleanup qui ne doit pas faire échouer la conversion** : un échec de suppression ne doit pas perdre le JPEG. Passe par la méthode qu'on peut simuler, comme prévu.

Il faut les écrire **avant** le vert, sinon ces deux correctifs partent sans filet.

**2. L'oracle des matrices avec miroir ne doit pas venir de votre propre code**
Si les 8 matrices canoniques et leurs rotations attendues sont écrites à la main avec la même règle `R = flipH·M` que l'implémentation, le test croisé confirme la règle par elle-même. Construisez plutôt les matrices de test avec les fonctions de FFmpeg, `av_display_rotation_set` puis `av_display_matrix_flip`, disponibles dans bytedeco avutil. L'oracle devient alors indépendant, y compris pour les miroirs.

**3. Petite remarque sur le rouge**
« Compilation failure » est un rouge valide, mais il est faible. Au début du vert, créez d'abord les classes vides et vérifiez que les tests échouent **sur leurs assertions**, en particulier le cas TIFF little-endian `II 2A 00`. C'est ce test-là qui doit prouver le bug.

**Ta question : oui, ajoute les 2 vrais HEIC comme fixtures**, avec trois précautions :
- **Les dimensions ne suffisent pas** : 3024x4032 sort aussi bien avec une rotation de 90° que de 270°. Ajoute une assertion d'orientation. Le plus simple est la couleur moyenne d'un coin ou d'un quart de l'image, comparée à une référence produite par un décodeur indépendant (aperçu Windows ou libheif), avec une tolérance.
- **Données personnelles du fichier iPhone** : si c'est une vraie photo (la tienne ou celle d'un collègue), vérifie les coordonnées GPS et les infos de l'appareil avant de la committer. `exiftool -gps:all=` les retire d'un HEIC sans ré-encoder l'image. Le Samsung vient d'un site d'échantillons libres, il n'y a pas ce souci.
- **Taille** : environ 3,3 Mo en tout, c'est acceptable. Vérifie juste si le repo impose Git LFS pour les binaires.

Garde aussi une copie dans `tickets\DSI-51784\` pour la traçabilité du diagnostic.

Pour la suppression des tests par réflexion sur `parseExifRotation` et `parseTiffOrientation` : d'accord, ce sont eux qui verrouillaient le bug. Mentionne-le dans la description du MR pour que le reviewer ne pense pas qu'on a perdu de la couverture.