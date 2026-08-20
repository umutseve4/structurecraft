# StructureCraft (Fabric 1.20.1)

[![CI](https://github.com/umutseve4/structurecraft/actions/workflows/build.yml/badge.svg)](https://github.com/umutseve4/structurecraft/actions/workflows/build.yml)

A Fabric mod that adds a dedicated **StructureCraft** creative tab containing **blueprint items** that instantly generate full structures in-world, plus two **custom vehicles**.

## Content

| Item | What it does |
|---|---|
| Cozy Home Blueprint | Detailed starter cottage: fireplace + chimney, furnace, crafting table, bed, stocked starter chests, crop farm, scarecrow (armor stand + pumpkin), water well, storage shed, fenced yard with gates |
| Fortress Blueprint | Multi-story fortress with moat + iron gate, courtyard fountain, grand dining hall with chandeliers, climbable corner towers, stocked storage room, enchanting library, Nether portal room with nether wart farm, living quarters, villager holding area, top-level beacon room |
| Mega City Blueprint | 129x129 layered city: paved road grid + railway, perimeter walls, skyscrapers (interior floors, rooftop antennas, billboards), apartments, houses, industrial zone, construction site with crane, park with gazebo and statue, airport runway + hangar |
| Skateboard | Rideable entity; jump off ledges to trigger a 360 trick spin animation |
| Airplane | Flyable vehicle: hold forward for thrust, look up/down to pitch, glides + gravity when slow |

## Usage

1. Open creative inventory, find the **StructureCraft** tab.
2. Right-click a blueprint on the ground: structure generates with its entrance facing you.
3. Right-click skateboard/airplane item on the ground to spawn the vehicle, then right-click the vehicle to ride. Punch the vehicle to pick it back up.

> Mega City places a very large number of blocks in a single action; expect a short freeze on placement. Use a superflat world for best results.

## Build

Requirements: **JDK 17+**, **Gradle 8.6+** (this repo does not ship the Gradle wrapper binary), internet access (Gradle downloads Fabric Loom + Minecraft).

```bash
gradle wrapper --gradle-version 8.8   # one-time: generates gradlew locally
./gradlew build
# jar output: build/libs/structurecraft-1.0.0.jar
```

Drop the jar plus [Fabric API](https://modrinth.com/mod/fabric-api) into `mods/` of a Fabric Loader 1.20.1 instance.

## CI

Every push and PR runs GitHub Actions (`build.yml`):

1. **build** — compiles the mod (JDK 17, Gradle 8.8) and uploads the jar artifact.
2. **smoke-test** — boots a real Fabric 1.20.1 dedicated server (loader 0.15.11) with the mod installed and asserts the server reaches `Done (`, printing an `===== OTOMATIK KONTROL =====` PASS/FAIL block.

## Status / known limitations

- Compile **and** in-server load are verified in CI on every push (see badge above); in-game gameplay (structure placement, vehicle physics) is verified manually.
- Structures generate relative to the clicked block; terrain is not flattened first.
- Vehicles use vanilla textures (planks/iron) mapped onto simple cuboid models; no custom PNG assets required.

## License

MIT
