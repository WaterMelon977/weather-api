import { useEffect, useState } from "react";


export function useDebounce<T>(value: T, delay = 300) {
    // State to store the debounced value
    const [debounced, setDebounced] = useState(value);

    useEffect(() => {
        // Set a timer to update the debounced value after the specified delay
        const timer = setTimeout(() => setDebounced(value), delay);

        // Clean up the timer if the value or delay changes, or if the component unmounts
        // This prevents the debounced value from updating if a new change occurs within the delay
        return () => clearTimeout(timer);
    }, [value, delay]);

    return debounced;
}

