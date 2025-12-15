# NoIceMelt Mod - Usage Guide

## Overview
This is a server-side Fabric mod for Minecraft 1.21.10 that allows players to create ice blocks that never melt.

## How to Use

### 1. Rename Ice in an Anvil
- Place an **Ice Block** in an anvil
- Rename it to exactly **"EternalIce"** (case-sensitive)
- Take the renamed ice block from the anvil

### 2. Place the Eternal Ice
- Place the renamed ice block anywhere in your world
- The mod will detect the custom name and mark this block as eternal
- This ice block will **never melt**, even in warm biomes or near light sources

### 3. Breaking Eternal Ice
- When you break an eternal ice block, it will be removed from the tracking system
- If you place it again without renaming, it will behave like normal ice

## Features
- ✅ Server-side only (no client mod required)
- ✅ Persistent storage (eternal ice positions are saved per world)
- ✅ Works in all dimensions
- ✅ No performance impact - uses efficient position tracking

## Installation
1. Make sure you have Fabric Loader installed for Minecraft 1.21.10
2. Place the `noicemelt-1.0.0.jar` file in your server's `mods/` folder
3. Start the server

## Technical Details
- The mod uses mixins to intercept ice block random ticks
- Eternal ice positions are stored in memory per server world
- When a renamed ice block is placed, its position is tracked
- The random tick (which causes melting) is cancelled for tracked positions

## Requirements
- Minecraft 1.21.10
- Fabric Loader
- Fabric API

## Build Information
Built with:
- Java 21
- Fabric Loom
- Fabric API

The compiled mod can be found in: `build/libs/noicemelt-1.0.0.jar`
