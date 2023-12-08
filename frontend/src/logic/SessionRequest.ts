export interface JoinRoomRequest {
    type: 'joinRoom'
    roomId?: string
    name: string,
    role?: string,
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

export interface KickParticipantRequest {
    type: 'kickParticipant'
    roomId: string
    participantId: string
}

export type SessionRequest =
    | JoinRoomRequest
    | ResetRoomRequest
    | RevealRoomRequest
    | UpdateSelectionRequest
    | KickParticipantRequest