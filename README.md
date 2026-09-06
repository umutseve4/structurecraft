<h1 align="center">StructureCraft</h1>

<p align="center">
  Right-click once, get a whole city.<br>
  A Fabric 1.20.1 mod whose blueprint items build finished structures in-world —<br>
  a cottage, a fortress, and a 129×129 layered city — plus a rideable skateboard and a flyable airplane.
</p>

<p align="center">
  <a href="https://github.com/umutseve4/structurecraft/actions/workflows/build.yml"><img src="https://github.com/umutseve4/structurecraft/actions/workflows/build.yml/badge.svg" alt="CI"></a>
  <img src="https://img.shields.io/badge/blueprints-3-FF4D4F?style=flat-square" alt="3 blueprints">
  <img src="https://img.shields.io/badge/vehicles-2-FF4D4F?style=flat-square" alt="2 vehicles">
  <img src="https://img.shields.io/badge/mega%20city-129%C3%97129-FF4D4F?style=flat-square" alt="129x129">
</p>

---

## 30 seconds in game

1. Open the creative inventory and find the **StructureCraft** tab.
2. Right-click a blueprint on the ground — the structure generates with its entrance facing you.
3. Right-click a skateboard/airplane item to spawn the vehicle, then right-click the vehicle to ride. Punch it to pick it back up.

## What's in the tab

| Item | What it does |
|---|---|
| Cozy Home Blueprint | Detailed starter cottage: fireplace + chimney, furnace, crafting table, bed, stocked starter chests, crop farm, scarecrow (armor stand + pumpkin), water well, storage shed, fenced yard with gates |
| Fortress Blueprint | Multi-story fortress with moat + iron gate, courtyard fountain, grand dining hall with chandeliers, climbable corner towers, stocked storage room, enchanting library, Nether portal room with nether wart farm, living quarters, villager holding area, top-level beacon room |
| Mega City Blueprint | 129×129 layered city: paved road grid + railway, perimeter walls, skyscrapers (interior floors, rooftop antennas, billboards), apartments, houses, industrial zone, construction site with crane, park with gazebo and statue, airport runway + hangar |
| Skateboard | Rideable entity; jump off ledges to trigger a 360 trick spin animation |
| Airplane | Flyable vehicle: hold forward for thrust, look up/down to pitch, glides + gravity when slow |

## Build it

Requirements: **JDK 17+**, **Gradle 8.6+** (this repo does not ship the Gradle
wrapper binary), and internet access (Gradle downloads Fabric Loom + Minecraft).

```bash
gradle wrapper --gradle-version 8.8   # one-time: generates gradlew locally
./gradlew build
# jar output: build/libs/structurecraft-1.0.0.jar
```

Drop the jar plus [Fabric API](https://modrinth.com/mod/fabric-api) into `mods/`
of a Fabric Loader 1.20.1 instance.

## How it is verified

Every push and PR runs `build.yml`:

| Job | What it proves |
|---|---|
| **build** | The mod compiles (JDK 17, Gradle 8.8) and the jar artifact is uploaded |
| **smoke-test** | A real Fabric 1.20.1 **dedicated server** boots with the mod installed and reaches `Done (` — a genuine load test, not just a compile |

Loader and Fabric API versions are pinned from `gradle.properties` (single source
of truth), so CI tests exactly the versions the mod is built against. On failure,
the full server log and any crash reports are uploaded as the `server-log`
artifact.

## Limits

- CI proves compile **and** in-server load. In-game gameplay — structure placement and vehicle physics — is verified **manually**, not automatically.
- Mega City places a very large number of blocks in a single action; expect a short freeze on placement. Use a superflat world for best results.
- Structures generate relative to the clicked block; terrain is **not** flattened first.
- Vehicles use vanilla textures (planks/iron) mapped onto simple cuboid models; no custom PNG assets are required — and none are provided.
- Fabric 1.20.1 only. Other loaders and Minecraft versions are untested.

---

MIT
