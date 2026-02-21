# LIRR Live Train Visualizer

A real-time Long Island Rail Road train visualizer built with Spring Boot and React. View live train positions, route lines, and delay information on an interactive map.

## Demo

Live at: [lirr-fpxbkwk31-duaarondus-projects.vercel.app](https://lirr-fpxbkwk31-duaarondus-projects.vercel.app)

## Features

- Live train positions updated every 30 seconds
- Color-coded routes matching official LIRR colors
- Delay information for each train and stop
- Interactive map with tooltips showing trip ID, stop, and delay
- Rate limiting to protect the API

## Project Structure

```
lirr-app/
├── demo/               ← Spring Boot backend
└── lirr-visualizer/    ← React frontend
```

## Tech Stack

**Backend:**
- Java 17
- Spring Boot 4
- GTFS Realtime (protobuf) for live train data
- Bucket4j for rate limiting

**Frontend:**
- React
- React Leaflet for the map
- CartoDB dark tiles

**Data Sources:**
- MTA GTFS Realtime feed: `https://api-endpoint.mta.info/Dataservice/mtagtfsfeeds/lirr%2Fgtfs-lirr`
- MTA Static GTFS: `https://rrgtfsfeeds.s3.amazonaws.com/gtfslirr.zip`

## Setup

### Prerequisites
- Java 17
- Node.js
- Gradle

### Backend (Spring Boot)

1. Download the LIRR static GTFS zip from:
   ```
   https://rrgtfsfeeds.s3.amazonaws.com/gtfslirr.zip
   ```

2. Extract and copy these files into `demo/src/main/resources/`:
   - `stops.txt`
   - `shapes.txt`
   - `trips.txt`

3. Run the backend:
   ```bash
   cd demo
   ./gradlew bootRun
   ```

   The API will be available at `http://localhost:8080`

### Frontend (React)

1. Install dependencies:
   ```bash
   cd lirr-visualizer
   npm install
   ```

2. Start the app:
   ```bash
   npm start
   ```

   The app will be available at `http://localhost:3000`

## API Endpoints

| Endpoint | Description |
|----------|-------------|
| `GET /trains` | Live train positions and delay data |
| `GET /shapes` | LIRR route shapes with colors |

## Deployment

**Backend:** Deployed on [Railway](https://railway.app)

**Frontend:** Deployed on [Vercel](https://vercel.com)

## LIRR Route Colors

| Route | Color |
|-------|-------|
| Babylon | #00985F |
| Hempstead | #CE8E00 |
| Oyster Bay | #00AF3F |
| Ronkonkoma | #A626AA |
| Montauk | #00B2A9 |
| Long Beach | #FF6319 |
| Far Rockaway | #6E3219 |
| West Hempstead | #00A1DE |
| Port Washington | #C60C30 |
| Port Jefferson | #006EC7 |
