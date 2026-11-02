**Le plan tient la route**, mais je vois trois points à corriger avant de donner le GO.

**1. irot/imir : risque de double rotation (le plus important).** En HEIF, la rotation normative est portée par les propriétés `irot`/`imir` (dans `ipma`). L'orientation EXIF n'est qu'informative. Les iPhone écrivent souvent les deux. Si votre décodeur (ffmpeg, libheif…) applique déjà `irot`, et que vous appliquez ensuite l'EXIF, l'image tourne deux fois. ffmpeg récent expose `irot` en display matrix, et le CLI fait l'autorotate par défaut. Je vous conseille d'ajouter un test rouge sur les vrais fichiers : vérifier les dimensions et pixels après décodage, avant toute rotation maison. Ensuite, décidez d'une seule source de vérité : `irot`/`imir` en priorité, l'EXIF seulement en fallback.

**2. Offset du TIFF.** Le TIFF commence à `payload[4 + exif_tiff_header_offset]`, pas à `payload[offset]`. La formulation « `payload[0:4]` = offset → TIFF direct » est ambiguë. Il faut un test avec offset=0 (Samsung sans préfixe) et un autre avec offset=6.

**3. Orientations 2/4/5/7 → 0.** Ces orientations ne sont pas « rien ». 5 et 7 combinent une rotation de 90°/270° avec un miroir. Les mapper à 0 donne une image couchée. Gérez-les, ou au minimum faites un mapping 5→90 et 7→270 en documentant que le miroir est ignoré.

**Pièges de parsing à couvrir par des tests :**
- **`iloc` :** versions 0/1/2, `offset_size`/`length_size`/`base_offset_size` variables (0/4/8), `construction_method` 1 (données dans `idat`), extents multiples, `item_ID` sur 32 bits en v2.
- **`iinf`/`infe` :** `entry_count` sur 16 ou 32 bits selon la version, `infe` v2 vs v3.
- **Tag 0x0112 :** un SHORT occupe les 2 premiers octets du champ valeur, dans l'endianness du TIFF. Il ne faut pas le lire comme un uint32.

Le reste est cohérent : le retrait du motif 2 octets, le fallback limité aux en-têtes complets, l'arrêt au premier hit valide, la correction du fixture `II 00 2A` et l'approche tests rouges d'abord. Vous pouvez valider en demandant d'intégrer ces trois points dans `PLAN.md`.