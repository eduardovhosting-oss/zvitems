# CustomItems

Plugin base estilo ItemsAdder para Paper 1.21.8.

## Requisitos
- Java 21
- Paper 1.21.8
- Gradle 8+

## Compilar
Linux/macOS:
`./gradlew build`

Windows:
`gradlew.bat build`

El JAR aparece en `build/libs/`.

## Comandos
- `/customitems give <jugador> <id> [cantidad]`
- `/customitems list`
- `/customitems reload`
- `/customitems pack`
- `/customitems editor`

Permisos:
- `customitems.admin`
- `customitems.give`

## Crear un item
Copia `plugins/CustomItems/items/ruby_sword.yml` y cambia su `id`.

El sistema usa PersistentDataContainer para identificar los objetos, por lo que no depende únicamente del nombre visible.

## Texturas
Coloca PNG en:
`plugins/CustomItems/resourcepack/assets/customitems/textures/item/`

Los archivos se copian al resource pack generado. La configuración incluye el `custom_model_data` del item.

## Nota
Esta versión es una base funcional y extensible. Los sistemas de bloques con modelos complejos, armaduras 3D, editor visual completo y generación avanzada de modelos requieren módulos adicionales.
