import {Dispatch, SetStateAction, useEffect, useState} from "react";

export function useStoredState<T>(key: string, value: T): [T, Dispatch<SetStateAction<T>>] {
    const [state, setState] = useState<T>(() => JSON.parse(window.localStorage.getItem(key) ?? 'null') ?? value)
    useEffect(() => localStorage.setItem(key, JSON.stringify(state)), [key, state]);
    return [state, setState]
}