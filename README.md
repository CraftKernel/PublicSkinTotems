# PublicSkinTotems

A mod for Minecraft that renders claimed Totems of Undying as 3D figures wearing the owner's skin.

## How it works

- Pick up a plain, unclaimed totem and it **automatically locks to your skin**.
- Claimed totems show their owner's skin **to everyone** on the server.
- You can carry and use **other players' totems** — they keep that player's skin.
- Hover a claimed totem to see **"Skin Totem of &lt;name&gt;"**.

It's still a real Totem of Undying, so it saves you from death exactly like vanilla.

## Supported versions

| Loader | Minecraft versions |
| --- | --- |
| Fabric | 1.20.1, 1.20.4, 1.21.1, 1.21.2, 1.21.4, 1.21.8 – 1.21.11, 26.1.x, 26.2, 26.3 |
| Forge | 1.20.1, 1.21.1, 1.21.4, 1.21.8 – 1.21.10, 26.1.x, 26.2, 26.3 |
| NeoForge | 1.21.1, 1.21.4, 1.21.8 – 1.21.11, 26.1.x, 26.2, 26.3 |

## Installing

Requires the mod loader for your version ([Fabric Loader](https://fabricmc.net/) with
[Fabric API](https://modrinth.com/mod/fabric-api), Forge, or NeoForge).

- Install on the **client** to see the 3D skins.
- Install on the **server** so totems get claimed in multiplayer.

Skins for players who aren't currently online are fetched from Mojang's session
servers in the background and cached.

## Project layout

| Path | Contents |
| --- | --- |
| `src/` | Fabric 1.20.1 source (the root project) |
| `api/` | Shared skin-totem protocol library |
| `targets/` | One Gradle project per loader family (`fabric-1.21`, `fabric-26`, `forge-1.20.1`, `forge-1.21`, `forge-26`, `neoforge`), plus `common/` code shared between them |
| `versions/`, `targets/*/versions/` | Per-Minecraft-version `.properties` files |

## Building

Build the root Fabric 1.20.1 jar:

```sh
./gradlew build
```

The jar is produced in `build/libs/`.

Build a specific version of another loader from its target directory:

```sh
cd targets/neoforge
./gradlew build -PmcVersion=1.21.4 -PmcLabel=1.21.4
```

`build-all.sh` builds every loader/version combination. `publish-all.sh`,
`publish-modrinth.ps1` and `publish-curseforge.ps1` upload releases; they read
`MODRINTH_TOKEN` and `CURSEFORGE_TOKEN` from the environment.

## License

Licensed under the [Apache License 2.0](LICENSE.txt).
