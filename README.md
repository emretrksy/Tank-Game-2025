# Tank 2025 🎮

A 2D top-down tank shooter made from scratch in Java 8 for a BBM103 OOP course.
No game engines, no external libraries — just pure JavaFX and a lot of collision math.

---

## What is this?

You spawn at the bottom of a walled arena. Enemy tanks pour in from the top.
They move, they shoot, and they will run you over if you let them.
You have 3 lives. Make them count.

The whole thing — rendering, physics, AI, camera — runs on a single JavaFX AnimationTimer.

---

## Demo

[![Tank 2025 Demo](https://img.youtube.com/vi/izB2dJV2_Uw/0.jpg)](https://www.youtube.com/watch?v=izB2dJV2_Uw)

---

## Controls

| Key         | Action       |
|-------------|--------------|
| Arrow Keys  | Move         |
| X           | Shoot        |
| P           | Pause        |
| R           | Restart      |
| Escape      | Quit         |

---

## Under the Hood

A few things I'm reasonably proud of:

- **Custom collision detection** — AABB logic written by hand for tanks, bullets, and walls
- **Camera with lerp** — the viewport smoothly follows the player across a map larger than the window
- **Delta time movement** — frame-rate independent physics so speed stays consistent
- **Tank animation** — sprite frame switching tied to movement state
- **Enemy respawning** — new tanks spawn at random intervals to keep pressure on

---

## How to Run

Requires Java 8 (Oracle JDK 8u441 or earlier).

```bash
javac Main.java
java Main
```

Or open in IntelliJ IDEA: mark `src` as Sources Root, set SDK to Java 8, run `Main`.

---

## What I Learned

This was my first time building a game loop without an engine.
Figuring out delta time, camera offset, and getting collisions to feel right
took most of the time — the AI was almost the easy part by comparison.
