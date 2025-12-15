# NoIceMelt mod
Yo yo yo, you know how minecraft ice melts when placed close to a light source? 
Well no more

## Requirements
- Minecraft Server (Preferably 1.21.10)
- Friends that play on that minecraft server
- Fabric API Mod
- This mod (who would have thought)
- Motivation to test this mod because I sure as fuck have not

## How to use this mod
- Place noicemelt-1.0.0.jar in your mods folder
- Restart Minecraft Server
- Console should now say shit like "NoIceMod initialized" and how many EternalIce blocks are stored in each world
- Take an anvil and Ice Blocks
- Name Ice Blocks "EternalIce"
- Have fun
- (Breaking EternalIce blocks with anything else than a silk touch tool will stick generate water)

## How this shit works in case anyone really cares
- Player places Ice block
- Ice block has name "EternalIce"
- Store block coordinates, so it won't melt randomly after a server restart (hopefully)
- Inject a cancelled "randomTick" for the IceBlock
---
- Player breaks Ice block
- Check if Ice Block coordinate exists in NoIceMelt-ModStorage
- Remove coordinate from NoIceMelt-ModStorage