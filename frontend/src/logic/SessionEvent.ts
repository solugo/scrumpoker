export interface SessionStartedEvent {
    type: 'sessionStarted'
    playerId: string
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

export interface PlayerJoinedRoomEvent {
    type: 'playerJoinedRoom'
    roomId: string
    playerId: string
}

export interface PlayerLeftRoomEvent {
    type: 'playerLeftRoom'
    roomId: string
    playerId: string
}

export interface PlayerInfoChangedEvent {
    type: 'playerInfoChanged'
    roomId: string
    playerId: string
    name: string
}

export type SessionEvent =
    | SessionStartedEvent
    | PlayerJoinedRoomEvent
    | PlayerLeftRoomEvent
    | PlayerInfoChangedEvent
    | RoomInfoUpdatedEvent
    | RoomSelectionChangedEvent
