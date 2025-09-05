# JavaChess

### A small, local chess application written in Java with a Swing UI.

## Summary
- Play standard chess against another local player (alternating turns).
- GUI: `com.clewi.javachess.ui.ChessGUI`
- Game logic: `com.clewi.javachess.game.GameManager`
- Rules & validation: `com.clewi.javachess.game.MoveValidator`

## Quick start
1. Build:
   ```sh
   mvn package
   ```
2. Run:
   ```sh
   mvn -Dexec.mainClass="com.clewi.javachess.ChessGame" exec:java
   ```
   Or run `com.clewi.javachess.ChessGame` from your IDE.

## How to play
- Click a piece to select it, then click a destination square to move.
- Valid moves are enforced by `MoveValidator`.
- UI indicates check/checkmate and ends the game accordingly.

## Saving and loading
- Use the Save / Load buttons in the status panel.
- Save format uses Java serialization via `com.clewi.javachess.model.GameSaveData`.
- Example save data is provided in `save.txt`.

## Project structure

```
javachess/
├── README.md
├── pom.xml
├── save.txt
├── lib/
└── src/
    └── main/
        └── java/
            └── com/
                └── clewi/
                    └── javachess/
                        ├── ChessGame.java
                        ├── ui/
                        │   ├── ChessGUI.java
                        │   └── BoardPanel.java
                        ├── game/
                        │   ├── GameManager.java
                        │   └── MoveValidator.java
                        ├── model/
                        │   ├── Board.java
                        │   └── GameSaveData.java
                        └── pieces/
                            ├── Piece.java
                            ├── King.java
                            ├── Queen.java
                            ├── Rook.java
                            ├── Bishop.java
                            ├── Knight.java
                            └── Pawn.java
```

## Current status & TODO
- Implemented: 
    - [ ] basic moves
    - [ ] captures
    - [ ] check/checkmate
    - [ ] Basic Tests
    - [ ] En passant
- Planned:
    - [ ] Fix King capturing into check
    - [ ] Castling
    - [ ] Promotion UI
    - [ ] Multi-save management
    - [ ] Undo / take-back
    - [ ] Timer / clocks
    - [ ] Redesign button
    - [ ] AI Opponent


## License
- No license file present. Add a LICENSE if you intend to open-source.

## Contact / Notes
- Check console output from `com.clewi.javachess.ChessGame` on startup for run/debug info.
- Asset images live in `lib/`. If images are missing, `BoardPanel` attempts to load from `lib/white_pieces` and `lib/black_pieces`.

### Enjoy developing!