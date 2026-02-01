"use client";

import { motion } from "framer-motion";

interface WeatherSliderProps {
    mode: "current" | "forecast";
    onChange: (mode: "current" | "forecast") => void;
}

export function WeatherSlider({ mode, onChange }: WeatherSliderProps) {
    return (
        <div className="relative flex p-1 glass rounded-full w-64 h-12 items-center cursor-pointer select-none">
            <motion.div
                className="absolute h-10 w-[124px] bg-white/40 rounded-full shadow-lg"
                initial={false}
                animate={{
                    x: mode === "current" ? 0 : 124,
                }}
                transition={{ type: "spring", stiffness: 300, damping: 30 }}
            />

            <div
                className={`flex-1 flex justify-center items-center z-10 font-semibold transition-colors duration-300 ${mode === "current" ? "text-primary" : "text-foreground/60"
                    }`}
                onClick={() => onChange("current")}
            >
                Current
            </div>

            <div
                className={`flex-1 flex justify-center items-center z-10 font-semibold transition-colors duration-300 ${mode === "forecast" ? "text-primary" : "text-foreground/60"
                    }`}
                onClick={() => onChange("forecast")}
            >
                Forecast
            </div>
        </div>
    );
}
