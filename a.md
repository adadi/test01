Non. **Le fichier temporaire n'est pas obligatoire.** L'autre assistant l'a vérifié dans les sources de JavaCV 1.5.14 :
- `new FFmpegFrameGrabber(InputStream)` gère le seek via `mark/reset`, ce dont le conteneur HEIF a besoin ;
- un `ByteArrayInputStream` **ne copie pas** le tableau : tous les grabbers peuvent partager le même `byte[] heicBytes`, sans dupliquer le fichier en mémoire.

**Ce qu'il faudrait changer**
- Remplacer `Path heicTempFile` par `byte[] heicBytes` dans `decodePrimaryImage`, `stitchTileGrid`, `decodeTilesInParallel` et `decodeVideoStream`.
- Écrire `new FFmpegFrameGrabber(new ByteArrayInputStream(heicBytes))` avec **un stream neuf par grabber**. Un stream partagé entre threads casserait les seeks.
- Supprimer `createPrivateTempFile`, `Files.write`, le `finally` avec `deleteQuietly` et le `@SuppressWarnings("java:S5443")`.

**Ce qu'on y gagne**
- Plus d'écriture disque, plus de nettoyage, plus de permissions 700.
- Le problème du fichier verrouillé sous Windows disparaît.
- Ça marche aussi sur un filesystem en lecture seule.

**Ce qu'on y perd**
Presque rien en mémoire, puisque les octets sont déjà dans `heicBytes`. Les lectures passent par un callback Java au lieu du cache disque de l'OS, ce qui est négligeable à cette taille.

**Mon conseil : pas dans ce MR.** Le code actuel vient d'être validé, et c'est un changement de la couche d'entrée/sortie. Fais-le dans un MR séparé, idéalement avec la mesure de la tâche 20. Les tests sur les vrais fichiers iPhone et Samsung serviront de filet : s'ils restent verts avec le passage en mémoire, le changement est sûr.