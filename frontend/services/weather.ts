const BASE_URL = process.env.NEXT_PUBLIC_API_BASE_URL;

export async function getCurrentWeather(city: string) {
    const res = await fetch(
        `${BASE_URL}/api/weather/current?city=${city}`,
        {
            cache: "no-store",
            credentials: "include"
        }
    );

    if (!res.ok) {
        throw new Error("Failed to fetch weather");
    }

    return res.json();
}

export async function getForecast(city: string) {
    const res = await fetch(
        `${BASE_URL}/api/weather/forecast?city=${city}`,
        {
            cache: "no-store",
            credentials: "include"
        }
    );

    if (!res.ok) {
        throw new Error("Failed to fetch forecast");
    }

    return res.json();
}

export async function getCitySuggestions(query: string) {
    if (!query || query.trim().length < 2) return [];

    const res = await fetch(
        `${process.env.NEXT_PUBLIC_API_BASE_URL}/api/cities?q=${query}`,
        { cache: "no-store" }
    );

    if (!res.ok) return [];
    return res.json();
}
