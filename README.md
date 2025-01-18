# AI Email Reply Assistant

AI Email Assistant is a simple Chrome Extension built with a **Spring Boot backend** and basic **JavaScript** to generate AI-powered email replies directly in Gmail. The project uses the Google Gemini API to provide intelligent email responses.

---

## Features

- **AI-Powered Replies**: Generate context-aware, professional email responses with a single click.
- **Customizable Tones**: Support for various tones (e.g., professional, casual) to fit different contexts.
- **Gmail Integration**: Injects an "AI Reply" button directly into the Gmail interface.

---

## Demo

Here is a demo of the extension:

<img width="1188" alt="Screenshot 2025-01-18 at 4 12 44 PM" src="https://github.com/user-attachments/assets/78441228-3247-44aa-894a-14636e2bb37e" />

---

## Project Structure

- **`emailgenerator/`**: Contains the Spring Boot application for processing API requests and generating email replies.
- **`emailExtension/`**: Chrome Extension files, including the script to inject the "AI Reply" button into Gmail.

---

## Setup Instructions

### Backend
1. Ensure you have **Java 21+** and **Maven** installed.
2. Navigate to the `emailgenerator/` directory:
   
   ``` bash
   cd emailgenerator
   ```
3. Build and run the application (application will start on LocalHost://8080):
   
  ``` bash
   mvn spring-boot:run
  ```
  (or Directly Start the application from your IDE)

### Chrome Extension
4. Open Chrome and navigate to chrome://extensions
5. Click Load unpacked and select the extension/ folder (enable developer mode as well)
6. The extension will appear in the Chrome toolbar
   
<img width="505" alt="Screenshot 2025-01-18 at 4 30 19 PM" src="https://github.com/user-attachments/assets/a9e1ae4b-5c82-49c2-a51c-d543350d781e" />

--- 

#### The chrome extensions is now ready for use, simply open an email reply box, and the "AI-reply" button will be injected onto the gmail UI!
