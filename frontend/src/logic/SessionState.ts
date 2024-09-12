export class SessionState {
    readonly rooms: {[key: string]: Room} = {}
    participantId?: string
}

export class Room {
    readonly participants: { [key: string]: Participant } = {}
    visible: boolean = false
    name?: string
    options?: { [key: string]: number }
}

export class Participant {
    name?: string
    role?: string
    selection?: string | null
}
