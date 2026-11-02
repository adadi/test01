Le verdict de l'autre assistant est sérieux, et il a raison de me corriger sur le **point 7** : il y a bien un `log.warn` avec l'exception complète, et un `catch (Exception)` laisse remonter un `OutOfMemoryError`. Je retire donc ce point, sauf sur un aspect : l'appelant reçoit le même `Optional.empty()` quelle que soit la cause.

En regardant le code maintenant visible, je vois **trois choses qu'il n'a pas relevées** :

**1. `parseExifRotation` peut produire une rotation aléatoire (bug probable)**
La boucle parcourt **tout le fichier**, octet par octet, à la recherche d'un en-tête TIFF (`II*\0` / `MM\0*`). Elle ne s'arrête que si l'orientation vaut 3, 6 ou 8. Pour une photo déjà droite (orientation 1), elle **continue donc à scanner** les données HEVC compressées. Ces données sont pseudo-aléatoires : tôt ou tard, 4 octets ressemblent à un en-tête TIFF, et si les octets suivants donnent « 6 », l'image est tournée à tort. C'est rare, mais non déterministe et très difficile à diagnostiquer.

- **Correctif minimal** : s'arrêter au premier en-tête TIFF valide, quelle que soit l'orientation (renvoyer 0 si elle vaut 1).
- **Correctif propre** : localiser l'item Exif via la structure HEIF, ou utiliser une bibliothèque comme `metadata-extractor`, qui gère le HEIC.

**2. Le `finally` peut faire échouer une conversion réussie**
Si `Files.deleteIfExists` lève une `IOException` (fichier encore verrouillé, ce qui arrive sous Windows), cette exception **remplace le `return Optional.of(...)`**. Le `catch (Exception)` renvoie alors `empty()` alors que le JPEG était prêt. De plus, si la première suppression échoue, le dossier parent n'est jamais supprimé. Il faut entourer chaque suppression d'un try/catch qui se contente de logger.

**3. Une rotation par `AffineTransform` bicubique est inutile pour des multiples de 90°**
C'est un ré-échantillonnage complet : coûteux sur 12–50 Mpx, et il peut laisser des artefacts en bordure. Une copie pixel à pixel (transposition et inversion) est exacte et bien plus rapide. C'est un point mineur.

**Sur la suite proposée** : son plan (PLAN.md puis TDD) est bon. Je mettrais le **bug du scan EXIF au même niveau que le plafond de taille**, car les deux se corrigent vite et se testent facilement :
- pour le scan, un test avec une photo en orientation 1 dont les données contiennent un faux en-tête ;
- pour le `finally`, un test qui simule l'échec de suppression.

La réécriture en décodeur unique peut attendre la mesure de performance.