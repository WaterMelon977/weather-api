"use client";

import { useState } from "react";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";
import { Card, CardContent } from "@/components/ui/card";
import { getCurrentWeather, getForecast } from "../services/weather";

export default function Home() {
  const [city, setCity] = useState("");
  const [current, setCurrent] = useState<any>(null);
  const [forecast, setForecast] = useState<any>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  const handleSearch = async () => {
    if (!city) return;

    try {
      setLoading(true);
      setError("");

      const [currentData, forecastData] = await Promise.all([
        getCurrentWeather(city),
        getForecast(city)
      ]);

      setCurrent(currentData);
      setForecast(forecastData);
    } catch (e: any) {
      setError("Failed to fetch weather");
    } finally {
      setLoading(false);
    }
  };

  console.log("Forecast: ", forecast);
  console.log("Current weather at ", city, ": ", current);
  return (
    <main className="min-h-screen p-6 flex flex-col items-center gap-6">
      <h1 className="text-3xl font-bold">Weather Dashboard</h1>

      <div className="flex gap-2 w-full max-w-md">
        <Input
          placeholder="Enter city"
          value={city}
          onChange={(e) => setCity(e.target.value)}
        />
        <Button onClick={handleSearch}>
          Search
        </Button>
      </div>

      {loading && <p>Loading...</p>}
      {error && <p className="text-red-500">{error}</p>}

      {current && (
        <Card className="w-full max-w-md">
          <CardContent className="p-4 space-y-2">
            <div className="flex justify-between items-center">
              <h2 className="text-2xl font-bold">{current.city}</h2>
              <p><span className="font-semibold">Date:</span> {new Date(current.timestamp).toLocaleDateString()}</p>
              <img
                src={`https://openweathermap.org/img/wn/${current.icon}@2x.png`}
                alt={current.condition}
              />
            </div>
            <div className="grid grid-cols-2 gap-2 text-sm">
              <p><span className="font-semibold">Temp:</span> {current.temperature}°C</p>
              <p><span className="font-semibold">Feels Like:</span> {current.feelsLike}°C</p>
              <p><span className="font-semibold">Condition:</span> {current.condition}</p>
              <p><span className="font-semibold">Humidity:</span> {current.humidity}%</p>
              <p><span className="font-semibold">Wind:</span> {current.windSpeed} m/s</p>
              <p><span className="font-semibold">Visibility:</span> {current.visibility}m</p>
              <p><span className="font-semibold">Lat:</span> {current.latitude}</p>
              <p><span className="font-semibold">Lon:</span> {current.longitude}</p>
              <p><span className="font-semibold">Sunrise:</span> {new Date(current.sunrise).toLocaleTimeString()}</p>
              <p><span className="font-semibold">Sunset:</span> {new Date(current.sunset).toLocaleTimeString()}</p>

            </div>
            <p className="text-[10px] text-muted-foreground pt-2">
              Last updated: {new Date(current.timestamp).toLocaleString()}
            </p>
          </CardContent>
        </Card>
      )}

      {forecast && (
        <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
          {forecast.dailyForecasts.map((f: any, i: number) => (
            <Card key={i}>
              <CardContent className="p-4 space-y-1">
                <div className="flex justify-between items-center">
                  <p className="font-bold">{new Date(f.date).toLocaleDateString()}</p>
                  <img
                    src={`https://openweathermap.org/img/wn/${f.icon}.png`}
                    alt={f.condition}
                  />
                </div>
                <p className="text-sm"><span className="font-semibold">Temp:</span> {f.minTemp}°C - {f.maxTemp}°C</p>
                <p className="text-sm capitalize"><span className="font-semibold">Condition:</span> {f.condition}</p>
                <p className="text-sm"><span className="font-semibold">Humidity:</span> {f.humidity}%</p>
              </CardContent>
            </Card>
          ))}
        </div>
      )}
    </main>
  );
}
