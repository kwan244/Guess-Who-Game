# Guess who is the thief - AI Interactive Detective Game

<img src="https://github.com/user-attachments/assets/b3ded0f5-31e3-4530-8632-8ae82112fd9e" width="200" height="100">
<img src="https://github.com/user-attachments/assets/051a9784-d171-4043-9fe0-473ead053104" width="100">
<img src="https://github.com/user-attachments/assets/6cf8921f-560e-4729-a75d-0d4d7510fe4a" width="100">

**Guess Who's the Thief** is an AI-driven interactive mystery game where players investigate clues, question suspects, and solve a complex jewelry theft case. By combining engaging gameplay with AI-powered dialogues and interactive game mechanics for clues. The player can fully immerse themselves into the story of the mystery jewelry theft case.

## Key Fetures
- **AI-Powered Dialogues**: Uses OpenAI's chat completion to provide interactive and unique feedback based on the players input.
- **Interactive Game mechanic**: Implement interactive clues containing puzzles such as "connect the lines."
- **Time Management**: Implement real-time countdown timer across all scene to maintain synchronization and enhance gameplay excitement.
- **Sound Integration**: Custom MP3 playback for immersive sound effects during gameplay.
- **Video Integration**: Custom MP4 player for detailed background story telling and immersive gameplay experience.

## Gameplay Control
- **Input dialogue** : Mouse click and keyboard Enter key to submit speech.
- **Select Items** : Mouse click.


## External Libraries and APIs
- **JavaFX**: Utilized for the entire UI and game scene management.
- **JLayer (JavaZoom)**: A library for MP3 playback, used for playing background music and sound effects.
- **Maven**: Manages project dependencies and build processes.
- **OpenAI API**: Used for generating dynamic dialogue responses and clues in the game.


## To setup the API to access Chat Completions and TTS

- add in the root of the project (i.e., the same level where `pom.xml` is located) a file named `apiproxy.config`
- put inside the credentials that you received from no-reply@digitaledu.ac.nz (put the quotes "")

  ```
  email: "UPI@aucklanduni.ac.nz"
  apiKey: "YOUR_KEY"
  ```
  These are your credentials to invoke the APIs. 

  The token credits are charged as follows:
  - 1 token credit per 1 character for Googlel "Standard" Text-to-Speech. 
  - 4 token credit per 1 character for Google "WaveNet" and "Neural2" Text-to-Speech.
  - 1 token credit per 1 character for OpenAI Text-to-Text.
  - 1 token credit per 1 token for OpenAI Chat Completions (as determined by OpenAI, charging both input and output tokens).

## To run the game
1. Clone the repo to your local computer.
2. Ensure you have a valid apiproxy.config file with API credentials.
3. Open the project in your preferred IDE.
4. Build and run the game using Maven :./mvnw clean javafx:run`

## Team Collaboration
| Team Member | Role | GitHub|
|-------------|------|-------|
| Kevin Wang  | Backend Developer & Game Logic| [@kwan244](https://github.com/kwan244)
| Junheng Chen| Frontend AI Integration & UI/UX Design| [@jchen305](https://github.com/jchen305)
| Yichao Wang | Dialogue System & Clue design| [@YcW12343211](https://github.com/YcW12343211)
