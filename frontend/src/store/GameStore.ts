import {create} from "zustand";
import {v4} from "uuid";


export interface GameState {
    roomId: string;
    playerId: string;
}

export const usePlayerStore = create<GameState>((set, get) => ({
    restore
}))


function resolvePlayerId() {
    const stored = localStorage.getItem("playerId");
    if (stored === null || stored.trim().length === 0) {
        const generated = v4()
        localStorage.setItem("playerId", generated)
        return generated
    } else {
        return stored
    }
}