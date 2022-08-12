export interface JoinRoomRequest {
    type: 'joinRoom'
    roomId?: string
}

export interface ResetRoomRequest {
    type: 'resetRoom'
    roomId?: string
}

export interface RevealRoomRequest {
    type: 'revealRoom'
    roomId?: string
    visible?: boolean
}

export interface UpdateSelectionRequest {
    type: 'updateSelection'
    roomId: string
    selection: string | null
}

export type SessionRequest =
    | JoinRoomRequest
    | ResetRoomRequest
    | RevealRoomRequest
    | UpdateSelectionRequest