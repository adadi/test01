Le plan est solide, mais j'y vois une erreur de mapping et un risque sérieux sur la source de rotation. Les deux sont à corriger avant le GO.

**1. Orientation 4 → 180, pas 0 (bug du tableau)**
Pour ignorer le miroir, la règle cohérente est de garder la rotation R telle que `affichage = flipH ∘ R(stocké)`. L'image reste alors à l'endroit, juste inversée gauche-droite.
- 2 = flipH → **0** ✓
- 4 = flipV = flipH ∘ rot180 → **180** (avec 0, l'image sort à l'envers)
- 5 = transpose = flipH ∘ rot90 → **90** ✓
- 7 = transverse = flipH ∘ rot270 → **270** ✓

La note « 2/4 = flip pur sans rotation » est donc fausse pour 4. Il faut corriger le tableau et la tâche 8 en ajoutant un test 4 → 180.

**2. `displayRotation == 0` comme sentinelle est ambigu**
Avec cette condition, on ne distingue pas « pas d'irot » de « irot = 0, pixels déjà droits ». Or un HEIC réexporté par un éditeur a souvent des pixels déjà tournés avec un EXIF resté à 6. Le fallback EXIF provoquerait alors exactement la double rotation qu'on veut éviter. C'est aussi pour ça que la tâche 6 n'est pas testable telle quelle.
→ Il faut tester la **présence** de la side data `DISPLAYMATRIX`, pas sa valeur. Le fallback EXIF ne s'applique que si elle est absente.

**3. Grilles de tuiles iPhone : la rotation n'est peut-être pas sur le stream lu**
Les HEIC iPhone sont des grilles. Dans FFmpeg 7.1+, il me semble que la display matrix d'une grille est exportée sur le **stream group** (`AVStreamGroupTileGrid.coded_side_data`), pas sur les streams de tuiles. Or `getDisplayRotation()` lit le stream vidéo du grabber. Sur un vrai iPhone, ça peut donc renvoyer 0 en permanence, et vous retombez silencieusement sur l'EXIF. C'est à vérifier dans les sources JavaCV. Le test de contrat (tâche 3) le révélera, ce qui le rend vraiment bloquant.

**4. Convention de signe et normalisation**
`av_display_rotation_get` renvoie un angle **anti-horaire** dans [-180, 180]. Un portrait iPhone ressort typiquement à -90. Il faut normaliser vers {0, 90, 180, 270} et rejeter ou logger les valeurs non multiples de 90. Avec un `imir`, la valeur extraite de la matrice peut aussi être faussée : il faut vérifier le déterminant.

**Points secondaires**
- **Plafond 50 Mo** : faire une lecture bornée (lire au plus 50 Mo + 1 octet). Lire tout le flux puis vérifier la taille annule l'intérêt du plafond.
- **« Sans ICC »** : les photos iPhone sont en Display P3. Sans profil ICC, les couleurs sortent ternes. C'est hors périmètre v1, mais ça mérite un ticket.
- **EXIF dans le JPEG** : si un jour l'EXIF est recopié, il faut forcer Orientation = 1, sinon les viewers tourneront l'image une seconde fois.

Le reste (validation TIFF, `payload[4+offset]`, pièges `iloc`/`iinf`, cleanup non-échouable, ordre rouge puis vert) me paraît juste.