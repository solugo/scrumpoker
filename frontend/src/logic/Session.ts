export class Session {
    readonly rooms: {[key: string]: Room} = {}
    playerId?: string
}

export class Room {
    readonly players: { [key: string]: Player } = {}
    readonly state: State = new State()
    visible: boolean = false
    name?: string
    options?: { [key: string]: number }
}

export class Player {
    name?: string
    selection?: string | null
}

export class State {
    min: number | null = null
    max: number | null = null
    avg: number | null = null
}