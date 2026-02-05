import { useEffect, useState } from "react";

type User = {
    email: string;
    name: string;
    picture: string;
};

export function useAuth() {
    const [user, setUser] = useState<User | null>(null);
    const [loading, setLoading] = useState(true);

    const fetchUser = () => {
        setLoading(true);
        fetch(`${process.env.NEXT_PUBLIC_API_BASE_URL}/auth/me`, {
            credentials: "include", // 🔑 REQUIRED for HttpOnly cookie
        })
            .then((res) => {
                if (!res.ok) throw new Error("Not authenticated");
                return res.json();
            })
            .then((data) => {
                console.log("✅ User authenticated:", data);
                setUser(data);
            })
            .catch((error) => {
                console.log("⚠️ Not authenticated:", error.message);
                setUser(null);
            })
            .finally(() => {
                setLoading(false);
            });
    };

    useEffect(() => {
        // Fetch user on mount
        fetchUser();

        // Re-fetch when page becomes visible (e.g., after OAuth2 redirect in same tab)
        const handleVisibilityChange = () => {
            if (document.visibilityState === "visible") {
                console.log("🔄 Page visible, re-checking authentication...");
                fetchUser();
            }
        };

        // Re-fetch when window regains focus (e.g., after OAuth2 redirect in new tab)
        const handleFocus = () => {
            console.log("🔄 Window focused, re-checking authentication...");
            fetchUser();
        };

        document.addEventListener("visibilitychange", handleVisibilityChange);
        window.addEventListener("focus", handleFocus);

        return () => {
            document.removeEventListener("visibilitychange", handleVisibilityChange);
            window.removeEventListener("focus", handleFocus);
        };
    }, []);

    const logout = () => {
        window.location.href = `${process.env.NEXT_PUBLIC_API_BASE_URL}/auth/logout`;
    };

    return {
        user,
        loading,
        isAuthenticated: !!user,
        logout,
    };
}
