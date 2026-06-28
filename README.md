# PublicSkinTotems

A Fabric mod for Minecraft **1.20.1** that renders claimed Totems of Undying as 3D figures wearing the owner's skin.

## How it works

- Pick up a plain, unclaimed totem and it **automatically locks to your skin**.
- Claimed totems show their owner's skin **to everyone** on the server.
- You can carry and use **other players' totems** — they keep that player's skin.
- Hover a claimed totem to see **"Skin Totem of &lt;name&gt;"**.

It's still a real Totem of Undying, so it saves you from death exactly like vanilla.

## Installing

Requires [Fabric Loader](https://fabricmc.net/) and [Fabric API](https://modrinth.com/mod/fabric-api).

- Install on the **client** to see the 3D skins.
- Install on the **server** so totems get claimed in multiplayer.

Skins for players who aren't currently online are fetched from Mojang's session
servers in the background and cached.

## Building

```sh
./gradlew build
```

The mod jar is produced in `build/libs/`.

## License

Licensed under the [Apache License 2.0](LICENSE.txt).
