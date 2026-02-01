"use client";

import { useState } from "react";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";
import { Card, CardContent } from "@/components/ui/card";
import { getCurrentWeather, getForecast } from "../services/weather";
import { WeatherSlider } from "@/components/ui/weather-slider";
import { motion, AnimatePresence } from "framer-motion";
import { Search, Wind, Droplets, Eye, Sun, Sunrise, Sunset, Thermometer, MapPin } from "lucide-react";

export default function Home() {
  const [city, setCity] = useState("");
  const [mode, setMode] = useState<"current" | "forecast">("current");
  const [current, setCurrent] = useState<any>(null);
  const [forecast, setForecast] = useState<any>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  const handleSearch = async () => {
    if (!city) return;

    try {
      setLoading(true);
      setError("");

      // Only fetch the one requested
      if (mode === "current") {
        const data = await getCurrentWeather(city);
        setCurrent(data);
        setForecast(null);
      } else {
        const data = await getForecast(city);
        setForecast(data);
        setCurrent(null);
      }
    } catch (e: any) {
      setError(`Failed to fetch ${mode} data`);
      setCurrent(null);
      setForecast(null);
    } finally {
      setLoading(false);
    }
  };

  const isDayTime = current ? (() => {
    const now = new Date(current.timestamp).getTime();
    const sunrise = new Date(current.sunrise).getTime();
    const sunset = new Date(current.sunset).getTime();
    return now >= sunrise && now <= sunset;
  })() : true;

  return (
    <main className={`min-h-screen flex flex-col items-center justify-start pt-20 px-6 pb-12 gap-12 font-sans transition-all duration-1000 ${isDayTime ? 'bg-blue-400/10' : 'bg-indigo-950/30'}`}>
      <motion.div
        initial={{ opacity: 0, y: -20 }}
        animate={{ opacity: 1, y: 0 }}
        className="text-center space-y-2"
      >
        <h1 className="text-5xl md:text-7xl font-bold crystal-text tracking-tight">
          Crystal Weather
        </h1>
        <p className="text-foreground/60 text-lg">Experience the elements in clarity</p>
      </motion.div>

      <motion.div
        initial={{ opacity: 0, scale: 0.95 }}
        animate={{ opacity: 1, scale: 1 }}
        transition={{ delay: 0.2 }}
        className={`glass p-6 md:p-8 rounded-[2rem] w-full max-w-2xl flex flex-col items-center gap-6 md:gap-8 border-white/40 shadow-2xl ${isDayTime ? 'bg-white/40' : 'bg-indigo-900/20'}`}
      >
        <WeatherSlider mode={mode} onChange={setMode} />

        <div className="flex flex-col sm:flex-row gap-4 w-full relative">
          <div className="relative flex-1">
            <MapPin className="absolute left-4 top-1/2 -translate-y-1/2 h-5 w-5 text-foreground/40" />
            <Input
              placeholder="Where to?"
              className="pl-12 h-12 md:h-14 rounded-xl md:rounded-2xl bg-white/20 border-white/30 focus:bg-white/40 focus:ring-primary/40 transition-all text-base md:text-lg w-full"
              value={city}
              onChange={(e) => setCity(e.target.value)}
              onKeyDown={(e) => e.key === "Enter" && handleSearch()}
            />
          </div>
          <Button
            onClick={handleSearch}
            className="h-12 md:h-14 px-8 rounded-xl md:rounded-2xl bg-primary hover:bg-primary/80 text-white shadow-xl shadow-primary/20 transition-all active:scale-95 w-full sm:w-auto"
            disabled={loading}
          >
            {loading ? (
              <motion.div
                animate={{ rotate: 360 }}
                transition={{ repeat: Infinity, duration: 1, ease: "linear" }}
              >
                <Search className="h-6 w-6" />
              </motion.div>
            ) : (
              <span className="flex items-center gap-2 font-bold text-base md:text-lg justify-center">
                Go <Search className="h-5 w-5" />
              </span>
            )}
          </Button>
        </div>


        {error && (
          <motion.p
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            className="text-red-500 font-medium"
          >
            {error}
          </motion.p>
        )}
      </motion.div>

      <AnimatePresence mode="wait">
        {current && mode === "current" && (
          <motion.div
            key="current-weather"
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            exit={{ opacity: 0, y: -20 }}
            className="w-full max-w-2xl"
          >
            <Card className={`overflow-hidden rounded-[2.5rem] border-0 transition-all duration-1000 shadow-xl ${isDayTime ? 'glass bg-linear-to-br from-blue-400/20 to-purple-400/20' : 'glass-dark bg-linear-to-br from-indigo-900/40 to-slate-900/40'}`}>
              <CardContent className="p-8 sm:p-10 space-y-10">
                <div className="flex flex-col md:flex-row justify-between items-center gap-6">
                  <div className="space-y-3 text-center md:text-left">
                    <div className="inline-flex items-center gap-2 px-3 py-1.5 rounded-full bg-white/10 border border-white/10 text-[10px] font-bold uppercase tracking-widest">
                      {isDayTime ? <Sun className="h-3.5 w-3.5 text-amber-400" /> : <Eye className="h-3.5 w-3.5 text-indigo-300" />}
                      {isDayTime ? 'Daytime' : 'Nighttime'}
                    </div>
                    <h2 className="text-4xl sm:text-5xl md:text-6xl font-black crystal-text break-words leading-none">{current.city}</h2>
                    <p className="text-base text-foreground/60 font-medium">{new Date(current.timestamp).toLocaleDateString(undefined, { weekday: 'long', day: 'numeric', month: 'long' })}</p>
                  </div>
                  <div className="flex flex-col items-center">
                    <motion.div
                      animate={{ y: [0, -8, 0] }}
                      transition={{ repeat: Infinity, duration: 4, ease: "easeInOut" }}
                    >
                      <img
                        src={`https://openweathermap.org/img/wn/${current.icon}@4x.png`}
                        alt={current.condition}
                        className="w-32 h-32 sm:w-40 sm:h-40 drop-shadow-2xl"
                      />
                    </motion.div>
                    <p className="text-xl sm:text-2xl font-black capitalize -mt-4 bg-clip-text text-transparent bg-linear-to-b from-white to-white/60">{current.condition}</p>
                  </div>
                </div>

                <div className="grid grid-cols-1 md:grid-cols-2 gap-8 items-center bg-white/5 p-8 rounded-[2rem] border border-white/10">
                  <div className="flex items-center gap-6 justify-center md:justify-start">
                    <div className="p-4 rounded-2xl bg-primary/20 text-primary shadow-lg">
                      <Thermometer className="h-10 w-10" />
                    </div>
                    <div className="space-y-0">
                      <p className="text-6xl font-black tracking-tighter">{Math.round(current.temperature)}°</p>
                      <p className="text-base text-foreground/50 font-bold">Feels like {Math.round(current.feelsLike)}°</p>
                    </div>
                  </div>

                  <div className="space-y-6">
                    {/* First Line: Humidity and Wind */}
                    <div className="grid grid-cols-2 gap-4">
                      <WeatherStat icon={<Droplets className="text-blue-400" />} label="Humidity" value={`${current.humidity}%`} />
                      <WeatherStat icon={<Wind className="text-emerald-400" />} label="Wind" value={`${current.windSpeed}m/s`} />
                    </div>
                    {/* Second Line: Sunrise and Sunset */}
                    <div className="grid grid-cols-2 gap-4 pt-4 border-t border-white/10">
                      <WeatherStat icon={<Sunrise className="text-amber-300" />} label="Sunrise" value={new Date(current.sunrise).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })} />
                      <WeatherStat icon={<Sunset className="text-orange-400" />} label="Sunset" value={new Date(current.sunset).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })} />
                    </div>
                  </div>
                </div>

                <div className="grid grid-cols-3 gap-4 border-t border-white/10 pt-8">
                  <div className="text-center">
                    <p className="text-[10px] font-bold uppercase tracking-widest text-foreground/40 mb-1">Visibility</p>
                    <p className="text-sm font-black">{current.visibility / 1000} km</p>
                  </div>
                  <div className="text-center border-x border-white/10">
                    <p className="text-[10px] font-bold uppercase tracking-widest text-foreground/40 mb-1">Latitude</p>
                    <p className="text-sm font-black">{current.latitude.toFixed(2)}</p>
                  </div>
                  <div className="text-center">
                    <p className="text-[10px] font-bold uppercase tracking-widest text-foreground/40 mb-1">Longitude</p>
                    <p className="text-sm font-black">{current.longitude.toFixed(2)}</p>
                  </div>
                </div>
              </CardContent>
            </Card>
          </motion.div>
        )}

        {forecast && mode === "forecast" && (
          <motion.div
            key="forecast-weather"
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            exit={{ opacity: 0, y: -20 }}
            className="w-full max-w-2xl space-y-6"
          >
            <div className="flex items-center justify-between px-4">
              <h3 className="text-xl font-black crystal-text uppercase tracking-widest">5-Day Forecast</h3>
              <div className="h-px flex-1 mx-6 bg-white/10" />
            </div>

            <div className="space-y-4">
              {forecast.dailyForecasts.map((f: any, i: number) => (
                <motion.div
                  key={i}
                  initial={{ opacity: 0, x: -20 }}
                  animate={{ opacity: 1, x: 0 }}
                  transition={{ delay: i * 0.1 }}
                >
                  <Card className={`overflow-hidden rounded-3xl border-0 transition-all hover:translate-x-2 duration-300 shadow-lg ${isDayTime ? 'glass bg-white/20' : 'glass-dark bg-indigo-900/10'}`}>
                    <CardContent className="p-0">
                      <div className="flex flex-col md:flex-row items-center">
                        {/* Date and Icon Section */}
                        <div className="flex items-center gap-6 p-6 md:w-1/3 border-b md:border-b-0 md:border-r border-white/10 justify-between md:justify-start w-full">
                          <div className="space-y-1">
                            <p className="font-black text-lg leading-none">
                              {new Date(f.date).toLocaleDateString(undefined, { weekday: 'long' })}
                            </p>
                            <p className="text-xs font-bold text-foreground/40 uppercase tracking-tighter">
                              {new Date(f.date).toLocaleDateString(undefined, { month: 'short', day: 'numeric' })}
                            </p>
                          </div>
                          <img
                            src={`https://openweathermap.org/img/wn/${f.icon}@2x.png`}
                            alt={f.condition}
                            className="w-16 h-16 drop-shadow-lg"
                          />
                        </div>

                        {/* Temperature and Condition */}
                        <div className="flex-1 p-6 flex flex-col sm:flex-row items-center justify-between gap-6 w-full">
                          <div className="text-center sm:text-left space-y-2">
                            <div className="flex items-baseline gap-2 justify-center sm:justify-start">
                              <span className="text-3xl font-black">{Math.round(f.maxTemp)}°</span>
                              <span className="text-lg font-bold text-foreground/30">{Math.round(f.minTemp)}°</span>
                            </div>
                            <p className="text-[10px] font-bold uppercase tracking-widest bg-primary/20 text-primary px-3 py-1 rounded-full inline-block">
                              {f.condition}
                            </p>
                          </div>

                          {/* Detailed Stats with Labels */}
                          <div className="grid grid-cols-3 gap-6 sm:gap-10 border-t sm:border-t-0 sm:border-l border-white/10 pt-4 sm:pt-0 sm:pl-8 w-full sm:w-auto">
                            <div className="text-center sm:text-left">
                              <p className="text-[9px] font-black uppercase text-foreground/30 mb-1 tracking-widest">Humidity</p>
                              <p className="text-sm font-black">{f.humidity}%</p>
                            </div>
                            <div className="text-center sm:text-left">
                              <p className="text-[9px] font-black uppercase text-foreground/30 mb-1 tracking-widest">Sunrise</p>
                              <p className="text-sm font-black">
                                {new Date(parseInt(f.sunRise) * 1000).toLocaleTimeString([], { hour: 'numeric', minute: '2-digit' })}
                              </p>
                            </div>
                            <div className="text-center sm:text-left">
                              <p className="text-[9px] font-black uppercase text-foreground/30 mb-1 tracking-widest">Sunset</p>
                              <p className="text-sm font-black">
                                {new Date(parseInt(f.sunSet) * 1000).toLocaleTimeString([], { hour: 'numeric', minute: '2-digit' })}
                              </p>
                            </div>
                          </div>
                        </div>
                      </div>
                    </CardContent>
                  </Card>
                </motion.div>
              ))}
            </div>
          </motion.div>
        )}

      </AnimatePresence>
    </main>
  );
}

function WeatherStat({ icon, label, value }: { icon: React.ReactNode, label: string, value: string }) {
  return (
    <div className="flex flex-col items-center sm:items-start gap-1 min-w-0">
      <div className="flex items-center gap-1.5 text-foreground/60 mb-1">
        <span className="h-3.5 w-3.5 sm:h-4 sm:w-4 flex-shrink-0">{icon}</span>
        <span className="text-[10px] sm:text-xs font-bold uppercase tracking-wider truncate">{label}</span>
      </div>
      <p className="text-base sm:text-lg font-bold truncate w-full text-center sm:text-left uppercase whitespace-nowrap">{value}</p>
    </div>
  );
}
