Très bon résultat : 66/66 verts, vrais fichiers avec vérification d'orientation, build global OK. Le code est propre et suit le plan. Je vois **un point d'organisation à vérifier avant le MR**, deux remarques techniques qui comptent, et quelques détails.

**1. Ce travail est sur la branche d'un autre ticket**
L'IDE affiche le MR **!2088** sur la branche `1686-dsi-52473-conversion-des-doc…-odf`, alors que le ticket et le dossier sont **DSI-51784**. Ça explique d'où venait le « DSI-52473 » d'hier. Si c'est voulu (bug découvert pendant la conversion ODF), précise-le dans le MR. Sinon, sors ce correctif sur sa propre branche et son propre MR : il sera plus simple à relire et à livrer séparément.

**2. Le chemin EXIF n'est jamais exercé par les vrais fichiers**
Les deux vrais fichiers ont `irot`, donc la matrice gagne toujours. Le fallback EXIF, qui était le bug d'origine, n'est couvert que par des fixtures synthétiques. C'est acceptable, mais :
- La « correction » sur l'endianness (iPhone en `MM` dans le fichier, mais `II` dans la side data) laisse penser que **FFmpeg 8 ré-écrit l'EXIF** au lieu de le transmettre tel quel. Ce comportement peut changer d'une version de FFmpeg à l'autre. Mets dans le MR et le PLAN : « à chaque montée de version FFmpeg/JavaCV, relancer `FfmpegHeicToJpegConverterRealHeicUnitTest` et le banc `ContractDiag` ».
- Si un jour un vrai HEIC sans `irot` passe en production (anciens iPhone, certains Samsung), garde-le comme fixture.

**3. La mémoire par conversion**
Pour une image de 50 Mpx en BGR : tuiles (~150 Mo), puis canvas (~150 Mo), puis image tournée (~150 Mo), puis JPEG. Cela peut dépasser **400 Mo en pic pour une seule conversion**, sans compter les 8 threads de décodage. Avec plusieurs requêtes simultanées, l'`OutOfMemoryError` devient réaliste. Ce n'est pas bloquant pour ce MR, mais je proposerais un **sémaphore** qui limite les conversions simultanées (2–3), dans le ticket de suivi de la tâche 20.

**Détails (non bloquants)**
- `applyDisplayRotation` : le `default ->` traite tout angle inconnu comme 270. Le resolver ne renvoie que 0, 90, 180 ou 270, mais un `case 270` explicite et un `default -> throw` seraient plus sûrs. La formule `((x % 360) + 360) % 360` y fait la même chose que `floorMod`, autant utiliser la même partout.
- La rotation et l'assemblage supposent une ligne de pixels de largeur × 3 octets, sans décalage. C'est vrai pour le canvas. Pour le chemin « image simple » (`Java2DFrameConverter`), c'est probable mais pas garanti. Un test sur un HEIC sans grille, ou une vérification du `SampleModel` avec repli sur `toBgrImage`, le confirmerait.
- `MAX_INPUT_BYTES = 50_000_000` vaut 50 Mo, pas 50 Mio (52 428 800). Il faut aligner le nom ou la doc.
- Le Samsung commité contient des coordonnées GPS. C'est un échantillon public, donc acceptable, mais un `exiftool -gps:all=` évite la question en revue.

**Pour clore**
Reste la description du MR (le brouillon est dans PLAN.md, n'oublie pas la mention des 12 tests par réflexion supprimés), puis deux tickets de suivi : profil ICC P3→sRGB, et mesure de performance avec limite de concurrence (tâche 20 et point 3 ci-dessus).

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