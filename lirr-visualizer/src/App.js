import { useEffect, useState } from 'react';
import { MapContainer, TileLayer, CircleMarker, Tooltip, Polyline } from 'react-leaflet';
import 'leaflet/dist/leaflet.css';

const routeColors = {
  '1': '#00985F',
  '2': '#CE8E00',
  '3': '#00AF3F',
  '4': '#A626AA',
  '5': '#00B2A9',
  '6': '#FF6319',
  '7': '#6E3219',
  '8': '#00A1DE',
  '9': '#C60C30',
  '10': '#006EC7',
  '11': '#60269E',
  '12': '#4D5357',
  '13': '#A626AA',
};

const getColor = (train) => routeColors[train.route] || 'gray';

function App() {
  const [trains, setTrains] = useState([]);
  const [shapes, setShapes] = useState({});

  useEffect(() => {
    const fetchTrains = async () => {
      try {
        const res = await fetch("https://lirr-app-production.up.railway.app/trains");
        if (!res.ok) throw new Error(`trains HTTP ${res.status}`);
        const data = await res.json();
        setTrains(data);
      } catch (err) {
        console.error("trains error:", err);
      }
    };

    const fetchShapes = async () => {
      try {
        const res = await fetch("https://lirr-app-production.up.railway.app/shapes");
        if (!res.ok) throw new Error(`shapes HTTP ${res.status}`);
        const data = await res.json();
        setShapes(data);
      } catch (err) {
        console.error("shapes error:", err);
      }
    };

    fetchTrains();
    const interval = setInterval(fetchTrains, 10000);
    return () => clearInterval(interval);
  }, []);

  return (
    <MapContainer key="map" center={[40.7, -73.8]} zoom={10} style={{ height: '100vh', width: '100%' }}>
    <TileLayer
  url="https://{s}.basemaps.cartocdn.com/dark_all/{z}/{x}/{y}{r}.png"
  attribution="© OpenStreetMap © CARTO"
/>
      {Object.entries(shapes).map(([shapeId, shape]) => (
        <Polyline
          key={shapeId}
          positions={shape.points.map(p => [p[0], p[1]])}
          color={routeColors[shape.route] || 'gray'}
          weight={2}
          opacity={0.7}
        />
      ))}
      {trains.map((train, i) => (
        train.lat && train.lon ? (
          <CircleMarker
            key={i}
            center={[train.lat, train.lon]}
            radius={6}
            color={getColor(train)}
            fillColor={getColor(train)}
            fillOpacity={0.8}
          >
            <Tooltip>
              Trip: {train.tripId}<br />
              Route: {train.route}<br />
              Stop: {train.stop}<br />
              Delay: {train.delay}s
            </Tooltip>
          </CircleMarker>
        ) : null
      ))}
    </MapContainer>
  );
}

export default App;