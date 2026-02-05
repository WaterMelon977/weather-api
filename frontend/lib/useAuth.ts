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
        const url = `${process.env.NEXT_PUBLIC_API_BASE_URL}/auth/me`;
        console.log("🌐 Fetching user from:", url);

        fetch(url, {
            credentials: "include", // 🔑 REQUIRED for HttpOnly cookie
        })
            .then(async (res) => {
                console.log("📡 /auth/me response status:", res.status);
                const contentType = res.headers.get("content-type");
                console.log("📄 Content-Type:", contentType);

                if (!res.ok) {
                    // Handle error responses
                    if (contentType?.includes("application/json")) {
                        const errorData = await res.json();
                        console.log("❌ Error response:", errorData);
                        throw new Error(errorData.error || "Not authenticated");
                    } else {
                        // HTML response (likely a redirect or error page)
                        const text = await res.text();
                        console.log("❌ HTML response (first 200 chars):", text.substring(0, 200));
                        throw new Error("Not authenticated");
                    }
                }

                // Check content type even for successful responses
                if (!contentType?.includes("application/json")) {
                    const text = await res.text();
                    console.log("⚠️ Expected JSON but got HTML (first 500 chars):", text.substring(0, 500));
                    throw new Error("Server returned HTML instead of JSON");
                }

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
