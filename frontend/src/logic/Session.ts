export class Session {
    readonly rooms: {[key: string]: Room} = {}
    participantId?: string
}

export class Room {
    readonly participants: { [key: string]: Participant } = {}
    readonly state: State = new State()
    visible: boolean = false
    name?: string
    options?: { [key: string]: number }
}

export class Participant {
    name?: string
    role?: string
    selection?: string | null
}

export class State {
    min: number | null = null
    max: number | null = null
    avg: number | null = null
}