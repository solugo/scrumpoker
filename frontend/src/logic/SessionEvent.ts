export interface SessionStartedEvent {
    type: 'sessionStarted'
    participantId: string
}

export interface RoomInfoUpdatedEvent {
    type: 'roomInfoChanged'
    roomId: string
    name: string
    options: { [key: string]: number }
}

export interface RoomSelectionChangedEvent {
    type: 'roomSelectionChanged'
    roomId: string
    visible: boolean
    selections: { [key: string]: string | null }
    avg: number | null
    min: number | null
    max: number | null
}

export interface ParticipantJoinedRoomEvent {
    type: 'participantJoinedRoom'
    roomId: string
    participantId: string
    name: string
    role: string
}

export interface ParticipantLeftRoomEvent {
    type: 'participantLeftRoom'
    roomId: string
    participantId: string
}

export interface ErrorEvent {
    type: 'error'
    message: string
}

export type SessionEvent =
    | ErrorEvent
    | SessionStartedEvent
    | ParticipantJoinedRoomEvent
    | ParticipantLeftRoomEvent
    | RoomInfoUpdatedEvent
    | RoomSelectionChangedEvent
